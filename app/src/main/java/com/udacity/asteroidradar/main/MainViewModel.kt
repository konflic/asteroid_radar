package com.udacity.asteroidradar.main

import android.content.Context
import androidx.lifecycle.*
import androidx.room.Room
import com.udacity.asteroidradar.db.Asteroid
import com.udacity.asteroidradar.api.Constants
import com.udacity.asteroidradar.utils.PictureOfDay
import com.udacity.asteroidradar.api.NASARepository
import com.udacity.asteroidradar.api.NASAApi
import com.udacity.asteroidradar.db.AsteroidDatabase
import com.udacity.asteroidradar.db.PictureDayDatabase
import com.udacity.asteroidradar.utils.Resource
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class MainViewModel(applicationContext: Context) : ViewModel() {
    private val repository: NASARepository =
        NASARepository(
            Retrofit.Builder().baseUrl(Constants.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
                .create(NASAApi::class.java),
            AsteroidDatabase.getInstance(applicationContext)
                .asteroidDao(),
            Room.databaseBuilder(
                applicationContext,
                PictureDayDatabase::class.java,
                "pic-day-db"
            )
                .fallbackToDestructiveMigration()
                .build()
                .pictureDayDatabase(),
            viewModelScope
        )

    val asteroidFeed: MediatorLiveData<Resource<List<Asteroid>>> = MediatorLiveData()
    val selectedAsteroid = MutableLiveData<Asteroid>()

    val pictureOfDay: LiveData<Resource<PictureOfDay>> = repository.fetchPictureOfTheDay()

    fun select(item: Asteroid) {
        selectedAsteroid.value = item
    }

    fun getFeed() {
        val response = repository.fetchAsteroids()

        asteroidFeed.addSource(response) { newData ->
            if (asteroidFeed.value != newData) {
                asteroidFeed.value = newData
            }
        }
    }
}