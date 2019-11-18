package com.example.locationsample

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), ManagerConnectionReceiver.NetworkReceiverListener {


    private var wifiManager: WifiManager? = null
    private var managerConnectionReceiver: ManagerConnectionReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        managerConnectionReceiver = ManagerConnectionReceiver()
        managerConnectionReceiver!!.setNetworkReceiverListener(this)

        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiSwitch.setOnCheckedChangeListener(wifiSwitchListener)

        btnLocationSetting.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }

        btnPlayServicesLocation.setOnClickListener {
            GpsGoogleApiClient(this).turnOnGPS() // play-services-location:17.0.0
        }


    }


    private var isGPS = false

    private fun turnGPSOn() {
        val provider = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.LOCATION_PROVIDERS_ALLOWED
        )
        if (!provider.contains("gps")) { //if gps is disabled
            val poke = Intent()
            poke.setClassName(
                "com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider"
            )
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE)
            poke.data = Uri.parse("3")
            sendBroadcast(poke)
        }
    }

    private fun turnGPSOff() {
        val provider = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.LOCATION_PROVIDERS_ALLOWED
        )
        if (provider.contains("gps")) { //if gps is enabled
            val poke = Intent()
            poke.setClassName(
                "com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider"
            )
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE)
            poke.data = Uri.parse("3")
            sendBroadcast(poke)
        }
    }

    private fun canToggleGPS(): Boolean {
        val pacman = packageManager
        var pacInfo: PackageInfo?
        pacInfo = try {
            pacman.getPackageInfo("com.android.settings", PackageManager.GET_RECEIVERS)
        } catch (e: PackageManager.NameNotFoundException) {
            return false //package not found
        }
        if (pacInfo != null) {
            for (actInfo in pacInfo.receivers) { //test if recevier is exported. if so, we can toggle GPS.
                if (actInfo.name == "com.android.settings.widget.SettingsAppWidgetProvider" && actInfo.exported) {
                    return true
                }
            }
        }
        return false //default
    }

    override fun onStart() {
        super.onStart()
        registerManagerConnectionReceiver()
        gpsSwitch.isChecked = getGPSState(this)
    }

    override fun onResume() {
        super.onResume()
        gpsSwitch.isChecked = isGpsEnabled(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterManagerConnectionReceiver()
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        println("TestLog MainActivity.onNetworkConnectionChanged isConnected ${isConnected}")

        if (isConnected) {
            txtNetworkConnection.setTextColor(Color.GREEN)
            txtNetworkConnection.text = "Network is connected"
        } else {
            txtNetworkConnection.setTextColor(Color.GRAY)
            txtNetworkConnection.text = "Network is disconnected"
        }
    }

    override fun onWifiStateExtraChanged(isState: Boolean) {
        if (isState) {
            wifiSwitch.isChecked = true
            wifiSwitch.text = "Wifi is ON"
        } else {
            wifiSwitch.isChecked = false
            wifiSwitch.text = "Wifi is OFF"
        }
    }

    override fun onGPSStateChanged(isState: Boolean) {
        if (isState) {
            gpsSwitch.isChecked = true
            gpsSwitch.text = "Location is ON"
        } else {
            gpsSwitch.isChecked = false
            gpsSwitch.text = "Location is OFF"
        }
    }

    //region Listener
    private val wifiSwitchListener =
        CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            when (isChecked) {
                true -> {
                    wifiManager!!.isWifiEnabled = isChecked
                    wifiSwitch.text = "Wifi is ON"
                }
                false -> {
                    wifiManager!!.isWifiEnabled = isChecked
                    wifiSwitch.text = "Wifi is OFF"
                }
            }
        }


    //endregion

    private fun registerManagerConnectionReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        intentFilter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION)
        registerReceiver(managerConnectionReceiver, intentFilter)
    }

    private fun unregisterManagerConnectionReceiver() {
        unregisterReceiver(managerConnectionReceiver)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("onActivityResult()", resultCode.toString())

        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        when (requestCode) {
            REQUEST_LOCATION -> when (resultCode) {
                Activity.RESULT_OK -> {
                    // All required changes were successfully made
                    Toast
                        .makeText(this, "Location enabled by user!", Toast.LENGTH_LONG)
                        .show()
                }
                Activity.RESULT_CANCELED -> {
                    // The user was asked to change settings, but chose not to
                    Toast
                        .makeText(this, "Location not enabled, user cancelled.", Toast.LENGTH_LONG)
                        .show()
                }
                else -> {
                }
            }
        }
    }


}
