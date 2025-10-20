package in.sunilpaulmathew.ashell.dialogs;

import android.app.Activity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on Sept. 21, 2025
 */
public class AccessUnavilableDialog extends MaterialAlertDialogBuilder {

    public AccessUnavilableDialog(Activity activity) {
        super(activity);

        setCancelable(false);
        setIcon(R.mipmap.ic_launcher);
        setTitle(activity.getString(R.string.shizuku_unavailable_title));
        setMessage(activity.getString(R.string.shizuku_unavailable_message));
        setNeutralButton(R.string.quit, (dialogInterface, i) -> activity.finish());
        setPositiveButton(R.string.shizuku_learn, (dialogInterface, i) ->
                Utils.loadUrl("https://shizuku.rikka.app/", activity)
        );
    }

}