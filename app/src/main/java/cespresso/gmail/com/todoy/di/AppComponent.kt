package cespresso.gmail.com.todoy.di

import android.app.Application
import cespresso.gmail.com.todoy.MyApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
    AndroidSupportInjectionModule::class,
    AppModule::class,
    ActivitiesModule::class))
interface AppComponent: AndroidInjector<MyApplication> {
    @Component.Builder
    interface Builder {

        //引数のない単一の中小メソッドでコンポートを返すなら、どんな名前でもかまわない

        fun build(): AppComponent
        //他のすべての抽象メソッドは単一の引数を取らなければならず、
        // void、Builderタイプ、またはBuilderのスーパータイプを戻す必要があります。
        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun appModule(appModule: AppModule):Builder
    }


    override fun inject(app: MyApplication)


}