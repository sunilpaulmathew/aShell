package in.sunilpaulmathew.ashell.adapters;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import in.sunilpaulmathew.ashell.R;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on June 07, 2026
 */
public class TitleAdapter extends RecyclerView.Adapter<TitleAdapter.ViewHolder> {

    private final List<String> data;
    private final OnItemClickListener clickListener;

    public TitleAdapter(List<String> data, OnItemClickListener clickListener) {
        this.data = data;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public TitleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_title, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull TitleAdapter.ViewHolder holder, int position) {
        holder.mTitle.setText(data.get(position));
        if (position < data.size() - 1) {
            holder.mTitle.setTypeface(null, Typeface.BOLD_ITALIC);
            holder.mButton.setVisibility(VISIBLE);
        } else {
            holder.mTitle.setTypeface(null, Typeface.BOLD);
            holder.mButton.setVisibility(GONE);
        }
    }

    public void updateData(List<String> newData) {
        final List<String> oldList = new ArrayList<>(this.data);
        final List<String> newList = (newData != null) ? new ArrayList<>(newData) : Collections.emptyList();

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
                return Objects.equals(oldList.get(oldItemPosition), newList.get(newItemPosition));
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return Objects.equals(oldList.get(oldItemPosition), newList.get(newItemPosition));
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

        private final MaterialButton mButton, mTitle;

        public ViewHolder(View view) {
            super(view);
            this.mButton = view.findViewById(R.id.icon);
            this.mTitle = view.findViewById(R.id.title);

            view.setOnClickListener(v -> {
                if (getBindingAdapterPosition() != getItemCount() - 1) {
                    clickListener.onItemClick(getBindingAdapterPosition());
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}