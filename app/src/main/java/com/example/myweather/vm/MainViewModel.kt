package com.example.myweather.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myweather.models.Weather

class MainViewModel : ViewModel() {
    val currentLiveDataForHeadItem = MutableLiveData<Weather>()
    val forecastLiveDataForListsItems = MutableLiveData<List<Weather>>()
}