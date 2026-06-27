package in.sunilpaulmathew.ashell.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on June 07, 2026
 */
public class FoldersAdapter extends RecyclerView.Adapter<FoldersAdapter.ViewHolder> {

    private final List<String> data;
    private final String parentPath;
    private static ClickListener mClickListener;

    public FoldersAdapter(List<String> data, String parentPath) {
        this.data = data;
        this.parentPath = parentPath;
    }

    @NonNull
    @Override
    public FoldersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_folders, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull FoldersAdapter.ViewHolder holder, int position) {
        holder.fileName.setText(data.get(position));
        holder.menuButton.setOnClickListener(v -> {
            Utils.toast("Coming soon...", v.getContext()).show();
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final AppCompatImageButton menuButton;
        private final MaterialTextView fileName;
        public ViewHolder(View view) {
            super(view);
            this.menuButton = view.findViewById(R.id.menu);
            this.fileName = view.findViewById(R.id.name);


            view.setOnClickListener(v -> {
                mClickListener.onItemClick(parentPath + "/" + data.get(getBindingAdapterPosition()));
            });
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        FoldersAdapter.mClickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(String string);
    }

}