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
import in.sunilpaulmathew.ashell.serializable.MenuEntry;
import in.sunilpaulmathew.ashell.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 25, 2026
 */
public class SingleChoiceAdapter extends RecyclerView.Adapter<SingleChoiceAdapter.ViewHolder> {

    private final List<MenuEntry> data;
    private final OnItemClickListener clickListener;
    private final String currentStatus;

    public SingleChoiceAdapter(List<MenuEntry> data, String currentStatus, OnItemClickListener clickListener) {
        this.data = data;
        this.currentStatus = currentStatus;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_appops, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuEntry item = this.data.get(position);
        holder.name.setText(item.getTile());
        holder.status.setText(item.getDescription());

        if (currentStatus.contains(data.get(position).getTile().toLowerCase())) {
            holder.icon.setImageDrawable(Utils.getDrawable(R.drawable.ic_select, holder.icon.getContext()));
            holder.itemView.setAlpha(1);
        } else {
            holder.icon.setImageDrawable(Utils.getDrawable(R.drawable.ic_checkbox_blank, holder.icon.getContext()));
            holder.itemView.setAlpha((float) 0.5);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final AppCompatImageButton icon;
        private final MaterialTextView name, status;
        ViewHolder(@NonNull View view) {
            super(view);
            icon = view.findViewById(R.id.icon);
            name = view.findViewById(R.id.name);
            status = view.findViewById(R.id.description);

            view.setOnClickListener(v -> clickListener.onItemClick(data.get(getBindingAdapterPosition()).getID()));
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}