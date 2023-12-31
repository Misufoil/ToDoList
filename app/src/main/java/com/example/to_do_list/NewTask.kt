package com.example.to_do_list

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.example.to_do_list.MainActivity.Companion.DELETE_RESULT_CODE
import com.example.to_do_list.MainActivity.Companion.TODO_ITEM_ID_KEY
import com.example.to_do_list.MainActivity.Companion.TODO_ITEM_KEY
import com.example.to_do_list.databinding.FragmentNewTaskSheetBinding
import com.example.to_do_list.model.Importance
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.datepicker.MaterialDatePicker
import com.example.to_do_list.model.TodoItem
import com.example.to_do_list.model.toImportance
import com.google.android.material.color.MaterialColors
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


class NewTask : AppCompatActivity() {
    private var isEditMode = false
    private var isHandlingEvent = true
    private lateinit var binding: FragmentNewTaskSheetBinding
    private lateinit var todoItem: TodoItem


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentNewTaskSheetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()

        val receivedItem: TodoItem? =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(TODO_ITEM_KEY, TodoItem::class.java)
            } else {
                intent.getParcelableExtra(TODO_ITEM_KEY)
            }

        processReceivedItem(receivedItem)
        setupSaveButtonListener()
        addElevationOnScroll(binding.nestedScrollView, binding.topAppBarNewTask)
    }

    private fun initView() {
        val spinner: Spinner = binding.spinImportance
        val importanceList = Importance.values().map { it.text }
        val adapter =
            CustomArrayAdapter(this, android.R.layout.simple_spinner_item, importanceList, 2)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        binding.setDeadline.setOnCheckedChangeListener { _, isChecked ->
            if (isHandlingEvent) {
                if (isChecked) {
                    showDatePicker()
                } else {
                    hideDatePicker()
                }
            }
        }

        binding.deleteButton.setOnClickListener {
            deleteItem()
        }

        binding.topMaterialToolbarNewTask.setNavigationOnClickListener {
            finish()
        }

    }

    private fun processReceivedItem(receivedItem: TodoItem?) {
        if (receivedItem != null) {
            // Редактирование существующего дела
            isEditMode = true
            todoItem = receivedItem

            binding.editDesc.setText(todoItem.desc)

            val deadLine = todoItem.deadline
            if(deadLine!!.isNotEmpty()) {
                isHandlingEvent = false
                binding.deadlineTextView.text = todoItem.deadline
                binding.setDeadline.isChecked = true
                isHandlingEvent = true
            }

            val selectedPosition = todoItem.priority.ordinal
            binding.spinImportance.setSelection(selectedPosition)
        } else {
            binding.spinImportance.setSelection(0)
        }

        setupDeleteButtonView()
    }

    private fun setupSaveButtonListener() {
        binding.topMaterialToolbarNewTask.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_save -> saveAction()

                else -> false
            }
        }
    }

    private fun addElevationOnScroll(
        nestedScrollView: NestedScrollView,
        topAppBarNewTask: AppBarLayout
    ) {
        nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            // Устанавливаем начальное значение elevation
            val initialElevation = 16f

            // Создаем анимацию изменения elevation
            val animator = ValueAnimator.ofFloat(0f, initialElevation).apply {
                duration = 100 // Длительность анимации в миллисекундах
                interpolator =
                    AccelerateDecelerateInterpolator() // Интерполятор для плавного изменения значения
                addUpdateListener {
                    // Обновляем значение elevation у AppBarLayout
                    topAppBarNewTask.elevation = it.animatedValue as Float
                }
            }

            if (scrollY > 0 && topAppBarNewTask.elevation == 0f) {
                animator.start()
            } else if (scrollY == 0 && topAppBarNewTask.elevation > 0f) {
                // Если NestedScrollView не прокручен вверх, удаляем тень с помощью анимации
                animator.reverse()
            }
        }
    }

    private fun deleteItem() {
        if (isEditMode) {
            val returnIntent = Intent()
            returnIntent.putExtra(TODO_ITEM_ID_KEY, todoItem.id.toString())
            setResult(DELETE_RESULT_CODE, returnIntent)
        }
        finish()
    }

    private fun showDatePicker() {
        val builder = MaterialDatePicker.Builder.datePicker()
            .setTheme(R.style.CustomMaterialDatePickerStyle)

        val datePicker = builder.build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = selection

            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

            binding.deadlineTextView.text = dateFormat.format(calendar.time)
        }

        datePicker.addOnCancelListener {
            // Устанавливаем isChecked в false, так как пользователь вышел из DatePicker окна
            binding.setDeadline.isChecked = false
        }

        datePicker.addOnNegativeButtonClickListener {
            // Устанавливаем isChecked в false, так как нажали "Отмена"
            binding.setDeadline.isChecked = false
        }

        datePicker.show(supportFragmentManager, "DatePickerDialog")
    }

    private fun hideDatePicker() {
        binding.deadlineTextView.text = ""
    }

    private fun saveAction(): Boolean {
        if (binding.editDesc.text?.isNotBlank() == true) {
            if (isEditMode) {
                todoItem.apply {
                    desc = binding.editDesc.text.toString()
                    priority = binding.spinImportance.selectedItem.toString().toImportance()
                    deadline = binding.deadlineTextView.text.toString()
                    modifiedDate = LocalDate.now()
                }
            } else {
                todoItem = TodoItem(
                    desc = binding.editDesc.text.toString(),
                    priority = binding.spinImportance.selectedItem.toString().toImportance(),
                    deadline = binding.deadlineTextView.text.toString(),
                    modifiedDate = LocalDate.now()
                )
            }
            val returnIntent = Intent()
            returnIntent.putExtra(TODO_ITEM_KEY, todoItem)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        } else {
            Toast.makeText(this, "Пожалуйста, введите описание", Toast.LENGTH_SHORT).show()
        }
        return true
    }

    private fun setupDeleteButtonView() {
        var color = MaterialColors.getColor(this, R.attr.disableStatus, Color.GRAY)

        if (isEditMode) {
            binding.deleteButton.isEnabled = true
            color = MaterialColors.getColor(this, R.attr.red, Color.RED)

        } else {
            binding.deleteButton.isEnabled = false
        }

        binding.deleteButton.setTextColor(color)
        binding.deleteButton.compoundDrawables[0].setTint(color)
    }

    class CustomArrayAdapter(
        context: Context,
        resource: Int,
        objects: List<String>,
        private val selectedPosition: Int
    ) : ArrayAdapter<String>(context, resource, objects) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getView(position, convertView, parent)
            val textView = view.findViewById<TextView>(android.R.id.text1)
            if (position == selectedPosition) {
                textView.setTextColor(
                    MaterialColors.getColor(
                        this.context, R.attr.red,
                        Color.RED
                    )
                )
            } else {
                textView.setTextColor(
                    MaterialColors.getColor(
                        this.context, com.google.android.material.R.attr.colorOnPrimary, Color.BLACK
                    )
                )
            }
            return view
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getDropDownView(position, convertView, parent)
            val textView = view.findViewById<TextView>(android.R.id.text1)

            // устанавливаем цвет текста для определенной строки
            if (position == selectedPosition) {
                textView.setTextColor(
                    MaterialColors.getColor(
                        this.context, R.attr.red,
                        Color.RED
                    )
                )
            } else {
                textView.setTextColor(
                    MaterialColors.getColor(
                        this.context, com.google.android.material.R.attr.colorOnPrimary, Color.BLACK
                    )
                )
            }
            return view
        }
    }
}