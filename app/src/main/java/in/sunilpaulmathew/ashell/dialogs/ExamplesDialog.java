package in.sunilpaulmathew.ashell.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.adapters.ExamplesAdapter;
import in.sunilpaulmathew.ashell.utils.Commands;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on November 05, 2022
 */
public abstract class ExamplesDialog extends BottomSheetDialog {

    public ExamplesDialog(boolean settings, Activity activity) {
        super(activity);

        View root = View.inflate(activity, R.layout.layout_examples, null);
        MaterialButton mCancel = root.findViewById(R.id.cancel);
        MaterialAutoCompleteTextView mSearchWord = root.findViewById(R.id.search_word);
        RecyclerView mRecyclerView = root.findViewById(R.id.recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        GridLayoutManager mLayoutManager = new GridLayoutManager(activity, activity.getResources().getConfiguration()
                .orientation == Configuration.ORIENTATION_LANDSCAPE ? 2 : 1);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(new ExamplesAdapter(Commands.getCommand(""), command -> {
            if (command != null) {
                if (settings) {
                    Intent intent = new Intent();
                    intent.putExtra("command", command);
                    activity.setResult(Activity.RESULT_OK, intent);
                    activity.finish();
                } else {
                    onCommandSelected(command);
                }
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
                        if (settings) {
                            Intent intent = new Intent();
                            intent.putExtra("command", command);
                            activity.setResult(Activity.RESULT_OK, intent);
                            activity.finish();
                        } else {
                            onCommandSelected(command);
                        }
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