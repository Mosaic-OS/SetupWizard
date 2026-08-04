package app.mosaicos.setupwizard.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.mosaicos.setupwizard.action.SecurityFeaturesActions

object SecurityFeaturesData : ViewModel() {
    val secureDnsEnabled = MutableLiveData<Boolean>()

    init {
        SecurityFeaturesActions
    }
}
