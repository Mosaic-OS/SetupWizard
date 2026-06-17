package app.mosaicos.setupwizard.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.mosaicos.setupwizard.action.SecurityActions

object SecurityData : ViewModel() {

    val isDeviceSecure = MutableLiveData<Boolean>()

    init {
        SecurityActions
    }
}
