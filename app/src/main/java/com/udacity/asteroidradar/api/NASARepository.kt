package com.udacity.asteroidradar.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.squareup.moshi.Moshi
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.utils.PictureOfDay
import com.udacity.asteroidradar.db.Asteroid
import com.udacity.asteroidradar.db.AsteroidDao
import com.udacity.asteroidradar.db.PictureDayDao
import com.udacity.asteroidradar.utils.*
import kotlinx.coroutines.CoroutineScope
import org.json.JSONObject
import java.util.concurrent.TimeUnit


class NASARepository(
    private val webService: NASAApi,
    private val asteroidDao: AsteroidDao,
    private val pictureDayDao: PictureDayDao,
    private val viewModelScope: CoroutineScope
) {


    fun fetchAsteroids(): LiveData<Resource<List<Asteroid>>> {
        return object : NetworkResource<List<Asteroid>, String>(viewModelScope) {
            override suspend fun loadFromDisk(): LiveData<List<Asteroid>> {
                return MutableLiveData(asteroidDao.getAll())
            }

            override fun shouldFetch(diskResponse: List<Asteroid>?): Boolean {
                return diskResponse.isNullOrEmpty()
            }

            override suspend fun fetchData(): Response<String> {
                val call = webService.getNEOFeed(apiKey = BuildConfig.NASA_API_KEY)
                val response = call.safeExecute()

                if (!response.isSuccessful || response.body().isNullOrEmpty()) {
                    return Failure(400, "Invalid")
                }

                return Success(response.body() as String)
            }

            override fun processResponse(response: String): List<Asteroid> {
                val json = JSONObject(response)

                return parseAsteroidsJsonResult(
                    json
                )
            }

            override suspend fun saveToDisk(data: List<Asteroid>): Boolean {
                val ids = asteroidDao.updateData(data)
                return ids.isNotEmpty()
            }
        }.asLiveData()
    }

    fun fetchPictureOfTheDay(): LiveData<Resource<PictureOfDay>> {
        return object : NetworkResource<PictureOfDay, String>(viewModelScope) {
            override suspend fun loadFromDisk(): LiveData<PictureOfDay> {
                return MutableLiveData(pictureDayDao.get())
            }

            override fun shouldFetch(diskResponse: PictureOfDay?): Boolean {
                return diskResponse == null
                        || diskResponse.timestamp +
                        TimeUnit.MILLISECONDS
                            .convert(24L, TimeUnit.HOURS) < System.currentTimeMillis()
            }

            override suspend fun fetchData(): Response<String> {
                val call = webService.getPictureOfDay(apiKey = BuildConfig.NASA_API_KEY)
                val response = call.safeExecute()

                if (!response.isSuccessful || response.body().isNullOrEmpty()) {
                    return Failure(400, "Error")
                }

                return Success(response.body() as String)
            }

            override fun processResponse(response: String): PictureOfDay {
                val pic = Moshi.Builder()
                    .build()
                    .adapter(PictureOfDay::class.java)
                    .fromJson(response)
                    ?: PictureOfDay(
                        -1,
                        "image",
                        "",
                        ""
                    )
                pic.timestamp = System.currentTimeMillis()
                return pic
            }

            override suspend fun saveToDisk(data: PictureOfDay): Boolean {
                return pictureDayDao.updateData(data) > 0
            }
        }.asLiveData()
    }
}