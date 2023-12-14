package com.oleg.oleglock

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import com.oleg.oleglock.data.AppLock

object Util {
    fun getAllApps(context: Context): List<ApplicationInfo> {
        val pm = context.packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        return packages
    }

    fun List<ApplicationInfo>.toAppLocks(context: Context): List<AppLock> {
        return this
            .filter {
                it.sourceDir.startsWith("/data/app/") || it.sourceDir.startsWith("/system/product/app/")
            }
            .map {
                println("cekcek ${it.packageName} sourceDir=${it.sourceDir}")
                val icon = context.packageManager.getApplicationIcon(it)
                AppLock(packageName = it.packageName, icon = icon)
            }
    }
}