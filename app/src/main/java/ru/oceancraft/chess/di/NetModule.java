package ru.oceancraft.chess.di;

import ru.oceancraft.chess.net.ActionTransmitterImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class NetModule {

    @Provides
    @Singleton
    ActionTransmitterImpl actionTransmitter() {
        return new ActionTransmitterImpl();
    }

}
