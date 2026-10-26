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
import in.sunilpaulmathew.ashell.adapters.PermissionsAdapter;
import in.sunilpaulmathew.ashell.serializable.PackageEntry;
import in.sunilpaulmathew.ashell.serializable.PermissionsEntry;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 25, 2026
 */
public class PermissionsDialog extends BottomSheetDialog {

    public PermissionsDialog(PackageEntry packageEntry, List<PermissionsEntry> permissions, Context context) {
        super(context);

        View rootView = View.inflate(context, R.layout.layout_bottom_menu, null);
        AppCompatImageButton icon = rootView.findViewById(R.id.icon);
        MaterialButton cancel = rootView.findViewById(R.id.cancel);
        MaterialTextView title = rootView.findViewById(R.id.title);
        MaterialTextView description = rootView.findViewById(R.id.description);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);

        icon.setImageDrawable(packageEntry.getAppIcon());
        title.setText(packageEntry.getAppLabel());
        description.setText(packageEntry.getPackageName());

        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(new PermissionsAdapter(permissions, packageEntry.getPackageName()));

        cancel.setOnClickListener(v -> dismiss());
        setOnCancelListener(DialogInterface::dismiss);
        setContentView(rootView);
        show();
    }

}