package com.udacity.asteroidradar.utils

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import kotlinx.coroutines.*

abstract class NetworkResource<T, K>(private val viewModelScope: CoroutineScope) {

    private val result = MediatorLiveData<Resource<T>>()

    init {
        launch()
    }

    @WorkerThread
    abstract suspend fun loadFromDisk(): LiveData<T>

    @MainThread
    abstract fun shouldFetch(diskResponse: T?): Boolean

    @WorkerThread
    abstract suspend fun fetchData(): Response<K>

    @MainThread
    abstract fun processResponse(response: K): T

    @WorkerThread
    abstract suspend fun saveToDisk(data: T): Boolean

    private fun launch() {
        viewModelScope.launch {
            val diskSource = withContext(Dispatchers.IO) { loadFromDisk() }

            if (shouldFetch(diskSource.value)) {

                result.addSource(diskSource) { newData ->
                    setValue(Resource.loading(newData))
                }

                result.removeSource(diskSource)

                val fetchTask = async(Dispatchers.IO) { fetchData() }
                when (val response = fetchTask.await()) {
                    is Success -> {
                        val str = response.data.toString()
                        withContext(Dispatchers.IO) {
                            saveToDisk(processResponse(response.data))
                        }
                        val diskResponse = withContext(Dispatchers.IO) { loadFromDisk() }
                        result.addSource(diskResponse) { newData ->
                            setValue(Resource.success(newData))
                        }
                    }
                    is Failure -> {
                        result.addSource(diskSource) { newData ->
                            setValue(Resource.error(response.message, newData))
                        }
                    }
                }
            } else {
                result.addSource(diskSource) { data ->
                    setValue(Resource.success(data))
                }
            }
        }
    }

    @MainThread
    private fun setValue(newValue: Resource<T>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    fun asLiveData(): LiveData<Resource<T>> = result
}