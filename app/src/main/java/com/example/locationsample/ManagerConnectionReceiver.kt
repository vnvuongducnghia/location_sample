package com.example.locationsample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager

class ManagerConnectionReceiver : BroadcastReceiver() {

    companion object {
        var mListener: NetworkReceiverListener? = null
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null && ConnectivityManager.CONNECTIVITY_ACTION == intent.action) {
            mListener?.onNetworkConnectionChanged(isInternetAvailable(context))
        }

        if (intent != null && WifiManager.WIFI_STATE_CHANGED_ACTION == intent.action) {
            mListener?.onWifiStateExtraChanged(getWifiState(intent))
        }

        if (context != null && intent != null && LocationManager.PROVIDERS_CHANGED_ACTION == intent.action) {
            mListener?.onGPSStateChanged(getGPSState(context))
        }
    }

    fun setNetworkReceiverListener(listener: NetworkReceiverListener) {
        mListener = listener
    }

    //region Lisntener
    interface NetworkReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
        fun onWifiStateExtraChanged(isState: Boolean)
        fun onGPSStateChanged(isState: Boolean)
    }
    //endregion

}