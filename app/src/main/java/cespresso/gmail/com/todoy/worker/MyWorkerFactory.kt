package cespresso.gmail.com.todoy.worker

import android.content.Context
import android.os.Parcelable
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import cespresso.gmail.com.todoy.worker.ChildWorkerFactory
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class MyWorkerFactory @Inject constructor(
    private val workerFactories: Map<Class<out ListenableWorker>,@JvmSuppressWildcards Provider<ChildWorkerFactory>>
    ) : WorkerFactory(){
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        val foundEntry =
            workerFactories.entries.find { Class.forName(workerClassName).isAssignableFrom(it.key)}
        val factoryProvider = foundEntry?.value
            ?: throw IllegalArgumentException("unknown worker class name: $workerClassName")
        return factoryProvider.get().create(appContext, workerParameters)
    }
}
