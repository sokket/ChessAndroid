package com.example.chess.di;

import com.example.chess.net.ActionTransmitterImpl;
import com.example.chess.net.ChessClient;

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
