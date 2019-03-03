package cespresso.gmail.com.todoy.di

import android.app.Application
import cespresso.gmail.com.todoy.ViewModelModule
import dagger.Module
import dagger.Provides
import okhttp3.Dispatcher
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule//(val application: Application)
{
    @Singleton
    @Provides
    internal fun providesDispatcher(app: Application): Dispatcher {
        return Dispatcher()
    }




}