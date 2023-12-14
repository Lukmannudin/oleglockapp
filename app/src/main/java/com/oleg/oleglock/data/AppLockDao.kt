package com.oleg.oleglock.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update

@Dao
interface AppLockDao {

    @Query("SELECT * FROM applock")
    fun getAll(): List<AppLock>

    @Update
    fun update(lock: AppLock)
}