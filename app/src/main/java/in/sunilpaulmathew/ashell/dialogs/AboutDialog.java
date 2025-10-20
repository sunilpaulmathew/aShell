package in.sunilpaulmathew.ashell.dialogs;

import android.content.Context;
import android.view.View;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import in.sunilpaulmathew.ashell.BuildConfig;
import in.sunilpaulmathew.ashell.R;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on Oct. 18, 2025
 */
public class AboutDialog extends MaterialAlertDialogBuilder {

    public AboutDialog(Context context) {
        super(context);

        View root = View.inflate(context, R.layout.layout_about, null);
        MaterialTextView mAppTile = root.findViewById(R.id.title);
        String titleText = context.getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME;
        mAppTile.setText(titleText);

        setView(root);
        show();
    }

}