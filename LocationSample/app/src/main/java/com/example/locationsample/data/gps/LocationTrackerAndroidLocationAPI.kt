package com.example.locationsample.data.gps

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.widget.Toast
import androidx.core.location.LocationManagerCompat
import java.util.*
import kotlin.math.roundToInt


@SuppressLint("Registered")
//declaring Context variable
class LocationTrackerAndroidLocationAPI(private val context: Context) : Service(),
    LocationListener {

    //flag for gps
    var isGPSOn = false
    //flag for network location
    var isNetWorkEnabled = false
    //flag to getlocation
    var isLocationEnabled = false
    //location
    var location: Location? = null
    var locationLast: Location? = null
    //latitude and longitude
    private var latitude = 0.0
    private var longitude = 0.0
    //Declaring a LocationManager
    var locationManager: LocationManager? = null

    companion object {
        // minimum distance to request for location update
        private const val MIN_DISTANCE_TO_REQUEST_LOCATION: Long = 3 // in meters
        // minimum time to request location updates
        private const val MIN_TIME_FOR_UPDATES: Long = 3000 // 1 sec
    }

    init {
        checkIfLocationAvailable()
    }

    fun checkIfLocationAvailableNew(): Boolean {
        isLocationEnabled =
            LocationManagerCompat.isLocationEnabled(context.getSystemService(Context.LOCATION_SERVICE) as LocationManager)
        return isLocationEnabled
    }

    @SuppressLint("MissingPermission")
    fun checkIfLocationAvailable(): Location? {
        try {
            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            //check for gps availability
            isGPSOn = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            //check for network availablity
            isNetWorkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGPSOn && !isNetWorkEnabled) {
                isLocationEnabled = false
                // no location provider is available show toast to user
                Toast
                    .makeText(context, "No Location Provider is Available", Toast.LENGTH_SHORT)
                    .show()
            } else {
                isLocationEnabled = true
                // if network location is available request location update
                if (isNetWorkEnabled) {
                    locationManager!!.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_FOR_UPDATES, MIN_DISTANCE_TO_REQUEST_LOCATION.toFloat(),
                        this
                    )
                    if (locationManager != null) {
                        location =
                            locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (location != null) {
                            latitude = location!!.latitude
                            longitude = location!!.longitude
                        }
                    }
                }
                if (isGPSOn) {
                    locationManager!!.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_FOR_UPDATES,
                        MIN_DISTANCE_TO_REQUEST_LOCATION.toFloat(),
                        this
                    )
                    if (locationManager != null) {
                        location =
                            locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        if (location != null) {
                            latitude = location!!.latitude
                            longitude = location!!.longitude
                        }
                    }
                }

                println("MainActivity.onCreate location ${location?.latitude} ${location?.longitude}")
                locationLast = if (locationLast == null) location
                else {
                    // Check new location
                    if (locationLast != null && location != null) {
                        val distance = distanceBetweenLocation(locationLast!!, location!!)
                        println("MainActivity.onCreate distance $distance")
                        if (distance > MIN_DISTANCE_TO_REQUEST_LOCATION) {
                            println("MainActivity.onCreate distance good")
                        } else {
                            println("MainActivity.onCreate distance bad")
                        }
                    } else {
                        println("MainActivity.onCreate locationLast null location null")
                    }
                    location
                }
            }
        } catch (e: Exception) {
        }
        return location
    }

    // call this to stop using location
    fun stopUsingLocation() {
        if (locationManager != null) {
            locationManager!!.removeUpdates(this@LocationTrackerAndroidLocationAPI)
        }
    }

    // call this to getLatitude
    fun getLatitude(): Double {
        if (location != null) {
            latitude = location!!.latitude
        }
        println("LocationTrackerAndroidLocationAPI.getLastLocation latitude $latitude")
        return latitude
    }

    //call this to getLongitude
    fun getLongitude(): Double {
        if (location != null) {
            longitude = location!!.longitude
        }
        println("LocationTrackerAndroidLocationAPI.getLastLocation longitude $longitude")
        return longitude
    }

    //call to open settings and ask to enable Location
    fun askToOnLocation() {
        val dialog = AlertDialog.Builder(context)
        //set title
        dialog.setTitle("Settings")
        //set Message
        dialog.setMessage("Location is not Enabled.Do you want to go to settings to enable it?")
        // on pressing this will be called
        dialog.setPositiveButton("Settings") { dialog, which ->
            val intent =
                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            context.startActivity(intent)
        }
        //on Pressing cancel
        dialog.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
        // show Dialog box
        dialog.show()
    }

    override fun onLocationChanged(location: Location) {
        this.location = location
    }

    override fun onProviderDisabled(provider: String) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    override fun onBind(intent: Intent): IBinder? {
        return null
    }


    fun getCompleteAddressString(LATITUDE: Double, LONGITUDE: Double): String? {
        var strAdd = ""
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1)
            if (addresses != null) {
                val returnedAddress = addresses[0]
                val strReturnedAddress = StringBuilder("")
                for (i in 0 until returnedAddress.maxAddressLineIndex) {
                    strReturnedAddress
                        .append(returnedAddress.getAddressLine(i)).append("\n")
                }
                strAdd = strReturnedAddress.toString()
                println("LocationTrackerAndroidLocationAPI.getCompleteAddressString address $strReturnedAddress")
            } else {
                println("LocationTrackerAndroidLocationAPI.getCompleteAddressString address No Address returned!")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("LocationTrackerAndroidLocationAPI.getCompleteAddressString address Cannot get Address!")
        }
        return strAdd
    }

    // distance in meters between 2 location
    fun distanceBetweenLocation(locationA: Location, locationB: Location): Int {
        return locationA.distanceTo(locationB).roundToInt()
    }


}

