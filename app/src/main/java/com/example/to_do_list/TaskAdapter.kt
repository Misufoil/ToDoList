package com.example.to_do_list

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Color.green
import android.graphics.PorterDuff
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.marginStart
import androidx.core.view.updateLayoutParams
import androidx.core.widget.CompoundButtonCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.to_do_list.databinding.TaskItemCellBinding
import com.google.android.material.resources.MaterialResources.getDimensionPixelSize
import kotlinx.coroutines.NonDisposableHandle.parent
import model.Importance
import model.TodoItem
import model.TodoItemsRepository

class TaskAdapter : RecyclerView.Adapter<TaskAdapter.TodoViewHolder>() {
    private val todoItems: List<TodoItem> = TodoItemsRepository.getAllTodoItems()

    class TodoViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val binding = TaskItemCellBinding.bind(item)

        fun bind(item: TodoItem) = with(binding) {
            lateinit var checkBoxColor: ColorStateList
            var color = R.color.light_gray
            textView.text = item.desc
            deadlineTextView.text = item.deadline

            when (item.priority) {
                Importance.HIGH -> {
                    priorityImage.setImageResource(R.drawable.ic_baseline_priority_high_24)
                    color = R.color.red
                    checkBoxColor =
                        ColorStateList.valueOf(ContextCompat.getColor(itemView.context, color))
                    // Установка цвета флажка
                    CompoundButtonCompat.setButtonTintList(checkBox, checkBoxColor)
                }
                Importance.LOW -> priorityImage.setImageResource(R.drawable.ic_baseline_south_24)
                else -> priorityImage.visibility = View.GONE
            }

            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, NewTaskSheet::class.java)
                val todoItemId = item.id.toString()
                intent.putExtra(MainActivity.TODO_ITEM_KEY, todoItemId)
                val activity = context as Activity

                val newTaskLauncher = (activity as MainActivity).newTaskLauncher
                newTaskLauncher.launch(intent)


//                val position = adapterPosition
//                if (position != RecyclerView.NO_POSITION) {
////                    val todoItem = todoItems[position]
//                    // Действия при клике на элемент
//
//                    // Получите ID объекта todoItem
//                    val todoItemId = item.id.toString()
//
//                    val intent = Intent(itemView.context, NewTaskSheet::class.java)
//
//                    intent.putExtra(MainActivity.TODO_ITEM_KEY, todoItemId)
//
//                    val activity = itemView.context as Activity
//
//                    activity.startActivityForResult(intent, MainActivity.REQUEST_CODE)
//                }
            }

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // Изменение цвета текста CheckBox при выборе
                    checkBoxColor = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.green
                        )
                    )
                    // Установка цвета флажка
                    CompoundButtonCompat.setButtonTintList(checkBox, checkBoxColor)
                } else {
                    checkBoxColor =
                        ColorStateList.valueOf(ContextCompat.getColor(itemView.context, color))
                    CompoundButtonCompat.setButtonTintList(checkBox, checkBoxColor)
//                    val color = ContextCompat.getColor(itemView.context, color)
//                    checkBox.buttonDrawable?.let { DrawableCompat.setTint(it, color) }
                }
            }




        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskAdapter.TodoViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.task_item_cell, parent, false)
        return TaskAdapter.TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todoItem = TodoItemsRepository.getTodoItemsByPosition(position)
        holder.bind(todoItem)
    }

    override fun getItemCount(): Int {
        return todoItems.size
    }
//    companion object {
//        private const val REQUEST_CODE_SECOND_ACTIVITY = 1
//    }

}