package com.oleg.oleglock

import android.app.Application
import androidx.room.Room
import com.oleg.oleglock.data.AppDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class OlegApp : Application() {
//    val db = Room.databaseBuilder(
//        this,
//        AppDatabase::class.java, "applock.db"
//    ).build()
}