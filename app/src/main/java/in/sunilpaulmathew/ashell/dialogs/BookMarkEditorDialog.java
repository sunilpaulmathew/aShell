package in.sunilpaulmathew.ashell.dialogs;

import android.content.Context;
import android.view.View;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;

import in.sunilpaulmathew.ashell.R;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on July 29, 2026
 */
public abstract class BookMarkEditorDialog extends MaterialAlertDialogBuilder {

    public BookMarkEditorDialog(String string, Context context) {
        super(context);
        View rootView = View.inflate(context, R.layout.layout_bookmark_editor, null);
        TextInputLayout layout = rootView.findViewById(R.id.text);
        MaterialAutoCompleteTextView value = rootView.findViewById(R.id.value);

        layout.setHint(string);
        value.setText(string);

        setView(rootView);
        setIcon(R.drawable.ic_edit);
        setTitle(context.getString(R.string.edit_title, string));
        setNegativeButton(R.string.cancel, (dialog, id) -> {
        });
        setPositiveButton(R.string.apply, (dialog, id) -> {
            if (!value.getText().toString().trim().isEmpty() && !value.getText().toString().trim().equals(string)) {
                editBookMark(value.getText().toString().trim());
            }
        });

        show();
    }

    public abstract void editBookMark(String newValue);

}