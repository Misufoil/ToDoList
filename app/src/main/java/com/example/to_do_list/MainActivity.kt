package com.example.to_do_list

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.to_do_list.databinding.ActivityMainBinding
import com.google.gson.reflect.TypeToken
import model.TodoItem
import model.TodoItemsRepository
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var taskAdapter: TaskAdapter
    var newTaskLauncher = registerNewTaskLauncher()
    private lateinit var sharedPreferences: SharedPreferences

    //    private lateinit var todoItemsRepository: TodoItemsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCompletedText()

        init()

//        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]
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

        val swipeCallBack = object : SwipeCallBack() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if(direction == ItemTouchHelper.LEFT) {
                    TodoItemsRepository.deleteTodoItem(position)
                    binding.recyclerView.adapter?.notifyItemRemoved(position)
                } else if (direction == ItemTouchHelper.RIGHT) {
                    TodoItemsRepository.itIsDone(position)
                    setCompletedText()
                    taskAdapter.notifyItemChanged(position)
                }
                saveTodoItems()
            }

            private val leftBackground  = ColorDrawable(resources.getColor(R.color.red))
            private val rightBackground = ColorDrawable(resources.getColor(R.color.green))
            private val intrinsicWidth = 0
            private val intrinsicHeight = 0

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val itemHeight = itemView.bottom - itemView.top

                when {
                    dX > 0 -> {
                        rightBackground.setBounds(
                            itemView.left,
                            itemView.top,
                            itemView.left + dX.toInt(),
                            itemView.bottom
                        )
                        rightBackground.draw(c)
                    }
                    dX < 0 -> {
                        leftBackground.setBounds(
                            itemView.right + dX.toInt(),
                            itemView.top,
                            itemView.right,
                            itemView.bottom
                        )
                        leftBackground.draw(c)
                    }
                    else -> {
                        leftBackground.setBounds(0, 0, 0, 0)
                        rightBackground.setBounds(0, 0, 0, 0)
                    }
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

            override fun onChildDrawOver(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder?,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder?.itemView

                if (itemView != null) {
                    when {
                        dX > 0 -> {
                            rightBackground.setBounds(
                                itemView.left,
                                itemView.top,
                                itemView.left + dX.toInt(),
                                itemView.bottom
                            )
                            rightBackground.draw(c)
                        }
                        dX < 0 -> {
                            leftBackground.setBounds(
                                itemView.right + dX.toInt(),
                                itemView.top,
                                itemView.right,
                                itemView.bottom
                            )
                            leftBackground.draw(c)
                        }
                        else -> {
                            leftBackground.setBounds(0, 0, 0, 0)
                            rightBackground.setBounds(0, 0, 0, 0)
                        }
                    }
                }

                super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

        }

        val itemTouchHelper = ItemTouchHelper(swipeCallBack)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
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
        taskAdapter = TaskAdapter()
        recyclerView.adapter = taskAdapter
    }



    private fun registerNewTaskLauncher(): ActivityResultLauncher<Intent> {
        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val item  = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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
        val todoItems = json?.let { TodoItemsRepository.fromJson(it) } ?: mutableListOf()
        TodoItemsRepository.addAllTodoItems(todoItems)
        taskAdapter.notifyDataSetChanged()
    }

    private fun saveTodoItems() {
        val json = TodoItemsRepository.toJson()
        sharedPreferences.edit().putString("todo_items", json).apply()
    }

    fun setCompletedText() {
        val completedTodoItemsCount = TodoItemsRepository.getAmountCompletedTodoItems()
        binding.completedTodoItemsTextView.text = resources.getString(R.string.done, completedTodoItemsCount)
    }

//    fun addItem(item:TodoItem) {
//        TodoItemsRepository.addTodoItem(item)
//        adapter.notifyDataSetChanged()
//    }

//    fun updateItem(updatedItem: TodoItem) {
//        val index = TodoItemsRepository.updateTodoItem(updatedItem)
//        adapter.notifyItemChanged(index)
//    }


    companion object {
        const val REQUEST_CODE= 1
        const val TODO_ITEM_ID_KEY = "todo_item_id"
        const val DELETE_RESULT_CODE = 2
        const val TODO_ITEM_KEY = "TODO_ITEM"
    }
}
