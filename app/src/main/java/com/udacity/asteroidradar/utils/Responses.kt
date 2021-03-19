package com.udacity.asteroidradar.utils

sealed class Response<out T>

class Success<out T>(val data: T) : Response<T>()

class Failure(val code: Int, val message: String) : Response<Nothing>()