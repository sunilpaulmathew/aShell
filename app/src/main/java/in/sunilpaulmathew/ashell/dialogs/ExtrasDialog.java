package in.sunilpaulmathew.ashell.dialogs;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.adapters.ChooseExtrasAdapter;
import in.sunilpaulmathew.ashell.serializable.ExtrasChooserEntry;
import in.sunilpaulmathew.ashell.serializable.ExtrasEntry;
import in.sunilpaulmathew.ashell.utils.Settings;
import in.sunilpaulmathew.ashell.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 18, 2026
 */
public abstract class ExtrasDialog extends BottomSheetDialog {

    private final ChooseExtrasAdapter adapter;

    public ExtrasDialog(Activity activity) {
        super(activity);

        View root = View.inflate(activity, R.layout.layout_extras, null);
        MaterialButton apply = root.findViewById(R.id.apply);
        MaterialButton cancel = root.findViewById(R.id.cancel);
        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);

        adapter = new ChooseExtrasAdapter(getData(activity));

        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(adapter);

        setContentView(root);
        setOnCancelListener(DialogInterface::dismiss);
        show();

        apply.setOnClickListener(v -> {
            onApplied(adapter.getData());
            dismiss();
        });

        cancel.setOnClickListener(v -> dismiss());
    }

    private List<ExtrasChooserEntry> getData(Activity activity) {
        List<ExtrasChooserEntry> data = new ArrayList<>();
        data.add(new ExtrasChooserEntry(new ExtrasEntry(R.drawable.ic_shell, R.string.shell_adb, R.string.shell_adb_description, -1), true));
        for (ExtrasEntry entry : Settings.getExtras()) {
            data.add(new ExtrasChooserEntry(entry, Utils.getBoolean("extras_" + entry.getId(), false, activity)));
        }
        return data;
    }

    public abstract void onApplied(List<ExtrasChooserEntry> entries);

}