package com.jbvincey.core.database

import androidx.room.TypeConverter
import java.util.Date

/**
 * Created by jbvincey on 16/09/2018.
 */
class MyTodoListTypeConverters {

    companion object {
        private const val DATE_LONG_NULL = -1L
    }

    @TypeConverter
    fun toDate(date: Date?): Long = date?.time ?: DATE_LONG_NULL

    @TypeConverter
    fun fromDate(dateLong: Long) = if (dateLong != DATE_LONG_NULL) Date(dateLong) else null

}