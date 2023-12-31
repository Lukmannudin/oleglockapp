package com.oleg.oleglock.util

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Process
import android.provider.Settings
import com.oleg.oleglock.data.AppLock

object Util {
    fun getAllApps(context: Context): List<ApplicationInfo> {
        val pm = context.packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        return packages
    }

    fun List<ApplicationInfo>.toAppLocks(context: Context): List<AppLock> {
        return this
            .filterNot {
                it.flags == ApplicationInfo.FLAG_SYSTEM ||
                        it.packageName.contains("provider") ||
                        it.packageName.startsWith("com.android.theme.") ||
                        it.packageName.startsWith("com.android.providers.") ||
                        it.packageName.startsWith("com.android.bips") ||
                        it.packageName.startsWith("com.android.bluetooth") ||
                        it.sourceDir.startsWith("/system/app/BluetoothMidiService/") ||
                        it.sourceDir.startsWith("/system/priv-app/") ||
                        it.sourceDir.startsWith("/system/app/BasicDreams/") ||
                        it.packageName.startsWith("com.google.") && it.packageName.count { char -> char =='.' } > 3
            }
            .map {
                val icon = context.packageManager.getApplicationIcon(it)
                AppLock(packageName = it.packageName).apply {
                    this.icon = icon
                }
            }
            .sortedBy {
                it.packageName.substringAfterLast(".")
            }
    }

    fun Collection<AppLock>.findIndexLockApp(appLock: AppLock) : Int {
        this.forEachIndexed { index, app ->
            if (app.packageName == appLock.packageName) {
                return index
            }
        }
        return -1
    }
}