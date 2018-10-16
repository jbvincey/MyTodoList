package com.jbvincey.ui.recycler.cells.checkablecell

import java.util.*

/**
 * Created by jbvincey on 28/09/2018.
 */
data class CheckableCellViewModel(
        val id: Long,
        val name: String,
        val completed: Boolean,
        val creationDate: Date,
        val completionDate: Date?,
        val archived: Boolean,
        val callback: CheckableCellCallback
)