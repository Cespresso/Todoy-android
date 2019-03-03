package cespresso.gmail.com.todoy.di

import cespresso.gmail.com.todoy.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivitiesModule {

    @ContributesAndroidInjector(modules = arrayOf(MainActivityModule::class))
    abstract fun contributeHomeActivity(): MainActivity
}