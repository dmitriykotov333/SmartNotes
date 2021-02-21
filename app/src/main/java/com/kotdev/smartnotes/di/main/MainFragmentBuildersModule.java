package com.kotdev.smartnotes.di.main;

import com.kotdev.smartnotes.ui.fragments.DetailFragmentImage;
import com.kotdev.smartnotes.ui.fragments.DrawingFragment;
import com.kotdev.smartnotes.ui.fragments.FirstFragment;
import com.kotdev.smartnotes.ui.fragments.SecondFragment;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MainFragmentBuildersModule {

    @ContributesAndroidInjector
    abstract FirstFragment contributeFirstFragment();

    @ContributesAndroidInjector
    abstract SecondFragment contributeSecondFragment();

    @ContributesAndroidInjector
    abstract DetailFragmentImage contributeDetailFragmentImage();

    @ContributesAndroidInjector
    abstract DrawingFragment contributeDrawingFragment();
}
