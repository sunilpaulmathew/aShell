package in.sunilpaulmathew.ashell.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.fragments.aShellFragment;
import in.sunilpaulmathew.ashell.utils.Async;
import in.sunilpaulmathew.ashell.utils.Commands;
import in.sunilpaulmathew.ashell.utils.Settings;
import in.sunilpaulmathew.ashell.utils.Utils;

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
                Commands.loadPackageInfo();
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