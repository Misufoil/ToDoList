package com.example.to_do_list

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.to_do_list.databinding.FragmentNewTaskSheetBinding


class NewTaskSheet : DialogFragment() {

    private lateinit var binding: FragmentNewTaskSheetBinding
    private lateinit var taskViewModel: TaskViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()
//        binding = FragmentNewTaskSheetBinding.inflate(layoutInflater)
        taskViewModel = ViewModelProvider(activity).get(TaskViewModel::class.java)

        val spinner: Spinner = binding.spinImportance
        val myStringArray = resources.getStringArray(R.array.priorities)

        val adapter = CustomArrayAdapter(this.requireContext(), android.R.layout.simple_spinner_item, myStringArray, 2)

//        val customSpinnerLayout = android.R.layout.simple_spinner_item
//        val adapter = object : ArrayAdapter<String>(this.requireContext(), customSpinnerLayout, myStringArray) {
//
//        }

// устанавливаем созданный макет в Spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.setSelection(0)
        spinner.adapter = adapter


//        this.context?.let {
//            ArrayAdapter.createFromResource(it,
//                R.array.priorities,
//                android.R.layout.simple_spinner_item
//            ).also { adapter ->
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                spinner.setSelection(0)
//                spinner.adapter = adapter
//            }
//        }

        binding.topAppBarNewTask.setOnMenuItemClickListener {menuItem ->
            when(menuItem.itemId) {
                R.id.action_save -> saveAction()
                else -> false
            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNewTaskSheetBinding.inflate(inflater, container, false)
        return binding.root
    }


    private fun saveAction(): Boolean {
        taskViewModel.desc.value = binding.editDesc.text.toString()
        binding.editDesc.setText("")
        dismiss()

        return true
    }

    class CustomArrayAdapter(context: Context, resource: Int, objects: Array<String>, private val selectedPosition: Int) : ArrayAdapter<String>(context, resource, objects) {
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