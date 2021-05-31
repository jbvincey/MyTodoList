package com.jbvincey.core.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*

/**
 * Created by jbvincey on 16/09/2018.
 */
@Entity(tableName = "todos")
data class Todo(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    val name: String,
    val completed: Boolean,
    val creationDate: Date,
    val completionDate: Date?,
    val archived: Boolean,
    val deleted: Boolean,
    val todoListId: Long
) {
    @Ignore
    constructor(name: String, todoListId: Long) : this(
        id = 0,
        name = name,
        completed = false,
        creationDate = Date(),
        completionDate = null,
        archived = false,
        deleted = false,
        todoListId = todoListId
    )

}