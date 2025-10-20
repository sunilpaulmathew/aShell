package in.sunilpaulmathew.ashell.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

import in.sunilpaulmathew.ashell.BuildConfig;
import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.adapters.SettingsAdapter;
import in.sunilpaulmathew.ashell.serializable.SettingsItems;
import in.sunilpaulmathew.ashell.utils.Settings;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 21, 2022
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        LinearLayoutCompat mTitleLayout = findViewById(R.id.title_layout);
        MaterialTextView mAppInfo = findViewById(R.id.app_info);
        MaterialTextView mCopyright = findViewById(R.id.copyright);
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);

        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAppInfo.setText(getString(R.string.app_version, BuildConfig.VERSION_NAME));
        mCopyright.setText(getString(R.string.copyright_text));

        mRecyclerView.setAdapter(new SettingsAdapter(getData(), this));

        mTitleLayout.setOnClickListener(v -> {
            Intent settings = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            settings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
            settings.setData(uri);
            startActivity(settings);
        });
    }

    private List<SettingsItems> getData() {
        List<SettingsItems> mData = new ArrayList<>();
        mData.add(new SettingsItems(getString(R.string.user_interface)));
        mData.add(new SettingsItems(1, R.drawable.ic_theme, getString(R.string.app_theme), Settings.getAppTheme(this)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && Settings.isDarkTheme(this)) {
            mData.add(new SettingsItems(2, R.drawable.ic_amoled_theme, getString(R.string.app_theme_amoled), getString(R.string.app_theme_amoled_description), true, Settings.isAmoledBlackEnabled(this)));
        }
        mData.add(new SettingsItems(3, R.drawable.ic_language, getString(R.string.language), Settings.getLanguageDescription(this)));
        mData.add(new SettingsItems(getString(R.string.general)));
        mData.add(new SettingsItems(4, R.drawable.ic_help, getString(R.string.examples), getString(R.string.examples_description)));
        mData.add(new SettingsItems(5, R.drawable.ic_learn, getString(R.string.shizuku_learn), getString(R.string.shizuku_learn_description)));
        mData.add(new SettingsItems(getString(R.string.miscellaneous)));
        mData.add(new SettingsItems(6, R.drawable.ic_translate, getString(R.string.translations), getString(R.string.translations_description)));
        mData.add(new SettingsItems(7, R.drawable.ic_privacy, getString(R.string.privacy_policy), getString(R.string.privacy_policy_description)));
        mData.add(new SettingsItems(8, R.drawable.ic_email, getString(R.string.developer_contact), getString(R.string.developer_contact_description)));
        return mData;
    }

}