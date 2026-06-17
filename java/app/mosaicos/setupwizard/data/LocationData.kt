package app.mosaicos.setupwizard.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.mosaicos.setupwizard.action.LocationActions

object LocationData : ViewModel() {
    val locationEnabled = MutableLiveData<Boolean>()
    val networkLocationEnabled = MutableLiveData<Boolean>()
    val wifiScanningAlwaysAvailableEnabled = MutableLiveData<Boolean>()
	val geocoderEnabled = MutableLiveData<Boolean>()

    init {
        LocationActions
    }
}
