package com.example.to_do_list.model

enum class Importance(val text: String) {
    NO("Нет"),
    LOW("Низкий"),
    HIGH("Высокий!!")
}

fun String.toImportance(): Importance {
    return when (this) {
        "Нет" -> Importance.NO
        "Низкий" -> Importance.LOW
        "Высокий!!" -> Importance.HIGH
        else -> throw IllegalArgumentException("Invalid importance value: $this")
    }
}