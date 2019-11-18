package com.example.locationsample

import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.os.Bundle
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes

const val REQUEST_LOCATION = 199

class GpsGoogleApiClient(var context: Context) : GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    var mLocationRequest: LocationRequest? = null
    var mGoogleApiClient: GoogleApiClient? = null


    fun turnOnGPS() {
        mGoogleApiClient = GoogleApiClient.Builder(context)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this).build()
        mGoogleApiClient?.connect()
    }

    override fun onConnected(p0: Bundle?) {
        mLocationRequest = LocationRequest.create()
        mLocationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest?.interval = 30 * 1000
        mLocationRequest?.fastestInterval = 5 * 1000

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
                            status.startResolutionForResult(context as Activity, REQUEST_LOCATION)
                        } catch (e: IntentSender.SendIntentException) {
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