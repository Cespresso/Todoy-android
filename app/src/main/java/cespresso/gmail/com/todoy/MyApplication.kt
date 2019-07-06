package cespresso.gmail.com.todoy

import androidx.work.Configuration
import androidx.work.WorkManager
import cespresso.gmail.com.todoy.di.AppModule
import cespresso.gmail.com.todoy.di.DaggerAppComponent
import cespresso.gmail.com.todoy.di.applyAutoInjector
import cespresso.gmail.com.todoy.worker.MyWorkerFactory
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import javax.inject.Inject

class MyApplication : DaggerApplication() {
    @Inject
    lateinit var myWorkerFactory: MyWorkerFactory
    override fun applicationInjector(): AndroidInjector<out DaggerApplication>? {
        return DaggerAppComponent.builder().appModule(AppModule()).application(this).build()
    }

    override fun onCreate() {
        super.onCreate()
        applyAutoInjector()
        WorkManager.initialize(this, Configuration.Builder().setWorkerFactory(myWorkerFactory).build())

    }
}