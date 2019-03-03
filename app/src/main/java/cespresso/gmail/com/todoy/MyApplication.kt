package cespresso.gmail.com.todoy

import android.app.Application
import cespresso.gmail.com.todoy.di.AppModule
import cespresso.gmail.com.todoy.di.DaggerAppComponent
import cespresso.gmail.com.todoy.di.applyAutoInjector
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

class MyApplication: DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication>? {
        return DaggerAppComponent.builder().appModule(AppModule()).application(this).build()
    }

    override fun onCreate() {
        super.onCreate()
        applyAutoInjector()
    }
}