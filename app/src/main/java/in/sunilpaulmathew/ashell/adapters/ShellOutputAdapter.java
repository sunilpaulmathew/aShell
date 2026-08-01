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
    private int count;

    public ShellOutputAdapter(List<String> data, List<String> dataFiltered) {
        this.data = data;
        this.dataFiltered = dataFiltered;
        this.count = dataFiltered != null ? dataFiltered.size() : data.size();
    }

    /*
     * The item count is pinned rather than read from the list on every call: output
     * is appended from a binder thread, and a count that grows between a layout pass
     * and the notify that announces it makes RecyclerView throw.
     */
    public void setItemCount(int count) {
        this.count = count;
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

        /*
         * Only the coloured lines -- errors, logcat levels, the command header -- carry
         * markup, and escaping leaves an entity behind for the rest. Parsing the plain
         * ones anyway costs a full HTML parse per bind, on every scroll frame.
         */
        if (line.indexOf('<') < 0 && line.indexOf('&') < 0) {
            holder.mOutput.setText(line);
        } else {
            holder.mOutput.setText(Html.fromHtml(line, Html.FROM_HTML_MODE_LEGACY));
        }

        Settings.setSlideInAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return this.count;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialTextView mOutput;

        public ViewHolder(View view) {
            super(view);
            this.mOutput = view.findViewById(R.id.shell_output);
        }
    }

}