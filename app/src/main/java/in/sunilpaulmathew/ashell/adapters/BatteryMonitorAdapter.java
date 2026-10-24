package in.sunilpaulmathew.ashell.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.serializable.BatteryEntry;
import in.sunilpaulmathew.ashell.utils.Settings;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 18, 2026
 */
public class BatteryMonitorAdapter extends RecyclerView.Adapter<BatteryMonitorAdapter.ViewHolder> {

    private final List<BatteryEntry> data;

    public BatteryMonitorAdapter(List<BatteryEntry> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_bottom_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BatteryEntry item = this.data.get(position);
        int color = getIconColor(item.getDrawable(), holder.itemView.getContext());

        holder.icon.setImageResource(item.getDrawable());
        holder.title.setText(item.getTile());
        holder.description.setText(item.getDescription());

        holder.icon.setColorFilter(color);
        holder.title.setTextColor(color);

        Settings.setSlideInAnimation(holder.description, position);
    }

    public int getIconColor(int iconRes, Context context) {
        boolean dark = Settings.isDarkTheme(context);
        String hexColor;
        if (iconRes == R.drawable.ic_cellphone_down || iconRes == R.drawable.ic_charging) {
            hexColor = dark ? "#82B1FF" : "#2962FF";
        } else if (iconRes == R.drawable.ic_voltage) {
            hexColor = dark ? "#FFB74D" : "#E65100";
        } else if (iconRes == R.drawable.ic_temperature) {
            hexColor = dark ? "#FF8A65" : "#D84315";
        } else if (iconRes == R.drawable.ic_technology) {
            hexColor = dark ? "#B0BEC5" : "#455A64";
        } else if (iconRes == R.drawable.ic_heart) {
            hexColor = dark ? "#CE93D8" : "#7B1FA2";
        } else {
            hexColor = dark ? "#4DB6AC" : "#00897B";
        }
        return Color.parseColor(hexColor);
    }

    public void updateData(List<BatteryEntry> newData) {
        final List<BatteryEntry> oldList = new ArrayList<>(this.data);
        final List<BatteryEntry> newList = (newData != null) ? new ArrayList<>(newData) : Collections.emptyList();

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
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final AppCompatImageButton icon;
        private final MaterialTextView title, description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
        }
    }

}