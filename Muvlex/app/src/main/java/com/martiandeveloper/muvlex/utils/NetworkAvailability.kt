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

            with(connectivityManager) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    registerDefaultNetworkCallback(this@NetworkAvailability)
                else
                    registerNetworkCallback(
                        NetworkRequest.Builder().build(),
                        this@NetworkAvailability
                    )
            }

        } catch (e: Exception) {
            networkAvailable = false
        }

    }

    override fun onAvailable(network: Network) {
        networkAvailable = true
    }

    override fun onLost(network: Network) {
        networkAvailable = false
    }

}
