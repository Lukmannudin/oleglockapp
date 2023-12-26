package com.oleg.oleglock

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oleg.oleglock.data.AppLock
import com.oleg.oleglock.data.AppLockDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LockScreenViewModel @Inject constructor(
    val dao: AppLockDao
): ViewModel() {

    private val currentApp: MutableLiveData<AppLock> by lazy {
        MutableLiveData<AppLock>()
    }

    private val isNeedUpdateLock: MutableLiveData<Boolean> = MutableLiveData(false)

    val checkPattern: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun init(packageName: String) {
        viewModelScope.launch {
            val appLock = dao.get(packageName)
            currentApp.postValue(appLock)
        }
    }

    fun shouldUpdateLock(state: Boolean) {
        isNeedUpdateLock.value = state
    }

    fun checkPattern() = currentApp.value?.pattern?.isNotEmpty() ?: false

    fun isCorrect(ids: ArrayList<Int>) = currentApp.value?.pattern == ids

    fun updateLock(ids: ArrayList<Int>) {
        currentApp.value?.pattern = ids
        viewModelScope.launch {
            currentApp.value?.let {
                dao.update(it)
            }
        }
    }

    fun isCorrect(packageName: String, ids: ArrayList<Int>) {
        viewModelScope.launch {
            val appLock = dao.get(packageName)
            checkPattern.postValue(appLock.pattern == ids)
        }
    }
}