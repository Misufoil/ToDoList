package com.example.to_do_list

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.toColor
import androidx.lifecycle.ViewModelProvider
import com.example.to_do_list.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var taskViewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        binding.newTaskButton.setOnClickListener {
            NewTaskSheet().show(supportFragmentManager, "newTaskTag")
        }

        taskViewModel.desc.observe(this) {
            binding.taskDesc.text = String.format("Task Desc: %s", it)
        }

        var iconState = true

        binding.topAppBar.setOnMenuItemClickListener{menuItem ->
            when(menuItem.itemId) {
                R.id.app_bar_visibility -> {
                    iconState = if (iconState) {
                        menuItem.setIcon(R.drawable.ic_baseline_visibility_off_24)
                        false
                    } else {
                        menuItem.setIcon(R.drawable.ic_baseline_visibility_24)
                        true
                    }
                    true
                }
                else -> false
            }
        }

    }
}