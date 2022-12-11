package com.freelance.controllers.Room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "homeplayers")
data class HomeEntity(
    @ColumnInfo(name = "type")
    val type: InstalType = InstalType.Default,
    @ColumnInfo(name = "host")
    var host: String = "192.168.0.102",
    @ColumnInfo(name = "port")
    var port: String = "5000",
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)