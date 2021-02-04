package com.jbvincey.core.models

import androidx.room.Embedded
import androidx.room.Relation

data class TodoListWithTodos(
    @Embedded val todoList: TodoList,
    @Relation(
        parentColumn = "id",
        entityColumn = "todoListId"
    )
    val todos: List<Todo>
)