package com.keyri.examplesupabase

import android.app.Application
import com.keyri.examplesupabase.di.networkModule
import com.keyri.examplesupabase.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class SupabaseApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@SupabaseApp)
            modules(networkModule, viewModelModule)
        }
    }
}
