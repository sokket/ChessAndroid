package ru.oceancraft.chess;

import androidx.fragment.app.Fragment;

import ru.terrakok.cicerone.android.support.SupportAppScreen;

public class Screens {
    public static final class LaunchScreen extends SupportAppScreen {
        @Override
        public Fragment getFragment() {
            return new LaunchFragment();
        }
    }

    public static final class NetworkGameSetupScreen extends SupportAppScreen {
        @Override
        public Fragment getFragment() {
            return new NetworkGameSetupFragment();
        }
    }

    public static final class WaitingScreen extends SupportAppScreen {
        @Override
        public Fragment getFragment() {
            return new WaitingFragment();
        }
    }

    public static final class GameScreen extends SupportAppScreen {

        final boolean netGame;
        final boolean isWhite;

        public GameScreen(boolean netGame, boolean isWhite) {
            this.netGame = netGame;
            this.isWhite = isWhite;
        }

        @Override
        public Fragment getFragment() {
            return GameFragment.newInstance(netGame, isWhite);
        }
    }
}
