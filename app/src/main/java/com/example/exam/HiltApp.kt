package com.example.exam

import android.app.Application
import android.app.Service
import android.content.Context
import android.net.ConnectivityManager
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Singleton


@HiltAndroidApp
class HiltApp : Application() {
}

@Module
@InstallIn(SingletonComponent::class)
class AppModules {

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext appContext: Context):
            Database {
        return Room.databaseBuilder(
            appContext,
            Database::class.java,
            "db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideRepository(db: Database): RecipeDao {
        val executorService = Executors.newSingleThreadExecutor()
        executorService.submit {
            db.dao.addTypes(ArrayList<RecipeType>(listOf(RecipeType("pui"), RecipeType("porc"))));
        }
        return db.dao;
    }

    @Provides
    @Singleton
    fun provideExecutorService(): ExecutorService {
        return Executors.newSingleThreadExecutor()
    }

    @Provides
    @Singleton
    fun provideRetrofitService(): com.example.exam.Service {
        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(com.example.exam.Service.SERVICE_ENDPOINT)
            .build()
        return retrofit.create(com.example.exam.Service::class.java)
    }


    @Provides
    @Singleton
    fun provideConnectivityManager(
        @ApplicationContext appContext: Context,
        service: com.example.exam.Service,
        dao: RecipeDao,
        executorService: ExecutorService
    ): Manager {
        return Manager(appContext, service, executorService, dao)
    }


}