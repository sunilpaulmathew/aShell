package in.sunilpaulmathew.ashell.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.adapters.PackageDetailsAdapter;
import in.sunilpaulmathew.ashell.adapters.PackageHeaderAdapter;
import in.sunilpaulmathew.ashell.serializable.PackageDetailsEntry;
import in.sunilpaulmathew.ashell.serializable.PackageHeaderEntry;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 18, 2026
 */
public class APKDetailsDialog extends BottomSheetDialog {

    public APKDetailsDialog(Drawable appIcon, String appName, String packageName, List<PackageHeaderEntry> headers,
                            List<PackageDetailsEntry> details, Context context) {
        super(context);

        View rootView = View.inflate(context, R.layout.layout_apk_details, null);
        AppCompatImageButton app_Icon = rootView.findViewById(R.id.app_icon);
        MaterialButton cancel_Button = rootView.findViewById(R.id.cancel);
        MaterialTextView app_Name = rootView.findViewById(R.id.app_name);
        MaterialTextView package_Name = rootView.findViewById(R.id.package_name);
        RecyclerView recyclerView_Header = rootView.findViewById(R.id.recycler_view_header);
        RecyclerView recyclerView_Details = rootView.findViewById(R.id.recycler_view_details);

        recyclerView_Header.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recyclerView_Header.setAdapter(new PackageHeaderAdapter(headers));

        recyclerView_Details.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView_Details.setAdapter(new PackageDetailsAdapter(details, splits -> {
            new SplitsDialog(appName, appIcon, splits, context);
            dismiss();
        }));

        app_Name.setText(appName);
        package_Name.setText(packageName);
        app_Icon.setImageDrawable(appIcon);

        cancel_Button.setOnClickListener(v -> dismiss());

        setContentView(rootView);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        setOnCancelListener(DialogInterface::dismiss);
        setOnCancelListener(dialog -> dismiss());
        show();
    }

}