package com.example.to_do_list.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Color.red
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
import com.google.android.material.color.MaterialColors
import java.time.LocalDate

class TodoViewHolder(
    item: View,
    private val mTouchHelper: ItemTouchHelper
) : RecyclerView.ViewHolder(item),
    View.OnTouchListener,
    GestureDetector.OnGestureListener {
    private val binding = TaskItemCellBinding.bind(item)
    private var mGestureDetector: GestureDetector = GestureDetector(itemView.context, this)
    private lateinit var todoItem: TodoItem


    fun bind(item: TodoItem) {
        itemView.setOnTouchListener(this)
        binding.textView.text = item.desc
        binding.deadlineTextView.text = item.deadline
        this.todoItem = item

        when (item.priority) {
            Importance.HIGH -> {
                binding.priorityImage.visibility = View.VISIBLE
                binding.priorityImage.setImageResource(R.drawable.ic_baseline_priority_high_24)
                setCheckBoxColor(MaterialColors.getColor(itemView.context, R.attr.red,Color.RED))
            }
            Importance.LOW -> {
                binding.priorityImage.visibility = View.VISIBLE
                binding.priorityImage.setImageResource(R.drawable.ic_baseline_south_24)
                setCheckBoxColor(MaterialColors.getColor(itemView.context, R.attr.separatorViewColor,Color.GRAY))
            }
            else -> {
                binding.priorityImage.visibility = View.GONE
                setCheckBoxColor(MaterialColors.getColor(itemView.context, R.attr.separatorViewColor,Color.GRAY))
            }
        }

        if (item.isDone) {
            setStrikeThroughText(true)
            setCheckBoxColor(MaterialColors.getColor(itemView.context, R.attr.green,Color.GREEN))
            binding.checkBox.isChecked = true

        } else {
            setStrikeThroughText(false)
            binding.checkBox.isChecked = false
        }
    }

    private fun setCheckBoxColor(color: Int) {
        val checkBoxColor = ColorStateList.valueOf(color)
        CompoundButtonCompat.setButtonTintList(binding.checkBox, checkBoxColor)
    }

    private fun setStrikeThroughText(isStrikeThrough: Boolean) {
        if (isStrikeThrough) {
            binding.textView.paintFlags =
                binding.textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            binding.textView.setTextColor(
                MaterialColors.getColor(itemView.context, R.attr.labelTertiary, Color.GRAY)
            )
        } else {
            binding.textView.paintFlags =
                binding.textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            binding.textView.setTextColor(
                MaterialColors.getColor(
                    itemView.context,
                    com.google.android.material.R.attr.colorOnPrimary,
                    Color.BLACK
                )
            )
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event != null) {
            mGestureDetector.onTouchEvent(event)
//            if (event.action == MotionEvent.ACTION_UP && !mGestureDetector.onTouchEvent(event)) {
//                v?.performClick()
//            }
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
        val intent = createIntent(context, todoItem)
        launchActivity(context, intent)
        return true
    }

    private fun createIntent(context: Context, todoItem: TodoItem): Intent {
        val intent = Intent(context, NewTaskSheet::class.java)
        todoItem.modifiedDate = LocalDate.now()
        intent.putExtra(MainActivity.TODO_ITEM_KEY, todoItem)
        return intent
    }

    private fun launchActivity(context: Context, intent: Intent) {
        val activity = context as Activity
        val newTaskLauncher = (activity as MainActivity).newTaskLauncher
        newTaskLauncher.launch(intent)
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