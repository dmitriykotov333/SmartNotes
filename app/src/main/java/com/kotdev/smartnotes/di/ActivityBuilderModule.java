package com.kotdev.smartnotes.di;


import com.kotdev.smartnotes.di.main.MainBindsModule;
import com.kotdev.smartnotes.di.main.MainFragmentBuildersModule;
import com.kotdev.smartnotes.di.main.MainModule;
import com.kotdev.smartnotes.di.main.MainScope;
import com.kotdev.smartnotes.ui.activity.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuilderModule {


    @MainScope
    @ContributesAndroidInjector(
            modules = {
                    MainFragmentBuildersModule.class,
                    MainBindsModule.class,
                    MainModule.class
            }
    )
    abstract MainActivity contributeMainActivity();

}
