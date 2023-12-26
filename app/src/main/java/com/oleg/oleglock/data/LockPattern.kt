package com.oleg.oleglock.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    foreignKeys = [ForeignKey(
        entity = AppLock::class,
        parentColumns = arrayOf("package_name"),
        childColumns = arrayOf("package_name"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class LockPattern(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "package_name")
    val packageName: String,

    @ColumnInfo(name = "pattern")
    val pattern: String
)