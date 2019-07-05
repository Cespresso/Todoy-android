package cespresso.gmail.com.todoy.worker

import androidx.work.WorkerFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import cespresso.gmail.com.todoy.worker.MyWorkerFactory

@Module
interface WorkerModule{
    @Binds
    @IntoMap
    @WorkerKey(GetAllTodosWorker::class)
    fun bindGetAllTodosWorker(factory: GetAllTodosWorker.Factory):ChildWorkerFactory


    @Binds
    fun bindWorkerFactory(factory: MyWorkerFactory): WorkerFactory
}