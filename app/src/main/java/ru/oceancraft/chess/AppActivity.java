package ru.oceancraft.chess;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

import javax.inject.Inject;

import ru.oceancraft.chess.net.NetworkActionTransmitter;
import ru.terrakok.cicerone.Navigator;
import ru.terrakok.cicerone.NavigatorHolder;
import ru.terrakok.cicerone.Router;
import ru.terrakok.cicerone.android.support.SupportAppNavigator;
import ru.terrakok.cicerone.commands.Command;

public class AppActivity extends AppCompatActivity {

    @Inject
    Router router;

    @Inject
    NavigatorHolder navigatorHolder;

    @Inject
    NetworkActionTransmitter actionTransmitter;

    Navigator navigator = new SupportAppNavigator(this, R.id.mainView) {
        @Override
        protected void setupFragmentTransaction(
                @NonNull Command command,
                @Nullable Fragment currentFragment,
                @Nullable Fragment nextFragment,
                @NonNull FragmentTransaction fragmentTransaction
        ) {
            fragmentTransaction.setCustomAnimations(
                    R.anim.fragment_open_enter,
                    R.anim.fragment_open_exit,
                    R.anim.fragment_close_enter,
                    R.anim.fragment_close_exit
            );
        }
    };

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        App app = (App) getApplication();
        app.appComponent.inject(this);

        if (savedInstanceState == null) {
            Uri uri = getIntent().getData();
            if (uri != null) {
                String inviteCode = uri.getPath().replace("/", "");
                if (inviteCode.matches("[0-9a-zA-Z]{3}-[0-9a-zA-Z]{3}")) {
                    actionTransmitter.connect(
                            () -> actionTransmitter.join(
                                    inviteCode,
                                    () -> router.newRootChain(
                                            new Screens.LaunchScreen(),
                                            new Screens.GameScreen(true, false)
                                    ),
                                    () -> showToast(getString(R.string.join_error))
                            ),
                            () -> showToast(getString(R.string.connection_error))
                    );
                }
            } else {
                router.newRootScreen(new Screens.LaunchScreen());
            }
        }
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