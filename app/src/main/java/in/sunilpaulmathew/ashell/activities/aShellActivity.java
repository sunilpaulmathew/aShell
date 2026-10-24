package in.sunilpaulmathew.ashell.activities;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.FrameLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.textview.MaterialTextView;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.adapters.PagerAdapter;
import in.sunilpaulmathew.ashell.fragments.ExtrasFragment;
import in.sunilpaulmathew.ashell.fragments.SettingsFragment;
import in.sunilpaulmathew.ashell.fragments.aShellFragment;
import in.sunilpaulmathew.ashell.utils.Async;
import in.sunilpaulmathew.ashell.utils.ButtonAnimator;
import in.sunilpaulmathew.ashell.utils.Packages;
import in.sunilpaulmathew.ashell.utils.Settings;
import in.sunilpaulmathew.ashell.utils.ShizukuShell;
import in.sunilpaulmathew.ashell.utils.Utils;
import rikka.shizuku.Shizuku;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 28, 2022
 */
public class aShellActivity extends BaseActivity {

    private AppCompatImageButton icon;
    private FrameLayout frameLayout;
    private int FRAGMENT_POSITION = 0;
    private LinearLayoutCompat titleLayout;
    private MaterialButton extras, settings, settingsButton, shell;
    private MaterialTextView title;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize App Theme & language
        Settings.initializeAppTheme(this);
        Settings.initializeAppLanguage(this);

        setContentView(R.layout.activity_ashell, R.id.layout_main);

        icon = findViewById(R.id.icon);
        frameLayout = findViewById(R.id.fragment_container);
        titleLayout = findViewById(R.id.title_layout);
        shell = findViewById(R.id.shell);
        extras = findViewById(R.id.extras);
        settings = findViewById(R.id.settings);
        settingsButton = findViewById(R.id.settings_button);
        title = findViewById(R.id.title);
        viewPager = findViewById(R.id.view_pager);

        if (!Utils.getBoolean("welcome_screen_viewed", false, this)) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        } else if (!Utils.getBoolean("extras_activity_viewed", false, this)) {
            activityResultLauncher.launch(new Intent(this, ExtrasActivity.class));
        }

        loadUI();

        if (Settings.isExtrasEnabled(this)) {
            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);

                    updateIcons(position, activeColor(aShellActivity.this), inactiveColor());

                    FRAGMENT_POSITION = position;
                }
            });
        }

        new Async() {
            @Override
            public void onPreExecute() {}

            @Override
            public void doInBackground() {
                if (Shizuku.pingBinder() && Shizuku.getVersion() >= 11 && Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                    ShizukuShell.ensureUserService(Packages::acquireData);
                }
            }

            @Override
            public void onPostExecute() {
            }
        }.execute();

        shell.setOnClickListener(v -> {
            if (FRAGMENT_POSITION == 0) {
                shell.setChecked(true);
                return;
            }
            new ButtonAnimator(shell, null) {
                @Override
                public void onItemClicked() {
                    viewPager.setCurrentItem(0, true);
                }
            };
        });

        extras.setOnClickListener(v -> {
            if (FRAGMENT_POSITION == 1) {
                extras.setChecked(true);
                return;
            }
            new ButtonAnimator(extras, null) {
                @Override
                public void onItemClicked() {
                    viewPager.setCurrentItem(1, true);
                }
            };
        });

        settings.setOnClickListener(v -> {
            if (FRAGMENT_POSITION == 2) {
                settings.setChecked(true);
                return;
            }
            new ButtonAnimator(settings, null) {
                @Override
                public void onItemClicked() {
                    viewPager.setCurrentItem(3, true);
                }
            };
        });

        settingsButton.setOnClickListener(v -> {
            requestFocus(false);
            viewPager.setVisibility(GONE);
            settingsButton.setVisibility(GONE);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
            frameLayout.setVisibility(VISIBLE);
            title.setText(R.string.settings);
            icon.setVisibility(GONE);
        });
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    if (result.getData().getBooleanExtra("extrasChanged", false)) {
                        Settings.restartApp(this);
                    }
                }
            }
    );

    private Fragment getExtrasFragment() {
        Fragment existingExtras = getSupportFragmentManager().findFragmentByTag("f1");
        if (existingExtras != null) {
            if (existingExtras instanceof ExtrasFragment) {
                ((ExtrasFragment) existingExtras).loadUI();
            }
            return existingExtras;
        } else {
            return new ExtrasFragment();
        }
    }

    private Fragment getShellFragment(String command) {
        Fragment existingShell = getSupportFragmentManager().findFragmentByTag("f0");
        if (existingShell != null) {
            if (command != null) {
                if (existingShell instanceof aShellFragment) {
                    ((aShellFragment) existingShell).updateCommand(command);
                }
            }
            return existingShell;
        } else {
            aShellFragment shell = new aShellFragment();
            if (command != null) {
                Bundle b = new Bundle();
                b.putString("command", command);
                shell.setArguments(b);
            }
            return shell;
        }
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

    public void enableViewPagerSwiping(boolean enabled) {
        if (viewPager != null) {
            if (viewPager.isUserInputEnabled() == enabled) return;
            viewPager.setUserInputEnabled(enabled);
        }
    }

    public void loadUI() {
        PagerAdapter adapter = new PagerAdapter(this);
        adapter.addFragment(getShellFragment(getIntent().getStringExtra("command")), getString(R.string.shell));
        if (Settings.isExtrasEnabled(this)) {
            adapter.addFragment(getExtrasFragment(), getString(R.string.extras));
            adapter.addFragment(new SettingsFragment(), getString(R.string.settings));
            titleLayout.setVisibility(GONE);
            settings.setVisibility(VISIBLE);
            extras.setVisibility(VISIBLE);
            shell.setVisibility(VISIBLE);
        } else {
            titleLayout.setVisibility(VISIBLE);
            settings.setVisibility(GONE);
            extras.setVisibility(GONE);
            shell.setVisibility(GONE);
        }
        viewPager.setAdapter(adapter);
    }

    public void navigateToFragment(int position, String command) {
        if (position == 0) {
            getShellFragment(command);
        }
        viewPager.setCurrentItem(position, true);
    }

    public void reloadExtras() {
        Fragment existingExtras = getSupportFragmentManager().findFragmentByTag("f1");
        if (existingExtras != null) {
            if (existingExtras instanceof ExtrasFragment) {
                ((ExtrasFragment) existingExtras).loadUI();
            }
        }
    }

    private void requestFocus(boolean show) {
        Fragment existingShell = getSupportFragmentManager().findFragmentByTag("f0");
        if (existingShell instanceof aShellFragment) {
            if (show) {
                ((aShellFragment) existingShell).requestFocus();
            } else {
                ((aShellFragment) existingShell).clearFocus();
            }
        }
    }

    public void showShell(String command) {
        frameLayout.setVisibility(GONE);
        viewPager.setVisibility(VISIBLE);
        title.setText(R.string.app_name);
        settingsButton.setVisibility(VISIBLE);
        icon.setVisibility(VISIBLE);
        requestFocus(true);
        if (command != null) {
            Fragment existingShell = getSupportFragmentManager().findFragmentByTag("f0");
            if (existingShell instanceof aShellFragment) {
                ((aShellFragment) existingShell).updateCommand(command);
            }
        }
    }

    private void updateIcons(int position, int activeColor, int inactiveColor) {
        ColorStateList activeTint = ColorStateList.valueOf(activeColor);
        ColorStateList inactiveTint = ColorStateList.valueOf(inactiveColor);

        shell.setChecked(position == 0);
        shell.setIconTint(position == 0 ? activeTint : inactiveTint);
        shell.setTextColor(position == 0 ? activeColor : inactiveColor);

        extras.setChecked(position == 1);
        extras.setIconTint(position == 1 ? activeTint : inactiveTint);
        extras.setTextColor(position == 1 ? activeColor : inactiveColor);

        settings.setChecked(position == 2);
        settings.setIconTint(position == 2 ? activeTint : inactiveTint);
        settings.setTextColor(position == 2 ? activeColor : inactiveColor);
    }

}