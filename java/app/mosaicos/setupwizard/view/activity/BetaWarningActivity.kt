package app.mosaicos.setupwizard.view.activity

import android.os.Bundle
import android.view.View
import androidx.annotation.MainThread
import app.mosaicos.setupwizard.R
import app.mosaicos.setupwizard.action.BetaWarningActions
import app.mosaicos.setupwizard.data.BetaWarningData

class BetaWarningActivity : SetupWizardActivity(
    R.layout.activity_beta_warning,
    R.drawable.baseline_warning_amber_orange_glif,
    R.string.beta_warning_title,
    null
) {
    @MainThread
    override fun bindViews() {
        BetaWarningData.ackTimer.observe(this) { timer ->
            updateContinueButton(timer)
        }
        BetaWarningActions.startAckTimer()
        primaryButton.visibility = View.GONE
    }

    override fun setupActions() {
        secondaryButton.apply {
            setText(this@BetaWarningActivity, R.string.beta_warning_continue)
            isEnabled = false
            setOnClickListener {
                BetaWarningActions.next(this@BetaWarningActivity)
            }
        }
        primaryButton.isEnabled = false
    }

    private fun updateContinueButton(timer: Int) {
        val text = if (timer == 0) {
            getString(R.string.beta_warning_continue)
        } else {
            getString(R.string.beta_warning_continue_timer, timer)
        }
        secondaryButton.text = text
        secondaryButton.isEnabled = (timer == 0)
    }
}
