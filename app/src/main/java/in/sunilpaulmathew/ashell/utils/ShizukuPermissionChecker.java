package in.sunilpaulmathew.ashell.utils;

import android.app.Activity;
import android.content.pm.PackageManager;

import in.sunilpaulmathew.ashell.dialogs.AccessDeniedDialog;
import rikka.shizuku.Shizuku;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on Oct. 18, 2025
 */
public abstract class ShizukuPermissionChecker {

    public ShizukuPermissionChecker(Activity activity) {
        if (Shizuku.isPreV11()) {
            Utils.toast("Pre-v11 is unsupported", activity).show();
            return;
        }

        if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            ShizukuShell.ensureUserService(this::onFinished);
        } else if (Shizuku.shouldShowRequestPermissionRationale()) {
            new AccessDeniedDialog(activity).show();
        } else {
            onRequestingPermission();
        }
    }

    public abstract void onRequestingPermission();

    public abstract void onFinished();

}