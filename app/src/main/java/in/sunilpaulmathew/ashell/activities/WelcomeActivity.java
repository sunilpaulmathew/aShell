package in.sunilpaulmathew.ashell.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

import in.sunilpaulmathew.ashell.BuildConfig;
import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.adapters.WelcomeAdapter;
import in.sunilpaulmathew.ashell.serializable.CommandItems;
import in.sunilpaulmathew.ashell.utils.Utils;
import rikka.shizuku.Shizuku;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 16, 2025
 */
public class WelcomeActivity extends AppCompatActivity {

    private final Shizuku.OnRequestPermissionResultListener REQUEST_PERMISSION_RESULT_LISTENER = this::onRequestPermissionsResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Shizuku.addRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER);

        MaterialButton mStart = findViewById(R.id.start);
        MaterialTextView mVersionInfo = findViewById(R.id.version_info);
        mVersionInfo.setText(getString(R.string.app_version, BuildConfig.VERSION_NAME));

        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        WelcomeAdapter mAdapter = new WelcomeAdapter(getData());
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!Shizuku.pingBinder() || Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                    mStart.setVisibility(newState == RecyclerView.SCROLL_STATE_IDLE ? View.VISIBLE : View.GONE);
                }
            }
        });

        if (Shizuku.pingBinder()) {
            if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                mStart.setIcon(Utils.getDrawable(R.drawable.ic_start, this));
                mStart.setText(R.string.start);
            } else {
                mStart.setVisibility(View.GONE);
            }
        } else {
            accessUnavailableDialog().show();
            mStart.setText(R.string.quit);
        }

        mAdapter.setOnItemClickListener(this::checkPermission);

        mStart.setOnClickListener(v-> {
            if (Shizuku.pingBinder() && Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(this, aShellActivity.class);
                startActivity(intent);
            }
            if (!Utils.getBoolean("welcome_screen_viewed", false, this)) {
                Utils.saveBoolean("welcome_screen_viewed", true, this);
            }
            finish();
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
            }
        });
    }

    private List<CommandItems> getData() {
        List<CommandItems> mData = new ArrayList<>();
        mData.add(new CommandItems(getString(R.string.features_title), getString(R.string.features_description)));
        mData.add(new CommandItems(getString(R.string.permissions_title), getString(R.string.permissions_shizuku_description) + ":" +
                (!Shizuku.pingBinder() ?  " *" + getString(R.string.shizuku_unavailable_title) + "*" : Shizuku.checkSelfPermission() ==
                        PackageManager.PERMISSION_GRANTED ? " " + getString(R.string.granted) : " " + getString(R.string.authorize_click_message)) + "\n" +
                (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ? getString(R.string.permissions_storage_description) : "")));
        mData.add(new CommandItems(getString(R.string.disclaimer), getString(R.string.disclaimer_description)));
        return mData;
    }

    private MaterialAlertDialogBuilder accessDeniedDialog() {
        return new MaterialAlertDialogBuilder(this)
                .setCancelable(false)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(getString(R.string.shizuku_access_denied_title))
                .setMessage(getString(R.string.shizuku_access_denied_message))
                .setNeutralButton(getString(R.string.quit), (dialogInterface, i) -> finish())
                .setPositiveButton(getString(R.string.request_permission), (dialogInterface, i) ->
                        Shizuku.requestPermission(0)
                );
    }

    public MaterialAlertDialogBuilder accessUnavailableDialog() {
        return new MaterialAlertDialogBuilder(this)
                .setCancelable(false)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(getString(R.string.shizuku_unavailable_title))
                .setMessage(getString(R.string.shizuku_unavailable_message))
                .setNeutralButton(getString(R.string.quit), (dialogInterface, i) -> finish())
                .setPositiveButton(getString(R.string.shizuku_learn), (dialogInterface, i) ->
                        Utils.loadUrl("https://shizuku.rikka.app/", this)
                );
    }

    private void onRequestPermissionsResult(int requestCode, int grantResult) {
        boolean granted = grantResult == PackageManager.PERMISSION_GRANTED;
        if (granted) {
            Utils.toast(getString(R.string.shizuku_access_granted_message), this).show();
            recreate();
        } else {
            accessDeniedDialog().show();
        }
        Utils.saveBoolean("welcome_screen_viewed", granted, this);
    }

    private void checkPermission() {
        if (Shizuku.isPreV11()) {
            return;
        }

        if (Shizuku.shouldShowRequestPermissionRationale()) {
            accessDeniedDialog().show();
        } else {
            Shizuku.requestPermission(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Shizuku.removeRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER);
    }

}