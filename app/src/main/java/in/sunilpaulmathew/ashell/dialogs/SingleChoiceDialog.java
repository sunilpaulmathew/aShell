package in.sunilpaulmathew.ashell.dialogs;

import android.content.Context;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 21, 2025
 */
public abstract class SingleChoiceDialog {

    private final int mIcon;
    private final int mPosition;
    private final MaterialAlertDialogBuilder mDialogBuilder;
    private final String mTitle;
    private final String[] mSingleChoiceItems;

    public SingleChoiceDialog(int icon, String title, String[] singleChoiceItems,
                              int position, Context context) {
        this.mIcon = icon;
        this.mTitle = title;
        this.mSingleChoiceItems = singleChoiceItems;
        this.mPosition = position;
        this.mDialogBuilder = new MaterialAlertDialogBuilder(context);
    }

    private void startDialog() {
        if (mIcon > Integer.MIN_VALUE) {
            mDialogBuilder.setIcon(mIcon);
        }
        if (mTitle != null) {
            mDialogBuilder.setTitle(mTitle);
        }
        mDialogBuilder.setSingleChoiceItems(mSingleChoiceItems, mPosition, (dialog, itemPosition) -> {
            onItemSelected(itemPosition);
            dialog.dismiss();
        }).show();
    }

    public void show() {
        startDialog();
    }

    public abstract void onItemSelected(int position);
}