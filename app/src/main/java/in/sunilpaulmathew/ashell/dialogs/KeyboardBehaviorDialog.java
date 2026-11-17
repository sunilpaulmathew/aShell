package in.sunilpaulmathew.ashell.dialogs;

import android.content.Context;
import android.content.DialogInterface;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on November 17, 2026
 */
public class KeyboardBehaviorDialog extends MaterialAlertDialogBuilder {

    private boolean keyboardOnStart, keyboardOnScroll;

    public KeyboardBehaviorDialog(Context context) {
        super(context);

        keyboardOnStart = Utils.keyboardOnStart(context);
        keyboardOnScroll = Utils.keyboardOnScroll(context);

        setIcon(R.drawable.ic_keyboard);
        setTitle(R.string.keyboard_behavior_description);
        setMultiChoiceItems(new CharSequence[] {
                        context.getString(R.string.keyboard_behavior_on_start),
                        context.getString(R.string.keyboard_behavior_on_scroll)
                }, new boolean[] {
                        keyboardOnStart,
                        keyboardOnScroll
                },
                (dialog, which, isChecked) -> {
                    if (which == 1) {
                        keyboardOnScroll = isChecked;
                    } else {
                        keyboardOnStart = isChecked;
                    }
                });
        setNeutralButton(R.string.cancel, (dialog, id) -> {
        });
        setOnDismissListener(DialogInterface::dismiss);
        setPositiveButton(R.string.apply, (dialog, id) -> {
            Utils.saveBoolean("keyboardOnStart", keyboardOnStart, context);
            Utils.saveBoolean("keyboardOnScroll", keyboardOnScroll, context);
        });
        show();
    }

}