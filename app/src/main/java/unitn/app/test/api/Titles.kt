package com.example.NonCiRestaCheGuardare.api

data class Titles(
    val iso_3166_1: String,
    val title: String,
    val type: String,
)

data class TitlesResult(val id: Int, val titles: List<Titles>)
