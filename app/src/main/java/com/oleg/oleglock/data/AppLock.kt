package com.oleg.oleglock.data

import android.graphics.drawable.Drawable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class AppLock(
    @PrimaryKey
    @ColumnInfo(name = "package_name")
    val packageName: String,

    @ColumnInfo(name = "is_lock")
    var isLock: Boolean = false,

    @ColumnInfo(name = "pattern")
    var pattern: ArrayList<Int>? = null
) {
    @Ignore
    var icon: Drawable? = null
}