package com.oleg.oleglock

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.oleg.oleglock.data.AppLock
import com.oleg.oleglock.data.AppLockDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask


@HiltWorker
class LockWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val dao: AppLockDao
) : Worker(appContext, workerParams) {

    private val cache = mutableListOf<AppLock>()

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_START_ACTIVITY) {
                val startActivityIntent = Intent(applicationContext, MainActivity::class.java)
                startActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                applicationContext.startActivity(startActivityIntent)
            }
        }
    }

    override fun doWork(): Result {
        CoroutineScope(Dispatchers.IO).launch {
            cache.addAll(dao.getAll())
        }

        applicationContext.registerReceiver(
            broadcastReceiver,
            IntentFilter(ACTION_START_ACTIVITY)
        )

        checkAllApps()

        val timer = Timer()

        val task = object : TimerTask() {
            override fun run() {
                if (isCurrentAppLock(appContext)) {
                    cancel()
                    timer.cancel()
                    val intent = Intent(ACTION_START_ACTIVITY)
                    appContext.sendBroadcast(intent)
                }
            }
        }

        timer.scheduleAtFixedRate(task, 0, 5000)
        return Result.success()
    }

    override fun onStopped() {
        super.onStopped()
        applicationContext.unregisterReceiver(broadcastReceiver)
    }

    fun isCurrentAppLock(context: Context): Boolean {
        val statsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val endTime = System.currentTimeMillis()
        val beginTime = endTime - 10000
        val event = UsageEvents.Event()
        val usageEvents: UsageEvents = statsManager.queryEvents(beginTime, endTime)
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED && cache.contains(
                    AppLock(
                        event.packageName,
                        true
                    )
                )
            ) {
                return true

            }
        }

        return false
    }

    private fun checkAllApps() {
        val pm = applicationContext.packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
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