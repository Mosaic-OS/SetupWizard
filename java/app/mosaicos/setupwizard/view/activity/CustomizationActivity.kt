package app.mosaicos.setupwizard.view.activity

import android.view.View
import android.widget.CheckBox
import android.widget.RadioGroup
import app.mosaicos.setupwizard.R
import app.mosaicos.setupwizard.action.CustomizationActions
import app.mosaicos.setupwizard.action.SetupWizard
import app.mosaicos.setupwizard.data.CustomizationData

class CustomizationActivity : SetupWizardActivity(
    R.layout.activity_customization,
    R.drawable.baseline_palette_glif,
    R.string.customization_title,
    R.string.customization_desc
) {
    private lateinit var themeGroup: RadioGroup
    private lateinit var blackTheme: CheckBox
    private lateinit var navigationGroup: RadioGroup
    private lateinit var hideNavigationPill: CheckBox

    private var applyingState = false

    override fun bindViews() {
        themeGroup = requireViewById(R.id.theme_radio_group)
        blackTheme = requireViewById(R.id.black_theme)
        navigationGroup = requireViewById(R.id.navigation_radio_group)
        hideNavigationPill = requireViewById(R.id.hide_navigation_pill)

        // The activity is recreated when night mode changes, so re-read the truth each time.
        CustomizationActions.syncFromSystem()

        CustomizationData.darkTheme.observe(this) { dark ->
            applyingState = true
            themeGroup.check(if (dark) R.id.radio_theme_dark else R.id.radio_theme_light)
            blackTheme.setVisible(dark && CustomizationActions.isBlackThemeAvailable)
            applyingState = false
        }
        CustomizationData.blackTheme.observe(this) { blackTheme.isChecked = it }
        CustomizationData.gesturalNavigation.observe(this) { gestural ->
            applyingState = true
            navigationGroup.check(
                if (gestural) R.id.radio_nav_gestural else R.id.radio_nav_buttons)
            hideNavigationPill.setVisible(gestural)
            applyingState = false
        }
        CustomizationData.hideNavigationPill.observe(this) {
            hideNavigationPill.isChecked = it
        }
    }

    /** Fades the dependent option in and out so the section does not jump. */
    private fun View.setVisible(visible: Boolean) {
        if (visible == (visibility == View.VISIBLE)) return
        animate().cancel()
        if (visible) {
            alpha = 0f
            visibility = View.VISIBLE
            animate().alpha(1f).setListener(null)
        } else {
            animate().alpha(0f).withEndAction { visibility = View.GONE }
        }
    }

    override fun setupActions() {
        themeGroup.setOnCheckedChangeListener { _, checkedId ->
            if (!applyingState) {
                CustomizationActions.setDarkTheme(checkedId == R.id.radio_theme_dark)
            }
        }
        blackTheme.setOnCheckedChangeListener { view, isChecked ->
            if (view.isPressed) CustomizationActions.setBlackTheme(isChecked)
        }
        navigationGroup.setOnCheckedChangeListener { _, checkedId ->
            if (!applyingState) {
                CustomizationActions.setGesturalNavigation(checkedId == R.id.radio_nav_gestural)
            }
        }
        hideNavigationPill.setOnCheckedChangeListener { view, isChecked ->
            if (view.isPressed) CustomizationActions.setNavigationPillHidden(isChecked)
        }
        primaryButton.setOnClickListener { SetupWizard.next(this) }
    }
}
