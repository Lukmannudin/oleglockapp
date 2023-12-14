package com.oleg.oleglock

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.Timer
import java.util.TimerTask


class LockWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_START_ACTIVITY) {
                // Start your activity here
                val startActivityIntent = Intent(applicationContext, MainActivity::class.java)
                startActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                applicationContext.startActivity(startActivityIntent)
            }
        }
    }
    override fun doWork(): Result {
        applicationContext.registerReceiver(broadcastReceiver,
            IntentFilter(ACTION_START_ACTIVITY))

        checkAllApps()

        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                getCurrentAppLaunch(applicationContext)
            }
        }, 0, 5000) //
        return Result.success()
    }

    override fun onStopped() {
        super.onStopped()
        applicationContext.unregisterReceiver(broadcastReceiver)
    }

    fun getCurrentAppLaunch(context: Context) {
        val statsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val endTime = System.currentTimeMillis()
        val beginTime = endTime - 10000
        var result = ""
        val event = UsageEvents.Event()
        val usageEvents: UsageEvents = statsManager.queryEvents(beginTime, endTime)
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED && event.packageName.equals("com.google.android.apps.maps")) {
                println("cekcek HORAY")
                val intent = Intent(ACTION_START_ACTIVITY)
                context.sendBroadcast(intent)
            }
        }
    }

    private fun checkAllApps() {
        val pm = applicationContext.packageManager
        //get a list of installed apps.
        //get a list of installed apps.
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
//        com.google.android.apps.photos
        for (packageInfo in packages) {
            Log.d(TAG, "Installed package :" + packageInfo.packageName)
            Log.d(TAG, "Source dir : " + packageInfo.sourceDir)
            Log.d(TAG, "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName))
        }
    }

    companion object {
        const val ACTION_START_ACTIVITY = "com.example.ACTION_START_ACTIVITY"
    }
}