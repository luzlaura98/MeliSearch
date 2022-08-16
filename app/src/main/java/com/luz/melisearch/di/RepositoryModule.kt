package com.luz.melisearch.di

import android.app.Application
import com.luz.melisearch.data.services.MeliAPI
import com.luz.melisearch.domain.repo.ItemsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by Luz on 4/8/2022.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideCharacterRepository(
        application: Application,
        appService : MeliAPI
    ) = ItemsRepository(application, appService)
}