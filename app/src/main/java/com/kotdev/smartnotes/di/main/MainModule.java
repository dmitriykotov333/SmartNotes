package com.kotdev.smartnotes.di.main;

import android.app.Application;
import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module
public class MainModule {

    @MainScope
    @Provides
    static Context provideContext(Application application){
        return application.getApplicationContext();
    }


}
