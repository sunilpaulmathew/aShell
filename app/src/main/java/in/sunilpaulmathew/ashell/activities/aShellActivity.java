package in.sunilpaulmathew.ashell.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.color.MaterialColors;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.fragments.FilesFragment;
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

    private static int FRAGMENT_POSITION = 0;
    private MaterialButton shell, files, settings;

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

        if (!Utils.getBoolean("welcome_screen_viewed", false, this)) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        }

        shell.setOnClickListener(v -> {
            if (FRAGMENT_POSITION == 0) return;
            new ButtonAnimator(shell, null) {
                @Override
                public void onItemClicked() {
                    loadUI(0).execute();
                }
            };
        });

        files.setOnClickListener(v -> {
            if (FRAGMENT_POSITION == 1) return;
            new ButtonAnimator(files, null) {
                @Override
                public void onItemClicked() {
                    loadUI(1).execute();
                }
            };
        });

        settings.setOnClickListener(v -> new ButtonAnimator(settings, null) {
            @Override
            public void onItemClicked() {
                Intent intent = new Intent(v.getContext(), SettingsActivity.class);
                activityResultLauncher.launch(intent);
            }
        });

        loadUI(FRAGMENT_POSITION).execute();
    }

    private Async loadUI(int position) {
        return new Async() {
            private final Activity activity = aShellActivity.this;
            private String mCommand = null;
            @Override
            public void onPreExecute() {
            }

            @Override
            public void doInBackground() {
                if (Shizuku.pingBinder() && Shizuku.getVersion() >= 11 && Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                    ShizukuShell.ensureUserService(Commands::loadPackageInfo);
                }
                mCommand = getIntent().getStringExtra("command");
            }

            @Override
            public void onPostExecute() {
                if (activity.isFinishing() || activity.isDestroyed()) return;
                loadFragments(mCommand, position, activity);
            }
        };
    }

    private void loadFragments(String command, int position, Activity activity) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        String tagFiles = "FILES_FRAGMENT";
        String tagShell = "SHELL_FRAGMENT";
        Fragment filesFragment = fragmentManager.findFragmentByTag(tagFiles);
        Fragment shellFragment = fragmentManager.findFragmentByTag(tagShell);

        if (filesFragment != null) transaction.hide(filesFragment);
        if (shellFragment != null) transaction.hide(shellFragment);

        if (position == 1) {
            if (filesFragment == null) {
                filesFragment = new FilesFragment();
                transaction.add(R.id.fragment_container, filesFragment, tagFiles);
            } else {
                transaction.show(filesFragment);
            }
        } else {
            if (shellFragment == null) {
                shellFragment = new aShellFragment();
                if (command != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("command", command);
                    shellFragment.setArguments(bundle);
                }
                transaction.add(R.id.fragment_container, shellFragment, tagShell);
            } else {
                if (command != null && shellFragment instanceof aShellFragment) {
                    ((aShellFragment) shellFragment).updateCommand(command);
                }
                transaction.show(shellFragment);
            }
        }

        updateIcons(position, activity);
        FRAGMENT_POSITION = position;

        transaction.commitAllowingStateLoss();
    }

    public void navigateToDefaultFragment() {
        loadUI(0).execute();
    }

    private void updateIcons(int position, Activity activity) {
        ColorStateList iconTint = settings.getForegroundTintList();
        int iconTintSelected = DynamicColors.isDynamicColorAvailable()
                ? MaterialColors.getColor(DynamicColors.wrapContextIfAvailable(
                        activity, com.google.android.material.R.style.Theme_Material3_DynamicColors_DayNight), androidx.appcompat.R.attr.colorPrimary,
                Utils.getColor(R.color.colorBlue, activity))
                : androidx.core.content.ContextCompat.getColor(activity, R.color.colorBlue);
        if (position == 2) {
            shell.setChecked(false);
            files.setChecked(false);
            shell.setIconTint(iconTint);
            files.setIconTint(iconTint);
        } else if (position == 1) {
            shell.setChecked(false);
            shell.setIconTint(iconTint);
            files.setIconTint(ColorStateList.valueOf(iconTintSelected));
            files.setChecked(true);
        } else {
            shell.setChecked(true);
            shell.setIconTint(ColorStateList.valueOf(iconTintSelected));
            files.setIconTint(iconTint);
            files.setChecked(false);
        }
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    loadFragments(data.getStringExtra("command"), 0, this);
                }
            }
    );

}