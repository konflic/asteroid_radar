package com.udacity.asteroidradar.utils

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.api.Constants
import com.udacity.asteroidradar.api.NASAApi
import com.udacity.asteroidradar.api.safeExecute
import com.udacity.asteroidradar.db.AsteroidDatabase
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class DownloadWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val webService = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(NASAApi::class.java)

        val asteroidDao = AsteroidDatabase
            .getInstance(applicationContext)
            .asteroidDao()

        val call = webService.getNEOFeed(apiKey = BuildConfig.NASA_API_KEY)
        val response = call.safeExecute()

        if (!response.isSuccessful || response.body().isNullOrEmpty()) {
            return Result.failure()
        }

        val asteroids = parseAsteroidsJsonResult(
            JSONObject(response.body() ?: "")
        )

        try {
            val rows = asteroidDao.updateData(asteroids)
            if (rows.isEmpty()) {
                return Result.failure()
            }
        } catch (e: SQLiteConstraintException) {
            return Result.failure()
        }

        return Result.success()
    }
}