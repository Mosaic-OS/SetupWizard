package app.mosaicos.setupwizard.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.mosaicos.setupwizard.TIMER
import app.mosaicos.setupwizard.action.OemUnlockActions

object OemUnlockData : ViewModel() {
    val ackTimer = MutableLiveData(TIMER)

    init {
        OemUnlockActions
    }
}
