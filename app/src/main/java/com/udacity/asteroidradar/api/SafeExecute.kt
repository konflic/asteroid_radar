package com.udacity.asteroidradar.api

import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.IOException

fun <T> Call<T>.safeExecute(): Response<T> {
    return try {
        this.execute()
    } catch (e: IOException) {
        Response.error<T>(
            400,
            ResponseBody.create(
                MediaType.parse("text/plain"),
                "Broken connection"
            )
        )
    }
}