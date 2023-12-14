package com.oleg.oleglock.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [AppLock::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): AppLockDao
}