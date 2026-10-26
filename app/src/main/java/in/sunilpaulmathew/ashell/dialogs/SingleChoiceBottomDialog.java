package in.sunilpaulmathew.ashell.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.adapters.SingleChoiceAdapter;
import in.sunilpaulmathew.ashell.serializable.MenuEntry;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 25, 2026
 */
public abstract class SingleChoiceBottomDialog extends BottomSheetDialog {

    public SingleChoiceBottomDialog(List<MenuEntry> menuItems, String currentStatus, Context context) {
        super(context);

        View rootView = View.inflate(context, R.layout.layout_recycler_view, null);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);

        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(new SingleChoiceAdapter(menuItems, currentStatus, id -> {
            onItemSelected(id);
            dismiss();
        }));

        setOnCancelListener(DialogInterface::dismiss);
        setContentView(rootView);
        show();
    }

    public abstract void onItemSelected(int position);

}