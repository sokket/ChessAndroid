package ru.oceancraft.chess.di;

import javax.inject.Singleton;

import dagger.Component;
import ru.oceancraft.chess.AppActivity;
import ru.oceancraft.chess.ui.WaitingFragment;
import ru.oceancraft.chess.ui.ChatFragment;
import ru.oceancraft.chess.ui.GameFragment;
import ru.oceancraft.chess.ui.LaunchFragment;
import ru.oceancraft.chess.ui.NetworkGameSetupFragment;

@Singleton
@Component(modules = {ContextModule.class, CiceroneModule.class, NetModule.class})
public interface AppComponent {
    void inject(AppActivity appActivity);

    void inject(LaunchFragment launchFragment);

    void inject(NetworkGameSetupFragment networkGameSetupFragment);

    void inject(WaitingFragment waitingFragment);

    void inject(GameFragment gameFragment);

    void inject(ChatFragment chatFragment);
}
