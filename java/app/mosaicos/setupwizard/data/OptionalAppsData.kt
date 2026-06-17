package app.mosaicos.setupwizard.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class OptionalApp(
    val name: String,
    val packageName: String,
    val assetFileName: String,
    val descriptionResId: Int,
    var isSelected: Boolean = false,
    var isInstalled: Boolean = false
)



object OptionalAppsData : ViewModel() {
    val apps = MutableLiveData<List<OptionalApp>>()
}
