package com.example.to_do_list

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.Color.green
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.marginStart
import androidx.core.view.updateLayoutParams
import androidx.core.widget.CompoundButtonCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.to_do_list.databinding.TaskItemCellBinding
import com.google.android.material.resources.MaterialResources.getDimensionPixelSize
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.NonDisposableHandle.parent
import model.Importance
import model.TodoItem
import model.TodoItemsRepository

//class TaskAdapter() : RecyclerView.Adapter<TodoViewHolder>() {
//    private val todoItems: List<TodoItem> = TodoItemsRepository.getAllTodoItems()
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
//        val view =
//            LayoutInflater.from(parent.context).inflate(R.layout.task_item_cell, parent, false)
//        return TodoViewHolder(this, view)
//    }
//
//    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
//        val todoItem = TodoItemsRepository.getTodoItemsByPosition(position)
//        holder.bind(todoItem)
//    }
//
//    override fun getItemCount(): Int {
//        return todoItems.size
//    }
//
////    fun deleteItem(position: Int) {
//////        todoItems.removeAt(position)
////        TodoItemsRepository.deleteTodoItem(position)
////        notifyItemRemoved(position)
////    }
//}

class TaskAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val todoItems: List<TodoItem> = TodoItemsRepository.getAllTodoItems()
    private val VIEW_TYPE_DEFAULT = 0
    private val VIEW_TYPE_ITEM = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_DEFAULT -> {
                val defaultView = LayoutInflater.from(parent.context).inflate(R.layout.default_item_layout, parent, false)
                DefaultViewHolder(defaultView)
            }
            VIEW_TYPE_ITEM -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item_cell, parent, false)
                TodoViewHolder(this, view)
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
}