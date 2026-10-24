package in.sunilpaulmathew.ashell.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.adapters.ChooseExtrasAdapter;
import in.sunilpaulmathew.ashell.serializable.ExtrasChooserEntry;
import in.sunilpaulmathew.ashell.serializable.ExtrasEntry;
import in.sunilpaulmathew.ashell.utils.Settings;
import in.sunilpaulmathew.ashell.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 18, 2026
 */
public class ExtrasActivity extends BaseActivity {

    private ChooseExtrasAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extras, R.id.layout_main);

        MaterialButton apply = findViewById(R.id.apply);
        MaterialButton cancel = findViewById(R.id.cancel);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        adapter = new ChooseExtrasAdapter(getData(this));

        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(new GridLayoutManager(this, getSpanSize()));
        recyclerView.setAdapter(adapter);

        apply.setOnClickListener(v -> new Thread(() -> {
            boolean extrasEnabled = Settings.isExtrasEnabled(this);
            for (ExtrasChooserEntry entry : adapter.getData()) {
                Utils.saveBoolean("extras_" + entry.getId(), entry.isEnabled(), this);
            }
            runOnUiThread(() -> {
                if (extrasEnabled) {
                    if (!Settings.isExtrasEnabled(this)) {
                        Settings.loadUI(this);
                    } else {
                        Settings.reloadExtras(this);
                    }
                } else {
                    if (!Settings.isExtrasEnabled(this)) {
                        Settings.showShell(null, this);
                    } else {
                        Settings.loadUI(this);
                        Settings.showShell(null, this);
                    }
                }
                Intent resultIntent = new Intent();
                resultIntent.putExtra("extrasChanged", true);
                setResult(RESULT_OK, resultIntent);
                exit();
            });
        }).start());

        cancel.setOnClickListener(v -> exit());

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                exit();
            }
        });
    }

    private int getSpanSize() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return 2;
        } else {
            return 1;
        }
    }

    private List<ExtrasChooserEntry> getData(Activity activity) {
        List<ExtrasChooserEntry> data = new ArrayList<>();
        data.add(new ExtrasChooserEntry(new ExtrasEntry(R.drawable.ic_shell, R.string.shell_adb, R.string.shell_adb_description, -1), true));
        for (ExtrasEntry entry : Settings.getExtras()) {
            data.add(new ExtrasChooserEntry(entry, Utils.getBoolean("extras_" + entry.getId(), false, activity)));
        }
        return data;
    }

    private void exit() {
        Utils.saveBoolean("extras_activity_viewed", true, this);
        finish();
    }

}