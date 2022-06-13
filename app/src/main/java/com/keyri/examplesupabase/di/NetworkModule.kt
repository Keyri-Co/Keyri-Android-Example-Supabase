package com.keyri.examplesupabase.di

import com.keyri.examplesupabase.BuildConfig
import com.keyri.examplesupabase.network.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tech.thdev.network.flowcalladapterfactory.FlowCallAdapterFactory

val networkModule = module {
    single { provideApiService(get()) }
    single { provideRetrofit(get()) }
    single { provideOkHttpClient() }
}

private fun provideApiService(retrofit: Retrofit) = retrofit.create(ApiService::class.java)

private fun provideRetrofit(client: OkHttpClient): Retrofit =
    Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(FlowCallAdapterFactory())
        .build()

private fun provideOkHttpClient(): OkHttpClient =
    OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()
