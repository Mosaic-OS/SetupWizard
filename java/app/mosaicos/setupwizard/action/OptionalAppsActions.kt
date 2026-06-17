package app.mosaicos.setupwizard.action

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageInstaller
import android.content.pm.PackageInstaller.SessionParams
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import app.mosaicos.setupwizard.data.OptionalApp
import app.mosaicos.setupwizard.view.activity.OptionalAppsActivity
import java.io.File
import java.io.FileOutputStream

object OptionalAppsActions {
    private const val TAG = "OptionalAppsActions"

    /**
     * Action part - Silent install in background
     * Install apks in toInstall list
     * with EXPLICIT mutable PendingIntent
     */
    fun installSelectedApps(activity: Activity, toInstall: List<OptionalApp>) {
        if (toInstall.isEmpty()) return
        val installer = activity.packageManager.packageInstaller

        toInstall.forEach { app ->
            try {
                val outFile = File(activity.getExternalFilesDir(null), app.assetFileName)
                if (!outFile.exists()) {
                    activity.assets.open(app.assetFileName).use { src ->
                        FileOutputStream(outFile).use { dst ->
                            src.copyTo(dst)
                        }
                    }
                }

                val apkUri: Uri = FileProvider.getUriForFile(
                    activity,
                    "${activity.packageName}.fileprovider",
                    outFile
                )

                val params = SessionParams(SessionParams.MODE_FULL_INSTALL).apply {
                    setRequireUserAction(SessionParams.USER_ACTION_NOT_REQUIRED)
                    appPackageName = app.packageName
                }

                val sessionId = installer.createSession(params)
                installer.openSession(sessionId).use { session ->
                    activity.contentResolver.openInputStream(apkUri)?.use { input ->
                        session.openWrite("base.apk", 0, -1).use { out ->
                            input.copyTo(out)
                            session.fsync(out)
                        }
                    }

                    val resultIntent = Intent(activity, OptionalAppsActivity::class.java).apply {
                        action = Intent.ACTION_VIEW
                    }

                    val pi = PendingIntent.getBroadcast(
                        activity,
                        sessionId,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                    )

                    session.commit(pi.intentSender)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error installing ${app.packageName}", e)
            }
        }
    }
}
