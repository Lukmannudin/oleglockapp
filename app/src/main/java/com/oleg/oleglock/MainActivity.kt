package com.oleg.oleglock

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.oleg.oleglock.util.Util.toAppLocks
import com.oleg.oleglock.ui.LockAppList
import com.oleg.oleglock.ui.theme.OleglockTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oleg.oleglock.util.PermissionUtil
import com.oleg.oleglock.util.Util


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val lockWorkRequest: WorkRequest = OneTimeWorkRequestBuilder<LockWorker>().build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WorkManager.getInstance(this).cancelAllWork()

        checkUsageStatsPermission()

        val deviceApps = Util.getAllApps(this).toAppLocks(this).toMutableList()

        setContent {
            OleglockTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel = hiltViewModel<MainActivityViewModel>()
                    val state by viewModel.viewModelState.collectAsStateWithLifecycle()

                    LaunchedEffect(true) {
                        viewModel.mergeAppLocks(deviceApps)
                    }

                    LockAppList(list = state.appLocks)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        WorkManager.getInstance(this).enqueue(lockWorkRequest)
    }

    private fun checkUsageStatsPermission() {
        // Check and request permission if needed
        if (!PermissionUtil.checkUsageStatsPermission(this)) {
            Log.e(TAG, "Usage stats permission not granted. Requesting...")
            PermissionUtil.requestUsageStatePermission(this)
        }
    }
}

const val TAG = "MainActivity"