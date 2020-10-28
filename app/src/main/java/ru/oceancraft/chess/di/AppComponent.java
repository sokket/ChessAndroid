package ru.oceancraft.chess.di;

import javax.inject.Singleton;

import dagger.Component;
import ru.oceancraft.chess.ChatFragment;
import ru.oceancraft.chess.GameFragment;
import ru.oceancraft.chess.LaunchFragment;
import ru.oceancraft.chess.MainActivity;
import ru.oceancraft.chess.NetworkGameSetupFragment;
import ru.oceancraft.chess.WaitingFragment;

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
