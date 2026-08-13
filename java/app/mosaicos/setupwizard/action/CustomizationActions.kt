package app.mosaicos.setupwizard.action

import android.app.UiModeManager
import android.content.om.OverlayManager
import android.content.res.Configuration
import android.os.UserHandle
import android.provider.Settings
import android.util.Log
import android.view.WindowManagerPolicyConstants.NAV_BAR_MODE_3BUTTON_OVERLAY
import android.view.WindowManagerPolicyConstants.NAV_BAR_MODE_GESTURAL_OVERLAY
import app.mosaicos.setupwizard.appContext
import app.mosaicos.setupwizard.data.CustomizationData

object CustomizationActions {
    private const val TAG = "CustomizationActions"
    private const val BLACK_THEME_OVERLAY = "com.mosaicos.blacktheme"
    private const val THEME_AMOLED_BLACK = "theme_amoled_black"
    private const val SHOW_NAVIGATION_PILL = "show_navigation_pill"
    private const val NAV_MODE_GESTURAL = 2

    init {
        syncFromSystem()
    }

    /** The black theme is an overlay, so hide the option entirely when it is not installed. */
    val isBlackThemeAvailable: Boolean by lazy {
        runCatching { appContext.packageManager.getPackageInfo(BLACK_THEME_OVERLAY, 0) }.isSuccess
    }

    /**
     * Night mode and the navigation overlay both land asynchronously, so the state is advanced
     * optimistically and only re-read from the system when the change was rejected.
     */
    fun setDarkTheme(dark: Boolean) {
        Log.d(TAG, "setDarkTheme: $dark")
        val applied =
            appContext.getSystemService(UiModeManager::class.java)?.setNightModeActivated(dark)
        if (applied != true) {
            Log.e(TAG, "Night mode change rejected")
            syncFromSystem()
            return
        }
        CustomizationData.darkTheme.value = dark
        // Pure black only makes sense on top of dark mode.
        if (!dark) setBlackTheme(false)
    }

    fun setBlackTheme(enabled: Boolean) {
        Log.d(TAG, "setBlackTheme: $enabled")
        Settings.Secure.putInt(
            appContext.contentResolver, THEME_AMOLED_BLACK, if (enabled) 1 else 0)
        CustomizationData.blackTheme.value = enabled
    }

    fun setGesturalNavigation(gestural: Boolean) {
        Log.d(TAG, "setGesturalNavigation: $gestural")
        val overlay =
            if (gestural) NAV_BAR_MODE_GESTURAL_OVERLAY else NAV_BAR_MODE_3BUTTON_OVERLAY
        val overlayManager = appContext.getSystemService(OverlayManager::class.java)
        if (overlayManager == null) {
            Log.e(TAG, "OverlayManager unavailable; navigation mode unchanged")
            syncFromSystem()
            return
        }
        val applied = runCatching {
            overlayManager.setEnabledExclusiveInCategory(
                overlay, UserHandle.of(UserHandle.myUserId()))
        }.onFailure { Log.e(TAG, "Failed to apply the navigation overlay $overlay", it) }.isSuccess
        if (applied) CustomizationData.gesturalNavigation.value = gestural else syncFromSystem()
    }

    fun setNavigationPillHidden(hidden: Boolean) {
        Log.d(TAG, "setNavigationPillHidden: $hidden")
        Settings.Secure.putInt(
            appContext.contentResolver, SHOW_NAVIGATION_PILL, if (hidden) 0 else 1)
        CustomizationData.hideNavigationPill.value = hidden
    }

    fun syncFromSystem() {
        CustomizationData.darkTheme.value =
            (appContext.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                Configuration.UI_MODE_NIGHT_YES
        CustomizationData.blackTheme.value =
            Settings.Secure.getInt(appContext.contentResolver, THEME_AMOLED_BLACK, 0) != 0
        CustomizationData.gesturalNavigation.value = isGesturalNavigation()
        CustomizationData.hideNavigationPill.value =
            Settings.Secure.getInt(appContext.contentResolver, SHOW_NAVIGATION_PILL, 1) == 0
    }

    fun isGesturalNavigation(): Boolean =
        runCatching {
            Settings.Secure.getInt(
                appContext.contentResolver, Settings.Secure.NAVIGATION_MODE, NAV_MODE_GESTURAL)
        }.getOrDefault(NAV_MODE_GESTURAL) == NAV_MODE_GESTURAL
}
