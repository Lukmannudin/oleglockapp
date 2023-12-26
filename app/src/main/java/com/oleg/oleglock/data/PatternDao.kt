package com.oleg.oleglock.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface PatternDao {
    @Query("SELECT * from lockpattern")
    fun getAll(): LockPattern
}