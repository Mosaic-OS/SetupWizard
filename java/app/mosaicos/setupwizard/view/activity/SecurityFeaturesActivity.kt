package app.mosaicos.setupwizard.view.activity

import android.app.AlertDialog
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.View
import app.mosaicos.setupwizard.R
import app.mosaicos.setupwizard.action.SecurityFeaturesActions
import app.mosaicos.setupwizard.action.SetupWizard
import app.mosaicos.setupwizard.data.SecurityFeaturesData
import com.google.android.setupdesign.GlifRecyclerLayout
import com.google.android.setupdesign.items.ItemGroup
import com.google.android.setupdesign.items.RecyclerItemAdapter
import com.google.android.setupdesign.items.SwitchItem

class SecurityFeaturesActivity : SetupWizardActivity(
    R.layout.activity_security_features,
    R.drawable.baseline_security_glif,
    R.string.security_features
) {
    private companion object {
        const val WARNING_ICON_SIZE_DP = 16f
        const val WARNING_FALLBACK_COLOR = 0xFFE53935.toInt()
    }

    private lateinit var secureDnsEnabled: SwitchItem
    private lateinit var adbPermanentlyDisabled: SwitchItem

    override fun bindViews() {
        val layout = requireViewById<GlifRecyclerLayout>(R.id.glif_layout)
        val itemGroup = ItemGroup()

        secureDnsEnabled = SwitchItem().apply {
            id = View.generateViewId()
            title = getString(R.string.secure_dns_enabled_title)
            summary = getString(R.string.secure_dns_enabled_desc)
        }
        itemGroup.addChild(secureDnsEnabled)
        SecurityFeaturesData.secureDnsEnabled.observe(this) { secureDnsEnabled.isChecked = it }

        adbPermanentlyDisabled = SwitchItem().apply {
            id = View.generateViewId()
            title = warningTitle(getString(R.string.adb_lock_title))
            summary = getString(R.string.adb_lock_desc)
        }
        itemGroup.addChild(adbPermanentlyDisabled)
        SecurityFeaturesData.adbLocked.observe(this) { adbPermanentlyDisabled.isChecked = it }

        val adapter = RecyclerItemAdapter(itemGroup)

        adapter.setOnItemSelectedListener { item ->
            if (item is SwitchItem) {
                item.toggle(findViewById(item.viewId))
            }
        }

        layout.adapter = adapter
    }

    private fun warningTitle(label: String): CharSequence {
        val warningColor = warningColor()
        val icon = getDrawable(R.drawable.baseline_warning_amber_orange)?.mutate()?.apply {
            val size = (WARNING_ICON_SIZE_DP * resources.displayMetrics.density).toInt()
            setBounds(0, 0, size, size)
            setTint(warningColor)
        }
        return SpannableStringBuilder().apply {
            icon?.let {
                append(" ")
                setSpan(ImageSpan(it, ImageSpan.ALIGN_CENTER), 0, 1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                append("  ")
            }
            val start = length
            append(label)
            setSpan(StyleSpan(Typeface.BOLD), start, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(ForegroundColorSpan(warningColor), start, length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    private fun warningColor(): Int {
        val value = TypedValue()
        if (!theme.resolveAttribute(android.R.attr.colorError, value, true)) {
            return WARNING_FALLBACK_COLOR
        }
        val color = if (value.resourceId != 0) getColor(value.resourceId) else value.data
        return if (color != 0) color else WARNING_FALLBACK_COLOR
    }

    override fun setupActions() {
        secureDnsEnabled.setOnCheckedChangeListener { _, isChecked ->
            SecurityFeaturesActions.setSecureDnsEnabled(isChecked)
        }
        adbPermanentlyDisabled.setOnCheckedChangeListener { item, isChecked ->
            when {
                // The lock cannot be lifted, so the switch never goes back down.
                SecurityFeaturesData.adbLocked.value == true -> item.isChecked = true
                isChecked -> confirmAdbLock(item)
            }
        }
        primaryButton.setOnClickListener { SetupWizard.next(this) }
    }

    private fun confirmAdbLock(item: SwitchItem) {
        AlertDialog.Builder(this)
            .setTitle(R.string.adb_lock_dialog_title)
            .setMessage(R.string.adb_lock_dialog_message)
            .setPositiveButton(R.string.adb_lock_dialog_confirm) { _, _ ->
                SecurityFeaturesActions.lockAdbPermanently()
            }
            .setNegativeButton(R.string.adb_lock_dialog_cancel) { _, _ -> item.isChecked = false }
            .setOnCancelListener { item.isChecked = false }
            .show()
    }
}
