package in.sunilpaulmathew.ashell.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.fragments.aShellFragment;
import in.sunilpaulmathew.ashell.utils.Commands;
import in.sunilpaulmathew.ashell.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 28, 2022
 */
public class aShellActivity extends AppCompatActivity {

    private String mCommand = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize App Theme
        setTheme(Utils.isAmoledBlackEnabled(this) ? R.style.AppTheme_Amoled : R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ashell);

        if (!Utils.getBoolean("welcome_screen_viewed", false, this)) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        }
        loadUI();
    }

    private void loadUI() {
        ExecutorService mExecutors = Executors.newSingleThreadExecutor();
        mExecutors.execute(() -> {
            Commands.loadPackageInfo();
            mCommand = getIntent().getStringExtra("command");
            new Handler(Looper.getMainLooper()).post(() -> {
                Bundle bundle = new Bundle();
                if (mCommand != null) {
                    bundle.putString("command", mCommand);
                }
                Fragment fragment = new aShellFragment();
                fragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        fragment).commit();
            });
            if (!mExecutors.isShutdown()) mExecutors.shutdown();
        });
    }

}