package com.example.chess.di;

import com.example.chess.*;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ContextModule.class, CiceroneModule.class, NetModule.class})
public interface AppComponent {
    void inject(MainActivity mainActivity);

    void inject(LaunchFragment launchFragment);

    void inject(NetworkGameSetupFragment networkGameSetupFragment);

    void inject(WaitingFragment waitingFragment);

    void inject(GameFragment gameFragment);

    void inject(ChatFragment chatFragment);
}
