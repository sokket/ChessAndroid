package ru.oceancraft.chess.presentation;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import ru.oceancraft.chess.ui.ChatFragment;
import ru.oceancraft.chess.ui.LogFragment;

public class GamePagerAdapter extends FragmentStateAdapter {

    private final boolean netGame;
    private final boolean whiteGame;

    public GamePagerAdapter(@NonNull Fragment fragment, boolean netGame, boolean whiteGame) {
        super(fragment);
        this.netGame = netGame;
        this.whiteGame = whiteGame;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0)
            return LogFragment.newInstance(netGame, whiteGame);
        else
            return new ChatFragment();
    }

    @Override
    public int getItemCount() {
        return netGame ? 2 : 1;
    }
}
