package app.mosaicos.setupwizard.action

import app.mosaicos.setupwizard.TIMER
import app.mosaicos.setupwizard.appContext
import app.mosaicos.setupwizard.data.OemUnlockData
import app.mosaicos.setupwizard.view.activity.OemUnlockActivity
import app.mosaicos.setupwizard.view.activity.WelcomeActivity

object OemUnlockActions {
    private const val TAG = "OemUnlockActions"

    private val timerRunnable = object : Runnable {
        override fun run() {
            val current = OemUnlockData.ackTimer.value ?: return
            if (current <= 0) return
            OemUnlockData.ackTimer.value = current - 1
            if (current - 1 > 0) {
                appContext.mainThreadHandler.postDelayed(this, 1_000)
            }
        }
    }

    fun startAckTimer() {
		if (OemUnlockData.ackTimer.value == 0) return
		appContext.mainThreadHandler.removeCallbacks(timerRunnable)
		OemUnlockData.ackTimer.value = TIMER
		appContext.mainThreadHandler.postDelayed(timerRunnable, 1_000)
	}

    fun rebootToBootloader() {
        WelcomeActions.rebootToBootloader()
    }

    fun next(activity: OemUnlockActivity) {
        SetupWizard.next(activity, WelcomeActivity::class.java)
    }
}
