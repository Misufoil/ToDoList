package com.example.to_do_list.model

import androidx.recyclerview.widget.DiffUtil.calculateDiff
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

object TodoItemsRepository {
    private val todoItems = mutableListOf<TodoItem>()
    private var completedTodoItems = 0
    private val gson = Gson()

    fun getAmountCompletedTodoItems() = completedTodoItems

    fun getAllTodoItems() = todoItems

    fun updateTodoItems(items: List<TodoItem>) {
        todoItems.clear()
        todoItems.addAll(items)
    }

    fun toJson(): String {
        return gson.toJson(todoItems)
    }

    fun fromJson(json: String) {
        val type: Type = object : TypeToken<List<TodoItem>>() {}.type
        val todoItemsList: List<TodoItem> = gson.fromJson(json, type)

        updateTodoItems(todoItemsList)
        completedTodoItems = todoItemsList.count { it.isDone }
    }
}