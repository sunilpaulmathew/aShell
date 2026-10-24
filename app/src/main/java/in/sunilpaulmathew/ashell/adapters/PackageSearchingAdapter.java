package in.sunilpaulmathew.ashell.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.serializable.PackageEntry;
import in.sunilpaulmathew.ashell.utils.Settings;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on November 05, 2022
 */
public class PackageSearchingAdapter extends RecyclerView.Adapter<PackageSearchingAdapter.ViewHolder> {

    private final List<PackageEntry> data;

    private final OnItemClickListener clickListener;

    public PackageSearchingAdapter(List<PackageEntry> data, OnItemClickListener clickListener) {
        this.data = data;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public PackageSearchingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_package_searching, parent, false);
        return new PackageSearchingAdapter.ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull PackageSearchingAdapter.ViewHolder holder, int position) {
        PackageEntry entry = this.data.get(position);
        entry.load(holder.cardView, holder.icon, holder.title, holder.description);

        Settings.setSlideInAnimation(holder.icon, position);
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final AppCompatImageButton icon;
        private final MaterialCardView cardView;
        private final MaterialTextView title, description;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.icon = view.findViewById(R.id.icon);
            this.cardView = view.findViewById(R.id.card);
            this.title = view.findViewById(R.id.title);
            this.description = view.findViewById(R.id.description);
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(data.get(getBindingAdapterPosition()).getPackageName());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String command);
    }

}