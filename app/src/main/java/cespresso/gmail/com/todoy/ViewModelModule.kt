package cespresso.gmail.com.todoy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cespresso.gmail.com.todoy.di.ViewModelFactory
import cespresso.gmail.com.todoy.di.ViewModelKey
import cespresso.gmail.com.todoy.ui.main.MainActivityViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal abstract class ViewModelModule{
    //Main
    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    abstract fun bindAppStore(mainActivityViewModel: MainActivityViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}