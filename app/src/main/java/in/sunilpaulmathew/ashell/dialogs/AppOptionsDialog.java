package in.sunilpaulmathew.ashell.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.adapters.FileOptionsAdapter;
import in.sunilpaulmathew.ashell.serializable.MenuEntry;
import in.sunilpaulmathew.ashell.serializable.PackageEntry;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 25, 2026
 */
public abstract class AppOptionsDialog extends BottomSheetDialog {

    public AppOptionsDialog(List<MenuEntry> menuEntries, PackageEntry packageEntry, Context context) {
        super(context);

        View rootView = View.inflate(context, R.layout.layout_app_options, null);
        AppCompatImageButton icon = rootView.findViewById(R.id.icon);
        MaterialButton cancel = rootView.findViewById(R.id.cancel);
        MaterialTextView title = rootView.findViewById(R.id.title);
        MaterialTextView description = rootView.findViewById(R.id.description);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);

        title.setText(packageEntry.getAppLabel());
        description.setText(packageEntry.getPackageName());
        icon.setImageDrawable(packageEntry.getAppIcon());

        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(new FileOptionsAdapter(menuEntries, id -> {
            onItemSelected(id);
            dismiss();
        }));

        cancel.setOnClickListener(v -> dismiss());
        setOnDismissListener(DialogInterface::dismiss);
        setContentView(rootView);
        show();
    }

    public abstract void onItemSelected(int menuID);

}