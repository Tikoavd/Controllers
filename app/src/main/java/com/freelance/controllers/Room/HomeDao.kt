package com.freelance.controllers.Room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface HomeDao {
    @Insert
    fun addUnit(homeEntity: HomeEntity)

    @Query("SELECT * FROM homeplayers WHERE type=:type")
    fun getPlayers(type: InstalType = InstalType.Default) : MutableList<HomeEntity>

    @Query("SELECT * FROM homeplayers WHERE type=:type")
    fun getSockets(type: InstalType = InstalType.Socket) : MutableList<HomeEntity>

    @Query("SELECT * FROM homeplayers WHERE type=:type")
    fun getProjectors(type: InstalType = InstalType.Projector) : MutableList<HomeEntity>

    @Update
    fun updateUnit(homeEntity: HomeEntity)

    @Delete
    fun deleteUnit(homeEntity: HomeEntity)
}