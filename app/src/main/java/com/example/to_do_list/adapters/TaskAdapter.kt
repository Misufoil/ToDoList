package com.example.to_do_list.adapters

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.to_do_list.R
import com.example.to_do_list.model.TodoItem
import com.example.to_do_list.model.TodoItemsRepository
import com.example.to_do_list.util.ItemTouchHelperAdapter
import com.google.android.material.snackbar.Snackbar
import java.util.*

class TaskAdapter(
    private val parentRecyclerView: RecyclerView,
    private val completedTodoItemsTextView: TextView
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemTouchHelperAdapter {
    private val todoItems: MutableList<TodoItem> = mutableListOf()
    private var completedTodoItems: Int = 0

    private lateinit var mTouchHelper: ItemTouchHelper
    private var sharedPreferences: SharedPreferences =
        completedTodoItemsTextView.context.getSharedPreferences("TodoItems", MODE_PRIVATE)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_DEFAULT -> {
                val defaultViewLayout = LayoutInflater.from(parent.context)
                    .inflate(R.layout.default_item_layout, parent, false)
                DefaultViewHolder(defaultViewLayout)
            }
            VIEW_TYPE_ITEM -> {
                val taskItemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.task_item_cell, parent, false)
                TodoViewHolder(taskItemView, mTouchHelper)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DefaultViewHolder) {
            holder.bind()
        } else if (holder is TodoViewHolder) {
            val todoItem = todoItems[position]
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
            swap(fromPosition, toPosition)
            saveTodoItems()
        }
    }

    override fun onItemSwiped(position: Int, direction: Int) {
        when (direction) {
            ItemTouchHelper.START -> {
                val deleteItem = todoItems[position]
                deleteTodoItem(position)
                // Измените android.R.layout.content_frame на вашу корневую разметку

                Snackbar.make(parentRecyclerView, "Дело удалено", Snackbar.LENGTH_LONG)
                    .setAction("Отменить") {
                        deleteItem.let {
                            addTodoItem(deleteItem, position)
                            setCompletedText()
                            saveTodoItems()
                        }
                    }.show()
            }

            ItemTouchHelper.END -> {
                toggleItemDoneStatus(position)
            }
        }
        setCompletedText()
        saveTodoItems()
    }

    private fun isDefaultViewHolderPosition(position: Int): Boolean {
        return position == itemCount - 1
    }

    fun setTouchHelper(touchHelper: ItemTouchHelper) {
        this.mTouchHelper = touchHelper
    }


    fun setData(items: List<TodoItem>, completedTodoItemsCount: Int) {
        todoItems.clear()
        todoItems.addAll(items)
        completedTodoItems = completedTodoItemsCount

        notifyDataSetChanged()
    }

    private fun setCompletedText() {
        completedTodoItemsTextView.text =
            completedTodoItemsTextView.context.resources.getString(
                R.string.done,
                completedTodoItems
            )
    }

    private fun saveTodoItems() {
        TodoItemsRepository.updateTodoItems(todoItems)
        val json = TodoItemsRepository.toJson()
        sharedPreferences.edit().putString("todo_items", json).apply()
    }

    fun idInTodoItems(id: UUID): Boolean {
        val index = todoItems.indexOfFirst { it.id == id }
        return index != -1
    }

    fun updateTodoItem(todoItem: TodoItem) {
        val position = todoItems.indexOfFirst { it.id == todoItem.id }
        if (position != -1) {
            todoItems[position] = todoItem
        }
        notifyItemChanged(position)
    }

    fun addTodoItem(todoItem: TodoItem, position: Int = 0) {
        todoItems.add(position, todoItem)
        if (todoItem.isDone){
            completedTodoItems++
        }
        notifyItemInserted(position)
    }

    fun deleteTodoItem(position: Int) {
        if(todoItems[position].isDone) {
            completedTodoItems--
        }

        todoItems.removeAt(position)
        setCompletedText()
        notifyItemRemoved(position)
    }

    fun getPositionById(id: UUID): Int {
        return todoItems.indexOfFirst { it.id == id }
    }

    private fun toggleItemDoneStatus(position: Int) {
        if (!todoItems[position].isDone) {
            todoItems[position].isDone = true
            completedTodoItems++
        }
        notifyItemChanged(position)
    }

    private fun swap(fromPosition: Int, toPosition: Int) {
        val element =
            todoItems.removeAt(fromPosition) // Удаляем элемент из исходной позиции и сохраняем его
        todoItems.add(toPosition, element)
        notifyItemMoved(fromPosition, toPosition)
    }

    fun getAllTodoItems(): MutableList<TodoItem> {
        return todoItems
    }

    fun getAmountCompletedTodoItems(): Int {
        return completedTodoItems
    }

    companion object {
        private const val VIEW_TYPE_DEFAULT = 0
        private const val VIEW_TYPE_ITEM = 1
    }
}