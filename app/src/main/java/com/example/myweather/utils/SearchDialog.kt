package com.example.myweather.utils

import android.app.AlertDialog
import android.content.Context
import android.widget.EditText

object SearchDialog {

    fun searchCityDialog(context: Context, listener: Listener){
        val builder = AlertDialog.Builder(context)
        val edNameCity = EditText(context)
        builder.setView(edNameCity)
        val dialog = builder.create()
        dialog.setTitle("City Name Weather:")
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { _, _ ->
            listener.searchCity(edNameCity.text.toString())
            dialog.dismiss()
        }

        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL"){
                _,_ -> dialog.dismiss()
        }

        dialog.show()
    }

    interface Listener {
    fun searchCity(cityName: String?)
    }
}