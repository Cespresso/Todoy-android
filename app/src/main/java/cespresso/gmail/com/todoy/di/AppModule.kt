package cespresso.gmail.com.todoy.di

import android.app.Application
import cespresso.gmail.com.todoy.ViewModelModule
import cespresso.gmail.com.todoy.data.source.remote.ApiService
import cespresso.gmail.com.todoy.data.source.remote.ITodoyApiService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule//(val application: Application)
{
    @Singleton
    @Provides
    internal fun providesApiService(app:Application): ITodoyApiService {
        return ApiService(app).service
    }

}