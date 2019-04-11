package cespresso.gmail.com.todoy.di

import cespresso.gmail.com.todoy.ui.main.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityModule {
    @ContributesAndroidInjector
    abstract fun contributeMainFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun contributeShowFragment(): ShowFragment

    @ContributesAndroidInjector
    abstract fun contributeAddFragment(): AddFragment

    @ContributesAndroidInjector
    abstract fun contributeEditFragment(): EditFragment

    @ContributesAndroidInjector
    abstract fun contributeProfileFragment(): ProfileFragment

    @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): LoginFragment
}