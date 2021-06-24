package ru.oceancraft.chess;

import android.app.Application;

import ru.oceancraft.chess.di.AppComponent;
import ru.oceancraft.chess.di.DaggerAppComponent;

public class App extends Application {
    public AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent =
                DaggerAppComponent
                        .builder()
                        .build();
    }
}
