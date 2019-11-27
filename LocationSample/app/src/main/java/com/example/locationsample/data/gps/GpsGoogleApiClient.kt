package com.example.locationsample.data.gps

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*


const val REQUEST_LOCATION = 199

class GpsGoogleApiClient(var context: Context) : GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    var mLocationRequest: LocationRequest? = null
    var mGoogleApiClient: GoogleApiClient? = null

    //flag to getlocation
    var isLocationEnabled = false
    //location
    var location: Location? = null
    var locationLast: Location? = null
    //latitude and longitude
    private var latitude = 0.0
    private var longitude = 0.0

    fun initGAC() {
        //Instantiating the GoogleApiClient
        mGoogleApiClient = GoogleApiClient.Builder(context)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build()

        //Instantiating the LocationRequest
        mLocationRequest = LocationRequest.create()
        mLocationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest?.interval = 30 * 1000
        mLocationRequest?.fastestInterval = 5 * 1000

        //Instantiating the FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    fun onConnect() {
        mGoogleApiClient?.connect()
    }

    fun onDisconnect() {
        mGoogleApiClient?.disconnect()
    }

    // One request location
    @SuppressLint("MissingPermission")
    fun getLastLocation() {
        if (isLocationEnabled)
            mFusedLocationClient?.lastLocation?.addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    println("GpsGoogleApiClient.getLastLocation latitude ${task.result!!.latitude}")
                    println("GpsGoogleApiClient.getLastLocation longitude ${task.result!!.longitude}")
                } else {
                    println("GpsGoogleApiClient.getLastLocation getLastLocation:exception ${task.exception!!}")
                }
            }

    }

    // Auto loop request location
    fun getLocationUpdates() {
        if (isLocationEnabled)
            mFusedLocationClient?.requestLocationUpdates(
                mLocationRequest, object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        println("GACActivity.requestLocationUpdates size ${locationResult.locations.size}")
                        for (location in locationResult.locations) { //Do what you want with location
                            println("GACActivity.requestLocationUpdates ${location.latitude} ${location.longitude} ")
                        }
                    }
                }, null
            )
    }

    override fun onConnected(p0: Bundle?) {
        isLocationEnabled = false
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(this.mLocationRequest!!)
        val task =
            LocationServices.getSettingsClient(context).checkLocationSettings(builder.build())
        task.addOnCompleteListener {
            try {
                task.getResult(ApiException::class.java)
                // All location settings are satisfied. The client can initialize location requests here.
                if (ContextCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    isLocationEnabled = true
                } else {
                    println("GACActivity.LocUpdate permission error. ")
                }
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                        // Location settings are not satisfied. But could be fixed by showing the user a dialog.
                        try {
                            // Cast to a resolvable exception.
                            val resolvable = exception as ResolvableApiException
                            // Show the dialog by calling startResolutionForResult() and check the result in onActivityResult().
                            resolvable.startResolutionForResult(
                                context as Activity,
                                REQUEST_LOCATION
                            )
                        } catch (e: SendIntentException) {
                            // Ignore the error.
                            println("GACActivity.LocUpdate Ignore the error. ${e.message}")
                        } catch (e: ClassCastException) {
                            // Ignore, should be an impossible error.
                            println("GACActivity.LocUpdate Ignore, should be an impossible error. ${e.message}")
                        }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                }
            }
        }
    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        isLocationEnabled = false
    }


}