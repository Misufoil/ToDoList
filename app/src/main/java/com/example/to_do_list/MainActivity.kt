package com.example.to_do_list

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.to_do_list.adapters.TaskAdapter
import com.example.to_do_list.databinding.ActivityMainBinding
import com.example.to_do_list.model.TodoItem
import com.example.to_do_list.model.TodoItemsRepository
import com.example.to_do_list.util.MyItemTouchHelper
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var taskAdapter: TaskAdapter
    var newTaskLauncher = registerNewTaskLauncher()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("TodoItems", MODE_PRIVATE)

        setupView()
        setupListeners()
        initRecyclerView()
        loadTodoItems()
        updateCompletedText()
    }

    private fun setupView() {
        binding.newTaskButton.setOnClickListener {
            val newTask: TodoItem? = null
            val intent = Intent(this, NewTaskSheet::class.java)
            intent.putExtra(TODO_ITEM_KEY, newTask)
            newTaskLauncher.launch(intent)
        }

        var isIconVisible  = true
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.app_bar_visibility -> {
                    isIconVisible  = if (isIconVisible ) {
                        menuItem.setIcon(R.drawable.ic_baseline_visibility_off_24)
                        false
                    } else {
                        menuItem.setIcon(R.drawable.ic_baseline_visibility_24)
                        true
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun setupListeners() {
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                handleRecyclerViewScroll(recyclerView, dy)
            }
        })
    }

    private fun handleRecyclerViewScroll(recyclerView: RecyclerView, dy: Int) {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

        val totalItemCount = layoutManager.itemCount
        val isScrollingUp = dy > 0

        // Проверка, выходит ли RecyclerView за пределы экрана
        val isRecyclerViewOutOfScreen =
            firstVisibleItemPosition > 0 || lastVisibleItemPosition < totalItemCount - 1

        // Установка соответствующего фона в зависимости от состояния RecyclerView
        if (isRecyclerViewOutOfScreen && isScrollingUp) {
            recyclerView.setBackgroundResource(R.drawable.recycler_view_bg_no_rounded)
        } else {
            recyclerView.setBackgroundResource(R.drawable.recycler_view_bg_rounded)
        }
    }

    private fun initRecyclerView() {
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        taskAdapter = TaskAdapter(recyclerView, binding.completedTodoItemsTextView)

        val callback: ItemTouchHelper.Callback = MyItemTouchHelper(taskAdapter, this)
        val itemTouchHelper: ItemTouchHelper = ItemTouchHelper(callback)
        taskAdapter.setTouchHelper(itemTouchHelper)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        recyclerView.adapter = taskAdapter
    }

    private fun registerNewTaskLauncher(): ActivityResultLauncher<Intent> {
        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val item = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    result.data?.getParcelableExtra(TODO_ITEM_KEY, TodoItem::class.java)
                } else {
                    result.data?.getParcelableExtra(TODO_ITEM_KEY)
                }
                if (item != null) {
                    if (taskAdapter.idInTodoItems(item.id)) {
                        taskAdapter.updateTodoItem(item)
                    } else {
                        taskAdapter.addTodoItem(item)
                    }
                }
            } else if (result.resultCode == DELETE_RESULT_CODE) {
                val itemId = result.data?.getStringExtra(TODO_ITEM_ID_KEY)
                if (itemId != null) {
                    val deletedItemId = UUID.fromString(itemId)
                    val position = taskAdapter.getPositionById(deletedItemId)
                    taskAdapter.deleteTodoItem(position)
                }
            }
            saveTodoItems()// Сохранение данных после добавления/обновления задачи

        }
    }

    private fun loadTodoItems() {
        val json = sharedPreferences.getString("todo_items", null)
        if (json != null) {
            TodoItemsRepository.fromJson(json)
        }
        taskAdapter.setData(TodoItemsRepository.getAllTodoItems(), TodoItemsRepository.getAmountCompletedTodoItems())
    }

    private fun updateCompletedText() {
        val completedTodoItemsCount = taskAdapter.getAmountCompletedTodoItems()
        binding.completedTodoItemsTextView.text =
            resources.getString(R.string.done, completedTodoItemsCount)
    }

    private fun saveTodoItems() {
        TodoItemsRepository.updateTodoItems(taskAdapter.getAllTodoItems())
        val json = TodoItemsRepository.toJson()
        sharedPreferences.edit().putString("todo_items", json).apply()
    }

    override fun onDestroy() {
        saveTodoItems()
        super.onDestroy()
    }

    companion object {
        const val TODO_ITEM_ID_KEY = "todo_item_id"
        const val DELETE_RESULT_CODE = 2
        const val TODO_ITEM_KEY = "TODO_ITEM"
    }
}
