package app.mosaicos.setupwizard.view.activity

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
    private lateinit var secureDnsEnabled: SwitchItem

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

        val adapter = RecyclerItemAdapter(itemGroup)

        adapter.setOnItemSelectedListener { item ->
            if (item is SwitchItem) {
                item.toggle(findViewById(item.viewId))
            }
        }

        layout.adapter = adapter
    }

    override fun setupActions() {
        secureDnsEnabled.setOnCheckedChangeListener { _, isChecked ->
            SecurityFeaturesActions.setSecureDnsEnabled(isChecked)
        }
        primaryButton.setOnClickListener { SetupWizard.next(this) }
    }
}
