package model

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

object  TodoItemsRepository {
    private val todoItems = mutableListOf<TodoItem>()
    private var completedTodoItems = 0
    private val gson = Gson()

    fun getAllTodoItems(): List<TodoItem> {
        return todoItems
    }

    fun addAllTodoItems(items: List<TodoItem>) {
        todoItems.clear()
        todoItems.addAll(items)
    }

    fun getSize(): Int {
        return todoItems.size
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

    fun removeTodoItemById(id: UUID) {
        val itemToRemove = todoItems.find { it.id == id }
        todoItems.remove(itemToRemove)
    }

    fun updateTodoItem(todoItem: TodoItem) {
        val index = todoItems.indexOfFirst { it.id == todoItem.id }
        if(index != -1) {
            todoItems[index] = todoItem
        }
    }

    fun idInTodoItems(id: UUID): Boolean {
        val index = todoItems.indexOfFirst { it.id == id }
        return index != - 1

    }

    fun toJson(): String {
        return gson.toJson(todoItems)
    }

    fun fromJson(json: String): List<TodoItem>? {
        val type: Type = object : TypeToken<List<TodoItem>>() {}.type
        return gson.fromJson(json, type)
    }

}