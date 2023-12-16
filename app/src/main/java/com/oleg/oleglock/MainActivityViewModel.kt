package com.oleg.oleglock

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oleg.oleglock.Util.findIndexLockApp
import com.oleg.oleglock.data.AppLock
import com.oleg.oleglock.data.AppLockDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val dao: AppLockDao
): ViewModel() {

//    private val _appLocks = MutableLiveData<List<AppLock>>()
//    val appLocks: LiveData<List<AppLock>> = _appLocks
//
    val viewModelState = MutableStateFlow(HomeViewModelState(isLoading = true))

    init {
        viewModelScope.launch {
            getAppLocks()
        }
    }

    fun setLocked(appLock: AppLock) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insert(appLock)
        }
    }

    fun mergeAppLocks(deviceApps: List<AppLock>): List<AppLock> {
        val apps = deviceApps.toMutableList()
        viewModelState.value = HomeViewModelState(isLoading = false, deviceApps)
//        val appLockCaches = dao.getAll()
//        appLockCaches.forEach { appLock ->
//            val index = apps.findIndexLockApp(appLock)
//            if (index >= 0) apps[index].isLock = appLock.isLock
//        }
        return apps
    }

    suspend fun getAppLocks() {
        val appLocks = dao.getAll()
    }
}

data class HomeViewModelState(
    val isLoading: Boolean = false,
    val appLocks: List<AppLock> = emptyList()
)