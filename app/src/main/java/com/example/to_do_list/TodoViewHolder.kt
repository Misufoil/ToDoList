package com.example.to_do_list

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Paint
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.CompoundButtonCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.to_do_list.databinding.TaskItemCellBinding
import model.Importance
import model.TodoItem
import java.util.logging.Handler

class TodoViewHolder(private val adapter: RecyclerView.Adapter<*>, item: View) : RecyclerView.ViewHolder(item) {
    private val binding = TaskItemCellBinding.bind(item)

//    private var checkBoxColor: ColorStateList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.light_gray))
//    private var color: Int = R.color.light_gray
//    private lateinit var checkBoxColor: ColorStateList


    fun bind(item: TodoItem) {
//        lateinit val checkBoxColor: ColorStateList
//        var color = R.color.light_gray
        binding.textView.text = item.desc
        binding.deadlineTextView.text = item.deadline

//        if (item.isDone) {
//            binding.textView.paintFlags = binding.textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
//            binding.checkBox.isChecked = item.isDone
//        } else {
//            binding.textView.paintFlags = binding.textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
//        }


        when (item.priority) {
            Importance.HIGH -> {
                binding.priorityImage.visibility = View.VISIBLE
                binding.priorityImage.setImageResource(R.drawable.ic_baseline_priority_high_24)
//                color = R.color.red
                val checkBoxColor =
                    ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.red))
                // Установка цвета флажка
                CompoundButtonCompat.setButtonTintList(binding.checkBox, checkBoxColor)
            }
            Importance.LOW -> {
                binding.priorityImage.visibility = View.VISIBLE
                binding.priorityImage.setImageResource(R.drawable.ic_baseline_south_24)
                val checkBoxColor =
                    ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.light_gray))
                // Установка цвета флажка
                CompoundButtonCompat.setButtonTintList(binding.checkBox, checkBoxColor)
            }
            else -> {
                binding.priorityImage.visibility = View.GONE
                val checkBoxColor =
                    ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.light_gray))
                // Установка цвета флажка
                CompoundButtonCompat.setButtonTintList(binding.checkBox, checkBoxColor)
            }
        }

        if (item.isDone) {
            binding.textView.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.darker_gray))
            binding.textView.paintFlags = binding.textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            binding.checkBox.isChecked = true
            // Установка зеленого цвета флажка
            val checkBoxColor = ColorStateList.valueOf(
                ContextCompat.getColor(itemView.context, R.color.green)
            )
            CompoundButtonCompat.setButtonTintList(binding.checkBox, checkBoxColor)
        } else {
            binding.textView.paintFlags = binding.textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            binding.textView.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.black))
            binding.checkBox.isChecked = false

            // Установка цвета флажка по умолчанию
//            val checkBoxColor = ColorStateList.valueOf(
//                ContextCompat.getColor(itemView.context, R.color.light_gray)
//            )
//            CompoundButtonCompat.setButtonTintList(binding.checkBox, checkBoxColor)
        }

        itemView.setOnClickListener {
            val context = itemView.context
            val intent = Intent(context, NewTaskSheet::class.java)
            val todoItemId = item.id.toString()
            intent.putExtra(MainActivity.TODO_ITEM_KEY, todoItemId)
            val activity = context as Activity

            val newTaskLauncher = (activity as MainActivity).newTaskLauncher
            newTaskLauncher.launch(intent)
        }


//        binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                // Изменение цвета текста CheckBox при выборе
//                val checkBoxColor = ColorStateList.valueOf(
//                    ContextCompat.getColor(
//                        itemView.context,
//                        R.color.green
//                    )
//                )
//                // Установка цвета флажка
//                CompoundButtonCompat.setButtonTintList(binding.checkBox, checkBoxColor)
//            }
////            } else {
////                val checkBoxColor =
////                    ColorStateList.valueOf(ContextCompat.getColor(itemView.context, color))
////                CompoundButtonCompat.setButtonTintList(checkBox, checkBoxColor)
////            }
//
//        }
    }
}