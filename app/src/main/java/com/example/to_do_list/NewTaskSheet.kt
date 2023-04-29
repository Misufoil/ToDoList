package com.example.to_do_list

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.example.to_do_list.databinding.ActivityMainBinding
import com.example.to_do_list.databinding.FragmentNewTaskSheetBinding


class NewTaskSheet : Fragment() {

    private lateinit var binding: FragmentNewTaskSheetBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentNewTaskSheetBinding.inflate(layoutInflater)

        val spinner: Spinner = binding.spinImportance

        this.context?.let {
            ArrayAdapter.createFromResource(it,
                R.array.priorities,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.setSelection(0)
                spinner.adapter = adapter
            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNewTaskSheetBinding.inflate(inflater, container, false)
        return binding.root
    }




}