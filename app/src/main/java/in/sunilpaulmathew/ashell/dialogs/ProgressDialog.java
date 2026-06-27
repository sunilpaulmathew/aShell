package in.sunilpaulmathew.ashell.dialogs;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.widget.ContentLoadingProgressBar;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on May 88, 2026
 */
public class ProgressDialog extends MaterialAlertDialogBuilder {

    private final AlertDialog mAlertDialog;
    private final AppCompatImageButton mIcon;
    private final ContentLoadingProgressBar mProgressBar;
    private final MaterialTextView mTitle;

    public ProgressDialog(Context context) {
        super(context);

        View progressLayout = View.inflate(context, R.layout.layout_progress, null);
        mProgressBar = progressLayout.findViewById(R.id.progress);
        mIcon = progressLayout.findViewById(R.id.icon);
        mTitle = progressLayout.findViewById(R.id.title);

        mAlertDialog = new MaterialAlertDialogBuilder(context)
                .setView(progressLayout)
                .setCancelable(false)
                .create();
    }

    public void setProgressIcon(Drawable drawable) {
        mIcon.setImageDrawable(drawable);
    }

    public void setProgressIcon(int resource) {
        mIcon.setImageDrawable(Utils.getDrawable(resource, mIcon.getContext()));
    }

    public void setProgressStatus(int resource) {
        mTitle.setText(mTitle.getContext().getString(resource));
    }

    public void setProgressStatus(String title) {
        mTitle.setText(title);
    }

    public void startDialog() {
        if (!mAlertDialog.isShowing()) {
            mAlertDialog.show();
        }
    }

    public void dismissDialog() {
        if (mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }

    public void updateProgress(int progress) {
        mProgressBar.setProgress(mProgressBar.getProgress() + progress);
    }

    public void setProgressMax(int max) {
        mProgressBar.setIndeterminate(false);
        mProgressBar.setMax(max);
    }

}