package ru.oceancraft.chess;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.oceancraft.chess.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

import ru.terrakok.cicerone.Navigator;
import ru.terrakok.cicerone.NavigatorHolder;
import ru.terrakok.cicerone.Router;
import ru.terrakok.cicerone.android.support.SupportAppNavigator;
import ru.terrakok.cicerone.commands.Command;

public class MainActivity extends AppCompatActivity {

    @Inject
    Router router;

    @Inject
    NavigatorHolder navigatorHolder;

    Navigator navigator = new SupportAppNavigator(this, R.id.mainView) {
        @Override
        protected void setupFragmentTransaction(
                @NotNull Command command,
                @Nullable Fragment currentFragment,
                @Nullable Fragment nextFragment,
                @NotNull FragmentTransaction fragmentTransaction
        ) {
            fragmentTransaction.setCustomAnimations(
                    R.anim.fragment_open_enter,
                    R.anim.fragment_open_exit,
                    R.anim.fragment_close_enter,
                    R.anim.fragment_close_exit
            );
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        App app = (App) getApplication();
        app.appComponent.inject(this);

        if (savedInstanceState == null)
            router.newRootScreen(new Screens.LaunchScreen());
    }

    @Override
    public void onBackPressed() {
        router.exit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigatorHolder.setNavigator(navigator);
    }

    @Override
    protected void onPause() {
        super.onPause();
        navigatorHolder.removeNavigator();
    }
}