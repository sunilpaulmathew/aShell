package in.sunilpaulmathew.ashell.dialogs;

import android.content.Context;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.adapters.BookmarksAdapter;
import in.sunilpaulmathew.ashell.serializable.CommandEntry;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on July 29, 2026
 */
public abstract class BookMarkDialog extends BottomSheetDialog {

    public BookMarkDialog(List<CommandEntry> data, int iconRef, String titleTxt, boolean editable, Context context) {
        super(context);

        View root = View.inflate(context, R.layout.layout_bookmarks, null);
        AppCompatImageButton icon = root.findViewById(R.id.icon);
        MaterialButton cancel = root.findViewById(R.id.cancel);
        MaterialTextView title = root.findViewById(R.id.title);
        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);

        icon.setImageResource(iconRef);
        title.setText(titleTxt);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setItemAnimator(null);
        recyclerView.setAdapter(new BookmarksAdapter(data, (command, toEdit) -> {
            onCommandSelected(command, toEdit);
            dismiss();
        }, editable));

        cancel.setOnClickListener(v -> dismiss());

        setContentView(root);
        show();
    }

    public abstract void onCommandSelected(String command, boolean toEdit);

}