package com.example.to_do_list.adapters

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Paint
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.CompoundButtonCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.to_do_list.MainActivity
import com.example.to_do_list.NewTaskSheet
import com.example.to_do_list.R
import com.example.to_do_list.databinding.TaskItemCellBinding
import com.example.to_do_list.model.Importance
import com.example.to_do_list.model.TodoItem

class TodoViewHolder(
    item: View,
    private val mTouchHelper: ItemTouchHelper
) : RecyclerView.ViewHolder(item),
    View.OnTouchListener,
    GestureDetector.OnGestureListener {
    private val binding = TaskItemCellBinding.bind(item)
    private var mGestureDetector: GestureDetector = GestureDetector(itemView.context, this)
    private lateinit var item: TodoItem


    fun bind(item: TodoItem) {
        itemView.setOnTouchListener(this)
        binding.textView.text = item.desc
        binding.deadlineTextView.text = item.deadline
        this.item = item

        when (item.priority) {
            Importance.HIGH -> {
                binding.priorityImage.visibility = View.VISIBLE
                binding.priorityImage.setImageResource(R.drawable.ic_baseline_priority_high_24)
                val checkBoxColor =
                    ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.red))
                // Установка цвета флажка
                CompoundButtonCompat.setButtonTintList(binding.checkBox, checkBoxColor)
            }
            Importance.LOW -> {
                binding.priorityImage.visibility = View.VISIBLE
                binding.priorityImage.setImageResource(R.drawable.ic_baseline_south_24)
                val checkBoxColor =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.light_gray
                        )
                    )
                // Установка цвета флажка
                CompoundButtonCompat.setButtonTintList(binding.checkBox, checkBoxColor)
            }
            else -> {
                binding.priorityImage.visibility = View.GONE
                val checkBoxColor =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.light_gray
                        )
                    )
                // Установка цвета флажка
                CompoundButtonCompat.setButtonTintList(binding.checkBox, checkBoxColor)
            }
        }

        if (item.isDone) {
            binding.textView.setTextColor(
                ContextCompat.getColor(
                    itemView.context,
                    android.R.color.darker_gray
                )
            )
            binding.textView.paintFlags = binding.textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            binding.checkBox.isChecked = true
            // Установка зеленого цвета флажка
            val checkBoxColor = ColorStateList.valueOf(
                ContextCompat.getColor(itemView.context, R.color.green)
            )
            CompoundButtonCompat.setButtonTintList(binding.checkBox, checkBoxColor)
        } else {
            binding.textView.paintFlags =
                binding.textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            binding.textView.setTextColor(
                ContextCompat.getColor(
                    itemView.context,
                    android.R.color.black
                )
            )
            binding.checkBox.isChecked = false
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event != null) {
            mGestureDetector.onTouchEvent(event)
        }
        return true
    }

    override fun onDown(e: MotionEvent): Boolean {
        return true
    }

    override fun onShowPress(e: MotionEvent) {
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        val context = itemView.context
        val intent = Intent(context, NewTaskSheet::class.java)
        val todoItemId = item.id.toString()
        intent.putExtra(MainActivity.TODO_ITEM_KEY, todoItemId)
        val activity = context as Activity

        val newTaskLauncher = (activity as MainActivity).newTaskLauncher
        newTaskLauncher.launch(intent)
        return true
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return true
    }

    override fun onLongPress(e: MotionEvent) {
        mTouchHelper.startDrag(this)
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return true
    }
}