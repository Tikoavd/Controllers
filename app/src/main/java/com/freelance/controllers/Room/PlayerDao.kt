package com.freelance.controllers.Room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PlayerDao {
    @Query("SELECT * FROM Players")
    fun getAllPlayers(): List<PlayerEntity>

    @Query("SELECT * FROM Players WHERE instKey=:instId")
    fun getPlayers(instId: Int): MutableList<PlayerEntity>

    @Query("DELETE FROM Players WHERE instKey=:instId")
    fun deletePlayers(instId: Int)

    @Insert
    fun addPlayer(playerEntity: PlayerEntity)

    @Update
    fun updatePlayer(playerEntity: PlayerEntity)

    @Delete
    fun deletePlayer(playerEntity: PlayerEntity)
}