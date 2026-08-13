package app.mosaicos.setupwizard.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.mosaicos.setupwizard.action.CustomizationActions

object CustomizationData : ViewModel() {
    val darkTheme = MutableLiveData<Boolean>()
    val blackTheme = MutableLiveData<Boolean>()
    val gesturalNavigation = MutableLiveData<Boolean>()
    val hideNavigationPill = MutableLiveData<Boolean>()

    init {
        CustomizationActions
    }
}
