package in.sunilpaulmathew.ashell.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import in.sunilpaulmathew.ashell.R;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on Sept. 21, 2025
 */
public class AccessDeniedDialog extends BottomSheetDialog {

    public AccessDeniedDialog(Activity activity) {
        super(activity);

        View rootView = View.inflate(activity, R.layout.layout_dialog_shizuku_access, null);

        MaterialButton quit = rootView.findViewById(R.id.quit);
        MaterialButton action = rootView.findViewById(R.id.action);
        MaterialTextView title = rootView.findViewById(R.id.title);
        MaterialTextView text = rootView.findViewById(R.id.text);

        String titleText = activity.getString(R.string.shizuku_access_denied_message) + "\n\n" + activity.getString(R.string.shizuku_app_open_message);
        title.setText(R.string.shizuku_access_denied_title);
        action.setIconResource(R.drawable.ic_open);
        action.setText(R.string.shizuku_open);
        text.setText(titleText);

        setContentView(rootView);
        setCancelable(false);

        quit.setOnClickListener(v -> {
            activity.finish();
            dismiss();
        });

        action.setOnClickListener(v -> {
            PackageManager pm = activity.getPackageManager();
            Intent launchIntent = pm.getLaunchIntentForPackage("moe.shizuku.privileged.api");
            activity.startActivity(launchIntent);
            dismiss();
        });

        show();
    }

}