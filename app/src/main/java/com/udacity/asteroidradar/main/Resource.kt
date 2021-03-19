package com.udacity.asteroidradar.main

/**
 * Interface for a UI element bound by a generic resource provided by a Repository.
 */
interface Resource<T> {
    fun observeViewModel()

    fun bindViewModelData(data: T)

    fun loading()

    fun idle()

    fun empty()

    fun error()
}