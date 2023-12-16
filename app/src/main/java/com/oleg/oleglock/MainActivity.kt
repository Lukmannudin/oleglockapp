package com.oleg.oleglock

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.oleg.oleglock.Util.findIndexLockApp
import com.oleg.oleglock.Util.toAppLocks
import com.oleg.oleglock.ui.LockAppList
import com.oleg.oleglock.ui.theme.OleglockTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.lifecycle.compose.collectAsStateWithLifecycle


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val lockWorkRequest: WorkRequest = OneTimeWorkRequestBuilder<LockWorker>().build()

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deviceApps = Util.getAllApps(this).toAppLocks(this).toMutableList()
//        viewModel.mergeAppLocks(deviceApps)

        setContent {
            OleglockTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel = hiltViewModel<MainActivityViewModel>()
                    val state by viewModel.viewModelState.collectAsStateWithLifecycle()

                    val coroutineScope = rememberCoroutineScope()

                    LaunchedEffect(true) {
                        viewModel.mergeAppLocks(deviceApps)
                    }

                    LockAppList(list = state.appLocks)
                }
            }
        }

        // Check and request permission if needed
        if (!checkUsageStatsPermission(this)) {
            Log.e(TAG, "Usage stats permission not granted. Requesting...")
            requestUsageStatsPermission(this)
        } else {
            WorkManager.getInstance(this).enqueue(lockWorkRequest)
        }
    }

    fun checkUsageStatsPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode = appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName
            )

            return mode == AppOpsManager.MODE_ALLOWED
        }
        return true
    }

    fun requestUsageStatsPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!", modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    OleglockTheme {
        Greeting("Android")
    }
}

const val TAG = "MainActivity"