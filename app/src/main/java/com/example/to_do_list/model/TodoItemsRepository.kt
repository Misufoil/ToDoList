package com.example.to_do_list.model

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

object  TodoItemsRepository {
    private val todoItems = mutableListOf<TodoItem>()
    private var completedTodoItems = 0
    private val gson = Gson()

    fun getPositionById(id: UUID): Int {
        return todoItems.indexOfFirst { it.id == id }
    }

    fun addOnPosition(toPosition: Int, todoItem: TodoItem) {
        todoItems.add(toPosition, todoItem)
        if (todoItem.isDone) {
            completedTodoItems++
        }
    }

    fun swap(fromPosition: Int, toPosition: Int) {
        val element = todoItems.removeAt(fromPosition) // Удаляем элемент из исходной позиции и сохраняем его
        todoItems.add(toPosition, element)
    }

    fun itIsDone(position: Int) {
        todoItems[position].isDone = true
        completedTodoItems++
    }
    fun getAmountCompletedTodoItems():Int {
        return completedTodoItems
    }

    fun getAllTodoItems(): MutableList<TodoItem> {
        return todoItems
    }

    fun deleteTodoItem(position: Int) {
        todoItems.removeAt(position)
        completedTodoItems--
    }

    fun getTodoItemsById(id: UUID): TodoItem? {
        return todoItems.find { it.id == id }
    }

    fun getTodoItemsByPosition(position: Int): TodoItem {
        return todoItems[position]
    }

    fun addTodoItem(todoItem: TodoItem) {
        todoItems.add(todoItem)
    }

    fun updateTodoItem(todoItem: TodoItem): Int {
        val index = todoItems.indexOfFirst { it.id == todoItem.id }
        if(index != -1) {
            todoItems[index] = todoItem
        }
        return index
    }

    fun idInTodoItems(id: UUID): Boolean {
        val index = todoItems.indexOfFirst { it.id == id }
        return index != - 1

    }

    fun toJson(): String {
        return gson.toJson(todoItems)
    }

    fun fromJson(json: String) {
        val type: Type = object : TypeToken<List<TodoItem>>() {}.type
        val todoItemsList: List<TodoItem> = gson.fromJson(json, type)

        todoItems.addAll(todoItemsList)
        for (todoItem in todoItemsList) {
            if (todoItem.isDone) {
                completedTodoItems++
            }
        }
    }

}