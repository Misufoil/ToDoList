package com.example.to_do_list

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.to_do_list.databinding.ActivityMainBinding
import com.google.gson.reflect.TypeToken
import model.TodoItem
import model.TodoItemsRepository



class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var taskViewModel: TaskViewModel
    private val adapter = TaskAdapter()
    var newTaskLauncher = registerNewTaskLauncher()
    private lateinit var sharedPreferences: SharedPreferences

    //    private lateinit var todoItemsRepository: TodoItemsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]
        sharedPreferences = getSharedPreferences("TodoItems", MODE_PRIVATE)

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
                handleScroll(recyclerView, dx, dy)
            }
        })

        loadTodoItems()

    }

    private fun handleScroll(recyclerView: RecyclerView, dx: Int, dy: Int) {
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
        binding.apply {
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }



    private fun registerNewTaskLauncher(): ActivityResultLauncher<Intent> {
        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val userData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    result.data?.getParcelableExtra(TODO_ITEM_KEY, TodoItem::class.java)
                } else {
                    result.data?.getParcelableExtra(TODO_ITEM_KEY)
                }
                if (userData != null) {
                    if (TodoItemsRepository.idInTodoItems(userData.id)) {
                        TodoItemsRepository.updateTodoItem(userData)
                    } else {
                        TodoItemsRepository.addTodoItem(userData)
                    }
                }
                adapter.notifyDataSetChanged()
                saveTodoItems()// Сохранение данных после добавления/обновления задачи
            }
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
        adapter.notifyDataSetChanged()
    }

    private fun saveTodoItems() {
        val json = TodoItemsRepository.toJson()
        sharedPreferences.edit().putString("todo_items", json).apply()
    }

    companion object {
        const val REQUEST_CODE= 1
        const val TODO_ITEM_KEY = "TODO_ITEM"
    }



}
