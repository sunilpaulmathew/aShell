package in.sunilpaulmathew.ashell.adapters;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.utils.Settings;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on November 09, 2022
 */
public class ShellOutputAdapter extends RecyclerView.Adapter<ShellOutputAdapter.ViewHolder> {

    private final List<String> data, dataFiltered;

    public ShellOutputAdapter(List<String> data, List<String> dataFiltered) {
        this.data = data;
        this.dataFiltered = dataFiltered;
    }

    @NonNull
    @Override
    public ShellOutputAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_shell_output, parent, false);
        return new ShellOutputAdapter.ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ShellOutputAdapter.ViewHolder holder, int position) {
        String line = dataFiltered != null ? this.dataFiltered.get(position) : this.data.get(position);
        holder.mOutput.setText(Html.fromHtml(line, Html.FROM_HTML_MODE_LEGACY));

        Settings.setSlideInAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return this.dataFiltered != null ? this.dataFiltered.size() : this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialTextView mOutput;

        public ViewHolder(View view) {
            super(view);
            this.mOutput = view.findViewById(R.id.shell_output);
        }
    }

}