package app.mosaicos.setupwizard.view.activity

import android.content.Intent
import android.content.res.Configuration
import android.database.ContentObserver
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.MainThread
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updatePadding
import app.mosaicos.setupwizard.R
import com.google.android.setupcompat.template.FooterBarMixin
import com.google.android.setupcompat.template.FooterButton
import com.google.android.setupdesign.GlifLayout
import com.google.android.setupdesign.R as SudR
import com.google.android.setupdesign.util.ThemeHelper

/**
 * This is the base activity for all setup wizard activities.
 */
abstract class SetupWizardActivity(
    @param:LayoutRes protected val layoutResID: Int? = null,
    @param:DrawableRes protected val icon: Int? = null,
    @param:StringRes protected val header: Int? = null,
    @param:StringRes protected val description: Int? = null,
) : AppCompatActivity() {

    private companion object {
        const val THEME_AMOLED_BLACK = "theme_amoled_black"
    }

    private var isResumedPage = false

    private val isBlackTheme: Boolean
        get() = Settings.Secure.getInt(contentResolver, THEME_AMOLED_BLACK, 0) == 1 &&
            (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                Configuration.UI_MODE_NIGHT_YES

    private val blackThemeObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) {
            if (isResumedPage && !isFinishing && !isChangingConfigurations) recreate()
        }
    }

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    private lateinit var footerBarMixin: FooterBarMixin
    protected val primaryButton: FooterButton by lazy {
        val button = FooterButton.Builder(this)
            .setButtonType(FooterButton.ButtonType.NEXT)
            .setTheme(SudR.style.SudGlifButton_Primary)
            .setText(R.string.next)
            .build()
        footerBarMixin.primaryButton = button
        button
    }
    protected val secondaryButton: FooterButton by lazy {
        val button = FooterButton.Builder(this)
            .setButtonType(FooterButton.ButtonType.SKIP)
            .setTheme(SudR.style.SudGlifButton_Secondary)
            .setText(R.string.skip)
            .build()
        footerBarMixin.secondaryButton = button
        button
    }

    protected fun superOnCreateAtBaseClass(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        superOnCreateAtBaseClass(savedInstanceState)
        activityResultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
            onActivityResult(result.resultCode, result.data)
        }
        // The in-flow glif theme hardcodes its colours, so only the full dynamic colour theme
        // routes the window background through the system palette the black overlay rewrites.
        setTheme(SudR.style.SudFullDynamicColorTheme_DayNight)
        ThemeHelper.trySetDynamicColor(this)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        if (layoutResID == null) return
        // setup view
        setContentView(layoutResID)
        window.decorView.setOnApplyWindowInsetsListener { view, insets ->
            view.updatePadding(
                top = insets.getInsets(WindowInsets.Type.systemBars()).top,
                bottom = insets.getInsets(WindowInsets.Type.navigationBars()).bottom
            )
            insets
        }
        initBaseView()
        bindViews()
        setupActions()
    }

    private fun initBaseView() {
        val glifLayout = findViewById<GlifLayout>(R.id.glif_layout) ?: return
        footerBarMixin = glifLayout.getMixin(FooterBarMixin::class.java)
        if (isBlackTheme) applyBlackSurfaces(glifLayout)
        if (icon != null) glifLayout.icon = getDrawable(icon)
        if (header != null) glifLayout.setHeaderText(header)
        if (description != null) glifLayout.setDescriptionText(description)
    }

    private fun applyBlackSurfaces(glifLayout: GlifLayout) {
        glifLayout.rootView.setBackgroundColor(Color.BLACK)
        glifLayout.findViewById<View>(SudR.id.suc_intrinsic_size_layout)
            ?.setBackgroundColor(Color.BLACK)
        window.setBackgroundDrawable(ColorDrawable(Color.BLACK))
    }

    @MainThread
    abstract fun bindViews()

    @MainThread
    abstract fun setupActions()

    override fun onResume() {
        super.onResume()
        isResumedPage = true
        contentResolver.registerContentObserver(
            Settings.Secure.getUriFor(THEME_AMOLED_BLACK), false, blackThemeObserver)
    }

    override fun onPause() {
        isResumedPage = false
        contentResolver.unregisterContentObserver(blackThemeObserver)
        super.onPause()
    }

    protected open fun onActivityResult(resultCode: Int, data: Intent?) {}

    fun startActivityForResult(intent: Intent) {
        activityResultLauncher.launch(intent)
    }
}
