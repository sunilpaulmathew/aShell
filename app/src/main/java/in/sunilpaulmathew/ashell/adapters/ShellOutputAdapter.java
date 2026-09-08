package in.sunilpaulmathew.ashell.adapters;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.utils.Settings;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on November 09, 2022
 */
public class ShellOutputAdapter extends RecyclerView.Adapter<ShellOutputAdapter.ViewHolder> {

    private final List<String> data;
    private int animatedUpTo = -1;

    public ShellOutputAdapter(List<String> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ShellOutputAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_shell_output, parent, false);
        return new ShellOutputAdapter.ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ShellOutputAdapter.ViewHolder holder, int position) {
        String line = this.data.get(position);

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

        /*
         * Animate a row only the first time it comes into view. Passing the position
         * unconditionally re-ran the slide on every rebind, so the whole visible list
         * animated continuously while output streamed in. -1 resets the view instead.
         */
        if (position > this.animatedUpTo) {
            this.animatedUpTo = position;
            Settings.setSlideInAnimation(holder.itemView, position);
        } else {
            Settings.setSlideInAnimation(holder.itemView, -1);
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
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialTextView mOutput;

        public ViewHolder(View view) {
            super(view);
            this.mOutput = view.findViewById(R.id.shell_output);
        }
    }

}