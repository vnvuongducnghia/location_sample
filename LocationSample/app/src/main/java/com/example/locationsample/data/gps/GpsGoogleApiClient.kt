package com.example.locationsample.data.gps

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender.SendIntentException
import android.os.Bundle
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*


const val REQUEST_LOCATION = 199

class GpsGoogleApiClient(var context: Context) : GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    private var fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context as Activity)
    var mLocationRequest: LocationRequest? = null
    var mGoogleApiClient: GoogleApiClient? = null

    fun turnOnGPS() {
        mGoogleApiClient = GoogleApiClient.Builder(context)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this).build()
        mGoogleApiClient?.connect()
    }

    @SuppressLint("MissingPermission")
    fun getLastLocation() {
        fusedLocationClient.lastLocation
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    println("GpsGoogleApiClient.getLastLocation latitude ${task.result!!.latitude}")
                    println("GpsGoogleApiClient.getLastLocation longitude ${task.result!!.longitude}")
                } else {
                    println("GpsGoogleApiClient.getLastLocation getLastLocation:exception ${task.exception!!}")
                }
            }

    }

    override fun onConnected(p0: Bundle?) {
        mLocationRequest = LocationRequest.create()
        mLocationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest?.interval = 30 * 1000
        mLocationRequest?.fastestInterval = 5 * 1000

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        // mLocationRequest is a Object of LocationRequest

        val locationSettingsRequest = builder.build()
        val settingsClient = LocationServices.getSettingsClient(context)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        LocationServices.SettingsApi
            .checkLocationSettings(
                mGoogleApiClient, LocationSettingsRequest
                    .Builder()
                    .addLocationRequest(mLocationRequest!!)
                    .setAlwaysShow(true).build()
            )
            .setResultCallback { result ->
                val status: Status = result.status
                when (status.statusCode) {
                    LocationSettingsStatusCodes.SUCCESS -> {

                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        // Location không mở. Nhưng có thể sửa bằng cách hiện 1 dialog cho user.
                        // Show the dialog by calling startResolutionForResult().
                        // and check the result in onActivityResult().
                        try {
                            status.startResolutionForResult(
                                context as Activity,
                                REQUEST_LOCATION
                            )
                        } catch (e: SendIntentException) {
                            // Ignore the error.
                        }
                    }
                }
            }
    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }


}