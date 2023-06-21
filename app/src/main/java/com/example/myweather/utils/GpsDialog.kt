package com.example.myweather.utils

import android.app.AlertDialog
import android.content.Context


object GpsDialog  {

    fun startDialogSettings(context: Context, clicker: Clicker){
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()

        dialog.setTitle("Turn on GPS?")
        dialog.setMessage("GPS not found")
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { _,_ ->
            clicker.transferUserGpsSettings()
            dialog.dismiss()
        }

        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL"){
            _,_ -> dialog.dismiss()
        }
        dialog.show()
    }

    interface Clicker {
        fun transferUserGpsSettings()
    }
}