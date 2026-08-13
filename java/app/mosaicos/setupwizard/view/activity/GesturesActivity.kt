package app.mosaicos.setupwizard.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import app.mosaicos.setupwizard.R
import app.mosaicos.setupwizard.action.CustomizationActions
import app.mosaicos.setupwizard.action.GesturesActions
import app.mosaicos.setupwizard.action.SetupWizard

class GesturesActivity : SetupWizardActivity(
    R.layout.activity_gestures,
    R.drawable.baseline_gesture_glif,
    R.string.swipe_gestures_title,
    R.string.swipe_gestures_desc
) {
    companion object {
        private const val TAG = "GesturesActivity"
    }

    /** Nothing to teach on 3-button navigation, so skip without inflating or joining the stack. */
    override fun onCreate(savedInstanceState: Bundle?) {
        if (!CustomizationActions.isGesturalNavigation()) {
            Log.d(TAG, "Gesture navigation is off, skipping the tutorial")
            superOnCreateAtBaseClass(savedInstanceState)
            SetupWizard.next(this)
            finish()
            return
        }
        super.onCreate(savedInstanceState)
    }

    override fun bindViews() {
        primaryButton.setText(this, R.string.try_it)
    }

    override fun setupActions() {
        secondaryButton.setOnClickListener { SetupWizard.next(this) }
        primaryButton.setOnClickListener { GesturesActions.launchTutorial(this) }
    }

    override fun onActivityResult(resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult: $resultCode, data=$data")
        GesturesActions.handleResult(this, resultCode)
        super.onActivityResult(resultCode, data)
    }
}
