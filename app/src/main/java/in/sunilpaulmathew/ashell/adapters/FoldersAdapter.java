package in.sunilpaulmathew.ashell.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.serializable.FilesEntry;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on June 07, 2026
 */
public class FoldersAdapter extends RecyclerView.Adapter<FoldersAdapter.ViewHolder> {

    private final List<FilesEntry> data;
    private final OnItemClickListener clickListener;

    public FoldersAdapter(List<FilesEntry> data, OnItemClickListener clickListener) {
        this.data = data;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public FoldersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_folders, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull FoldersAdapter.ViewHolder holder, int position) {
        holder.fileName.setText(data.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final MaterialTextView fileName;
        public ViewHolder(View view) {
            super(view);
            this.fileName = view.findViewById(R.id.name);

            view.setOnClickListener(v -> clickListener.onItemClick(data.get(getBindingAdapterPosition()).getAbsolutePath()));
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String filePath);
    }

}