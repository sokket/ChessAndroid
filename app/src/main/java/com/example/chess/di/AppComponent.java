package com.example.chess.di;

import com.example.chess.GameFragment;
import com.example.chess.LaunchFragment;
import com.example.chess.MainActivity;
import com.example.chess.NetworkGameSetupFragment;
import com.example.chess.WaitingFragment;

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
}
