package com.example.locationsample

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.locationsample.data.ConnectionReceiver
import com.example.locationsample.data.gps.GpsGoogleApiClient
import com.example.locationsample.data.gps.LocationTrackerAndroidLocationAPI
import com.example.locationsample.data.gps.REQUEST_LOCATION
import com.example.locationsample.data.networks.isEnableMobileNetwork
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), ConnectionReceiver.NetworkReceiverListener {

    private var wifiManager: WifiManager? = null
    private var managerConnectionReceiver: ConnectionReceiver? = null
    private var trackerALA: LocationTrackerAndroidLocationAPI? = null
    private var trackerGAC: GpsGoogleApiClient? = null
    private var locationLast: Location? = null

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //create LocationTracker Object
        trackerALA = LocationTrackerAndroidLocationAPI(this)
        trackerGAC = GpsGoogleApiClient(this)

        managerConnectionReceiver = ConnectionReceiver()
        managerConnectionReceiver!!.setNetworkReceiverListener(this)

        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiSwitch.setOnCheckedChangeListener(wifiSwitchListener)

        mobileNetworkSwitch.isChecked = isEnableMobileNetwork(this)!!


        btnLocationSetting.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }

        btnPlayServicesLocation.setOnClickListener {
            trackerGAC!!.turnOnGPS() // play-services-location:17.0.0
        }

        btnGetLocation.setOnClickListener {
            // check if location is available
            trackerALA!!.checkIfLocationAvailable()
            if (trackerALA!!.isLocationEnabled) {
                val latitude = trackerALA!!.getLatitude()
                val longitude = trackerALA!!.getLongitude()
                // trackerALA!!.getCompleteAddressString(latitude, longitude)
            } else { // show dialog box to user to enable location
                trackerALA!!.askToOnLocation()
            }
            trackerGAC!!.getLastLocation()
        }

        moreSetting.setOnClickListener {
            /* val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
             val networkInfo = cm.activeNetworkInfo
             val type = networkInfo.type
             val typeName = networkInfo.typeName
             val connected = networkInfo.isConnected

             println("MainActivity.onCreate type $type")
             println("MainActivity.onCreate typeName $typeName")
             println("MainActivity.onCreate connected $connected")*/

        }

        var locationClient = object : LocationListener {
            override fun onLocationChanged(location: Location?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onProviderEnabled(provider: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onProviderDisabled(provider: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }

    }


    override fun onStart() {
        super.onStart()
        registerManagerConnectionReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterManagerConnectionReceiver()
    }

    override fun onMobileNetworkStateChanged(isState: Boolean) {
        mobileNetworkSwitch.isChecked = isState
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

    @SuppressLint("InlinedApi")
    private fun registerManagerConnectionReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        intentFilter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION)
        intentFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        intentFilter.addAction(TelephonyManager.ACTION_NETWORK_COUNTRY_CHANGED)
        intentFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
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
