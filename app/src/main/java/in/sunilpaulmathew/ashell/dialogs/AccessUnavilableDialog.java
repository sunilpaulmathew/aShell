package in.sunilpaulmathew.ashell.dialogs;

import android.app.Activity;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on Sept. 21, 2025
 */
public class AccessUnavilableDialog extends BottomSheetDialog {

    public AccessUnavilableDialog(Activity activity) {
        super(activity);

        View rootView = View.inflate(activity, R.layout.layout_dialog_shizuku_access, null);

        MaterialButton quit = rootView.findViewById(R.id.quit);
        MaterialButton action = rootView.findViewById(R.id.action);

        MaterialTextView title = rootView.findViewById(R.id.title);
        MaterialTextView text = rootView.findViewById(R.id.text);

        action.setText(R.string.shizuku_learn);
        action.setIconResource(R.drawable.ic_learn);
        title.setText(R.string.shizuku_unavailable_title);
        text.setText(R.string.shizuku_unavailable_message);

        setContentView(rootView);
        setCancelable(false);

        quit.setOnClickListener(v -> {
            activity.finish();
            dismiss();
        });

        action.setOnClickListener(v -> {
            Utils.loadUrl("https://shizuku.rikka.app/", activity);
            dismiss();
        });

        show();
    }

}