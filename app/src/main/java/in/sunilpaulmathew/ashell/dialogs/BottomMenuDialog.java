package in.sunilpaulmathew.ashell.dialogs;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
import in.sunilpaulmathew.ashell.adapters.BottomMenuAdapter;
import in.sunilpaulmathew.ashell.serializable.MenuEntry;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on June 24, 2026
 */
public abstract class BottomMenuDialog extends BottomSheetDialog {

    public BottomMenuDialog(List<MenuEntry> menuEntries, Drawable headerIcon, String headerTitle, String headerDescription, Context context) {
        super(context);

        View rootView = View.inflate(context, R.layout.layout_bottom_menu, null);
        AppCompatImageButton icon = rootView.findViewById(R.id.icon);
        MaterialButton cancel = rootView.findViewById(R.id.cancel);
        MaterialTextView title = rootView.findViewById(R.id.title);
        MaterialTextView description = rootView.findViewById(R.id.description);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);

        title.setText(headerTitle);
        if (headerDescription != null) {
            description.setText(headerDescription);
            description.setVisibility(VISIBLE);
        } else {
            description.setVisibility(GONE);
        }
        icon.setImageDrawable(headerIcon);

        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(new BottomMenuAdapter(menuEntries, id -> {
            onMenuItemClicked(id);
            dismiss();
        }));

        cancel.setOnClickListener(v -> dismiss());
        setContentView(rootView);
        show();
    }

    public abstract void onMenuItemClicked(int menuID);

}