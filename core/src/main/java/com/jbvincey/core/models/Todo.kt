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
        val deleted: Boolean
) {
    @Ignore
    constructor(name: String) : this(0, name, false, Date(), null, false, false)

}