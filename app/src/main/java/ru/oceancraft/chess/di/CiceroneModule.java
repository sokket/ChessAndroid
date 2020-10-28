package ru.oceancraft.chess.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.terrakok.cicerone.Cicerone;
import ru.terrakok.cicerone.NavigatorHolder;
import ru.terrakok.cicerone.Router;

@Module
public class CiceroneModule {

    @Provides
    @Singleton
    Cicerone<Router> getCicerone() {
        return Cicerone.create();
    }

    @Provides
    @Singleton
    NavigatorHolder getNavigatorHolder(Cicerone<Router> cicerone) {
        return cicerone.getNavigatorHolder();
    }

    @Provides
    @Singleton
    Router getRouter(Cicerone<Router> cicerone) {
        return cicerone.getRouter();
    }

}
