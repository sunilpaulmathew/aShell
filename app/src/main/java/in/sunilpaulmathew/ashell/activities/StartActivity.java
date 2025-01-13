/*
 * Copyright (C) 2021-2022 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of Package Manager, a simple, yet powerful application
 * to manage other application installed on an android device.
 *
 */

package in.sunilpaulmathew.ashell.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.utils.Commands;
import in.sunilpaulmathew.ashell.utils.Utils;
import rikka.shizuku.Shizuku;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 28, 2022
 */
public class StartActivity extends AppCompatActivity {

    private String mCommand = null;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(in.sunilpaulmathew.ashell.R.layout.activity_start);

        LinearLayoutCompat mMainLayout = findViewById(R.id.layout_main);
        MaterialButton mStartButton = findViewById(R.id.start_button);
        MaterialTextView mAboutText = findViewById(R.id.about_text);

        if (Shizuku.pingBinder()) {
            if (Utils.getBoolean("firstLaunch", true, this)) {
                Shizuku.requestPermission(0);
                mMainLayout.setVisibility(View.VISIBLE);
                mAboutText.setText(getString(R.string.app_summary));
            } else {
                loadUI();
            }
        } else {
            mMainLayout.setVisibility(View.VISIBLE);
            mAboutText.setText(getString(R.string.shizuku_unavailable_message));
            mAboutText.setTextColor(Color.RED);
            mStartButton.setVisibility(View.GONE);
        }

        mStartButton.setOnClickListener(v -> {
            Utils.saveBoolean("firstLaunch", false, this);
            loadUI();
        });

        mAboutText.setOnClickListener(v -> Utils.loadShizukuWeb(this));
    }

    private void loadUI() {
        ExecutorService mExecutors = Executors.newSingleThreadExecutor();
        mExecutors.execute(() -> {
            Commands.loadPackageInfo();
            mCommand = getIntent().getStringExtra("command");
            new Handler(Looper.getMainLooper()).post(() -> {
                Intent aShellActivity = new Intent(this, aShellActivity.class);
                if (mCommand != null) {
                    aShellActivity.putExtra("command", mCommand);
                }
                startActivity(aShellActivity);
                finish();
            });
            if (!mExecutors.isShutdown()) mExecutors.shutdown();
        });
    }

}