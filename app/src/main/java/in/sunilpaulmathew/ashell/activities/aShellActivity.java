package in.sunilpaulmathew.ashell.activities;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textview.MaterialTextView;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.adapters.PagerAdapter;
import in.sunilpaulmathew.ashell.fragments.ExtrasFragment;
import in.sunilpaulmathew.ashell.fragments.aShellFragment;
import in.sunilpaulmathew.ashell.utils.Async;
import in.sunilpaulmathew.ashell.utils.Packages;
import in.sunilpaulmathew.ashell.utils.Settings;
import in.sunilpaulmathew.ashell.utils.ShizukuShell;
import in.sunilpaulmathew.ashell.utils.Utils;
import rikka.shizuku.Shizuku;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 28, 2022
 */
public class aShellActivity extends BaseActivity {

    private MaterialTextView title;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize App Theme & language
        Settings.initializeAppTheme(this);
        Settings.initializeAppLanguage(this);

        setContentView(R.layout.activity_ashell, R.id.layout_main);

        MaterialButton settings = findViewById(R.id.settings_button);
        title = findViewById(R.id.title);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        if (!Utils.getBoolean("welcome_screen_viewed", false, this)) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        } else if (!Utils.getBoolean("extras_activity_viewed", false, this)) {
            activityResultLauncher.launch(new Intent(this, ExtrasActivity.class));
        }

        loadUI(savedInstanceState);

        if (Settings.isExtrasEnabled(this)) {
            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
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

        settings.setOnClickListener(v -> activityResultLauncher.launch(new Intent(this, SettingsActivity.class)));
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    int extrasChanged = result.getData().getIntExtra("extrasChanged", -1);
                    String selectedCommand = result.getData().getStringExtra("selectedCommand");
                    if (selectedCommand != null) {
                        navigateToFragment(0, selectedCommand);
                    } else if (extrasChanged >= 0) {
                        if (extrasChanged == 1) {
                            reloadExtras();
                        } else {
                            loadUI(null);
                        }
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

    public void enableViewPagerSwiping(boolean enabled) {
        if (viewPager != null) {
            if (viewPager.isUserInputEnabled() == enabled) return;
            viewPager.setUserInputEnabled(enabled);
        }
    }

    private void loadUI(Bundle savedInstanceState) {
        PagerAdapter adapter = new PagerAdapter(this);
        adapter.addFragment(getShellFragment(getIntent().getStringExtra("command")), getString(R.string.shell), R.drawable.ic_shell);
        if (Settings.isExtrasEnabled(this)) {
            adapter.addFragment(getExtrasFragment(), getString(R.string.extras), R.drawable.ic_extras);
            title.setVisibility(GONE);
            tabLayout.setVisibility(VISIBLE);
        } else {
            tabLayout.setVisibility(GONE);
            title.setVisibility(VISIBLE);
        }
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    tab.setText(adapter.getPageTitle(position));
                    tab.setIcon(adapter.getPageIcon(position));
                }
        ).attach();

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                View customView = View.inflate(this, R.layout.recycler_view_tab_button, null);
                MaterialButton tabButton = customView.findViewById(R.id.button);
                if (tab.getText() != null) {
                    tabButton.setText(tab.getText());
                }
                tab.setCustomView(customView);
            }
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getCustomView() != null) {
                    MaterialButton button = tab.getCustomView().findViewById(R.id.button);
                    button.setChecked(true);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getCustomView() != null) {
                    MaterialButton button = tab.getCustomView().findViewById(R.id.button);
                    button.setChecked(false);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        if (savedInstanceState == null) {
            tabLayout.post(() -> {
                TabLayout.Tab firstTab = tabLayout.getTabAt(0);
                if (firstTab != null) {
                    firstTab.select();
                    if (firstTab.getCustomView() != null) {
                        MaterialButton button = firstTab.getCustomView().findViewById(R.id.button);
                        if (button != null) button.setChecked(true);
                    }
                }
            });
        }
    }

    public void navigateToFragment(int position, String command) {
        if (position == 0) {
            getShellFragment(command);
        }
        viewPager.setCurrentItem(position, true);
    }

    private void reloadExtras() {
        Fragment existingExtras = getSupportFragmentManager().findFragmentByTag("f1");
        if (existingExtras != null) {
            if (existingExtras instanceof ExtrasFragment) {
                ((ExtrasFragment) existingExtras).loadUI();
            }
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration configuration) {
        super.onConfigurationChanged(configuration);

        Fragment existingExtras = getSupportFragmentManager().findFragmentByTag("f1");
        if (existingExtras != null) {
            if (existingExtras instanceof ExtrasFragment) {
                ((ExtrasFragment) existingExtras).handleOrientationChange();
            }
        }
    }

}