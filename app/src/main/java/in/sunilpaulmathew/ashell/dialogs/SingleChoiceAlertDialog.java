package in.sunilpaulmathew.ashell.dialogs;

import android.content.Context;
import android.content.DialogInterface;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import in.sunilpaulmathew.ashell.R;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 21, 2025
 */
public abstract class SingleChoiceAlertDialog extends MaterialAlertDialogBuilder {

    private int selectedPosition;

    public SingleChoiceAlertDialog(int icon, String title, String[] singleChoiceItems,
                                   int position, Context context) {
        super(context);

        if (icon > Integer.MIN_VALUE) {
            setIcon(icon);
        }
        if (title != null) {
            setTitle(title);
        }

        setSingleChoiceItems(singleChoiceItems, position, (dialog, itemPosition) -> {
            selectedPosition = itemPosition;
        });
        setNeutralButton(R.string.cancel, (dialog, id) -> {
            dialog.dismiss();
        });
        setOnDismissListener(DialogInterface::dismiss);
        setPositiveButton(R.string.apply, (dialog, id) -> {
            onItemSelected(selectedPosition);
            dialog.dismiss();
        });
        show();
    }

    public abstract void onItemSelected(int position);
}