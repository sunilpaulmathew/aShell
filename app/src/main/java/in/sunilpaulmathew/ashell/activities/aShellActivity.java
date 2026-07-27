package in.sunilpaulmathew.ashell.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.TypedValue;

import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.color.MaterialColors;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.adapters.PagerAdapter;
import in.sunilpaulmathew.ashell.fragments.aShellFragment;
import in.sunilpaulmathew.ashell.utils.Async;
import in.sunilpaulmathew.ashell.utils.ButtonAnimator;
import in.sunilpaulmathew.ashell.utils.Commands;
import in.sunilpaulmathew.ashell.utils.Settings;
import in.sunilpaulmathew.ashell.utils.ShizukuShell;
import in.sunilpaulmathew.ashell.utils.Utils;
import rikka.shizuku.Shizuku;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 28, 2022
 */
public class aShellActivity extends BaseActivity {

    private int FRAGMENT_POSITION = 0;
    private MaterialButton shell, files, settings;
    private ViewPager2 viewPager;
    private PagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize App Theme & language
        Settings.initializeAppTheme(this);
        Settings.initializeAppLanguage(this);

        setContentView(R.layout.activity_ashell, R.id.layout_main);

        shell = findViewById(R.id.shell);
        files = findViewById(R.id.files);
        settings = findViewById(R.id.settings);
        viewPager = findViewById(R.id.view_pager);

        if (!Utils.getBoolean("welcome_screen_viewed", false, this)) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        }

        adapter = new PagerAdapter(this, getIntent().getStringExtra("command"));
        viewPager.setAdapter(adapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                updateIcons(position, activeColor(aShellActivity.this), inactiveColor());

                FRAGMENT_POSITION = position;
            }
        });

        new Async() {
            @Override
            public void onPreExecute() {}

            @Override
            public void doInBackground() {
                if (Shizuku.pingBinder() && Shizuku.getVersion() >= 11 && Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                    ShizukuShell.ensureUserService(Commands::loadPackageInfo);
                }
            }

            @Override
            public void onPostExecute() {
            }
        }.execute();

        shell.setOnClickListener(v -> {
            if (FRAGMENT_POSITION == 0) return;
            new ButtonAnimator(shell, null) {
                @Override
                public void onItemClicked() {
                    viewPager.setCurrentItem(0, true);
                }
            };
        });

        files.setOnClickListener(v -> {
            if (FRAGMENT_POSITION == 1) return;
            new ButtonAnimator(files, null) {
                @Override
                public void onItemClicked() {
                    viewPager.setCurrentItem(1, true);
                }
            };
        });

        settings.setOnClickListener(v -> {
            if (FRAGMENT_POSITION == 2) return;
            new ButtonAnimator(settings, null) {
                @Override
                public void onItemClicked() {
                    viewPager.setCurrentItem(2, true);
                }
            };
        });
    }

    private int activeColor(Activity activity) {
        return DynamicColors.isDynamicColorAvailable()
                ? MaterialColors.getColor(DynamicColors.wrapContextIfAvailable(
                        activity, com.google.android.material.R.style.Theme_Material3_DynamicColors_DayNight), androidx.appcompat.R.attr.colorPrimary,
                Utils.getColor(R.color.colorBlue, activity))
                : androidx.core.content.ContextCompat.getColor(activity, R.color.colorBlue);
    }

    private int inactiveColor() {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typedValue, true);
        int baseColor = typedValue.data;

        return ColorUtils.setAlphaComponent(baseColor, Math.round(255 * 0.76f));
    }

    public void navigateToFragment(int position, String command) {
        if (command != null) {
            Fragment existingShell = getSupportFragmentManager().findFragmentByTag("f0");
            if (existingShell instanceof aShellFragment) {
                ((aShellFragment) existingShell).updateCommand(command);
            } else if (adapter != null) {
                adapter.setCommand(command);
            }
        }
        viewPager.setCurrentItem(position, true);
    }

    private void updateIcons(int position, int activeColor, int inactiveColor) {
        ColorStateList activeTint = ColorStateList.valueOf(activeColor);
        ColorStateList inactiveTint = ColorStateList.valueOf(inactiveColor);

        files.setChecked(position == 1);
        files.setIconTint(position == 1 ? activeTint : inactiveTint);
        files.setTextColor(position == 1 ? activeColor : inactiveColor);

        shell.setChecked(position == 0);
        shell.setIconTint(position == 0 ? activeTint : inactiveTint);
        shell.setTextColor(position == 0 ? activeColor : inactiveColor);

        settings.setChecked(position == 2);
        settings.setIconTint(position == 2 ? activeTint : inactiveTint);
        settings.setTextColor(position == 2 ? activeColor : inactiveColor);
    }

}