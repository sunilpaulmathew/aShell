package in.sunilpaulmathew.ashell.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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

    public void updateData(List<FilesEntry> newData) {
        final List<FilesEntry> oldList = new ArrayList<>(this.data);
        final List<FilesEntry> newList = (newData != null) ? new ArrayList<>(newData) : Collections.emptyList();

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldList.size();
            }

            @Override
            public int getNewListSize() {
                return newList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return Objects.equals(oldList.get(oldItemPosition).getAbsolutePath(),
                        newList.get(newItemPosition).getAbsolutePath());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return Objects.equals(oldList.get(oldItemPosition),
                        newList.get(newItemPosition));
            }
        });

        this.data.clear();
        this.data.addAll(newList);

        diffResult.dispatchUpdatesTo(this);
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