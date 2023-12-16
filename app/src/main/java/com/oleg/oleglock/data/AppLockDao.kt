package com.oleg.oleglock.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface AppLockDao {

    @Query("SELECT * FROM applock")
    suspend fun getAll(): List<AppLock>

    @Update
    suspend fun update(lock: AppLock)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(appLock: AppLock)
}