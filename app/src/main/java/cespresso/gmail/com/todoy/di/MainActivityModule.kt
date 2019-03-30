package cespresso.gmail.com.todoy.di

import cespresso.gmail.com.todoy.ui.main.HomeFragment
import cespresso.gmail.com.todoy.ui.main.ShowFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityModule {
    @ContributesAndroidInjector
    abstract fun contributeMainFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun contributeShowFragment(): ShowFragment
}