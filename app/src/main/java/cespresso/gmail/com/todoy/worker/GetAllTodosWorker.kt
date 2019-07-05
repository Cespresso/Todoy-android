package cespresso.gmail.com.todoy.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import cespresso.gmail.com.todoy.data.source.remote.ITodoyApiService
import javax.inject.Inject
import javax.inject.Provider

class GetAllTodosWorker @Inject constructor(
    private val api: ITodoyApiService,
    ctx: Context,
    params: WorkerParameters
) : Worker(ctx, params) {
    override fun doWork(): Result {
        return Result.success()
    }

    class Factory @Inject constructor(
        private val api:Provider<ITodoyApiService>
    ):ChildWorkerFactory{
        override fun create(appContext: Context, params: WorkerParameters):ListenableWorker {
            return GetAllTodosWorker(
                api.get(),
                appContext,
                params
            )
        }
    }
}