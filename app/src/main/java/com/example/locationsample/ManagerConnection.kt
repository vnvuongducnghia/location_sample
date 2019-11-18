package com.example.locationsample

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings.Secure
import android.provider.Settings.SettingNotFoundException
import android.text.TextUtils
import androidx.core.location.LocationManagerCompat


object PackageUtil {
    fun checkPermission(context: Context, accessFineLocation: String?): Boolean {
        val res = context.checkCallingOrSelfPermission(accessFineLocation!!)
        return res == PackageManager.PERMISSION_GRANTED
    }
}

//region GPS
fun getGPSState(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return LocationManagerCompat.isLocationEnabled(locationManager)
}

fun isGpsEnabled(context: Context): Boolean {
    if (PackageUtil.checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
        val providers = Secure.getString(context.contentResolver, Secure.LOCATION_PROVIDERS_ALLOWED)
        if (TextUtils.isEmpty(providers)) {
            false
        } else providers.contains(LocationManager.GPS_PROVIDER)
    } else {
        val locationMode: Int
        locationMode = try {
            Secure.getInt(context.contentResolver, Secure.LOCATION_MODE)
        } catch (e: SettingNotFoundException) {
            e.printStackTrace()
            return false
        }
        when (locationMode) {
            Secure.LOCATION_MODE_HIGH_ACCURACY, Secure.LOCATION_MODE_SENSORS_ONLY -> true
            Secure.LOCATION_MODE_BATTERY_SAVING, Secure.LOCATION_MODE_OFF -> false
            else -> false
        }
    }
}
//endregion

//region NETWORK
fun isInternetAvailable(context: Context): Boolean {
    var result = false
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?


    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // > android 6
        cm?.run {
            cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                result = when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            }
        }
    } else {
        cm?.run {
            cm.activeNetworkInfo?.run {
                if (type == ConnectivityManager.TYPE_WIFI) {
                    result = true
                } else if (type == ConnectivityManager.TYPE_MOBILE) {
                    result = true
                }
            }
        }
    }
    return result
}
//endregion

//region WIFI
fun getWifiState(intent: Intent): Boolean {
    val wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)

    if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
        return true
    }

    if (wifiState == WifiManager.WIFI_STATE_DISABLED) {
        return false
    }

    return false
}
/*fun printlnWifiState(intent: Intent) {
    val wifiState =
        intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)

    var wifiStateText = "No State"

    when (wifiState) {
        WifiManager.WIFI_STATE_DISABLING -> wifiStateText = "WIFI_STATE_DISABLING"
        WifiManager.WIFI_STATE_DISABLED -> wifiStateText = "WIFI_STATE_DISABLED"
        WifiManager.WIFI_STATE_ENABLING -> wifiStateText = "WIFI_STATE_ENABLING"
        WifiManager.WIFI_STATE_ENABLED -> wifiStateText = "WIFI_STATE_ENABLED"
        WifiManager.WIFI_STATE_UNKNOWN -> wifiStateText = "WIFI_STATE_UNKNOWN"
        else -> {
        }
    }
    println("wifiState =   $wifiStateText")
}*/
//endregion

