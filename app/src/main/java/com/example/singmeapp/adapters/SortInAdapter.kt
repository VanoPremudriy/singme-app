package com.example.singmeapp.adapters

interface SortInAdapter {
    fun sortByDefault()
    fun sortByName()
    fun sortByDate()
    fun sortByBand()
    fun sortByAlbum()
    fun sortBySearch(search: String)
}