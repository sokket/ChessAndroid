package com.example.chess;

import android.app.Application;

import com.example.chess.di.AppComponent;
import com.example.chess.di.ContextModule;
import com.example.chess.di.DaggerAppComponent;

public class App extends Application {
    public AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent =
                DaggerAppComponent
                        .builder()
                        .contextModule(new ContextModule(getApplicationContext()))
                        .build();
    }
}
