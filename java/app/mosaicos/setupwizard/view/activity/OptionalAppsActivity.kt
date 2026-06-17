package app.mosaicos.setupwizard.view.activity

import android.content.Context
import android.os.Bundle
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.AttrRes
import android.graphics.Color
import app.mosaicos.setupwizard.R
import app.mosaicos.setupwizard.action.OptionalAppsActions
import app.mosaicos.setupwizard.action.SetupWizard
import app.mosaicos.setupwizard.data.OptionalAppsData
import app.mosaicos.setupwizard.data.OptionalApp

class OptionalAppsActivity : SetupWizardActivity(
    R.layout.activity_optional_apps,
    R.drawable.baseline_optional_apps_glif,
    R.string.optional_apps_title,
    R.string.optional_apps_desc
) {
    private lateinit var appsContainer: LinearLayout

    override fun bindViews() {
        appsContainer = findViewById(R.id.apps_container)
    }

    override fun setupActions() {
        secondaryButton.apply {
            setText(this@OptionalAppsActivity, R.string.next)
            isEnabled = true
            setOnClickListener {
                SetupWizard.next(this@OptionalAppsActivity)
            }
        }

        primaryButton.apply {
            setText(this@OptionalAppsActivity, R.string.install_optional_apps)
            isEnabled = false
            setOnClickListener {
                val toInstall = OptionalAppsData.apps.value
                    ?.filter { it.isSelected && !it.isInstalled }
                    .orEmpty()

                OptionalAppsActions.installSelectedApps(
                    this@OptionalAppsActivity,
                    toInstall
                )

                OptionalAppsData.apps.value = OptionalAppsData.apps.value
                    ?.map { app ->
                        if (toInstall.any { it.packageName == app.packageName }) {
                            app.copy(isInstalled = true, isSelected = false)
                        } else app
                    }

                SetupWizard.next(this@OptionalAppsActivity)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (OptionalAppsData.apps.value == null) {
            OptionalAppsData.apps.value = listOf(
                OptionalApp(
                    name             = "HeliBoard",
                    packageName      = "helium314.keyboard",
                    assetFileName    = "HeliBoard.apk",
                    descriptionResId = R.string.heli_board_description
                ),
                OptionalApp(
                    name             = "Etar",
                    packageName      = "ws.xsoh.etar",
                    assetFileName    = "Etar.apk",
                    descriptionResId = R.string.etar_description
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()
        refreshUi()
    }

    private fun refreshUi() {
        val apps = OptionalAppsData.apps.value.orEmpty()
        appsContainer.removeAllViews()

        apps.forEach { app ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(
                    0, 0, 0,
                    resources.getDimensionPixelSize(R.dimen.list_item_spacing)
                )
            }

            val cb = CheckBox(this).apply {
                setTextAppearance(R.style.TextAppearance_MaterialComponents_Body1)
                text = app.name
                isChecked = app.isInstalled || app.isSelected
                isEnabled = !app.isInstalled
                if (!app.isInstalled) {
                    setOnCheckedChangeListener { _, checked ->
                        app.isSelected = checked
                        primaryButton.isEnabled =
                            apps.any { it.isSelected && !it.isInstalled }
                    }
                }
            }
            row.addView(cb)


            val desc = TextView(this).apply {
				text = getString(app.descriptionResId)
				setTextAppearance(R.style.TextAppearance_MaterialComponents_Caption)
				setTextColor(obtainStyledColor(context, android.R.attr.textColorSecondary))
				setPadding(
					cb.compoundPaddingLeft,
					resources.getDimensionPixelSize(R.dimen.description_top_margin),
					0, 0
				)
			}
            row.addView(desc)

            appsContainer.addView(row)
        }

        primaryButton.isEnabled = apps.any { it.isSelected && !it.isInstalled }
    }

    private fun obtainStyledColor(context: Context, @AttrRes attr: Int): Int {
		val ta = context.theme.obtainStyledAttributes(intArrayOf(attr))
		return try {
			ta.getColor(0, Color.GRAY)
		} finally {
			ta.recycle()
		}
	}
}
