package com.jbvincey.mytodolist.database

import android.arch.persistence.room.TypeConverter
import java.util.Date

/**
 * Created by jbvincey on 16/09/2018.
 */
class MyTodoListTypeConverters {

    @TypeConverter
    fun toDate(date: Date): Long = date.time

    @TypeConverter
    fun fromDate(dateLong: Long) = Date(dateLong)

}