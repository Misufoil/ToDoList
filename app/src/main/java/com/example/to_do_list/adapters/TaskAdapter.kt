package com.example.to_do_list.adapters

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.to_do_list.MainActivity
import com.example.to_do_list.R
import com.example.to_do_list.model.TodoItem
import com.example.to_do_list.model.TodoItemsRepository
import com.example.to_do_list.util.ItemTouchHelperAdapter
import com.google.android.material.snackbar.Snackbar

class TaskAdapter(
    private val recyclerView: RecyclerView,
    private val context: Context,
    private val completedTodoItemsTextView: TextView
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemTouchHelperAdapter {
    private val todoItems: List<TodoItem> = TodoItemsRepository.getAllTodoItems()
    private val VIEW_TYPE_DEFAULT = 0
    private val VIEW_TYPE_ITEM = 1
    private lateinit var mTouchHelper: ItemTouchHelper
    private var sharedPreferences: SharedPreferences =
        context.getSharedPreferences("TodoItems", MODE_PRIVATE)
    private lateinit var deleteItem: TodoItem

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_DEFAULT -> {
                val defaultView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.default_item_layout, parent, false)
                DefaultViewHolder(defaultView)
            }
            VIEW_TYPE_ITEM -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.task_item_cell, parent, false)
                TodoViewHolder(this, view, mTouchHelper)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DefaultViewHolder) {
            holder.bind()
        } else if (holder is TodoViewHolder) {
            val todoItem = TodoItemsRepository.getTodoItemsByPosition(position)
            holder.bind(todoItem)
        }
    }

    override fun getItemCount(): Int {
        // Учитываем статический элемент, добавляем 1 к общему количеству элементов
        return todoItems.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1) {
            VIEW_TYPE_DEFAULT
        } else {
            VIEW_TYPE_ITEM
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (!isDefaultViewHolderPosition(fromPosition) && !isDefaultViewHolderPosition(toPosition)) {
            TodoItemsRepository.swap(fromPosition, toPosition)
            notifyItemMoved(fromPosition, toPosition)
            saveTodoItems()
        }
    }

    private fun isDefaultViewHolderPosition(position: Int): Boolean {
        return position == itemCount - 1
    }

    override fun onItemSwiped(position: Int, direction: Int) {
        when (direction) {
            ItemTouchHelper.START -> {
                deleteItem = todoItems[position]
                TodoItemsRepository.deleteTodoItem(position)
                notifyItemRemoved(position)
                // Измените android.R.layout.content_frame на вашу корневую разметку

                Snackbar.make(recyclerView, "Дело удалено", Snackbar.LENGTH_LONG)
                    .setAction("Отменить") {
                        deleteItem.let {
                            TodoItemsRepository.addOnPosition(position, deleteItem)
                            notifyItemInserted(position)
                            saveTodoItems()
                            setCompletedText()
                        }
                    }.show()
                saveTodoItems()
                setCompletedText()
            }

            ItemTouchHelper.END -> {
                TodoItemsRepository.itIsDone(position)
                notifyItemChanged(position)
                saveTodoItems()
            }
        }
        saveTodoItems()
        setCompletedText()
    }

    fun setTouchHelper(touchHelper: ItemTouchHelper) {
        this.mTouchHelper = touchHelper
    }

    private fun setCompletedText() {
        val completedTodoItemsCount = TodoItemsRepository.getAmountCompletedTodoItems()
        completedTodoItemsTextView.text =
            context.resources.getString(R.string.done, completedTodoItemsCount)
    }

    private fun saveTodoItems() {
        val json = TodoItemsRepository.toJson()
        sharedPreferences.edit().putString("todo_items", json).apply()
    }

}