package in.sunilpaulmathew.ashell.serializable;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.utils.Settings;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 18, 2026
 */
public class PackageEntry implements Serializable {

    private CharSequence appLabel;
    private Drawable appIcon;
    private final String appType;
    private final String apkPath, packageName;
    private int compileSDK = -1, minSDK = -1, targetSDK = -1;
    private String versionName, versionCode = null;

    public PackageEntry(String packageName, String apkPath, String appType) {
        this.packageName = packageName;
        this.apkPath = apkPath;
        this.appType = appType;
    }

    public boolean isDisabled() {
        return this.appType.contains("DISABLED USER APPS") || this.appType.contains("DISABLED SYSTEM APPS");
    }

    public boolean isAppBundle() {
        return splitAPKs() != null && splitAPKs().size() > 1;
    }

    public boolean isSystemApp() {
        return this.appType.contains("ENABLED SYSTEM APPS") || this.appType.contains("DISABLED SYSTEM APPS");
    }

    public Drawable getAppIcon() {
        return this.appIcon;
    }

    private int getAppType() {
        if (isDisabled()) {
            return 3;
        } else if (isSystemApp() && !isDisabled()) {
            return 2;
        } else if (!isSystemApp() && !isDisabled()) {
            return 1;
        } else {
            return 0;
        }
    }

    public int getBackgroundColor(int category, Context context) {
        boolean dark = Settings.isDarkTheme(context);
        String hexColor;

        switch (category) {
            case 2:
                hexColor = dark ? "#064E3B" : "#ECFDF5";
                break;
            case 1:
                hexColor = dark ? "#1F2937" : "#F3F4F6";
                break;
            default:
                hexColor = dark ? "#1E3A8A" : "#EFF6FF";
                break;
        }
        return Color.parseColor(hexColor);
    }

    public int getCompileSDK() {
        return this.compileSDK;
    }

    public int getMinSDK() {
        return this.minSDK;
    }

    public int getTargetSDK() {
        return this.targetSDK;
    }

    public List<File> splitAPKs() {
        File parentFile = new File(apkPath).getParentFile();
        if (parentFile != null) {
            List<File> apkFiles = new ArrayList<>();
            for (File file : Objects.requireNonNull(parentFile.listFiles())) {
                if (file.getName().endsWith(".apk")) {
                    apkFiles.add(file);
                }
            }
            return apkFiles;
        }
        return null;
    }

    public String getApkPath() {
        return this.apkPath;
    }

    public String getAppLabel() {
        return this.appLabel.toString();
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getVersionCode() {
        return this.versionCode;
    }

    public String getVersionName() {
        return this.versionName;
    }

    public void load(CardView card, ImageButton icon, TextView title, TextView description) {
        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                PackageManager pm = icon.getContext().getPackageManager();
                PackageInfo pi = pm.getPackageArchiveInfo(apkPath, 0);
                if (pi != null) {
                    ApplicationInfo ai = pi.applicationInfo;
                    Objects.requireNonNull(ai).sourceDir = apkPath;
                    ai.publicSourceDir = apkPath;

                    this.appLabel = pm.getApplicationLabel(ai);
                    this.appIcon = pm.getApplicationIcon(ai);
                    this.versionName = pi.versionName;

                    try {
                        minSDK = ai.minSdkVersion;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            compileSDK = ai.compileSdkVersion;
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            this.versionCode = String.valueOf(pi.getLongVersionCode());
                        }
                        targetSDK = ai.targetSdkVersion;
                    } catch (NumberFormatException ignored) {
                    }
                }

                handler.post(() -> {
                    if (appLabel != null) {
                        title.setText(this.appLabel);
                        title.setVisibility(VISIBLE);
                    } else {
                        title.setVisibility(GONE);
                    }

                    if (description != null) {
                        description.setText(this.packageName);
                    }

                    if (this.appIcon != null) {
                        icon.setImageDrawable(this.appIcon);
                    } else {
                        icon.setImageResource(R.drawable.ic_apks);
                    }

                    card.setCardBackgroundColor(getBackgroundColor(getAppType(), card.getContext()));
                });
            });
        }
    }

}