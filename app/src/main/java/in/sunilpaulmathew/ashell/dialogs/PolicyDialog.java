package in.sunilpaulmathew.ashell.dialogs;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import in.sunilpaulmathew.ashell.BuildConfig;
import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.adapters.PolicyAdapter;
import in.sunilpaulmathew.ashell.utils.Settings;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on Oct. 18, 2025
 */
public class PolicyDialog extends BottomSheetDialog {

    public PolicyDialog(Context context) {
        super(context);

        View root = View.inflate(context, R.layout.layout_privacy_policy, null);
        MaterialButton cancel = root.findViewById(R.id.cancel);
        MaterialTextView version = root.findViewById(R.id.title);
        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        version.setText(context.getString(R.string.app_version, BuildConfig.VERSION_NAME));
        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new PolicyAdapter(Settings.getPolicyData()));

        cancel.setOnClickListener(v -> dismiss());

        setContentView(root);
        show();
    }

}