package com.oleg.oleglock.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.oleg.oleglock.MainActivityViewModel
import com.oleg.oleglock.data.AppLock
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oleg.oleglock.LockScreenActivity


@Composable
fun LockAppList(
    list: List<AppLock>,
    viewModel: MainActivityViewModel = viewModel()
) {
    // TODO: use database
    val mutableList = list.toTypedArray()

    val launcher = rememberLauncherForActivityResult(
        contract = PickLockPattern(),
        onResult = { uri ->
            uri
        }
    )
    LazyColumn {
        itemsIndexed(mutableList) { index, item ->
            val context = LocalContext.current
            LockCheckbox(
                item.packageName, item.isLock, item.icon
            ) { isChecked ->
                mutableList[index].isLock = isChecked
                viewModel.setLocked(mutableList[index])
                if (isChecked) {
//                    val intent = Intent(context, LockScreenActivity::class.java).apply {
//                        putExtra(LockScreenActivity.PACKAGE_NAME, item.packageName)
//                        putExtra(LockScreenActivity.SHOULD_UPDATE_LOCK, true)
//                    }
//                    context.startActivity(intent)
                    launcher.launch(item.packageName)
                }

            }
        }
    }
}

class PickLockPattern : ActivityResultContract<String, Uri?>() {
    override fun createIntent(context: Context, input: String): Intent {
        return Intent(context, LockScreenActivity::class.java).apply {
            putExtra(LockScreenActivity.PACKAGE_NAME, input)
            putExtra(LockScreenActivity.SHOULD_UPDATE_LOCK, true)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        if (resultCode != Activity.RESULT_OK) {
            return null
        }
        return intent?.getParcelableExtra(LockScreenActivity.PATTERN_RESULT)
    }
}

@Composable
fun LockCheckbox(
    packageName: String,
    isChecked: Boolean,
    icon: Drawable? = null,
    onChecked: (Boolean) -> Unit
) {
    var checkedState by remember { mutableStateOf(isChecked) }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = checkedState, onCheckedChange = {
            checkedState = it
            onChecked.invoke(checkedState)
        })

        icon?.let {
            Image(
                bitmap = icon.toBitmap().asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(text = packageName.substringAfterLast("."))
    }
}

@Preview(showBackground = true, backgroundColor = 0xfff)
@Composable
fun HomePreview() {
    LockAppList(
        list = listOf(AppLock("test1", false), AppLock("test2", false))
    )
}