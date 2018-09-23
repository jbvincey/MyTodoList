package com.jbvincey.mytodolist.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
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
        val completionDate: Date,
        val archived: Boolean
)