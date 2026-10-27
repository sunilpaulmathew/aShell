package in.sunilpaulmathew.ashell.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textview.MaterialTextView;

import java.io.File;
import java.util.List;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.adapters.SplitsAdapter;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 25, 2026
 */
public class SplitsDialog extends BottomSheetDialog {

    public SplitsDialog(String appName, Drawable appIcon, List<File> splits, Context context) {
        super(context);

        View root = View.inflate(context, R.layout.layout_file_info, null);
        AppCompatImageView image = root.findViewById(R.id.image);
        MaterialTextView title = root.findViewById(R.id.title);
        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);

        image.setImageDrawable(appIcon);
        title.setText(appName);

        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(new SplitsAdapter(splits, appIcon));

        setContentView(root);
        setOnDismissListener(DialogInterface::dismiss);
        show();
    }

}