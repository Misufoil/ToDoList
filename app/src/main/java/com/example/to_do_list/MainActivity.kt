package com.example.to_do_list

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
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
        setCompletedText()
        init()

        sharedPreferences = getSharedPreferences("TodoItems", MODE_PRIVATE)
        loadTodoItems()

        binding.newTaskButton.setOnClickListener {
            val intent = Intent(this, NewTaskSheet::class.java)
            intent.putExtra(TODO_ITEM_KEY, "")
            newTaskLauncher.launch(intent)
        }

        var iconState = true
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.app_bar_visibility -> {
                    iconState = if (iconState) {
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

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                handleScroll(recyclerView, dy)
            }
        })
    }

    private fun handleScroll(recyclerView: RecyclerView, dy: Int) {
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

    private fun init() {
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
                    if (TodoItemsRepository.idInTodoItems(item.id)) {
                        TodoItemsRepository.updateTodoItem(item)
                    } else {
                        TodoItemsRepository.addTodoItem(item)
                    }
                    taskAdapter.notifyDataSetChanged()
                }
            } else if (result.resultCode == DELETE_RESULT_CODE) {
                val itemId = result.data?.getStringExtra(TODO_ITEM_ID_KEY)
                if (itemId != null) {
                    val deletedItemId = UUID.fromString(itemId)
                    val position = TodoItemsRepository.getPositionById(deletedItemId)
                    TodoItemsRepository.deleteTodoItem(position)
                    binding.recyclerView.adapter?.notifyItemRemoved(position)
                }
            }
            saveTodoItems()// Сохранение данных после добавления/обновления задачи

        }
    }

    override fun onDestroy() {
        saveTodoItems()
        super.onDestroy()
    }

    private fun loadTodoItems() {
        val json = sharedPreferences.getString("todo_items", null)
        if (json != null) {
            TodoItemsRepository.fromJson(json)
        }
        setCompletedText()
        taskAdapter.notifyDataSetChanged()
    }

    private fun saveTodoItems() {
        val json = TodoItemsRepository.toJson()
        sharedPreferences.edit().putString("todo_items", json).apply()
    }

    private fun setCompletedText() {
        val completedTodoItemsCount = TodoItemsRepository.getAmountCompletedTodoItems()
        binding.completedTodoItemsTextView.text =
            resources.getString(R.string.done, completedTodoItemsCount)
    }

    companion object {
        const val REQUEST_CODE = 1
        const val TODO_ITEM_ID_KEY = "todo_item_id"
        const val DELETE_RESULT_CODE = 2
        const val TODO_ITEM_KEY = "TODO_ITEM"
    }
}
