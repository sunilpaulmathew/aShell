package in.sunilpaulmathew.ashell.adapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import in.sunilpaulmathew.ashell.fragments.FilesFragment;
import in.sunilpaulmathew.ashell.fragments.SettingsFragment;
import in.sunilpaulmathew.ashell.fragments.aShellFragment;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on July 27, 2026
 */
public class PagerAdapter extends FragmentStateAdapter {

    private String command;

    public PagerAdapter(@NonNull FragmentActivity fragmentActivity, String command) {
        super(fragmentActivity);
        this.command = command;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                aShellFragment shell = new aShellFragment();
                if (command != null) {
                    Bundle b = new Bundle();
                    b.putString("command", command);
                    shell.setArguments(b);
                }
                return shell;
            case 1:
                return new FilesFragment();
            case 2:
                return new SettingsFragment();
            default:
                throw new IllegalArgumentException("Invalid position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public void setCommand(String command) {
        this.command = command;
    }

}