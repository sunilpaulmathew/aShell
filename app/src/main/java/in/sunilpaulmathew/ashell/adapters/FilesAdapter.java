package in.sunilpaulmathew.ashell.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.serializable.FilesEntry;
import in.sunilpaulmathew.ashell.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on June 07, 2026
 */
public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.ViewHolder> {

    private final List<FilesEntry> data;
    private static ClickListener mClickListener;

    public FilesAdapter(List<FilesEntry> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public FilesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_files, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull FilesAdapter.ViewHolder holder, int position) {
        FilesEntry item = data.get(position);

        item.loadFileInfo(holder.imageIcon,
                holder.fileName,
                holder.fileSize);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final AppCompatImageView imageIcon;
        private final MaterialTextView fileName, fileSize;
        public ViewHolder(View view) {
            super(view);
            this.imageIcon = view.findViewById(R.id.image);
            this.fileName = view.findViewById(R.id.name);
            this.fileSize = view.findViewById(R.id.size);


            view.setOnClickListener(v -> {
                FilesEntry file = data.get(getBindingAdapterPosition());
                if (file.isSymbolicLink()) {
                    mClickListener.onItemClick(file.decodeSymLink());
                } else {
                    Utils.toast("Coming soon...", v.getContext()).show();
                }
            });
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        FilesAdapter.mClickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(String string);
    }

}