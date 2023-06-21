package com.example.myweather.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myweather.R
import com.example.myweather.view.fragments.MainFragment


class  MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().replace(R.id.fragmentHolder, MainFragment.newInstance()).commit()
    }
}