package in.sunilpaulmathew.ashell.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on June 07, 2026
 */
public class PagerAdapter extends FragmentStateAdapter {

    private final List<PageItem> pages = new ArrayList<>();

    public PagerAdapter(@NonNull FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return pages.get(position).fragment;
    }

    @Override
    public int getItemCount() {
        return pages.size();
    }

    public void addFragment(@NonNull Fragment fragment, @NonNull String title, int icon) {
        pages.add(new PageItem(fragment, title, icon));
    }

    public CharSequence getPageTitle(int position) {
        return pages.get(position).title;
    }

    public int getPageIcon(int position) {
        return pages.get(position).icon;
    }

    private static class PageItem {
        final Fragment fragment;
        final int icon;
        final String title;

        PageItem(Fragment fragment, String title, int icon) {
            this.fragment = fragment;
            this.title = title;
            this.icon = icon;
        }
    }

}