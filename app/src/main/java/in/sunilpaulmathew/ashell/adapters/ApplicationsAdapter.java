package in.sunilpaulmathew.ashell.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.serializable.PackageEntry;
import in.sunilpaulmathew.ashell.utils.Settings;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 18, 2026
 */
public class ApplicationsAdapter extends RecyclerView.Adapter<ApplicationsAdapter.ViewHolder> {

    private final List<PackageEntry> data;

    private final OnItemClickListener clickListener;

    public ApplicationsAdapter(List<PackageEntry> data, OnItemClickListener clickListener) {
        this.data = data;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_applications, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        this.data.get(position).load(holder.card, holder.icon, holder.title, null);

        Settings.setSlideInAnimation(holder.icon, position);
    }

    public void updateData(List<PackageEntry> newData) {
        final List<PackageEntry> oldList = new ArrayList<>(this.data);
        final List<PackageEntry> newList = (newData != null) ? new ArrayList<>(newData) : Collections.emptyList();

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
                return Objects.equals(oldList.get(oldItemPosition),
                        newList.get(newItemPosition));
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return Objects.equals(oldList.get(oldItemPosition).getPackageName(),
                        newList.get(newItemPosition).getPackageName());
            }
        });

        this.data.clear();
        this.data.addAll(newList);

        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final AppCompatImageButton icon;
        private final MaterialCardView card;
        private final MaterialTextView title;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.icon = view.findViewById(R.id.icon);
            this.card = view.findViewById(R.id.main_card);
            this.title = view.findViewById(R.id.title);
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(data.get(getBindingAdapterPosition()));
        }
    }

    public interface OnItemClickListener {
        void onItemClick(PackageEntry packageEntry);
    }

}