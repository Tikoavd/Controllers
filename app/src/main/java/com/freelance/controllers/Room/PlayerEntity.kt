package com.freelance.controllers.Room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Players")
data class PlayerEntity(
    @ColumnInfo(name = "instKey")
    val instKey: Int,
    @ColumnInfo(name = "host")
    var host: String = "192.168.0.102",
    @ColumnInfo(name = "port")
    var port: String = "5000",
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)