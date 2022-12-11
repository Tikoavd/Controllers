package com.freelance.controllers.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [InstalEntity::class, PlayerEntity::class, HomeEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun instalDao(): InstalDao
    abstract fun playerDao(): PlayerDao
    abstract fun homeDao(): HomeDao

    companion object {
        fun createDatabase(context: Context): AppDatabase = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
        "InstalDB")
            .allowMainThreadQueries()
            .build()
    }
}