package in.sunilpaulmathew.ashell.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import in.sunilpaulmathew.ashell.BuildConfig;
import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.adapters.SettingsAdapter;
import in.sunilpaulmathew.ashell.serializables.SettingsEntry;
import in.sunilpaulmathew.ashell.utils.Async;
import in.sunilpaulmathew.ashell.utils.Bookmarks;
import in.sunilpaulmathew.ashell.utils.Settings;
import in.sunilpaulmathew.ashell.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on July 27, 2026
 */
public class SettingsActivity extends BaseActivity {

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings, R.id.layout_main);

        mRecyclerView = findViewById(R.id.recycler_view);

        List<SettingsEntry> data = getData();

        int spanCount = Settings.getSpanCount(getResources().getConfiguration());

        GridLayoutManager layoutManager = new GridLayoutManager(this, spanCount);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int currentSpanCount = layoutManager.getSpanCount();
                int requestedSpan = (data.get(position).getPosition() == 0) ? currentSpanCount : 1;
                return Math.min(requestedSpan, currentSpanCount);
            }
        });

        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(new SettingsAdapter(data, restoreBookMarks, this));
    }

    private Async restoreBookMarks(Uri uri) {
        return new Async() {
            private boolean success;
            private final Activity activity = SettingsActivity.this;
            @Override
            public void onPreExecute() {
            }

            @Override
            public void doInBackground() {
                try (InputStream in = getContentResolver().openInputStream(Objects.requireNonNull(uri))) {
                    if (in == null) return;
                    String jsonStr = readStream(in);
                    JSONObject importedRoot = new JSONObject(jsonStr);

                    if (!"bookMarkedShellCommands".equals(importedRoot.optString("content")) || !importedRoot.has("bookmarks")) {
                        success = false;
                        return;
                    }

                    JSONArray importedArray = importedRoot.getJSONArray("bookmarks");
                    for (int i = 0; i < importedArray.length(); i++) {
                        JSONObject item = importedArray.optJSONObject(i);
                        if (item != null && item.has("command")) {
                            Bookmarks.addToBookmark(item.getString("command"), activity);
                        }
                    }
                    success = true;
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onPostExecute() {
                if (activity.isDestroyed() || activity.isFinishing()) return;
                Utils.toast(success ? R.string.bookmarks_restored_toast : R.string.bookmarks_invalid_backup_toast, activity).show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("bookMarksChanged", true);
                activity.setResult(Activity.RESULT_OK, resultIntent);
                activity.finish();
            }
        };
    }

    private final ActivityResultLauncher<Intent> restoreBookMarks = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent intent = result.getData();
                    Uri uri = intent.getData();

                    restoreBookMarks(uri).execute();
                }
            }
    );

    private List<SettingsEntry> getData() {
        List<SettingsEntry> mData = new ArrayList<>();
        mData.add(new SettingsEntry(getString(R.string.user_interface)));
        mData.add(new SettingsEntry(1, R.mipmap.ic_launcher, getString(R.string.app_version, BuildConfig.VERSION_NAME), getString(R.string.copyright_text)));
        mData.add(new SettingsEntry(2, R.drawable.ic_theme, getString(R.string.app_theme), Settings.getAppTheme(this)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && Settings.isDarkTheme(this)) {
            mData.add(new SettingsEntry(3, R.drawable.ic_amoled_theme, getString(R.string.app_theme_amoled), getString(R.string.app_theme_amoled_description), true, Settings.isAmoledBlackEnabled(this)));
        }
        mData.add(new SettingsEntry(4, R.drawable.ic_keyboard, getString(R.string.keyboard_behavior), getString(R.string.keyboard_behavior_description)));
        mData.add(new SettingsEntry(5, R.drawable.ic_language, getString(R.string.language), Settings.getLanguageDescription(this)));
        mData.add(new SettingsEntry(6, R.drawable.ic_extras, getString(R.string.extras), getString(R.string.extras_description)));
        mData.add(new SettingsEntry(getString(R.string.general)));
        mData.add(new SettingsEntry(7, R.drawable.ic_bookmarks, getString(R.string.bookmarks_manage), getString(R.string.bookmarks_manage_description)));
        mData.add(new SettingsEntry(8, R.drawable.ic_help, getString(R.string.examples), getString(R.string.examples_description)));
        mData.add(new SettingsEntry(9, R.drawable.ic_learn, getString(R.string.shizuku_learn), getString(R.string.shizuku_learn_description)));
        mData.add(new SettingsEntry(getString(R.string.miscellaneous)));
        mData.add(new SettingsEntry(10, R.drawable.ic_github, getString(R.string.source_code), getString(R.string.source_code_description)));
        mData.add(new SettingsEntry(11, R.drawable.ic_translate, getString(R.string.translations), getString(R.string.translations_description)));
        mData.add(new SettingsEntry(12, R.drawable.ic_privacy, getString(R.string.privacy_policy), getString(R.string.privacy_policy_description)));
        mData.add(new SettingsEntry(13, R.drawable.ic_email, getString(R.string.developer_contact), getString(R.string.developer_contact_description)));
        return mData;
    }

    private static String readStream(InputStream is) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return result.toString(StandardCharsets.UTF_8);
        } else {
            return StandardCharsets.UTF_8.name();
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration configuration) {
        super.onConfigurationChanged(configuration);

        Settings.updateSpanCount(configuration, mRecyclerView);
    }

}