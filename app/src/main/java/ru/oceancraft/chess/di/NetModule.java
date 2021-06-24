package ru.oceancraft.chess.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.oceancraft.chess.net.JavaActionTransmitterImpl;
import ru.oceancraft.chess.net.NetworkActionTransmitter;

@Module
public class NetModule {

    @Provides
    @Singleton
    NetworkActionTransmitter actionTransmitter() {
        return new JavaActionTransmitterImpl();
    }

}
