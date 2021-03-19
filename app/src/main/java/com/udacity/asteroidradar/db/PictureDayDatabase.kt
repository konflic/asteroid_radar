package com.udacity.asteroidradar.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.udacity.asteroidradar.utils.PictureOfDay

@Database(entities = [PictureOfDay::class], version = 4)
abstract class PictureDayDatabase : RoomDatabase()  {
    abstract fun pictureDayDatabase(): PictureDayDao
}