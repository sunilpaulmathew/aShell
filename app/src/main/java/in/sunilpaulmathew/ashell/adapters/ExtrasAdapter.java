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
import in.sunilpaulmathew.ashell.serializable.ExtrasEntry;
import in.sunilpaulmathew.ashell.utils.Settings;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 18, 2026
 */
public class ExtrasAdapter extends RecyclerView.Adapter<ExtrasAdapter.ViewHolder> {

    private final List<ExtrasEntry> data;
    private final OnItemClickListener clickListener;

    public ExtrasAdapter(List<ExtrasEntry> data, OnItemClickListener clickListener) {
        this.data = data;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_extras, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExtrasEntry extrasEntry = this.data.get(position);
        extrasEntry.load(holder.cardView, holder.icon, holder.title, holder.description);

        Settings.setSlideInAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public void updateData(List<ExtrasEntry> newData) {
        final List<ExtrasEntry> oldList = new ArrayList<>(this.data);
        final List<ExtrasEntry> newList = (newData != null) ? new ArrayList<>(newData) : Collections.emptyList();

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
                return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final AppCompatImageButton icon;
        private final MaterialCardView cardView;
        private final MaterialTextView title, description;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.icon = view.findViewById(R.id.icon);
            this.cardView = view.findViewById(R.id.main_card);
            this.title = view.findViewById(R.id.title);
            this.description = view.findViewById(R.id.description);
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(data.get(getBindingAdapterPosition()).getId());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int id);
    }

}