package com.example.chess;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chess.game.ActionTransmitter;
import com.example.chess.game.ChessGame;
import com.example.chess.game.ChessView;
import com.example.chess.game.LogLine;
import com.example.chess.game.OnPressListener;
import com.example.chess.game.ResetOnPressListener;
import com.example.chess.game.TileType;
import com.example.chess.net.ActionTransmitterImpl;
import com.google.android.material.snackbar.Snackbar;

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