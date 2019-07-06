package cespresso.gmail.com.todoy.worker

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.work.*
import cespresso.gmail.com.todoy.data.source.remote.ITodoyApiService
import cespresso.gmail.com.todoy.ui.main.MainActivityViewModel
import javax.inject.Inject
import javax.inject.Provider

class GetAllTodosWorker @Inject constructor(
    private val api: ITodoyApiService,
    ctx: Context,
    params: WorkerParameters
) : Worker(ctx, params) {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: MainActivityViewModel
    override fun doWork(): Result {
        api
        Log.i("^v^","うい")
        val outputData: Data = workDataOf("result" to "^v^ti")
        return Result.success(outputData)
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