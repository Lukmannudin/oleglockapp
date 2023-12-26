package com.oleg.oleglock

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.itsxtt.patternlock.PatternLockView
import java.util.ArrayList
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LockScreenActivity : ComponentActivity() {

    val viewModel: LockScreenViewModel by viewModels()

    lateinit var patternLockView: PatternLockView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock_screen)

        val packageName = intent.getStringExtra(PACKAGE_NAME)
        viewModel.init(packageName!!)

        val shouldUpdateLock = intent.getBooleanExtra(SHOULD_UPDATE_LOCK, false)
        viewModel.shouldUpdateLock(shouldUpdateLock)

        patternLockView = findViewById(R.id.patternLockView)

        patternLockView.setOnPatternListener(object : PatternLockView.OnPatternListener {
            override fun onComplete(ids: ArrayList<Int>): Boolean {
                return if (shouldUpdateLock) {
                    viewModel.updateLock(ids)
                    val intent = intent.apply {
                        putExtra(PATTERN_RESULT, ids.toIntArray())
                        putExtra("Test", "test")
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                    true
                } else {
                    viewModel.isCorrect(ids)
                }
            }
        })
    }

    companion object {
        const val PACKAGE_NAME = "package_name"
        const val SHOULD_UPDATE_LOCK = "should_update_lock"
        const val PATTERN_RESULT = "pattern_result"
    }
}