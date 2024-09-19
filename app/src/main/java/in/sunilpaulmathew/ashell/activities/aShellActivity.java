package in.sunilpaulmathew.ashell.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.fragments.aShellFragment;
import in.sunilpaulmathew.ashell.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 28, 2022
 */
public class aShellActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize App Theme
        setTheme(Utils.getBoolean("amoledTheme", false, this) ? R.style.AppTheme_Amoled : R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ashell);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new aShellFragment()).commit();
    }

}