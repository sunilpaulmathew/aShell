package in.sunilpaulmathew.ashell.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

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
import in.sunilpaulmathew.ashell.serializable.CPUEntry;
import in.sunilpaulmathew.ashell.utils.Settings;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 18, 2026
 */
public class CPUMonitorAdapter extends RecyclerView.Adapter<CPUMonitorAdapter.ViewHolder> {

    private final List<CPUEntry> data;

    public CPUMonitorAdapter(List<CPUEntry> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_cpu_monitor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CPUEntry item = this.data.get(position);

        item.load(holder.progressBar, holder.icon, holder.textPackageName, holder.textCpuPercent, holder.textPidDetails, position + 1);

        Settings.setSlideInAnimation(holder.progressBar, position);
    }

    public void updateData(List<CPUEntry> newData) {
        final List<CPUEntry> oldList = new ArrayList<>(this.data);
        final List<CPUEntry> newList = (newData != null) ? new ArrayList<>(newData) : Collections.emptyList();

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
        private final MaterialTextView textPackageName, textCpuPercent, textPidDetails;
        private final ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            textPackageName = itemView.findViewById(R.id.textPackageName);
            textCpuPercent = itemView.findViewById(R.id.textCpuPercent);
            textPidDetails = itemView.findViewById(R.id.textPidDetails);
            progressBar = itemView.findViewById(R.id.progressProcessCpu);
        }
    }

}