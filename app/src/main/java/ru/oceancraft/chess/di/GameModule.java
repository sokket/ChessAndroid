package ru.oceancraft.chess.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.oceancraft.chess.presentation.GameViewModel;

@Module
public class GameModule {

    @Provides
    @Singleton
    GameViewModel gameViewModel() {
        return new GameViewModel();
    }

}
