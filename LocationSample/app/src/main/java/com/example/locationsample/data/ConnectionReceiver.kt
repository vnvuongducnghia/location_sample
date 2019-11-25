package com.example.locationsample.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import androidx.core.location.LocationManagerCompat
import com.example.locationsample.data.networks.isEnableMobileNetwork
import com.example.locationsample.data.networks.isEnableWifi
import com.example.locationsample.data.networks.isInternetAvailable

class ConnectionReceiver : BroadcastReceiver() {

    companion object {
        var mListener: NetworkReceiverListener? = null
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        println("ConnectionReceiver.onReceive intent ${intent?.action}")
        if (context != null && intent != null && ConnectivityManager.CONNECTIVITY_ACTION == intent.action) {
            mListener?.onNetworkConnectionChanged(
                isInternetAvailable(context)
            )
        }

        if (intent != null && WifiManager.WIFI_STATE_CHANGED_ACTION == intent.action) {
            mListener?.onWifiStateExtraChanged(
                isEnableWifi(intent)
            )
        }

        if (context != null && intent != null && LocationManager.PROVIDERS_CHANGED_ACTION == intent.action) {
            mListener?.onGPSStateChanged(
                LocationManagerCompat.isLocationEnabled(
                    context.getSystemService(
                        Context.LOCATION_SERVICE
                    ) as LocationManager
                )
            )
        }

        if (context != null) {
            mListener?.onMobileNetworkStateChanged(isEnableMobileNetwork(context))
        }

    }

    fun setNetworkReceiverListener(listener: NetworkReceiverListener) {
        mListener = listener
    }

    //region Lisntener
    interface NetworkReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
        fun onMobileNetworkStateChanged(isState: Boolean)
        fun onWifiStateExtraChanged(isState: Boolean)
        fun onGPSStateChanged(isState: Boolean)

    }
    //endregion

}