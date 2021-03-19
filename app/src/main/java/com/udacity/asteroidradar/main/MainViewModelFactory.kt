package com.udacity.asteroidradar.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainViewModelFactory(private val applicationContext: Context?) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = applicationContext?.let {
        MainViewModel(
            it
        )
    } as T
}