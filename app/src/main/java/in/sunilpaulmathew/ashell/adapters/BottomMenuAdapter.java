package in.sunilpaulmathew.ashell.adapters;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.serializable.MenuEntry;
import in.sunilpaulmathew.ashell.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on June 24, 2026
 */
public class BottomMenuAdapter extends RecyclerView.Adapter<BottomMenuAdapter.ViewHolder> {

    private final List<MenuEntry> data;
    private final OnItemClickListener listener;

    public BottomMenuAdapter(List<MenuEntry> items, OnItemClickListener listener) {
        this.data = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_bottom_menu, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuEntry item = this.data.get(position);
        holder.title.setText(item.getTile());

        if (this.data.get(position).getDrawable() != Integer.MIN_VALUE) {
            holder.icon.setImageDrawable(Utils.getDrawable(this.data.get(position).getDrawable(), holder.icon.getContext()));
            holder.icon.setVisibility(VISIBLE);
        } else {
            holder.icon.setVisibility(GONE);
        }

        if (this.data.get(position).getDescription() != null) {
            holder.description.setText(item.getDescription());
            holder.description.setVisibility(VISIBLE);
        } else {
            holder.description.setVisibility(GONE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final AppCompatImageButton icon;
        private final MaterialTextView title, description;
        ViewHolder(@NonNull View view) {
            super(view);
            icon = view.findViewById(R.id.icon);
            title = view.findViewById(R.id.title);
            description = view.findViewById(R.id.description);

            view.setOnClickListener(v -> listener.onItemClick(data.get(getBindingAdapterPosition()).getID()));
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int id);
    }

}