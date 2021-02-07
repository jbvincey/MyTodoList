package com.jbvincey.ui.recycler.cells.checkablecell

/**
 * Created by jbvincey on 15/10/2018.
 */
interface CheckableCellCallback<T> {

    fun onCheckChanged(data: T)

    fun onClick(data: T)

}