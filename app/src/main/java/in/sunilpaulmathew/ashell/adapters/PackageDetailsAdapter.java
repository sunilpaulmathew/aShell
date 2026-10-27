package in.sunilpaulmathew.ashell.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.io.File;
import java.util.List;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.serializable.PackageDetailsEntry;
import in.sunilpaulmathew.ashell.utils.Settings;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 18, 2026
 */
public class PackageDetailsAdapter extends RecyclerView.Adapter<PackageDetailsAdapter.ViewHolder> {

    private final List<PackageDetailsEntry> data;
    private final OnItemClickListener clickListener;

    public PackageDetailsAdapter(List<PackageDetailsEntry> data, OnItemClickListener clickListener) {
        this.data = data;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_package_details, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        this.data.get(position).load(holder.title, holder.text);
        Settings.setSlideInAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialTextView title, text;

        public ViewHolder(View view) {
            super(view);
            this.title = view.findViewById(R.id.title);
            this.text = view.findViewById(R.id.text);

            view.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                List<File> splits = data.get(position).getSplits();
                if (position != RecyclerView.NO_POSITION && splits != null) {
                    clickListener.onItemClick(splits);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(List<File> splits);
    }

}