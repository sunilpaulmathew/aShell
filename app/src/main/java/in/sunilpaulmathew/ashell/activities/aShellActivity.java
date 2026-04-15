package in.sunilpaulmathew.ashell.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.fragments.aShellFragment;
import in.sunilpaulmathew.ashell.utils.Async;
import in.sunilpaulmathew.ashell.utils.Commands;
import in.sunilpaulmathew.ashell.utils.Settings;
import in.sunilpaulmathew.ashell.utils.ShizukuShell;
import in.sunilpaulmathew.ashell.utils.Utils;
import rikka.shizuku.Shizuku;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 28, 2022
 */
public class aShellActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize App Theme & language
        Settings.initializeAppTheme(this);
        Settings.initializeAppLanguage(this);

        setContentView(R.layout.activity_ashell);

        View root = findViewById(R.id.fragment_container);

        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            view.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
            );

            return insets;
        });

        if (!Utils.getBoolean("welcome_screen_viewed", false, this)) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        }

        loadUI().execute();
    }

    private Async loadUI() {
        return new Async() {
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
                Bundle bundle = new Bundle();
                if (mCommand != null) {
                    bundle.putString("command", mCommand);
                }
                Fragment fragment = new aShellFragment();
                fragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        fragment).commitAllowingStateLoss();
            }
        };
    }

}