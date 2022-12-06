package com.freelance.controllers.Room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface InstalDao{
    @Insert
    fun insertInstal(instalEntity: InstalEntity)

    @Query("SELECT * FROM Instalations")
    fun getAll(): List<InstalEntity>

    @Delete
    fun delete(instalEntity: InstalEntity)

    @Update
    fun updateInstal(instalEntity: InstalEntity)
}