package com.example.myweather.models

data class Weather(
    val nameCity: String,
    val date: String,
    val condition: String,
    val imageCondition: String,
    val tempCurrent: String,
    val tempMax: String,
    val tempMin: String,
    val hoursCurrentDay: String
    )

