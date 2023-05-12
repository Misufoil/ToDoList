package com.example.to_do_list

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.to_do_list.databinding.FragmentNewTaskSheetBinding
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar


class NewTaskSheet : AppCompatActivity() {
    private lateinit var binding: FragmentNewTaskSheetBinding
    private lateinit var taskViewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentNewTaskSheetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        val spinner: Spinner = binding.spinImportance
        val myStringArray = resources.getStringArray(R.array.priorities)

        addElevationOnScroll(binding.nestedScrollView, binding.topAppBarNewTask)

        val adapter =
            CustomArrayAdapter(this, android.R.layout.simple_spinner_item, myStringArray, 2)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.setSelection(0)
        spinner.adapter = adapter

        binding.topMaterialToolbarNewTask.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_save -> saveAction()
                else -> false
            }
        }
    }

    class CustomArrayAdapter(
        context: Context,
        resource: Int,
        objects: Array<String>,
        private val selectedPosition: Int
    ) : ArrayAdapter<String>(context, resource, objects) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getView(position, convertView, parent)
            val textView = view.findViewById<TextView>(android.R.id.text1)
            if (position == selectedPosition) {
                textView.setTextColor(ContextCompat.getColor(this.context, R.color.red))
            } else {
                textView.setTextColor(ContextCompat.getColor(this.context, R.color.black))
            }
            return view
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getDropDownView(position, convertView, parent)
            val textView = view.findViewById<TextView>(android.R.id.text1)

            // устанавливаем цвет текста для определенной строки
            if (position == selectedPosition) {
                textView.setTextColor(ContextCompat.getColor(this.context, R.color.red))
            } else {
                textView.setTextColor(ContextCompat.getColor(this.context, R.color.black))
            }
            return view
        }
    }

    private fun saveAction(): Boolean {
        val returnIntent = Intent()
        returnIntent.putExtra("text", binding.editDesc.text.toString())
        setResult(Activity.RESULT_OK, returnIntent)
        taskViewModel.desc.value = binding.editDesc.text.toString()
        binding.editDesc.setText("")
        finish()
        return true
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

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        val activity = requireActivity()
////        binding = FragmentNewTaskSheetBinding.inflate(layoutInflater)
//        taskViewModel = ViewModelProvider(activity).get(TaskViewModel::class.java)
//
//        val spinner: Spinner = binding.spinImportance
//        val myStringArray = resources.getStringArray(R.array.priorities)
//
//        val adapter = CustomArrayAdapter(this.requireContext(), android.R.layout.simple_spinner_item, myStringArray, 2)
//
////        val customSpinnerLayout = android.R.layout.simple_spinner_item
////        val adapter = object : ArrayAdapter<String>(this.requireContext(), customSpinnerLayout, myStringArray) {
////
////        }
//
//// устанавливаем созданный макет в Spinner
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        spinner.setSelection(0)
//        spinner.adapter = adapter
//
//
////        this.context?.let {
////            ArrayAdapter.createFromResource(it,
////                R.array.priorities,
////                android.R.layout.simple_spinner_item
////            ).also { adapter ->
////                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
////                spinner.setSelection(0)
////                spinner.adapter = adapter
////            }
////        }
//
//        binding.topAppBarNewTask.setOnMenuItemClickListener {menuItem ->
//            when(menuItem.itemId) {
//                R.id.action_save -> saveAction()
//                else -> false
//            }
//        }
//    }


//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        binding = FragmentNewTaskSheetBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//


//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        val view: View = inflater.inflate(R.layout.fragment_dialog, container, false)
//
//        // Set the dialog to full screen
//        val window = dialog!!.window
//        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//        return view
//    }
//
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val dialog = super.onCreateDialog(savedInstanceState)
//        dialog.window?.apply {
//            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        }
//        return dialog
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setStyle(STYLE_NORMAL, R.style.FullScreenDialogTheme)
//    }

}