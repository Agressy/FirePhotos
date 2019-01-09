package com.bortnikov.artem.firephotos;

import android.app.Application;

import com.bortnikov.artem.firephotos.di.AppComponent;
import com.bortnikov.artem.firephotos.di.DaggerAppComponent;

public class MainApp extends Application {

    private static AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        component = DaggerAppComponent.builder().build();
    }

    public static AppComponent getComponent() {
        return component;
    }
}