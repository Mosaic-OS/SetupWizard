package app.mosaicos.setupwizard.action

import android.debug.AdbManager
import android.net.ConnectivitySettingsManager
import android.net.ConnectivitySettingsManager.PRIVATE_DNS_MODE_OPPORTUNISTIC
import android.net.ConnectivitySettingsManager.PRIVATE_DNS_MODE_PROVIDER_HOSTNAME
import android.util.Log
import app.mosaicos.setupwizard.appContext
import app.mosaicos.setupwizard.data.SecurityFeaturesData

object SecurityFeaturesActions {
    private const val TAG = "SecurityFeaturesActions"
    private const val SECURE_DNS_HOSTNAME = "dns1.mosaicos.io"

    init {
        refreshCurrentState()
    }

    fun setSecureDnsEnabled(enabled: Boolean) {
        Log.d(TAG, "setSecureDnsEnabled: $enabled")
        if (enabled) {
            // Set the hostname
            ConnectivitySettingsManager.setPrivateDnsHostname(appContext, SECURE_DNS_HOSTNAME)
            ConnectivitySettingsManager.setPrivateDnsMode(
                appContext, PRIVATE_DNS_MODE_PROVIDER_HOSTNAME)
        } else {
            // Back to the platform default
            ConnectivitySettingsManager.setPrivateDnsMode(
                appContext, PRIVATE_DNS_MODE_OPPORTUNISTIC)
        }
        refreshCurrentState()
    }

    fun lockAdbPermanently() {
        Log.d(TAG, "lockAdbPermanently")
        val adbManager = appContext.getSystemService(AdbManager::class.java)
        if (adbManager == null) {
            Log.e(TAG, "AdbManager unavailable; ADB was not disabled")
            return
        }
        runCatching { adbManager.lockAdbPermanently() }
            .onFailure { Log.e(TAG, "Failed to permanently disable ADB", it) }
        refreshCurrentState()
    }

    private fun refreshCurrentState() {
        SecurityFeaturesData.adbLocked.value =
            appContext.getSystemService(AdbManager::class.java)?.isAdbPermanentlyLocked() == true
        SecurityFeaturesData.secureDnsEnabled.value =
            ConnectivitySettingsManager.getPrivateDnsMode(appContext) ==
                PRIVATE_DNS_MODE_PROVIDER_HOSTNAME
        Log.d(
            TAG,
            "refreshCurrentState: secureDnsEnabled = ${SecurityFeaturesData.secureDnsEnabled.value}"
        )
    }
}
