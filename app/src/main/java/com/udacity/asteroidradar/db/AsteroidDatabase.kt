package com.udacity.asteroidradar.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Asteroid::class], version = 3)
abstract class AsteroidDatabase : RoomDatabase() {
    abstract fun asteroidDao(): AsteroidDao

    companion object {
        private var instance: AsteroidDatabase? = null

        fun getInstance(context: Context): AsteroidDatabase {
            if (instance == null) {
                instance = Room
                    .databaseBuilder(
                        context.applicationContext,
                        AsteroidDatabase::class.java,
                        "asteroid-db"
                    )
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance!!
        }
    }
}