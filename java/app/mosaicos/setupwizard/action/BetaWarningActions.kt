package app.mosaicos.setupwizard.action

import app.mosaicos.setupwizard.TIMER
import app.mosaicos.setupwizard.appContext
import app.mosaicos.setupwizard.data.BetaWarningData
import app.mosaicos.setupwizard.view.activity.BetaWarningActivity


object BetaWarningActions {
    private val timerRunnable = object : Runnable {
        override fun run() {
            val current = BetaWarningData.ackTimer.value ?: return
            if (current <= 0) return
            BetaWarningData.ackTimer.value = current - 1
            if (current - 1 > 0) {
                appContext.mainThreadHandler.postDelayed(this, 1_000)
            }
        }
    }

    fun startAckTimer() {
		if (BetaWarningData.ackTimer.value == 0) return
		appContext.mainThreadHandler.removeCallbacks(timerRunnable)
		BetaWarningData.ackTimer.value = TIMER
		appContext.mainThreadHandler.postDelayed(timerRunnable, 1_000)
	}

    fun next(activity: BetaWarningActivity) {
        SetupWizard.next(activity)
    }
}
