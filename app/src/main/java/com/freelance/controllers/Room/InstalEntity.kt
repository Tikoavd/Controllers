package com.freelance.controllers.Room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Instalations")
data class InstalEntity (
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "type")
    val type: InstalType = InstalType.Default,
    @ColumnInfo(name = "video")
    var video: String = "Видео",
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)