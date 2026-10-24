package in.sunilpaulmathew.ashell.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.io.File;
import java.util.List;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.utils.Settings;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 18, 2026
 */
public class SplitsAdapter extends RecyclerView.Adapter<SplitsAdapter.ViewHolder> {

    private final List<File> data;

    public SplitsAdapter(List<File> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_splits, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.icon.setImageResource(R.drawable.ic_apks);
        holder.text.setText(this.data.get(position).getName());
        Settings.setSlideInAnimation(holder.text, position);
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final AppCompatImageButton icon;
        private final MaterialTextView text;

        public ViewHolder(View view) {
            super(view);
            this.icon = view.findViewById(R.id.icon);
            this.text = view.findViewById(R.id.text);
        }
    }

}