package com.jbvincey.core.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "todolists")
data class TodoList(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    val name: String,
    val deleted: Boolean
    ) {
    @Ignore
    constructor(name: String) : this (
        id = 0,
        name = name,
        deleted = false
    )
}