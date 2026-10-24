package in.sunilpaulmathew.ashell.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.serializable.PackageHeaderEntry;
import in.sunilpaulmathew.ashell.utils.Settings;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 18, 2026
 */
public class PackageHeaderAdapter extends RecyclerView.Adapter<PackageHeaderAdapter.ViewHolder> {

    private final List<PackageHeaderEntry> data;

    public PackageHeaderAdapter(List<PackageHeaderEntry> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_package_header, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        this.data.get(position).load(holder.button);
        Settings.setSlideInAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialButton button;

        public ViewHolder(View view) {
            super(view);
            this.button = view.findViewById(R.id.button);
        }
    }

}