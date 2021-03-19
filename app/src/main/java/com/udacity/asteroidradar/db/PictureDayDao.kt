package com.udacity.asteroidradar.db

import androidx.room.*
import com.udacity.asteroidradar.utils.PictureOfDay

@Dao
interface PictureDayDao {
    @Query("SELECT * FROM pictureofday")
    fun get(): PictureOfDay

    @Transaction
    fun updateData(pic: PictureOfDay): Long {
        deleteAll()
        return insert(pic)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(pic: PictureOfDay): Long

    @Query("DELETE FROM pictureofday")
    fun deleteAll()
}