package app.mosaicos.setupwizard.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.mosaicos.setupwizard.TIMER


object BetaWarningData : ViewModel() {
    val ackTimer = MutableLiveData(TIMER)
}
