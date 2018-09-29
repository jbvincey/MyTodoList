package com.jbvincey.ui.recycler.cells.todo

import java.util.*

/**
 * Created by jbvincey on 28/09/2018.
 */
data class TodoViewModel(
        val id: Long,
        val name: String,
        val completed: Boolean,
        val creationDate: Date,
        val completionDate: Date?,
        val archived: Boolean
)