package in.sunilpaulmathew.ashell.dialogs;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.utils.Settings;
import in.sunilpaulmathew.ashell.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on Oct. 18, 2025
 */
public class ExamplesDialog extends MaterialAlertDialogBuilder {

    public ExamplesDialog(String title, String example, Context context) {
        super(context);

        LinearLayoutCompat layout = new LinearLayoutCompat(context);
        layout.setPadding(15, 15, 15, 15);
        final MaterialButton button = new MaterialButton(context);
        button.setGravity(Gravity.START);
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        button.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_END);
        button.setBackgroundColor(Color.TRANSPARENT);
        button.setIconTint(ColorStateList.valueOf(Settings.getColorAccent(context)));
        button.setTextColor(Settings.getColorAccent(context));
        button.setIcon(Utils.getDrawable(R.drawable.ic_copy, context));
        button.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        button.setText(example);
        layout.addView(button);
        button.setOnClickListener(v -> Utils.copyToClipboard(example, context));

        setIcon(R.mipmap.ic_launcher);
        setTitle(title);
        setView(layout);
        setPositiveButton(R.string.cancel, (dialogInterface, i) -> {
        });
        show();
    }

}