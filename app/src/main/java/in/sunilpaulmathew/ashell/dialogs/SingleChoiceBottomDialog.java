package in.sunilpaulmathew.ashell.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.adapters.SingleChoiceAdapter;
import in.sunilpaulmathew.ashell.serializables.MenuEntry;
import in.sunilpaulmathew.ashell.utils.Settings;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 25, 2026
 */
public abstract class SingleChoiceBottomDialog extends BottomSheetDialog {

    public SingleChoiceBottomDialog(List<MenuEntry> menuItems, String currentStatus, Context context) {
        super(context);

        View rootView = View.inflate(context, R.layout.layout_recycler_view, null);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);

        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(new GridLayoutManager(context, Settings.getSpanCount(context.getResources().getConfiguration())));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(new SingleChoiceAdapter(menuItems, currentStatus, id -> {
            onItemSelected(id);
            dismiss();
        }));

        rootView.post(() -> {
            FrameLayout bottomSheet = findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;

                BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setSkipCollapsed(true);
                behavior.setFitToContents(true);
            }
        });

        setOnDismissListener(DialogInterface::dismiss);
        setContentView(rootView);
        show();
    }

    public abstract void onItemSelected(int position);

}