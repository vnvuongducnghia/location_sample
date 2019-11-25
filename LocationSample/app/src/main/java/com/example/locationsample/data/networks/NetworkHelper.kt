package com.example.locationsample.data.networks

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.TelephonyManager
import java.lang.reflect.Method


fun isEnableMobileNetwork(context: Context): Boolean {
    var result = false
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    cm?.run {
        val c = Class.forName(cm.javaClass.name)
        val m: Method = c.getDeclaredMethod("getMobileDataEnabled")
        m.isAccessible = true
        result = m.invoke(cm) as Boolean
    }
    return result
}

fun isOnline(context: Context): Boolean {
    val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo
    return networkInfo?.isConnected == true
}

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

fun mobileNetwork(context: Context): Boolean {
    val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    var isMobileConn = false
    connMgr.allNetworks.forEach { network ->
        connMgr.getNetworkInfo(network).apply {
            if (type == ConnectivityManager.TYPE_MOBILE) {
                isMobileConn = isMobileConn or isConnected
            }
        }
    }
    return isMobileConn
}

fun setMobileDataEnabled(context: Context, enabled: Boolean) {
    val conman = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val conmanClass = Class.forName(conman.javaClass.name)
    val iConnectivityManagerField = conmanClass.getDeclaredField("mService")
    iConnectivityManagerField.isAccessible = true
    val iConnectivityManager: Any = iConnectivityManagerField.get(conman)
    val iConnectivityManagerClass = Class.forName(iConnectivityManager.javaClass.name)
    val setMobileDataEnabledMethod =
        iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", java.lang.Boolean.TYPE)
    setMobileDataEnabledMethod.isAccessible = true
    setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled)
}

fun getMobileDataState(context: Context): String? {
    val mTelephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    return when (mTelephonyManager.networkType) {
        TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN -> "2G"
        TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP -> "3G"
        TelephonyManager.NETWORK_TYPE_LTE -> "4G"
        else -> "Unknown"
    }
}

fun isEnableWifi(intent: Intent): Boolean {
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