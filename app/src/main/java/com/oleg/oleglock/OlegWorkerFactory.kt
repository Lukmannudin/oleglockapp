//package com.oleg.oleglock
//
//import android.content.Context
//import androidx.work.ListenableWorker
//import androidx.work.WorkerFactory
//import androidx.work.WorkerParameters
//import com.oleg.oleglock.data.AppLockDao
//import javax.inject.Inject
//
//class OlegWorkerFactory @Inject constructor(
//    private val dao: AppLockDao
//): WorkerFactory() {
//    override fun createWorker(
//        appContext: Context,
//        workerClassName: String,
//        workerParameters: WorkerParameters
//    ): ListenableWorker {
//        return LockWorker(appContext, workerParameters, dao)
//    }
//}