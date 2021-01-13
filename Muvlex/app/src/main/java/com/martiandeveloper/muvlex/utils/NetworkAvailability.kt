package com.martiandeveloper.muvlex.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkRequest
import android.os.Build

class NetworkAvailability(val context: Context) : NetworkCallback() {

    fun registerNetworkCallback() {

        try {

            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager.registerDefaultNetworkCallback(this)
            } else {
                val builder = NetworkRequest.Builder()
                connectivityManager.registerNetworkCallback(builder.build(), this)
            }

        } catch (e: Exception) {
            isNetworkAvailable = false
        }

    }

    override fun onAvailable(network: Network) {
        isNetworkAvailable = true
    }

    override fun onLost(network: Network) {
        isNetworkAvailable = false
    }

}
