package com.erdince.yabancidilkelimehaznesi6.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.erdince.yabancidilkelimehaznesi6.activity.*
import com.erdince.yabancidilkelimehaznesi6.activity.MainActivity

fun Activity.createAndShowDialog(msg: String, clicked: (Boolean) -> Unit) {
    val alert: AlertDialog.Builder = AlertDialog.Builder(this)
    alert.setMessage(msg)
    alert.setCancelable(true)
    alert.setPositiveButton("Evet") { dialog, _ ->
        clicked(true)
        dialog.cancel()
    }
    alert.setNegativeButton("Hayır") { dialog, _ ->
        clicked(false)
        dialog.cancel()
    }


    alert.create().show()
}

fun Activity.openNetworkSettings(MobileOrWifi: String) {

    if (MobileOrWifi == "Wifi") {
        val networkIntent = Intent()
        networkIntent.action = Settings.ACTION_WIFI_SETTINGS
        startActivity(networkIntent)
    } else if (MobileOrWifi == "Mobile") {
        val networkIntent = Intent(Settings.ACTION_DATA_ROAMING_SETTINGS)
        startActivity(networkIntent)
    }
}

fun Activity.makeToast(msg: String) {
    Toast.makeText(
        this,
        msg,
        Toast.LENGTH_SHORT
    ).show()
}

fun Activity.restartActivity() {
    finish()
    startActivity(intent)
}

fun Activity.isOnline(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
    val netInfo = connectivityManager.activeNetworkInfo
    return (netInfo != null && netInfo.isAvailable
            && netInfo.isConnected)
}

