package in.sunilpaulmathew.ashell.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import in.sunilpaulmathew.ashell.R;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on Sept. 21, 2025
 */
public class AccessDeniedDialog extends MaterialAlertDialogBuilder {

    public AccessDeniedDialog(Activity activity) {
        super(activity);

        setCancelable(false);
        setIcon(R.mipmap.ic_launcher);
        setTitle(activity.getString(R.string.shizuku_access_denied_title));
        setMessage(activity.getString(R.string.shizuku_access_denied_message) + "\n\n" + activity.getString(R.string.shizuku_app_open_message));
        setNeutralButton(R.string.quit, (dialogInterface, i) -> activity.finish());
        setPositiveButton(R.string.shizuku_open, (dialogInterface, i) -> {
            PackageManager pm = activity.getPackageManager();
            Intent launchIntent = pm.getLaunchIntentForPackage("moe.shizuku.privileged.api");
            activity.startActivity(launchIntent);
        });
    }

}