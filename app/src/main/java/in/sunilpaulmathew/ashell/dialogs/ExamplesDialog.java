package in.sunilpaulmathew.ashell.dialogs;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.adapters.ExamplesAdapter;
import in.sunilpaulmathew.ashell.utils.Commands;
import in.sunilpaulmathew.ashell.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on November 05, 2022
 */
public abstract class ExamplesDialog extends BottomSheetDialog {

    public ExamplesDialog(Context context) {
        super(context);

        View root = View.inflate(context, R.layout.layout_examples, null);
        MaterialButton mCancel = root.findViewById(R.id.cancel);
        MaterialAutoCompleteTextView mSearchWord = root.findViewById(R.id.search_word);
        RecyclerView mRecyclerView = root.findViewById(R.id.recycler_view);

        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setLayoutManager(new GridLayoutManager(context, Utils.isPortraitMode(context) ? 1 : Utils.isTablet(context) ? 3 : 2));
        mRecyclerView.setAdapter(new ExamplesAdapter(Commands.getCommand(""), command -> {
            if (command != null) {
                onCommandSelected(command);
                dismiss();
            }
        }));

        mRecyclerView.setVisibility(View.VISIBLE);

        mSearchWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mRecyclerView.setAdapter(new ExamplesAdapter(Commands.getCommand(s.toString().trim()), command -> {
                    if (command != null) {
                        onCommandSelected(command);
                        dismiss();
                    }
                }));
            }
        });

        mCancel.setOnClickListener(v -> dismiss());

        setContentView(root);
        show();
    }

    public abstract void onCommandSelected(String command);

}