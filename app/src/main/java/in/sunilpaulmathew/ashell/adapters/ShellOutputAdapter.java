package in.sunilpaulmathew.ashell.adapters;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.util.TypedValue;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.serializables.LogcatEntry;
import in.sunilpaulmathew.ashell.utils.Settings;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on November 09, 2022
 */
public class ShellOutputAdapter extends RecyclerView.Adapter<ShellOutputAdapter.ViewHolder> {

    private final List<String> data;
    private int animatedUpTo = -1;
    private static final Pattern BRIEF_PATTERN = Pattern.compile(
            "^([VDIWEF])/([^(:\\s]+)(?:\\s*\\(\\s*\\d+\\s*\\))?:\\s*(.*)$"
    );
    private static final Pattern THREADTIME_PATTERN = Pattern.compile(
            "^\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}\\.\\d{3}\\s+\\d+\\s+\\d+\\s+([VDIWEF])\\s+(.+?):\\s+(.*)$"
    );

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

        LogcatEntry log = parseLogcatLine(line);

        if (log == null) {
            /*
             * Only the coloured lines -- errors, logcat levels, the command header -- carry
             * markup, and escaping leaves an entity behind for the rest. Parsing the plain
             * ones anyway costs a full HTML parse per bind, on every scroll frame.
             */
            if (line.indexOf('<') < 0 && line.indexOf('&') < 0) {
                holder.message.setText(line);
            } else {
                holder.message.setText(Html.fromHtml(line, Html.FROM_HTML_MODE_LEGACY));
            }
            holder.message.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            holder.message.setTextColor(getDefaultTextColor(holder.itemView.getContext()));
            holder.level.setVisibility(GONE);
            holder.tag.setVisibility(GONE);
        } else {
            int textColor = log.getTextColor(holder.itemView.getContext());

            holder.level.setText(String.valueOf(log.getLevel()));
            holder.tag.setText(log.getTag());
            holder.message.setText(Html.fromHtml(log.getMessage(), Html.FROM_HTML_MODE_LEGACY));

            holder.message.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

            holder.level.setTextColor(textColor);
            holder.message.setTextColor(textColor);
            holder.level.setVisibility(VISIBLE);
            holder.tag.setVisibility(VISIBLE);
        }

        animateView(holder.itemView, position);
    }

    /*
     * Animate a row only the first time it comes into view. Passing the position
     * unconditionally re-ran the slide on every rebind, so the whole visible list
     * animated continuously while output streamed in. -1 resets the view instead.
     */
    private void animateView(View view, int position) {
        if (position > this.animatedUpTo) {
            this.animatedUpTo = position;
            Settings.setSlideInAnimation(view, position);
        } else {
            Settings.setSlideInAnimation(view, -1);
        }
    }

    private int getDefaultTextColor(Context context) {
        boolean dark = Settings.isDarkTheme(context);
        return Color.parseColor(dark ? "#FFFFFF" : "#DE000000");
    }

    private LogcatEntry parseLogcatLine(String line) {
        if (line == null || line.length() < 3) return null;

        Matcher threadtimeMatcher = THREADTIME_PATTERN.matcher(line);
        if (threadtimeMatcher.matches()) {
            char level = Objects.requireNonNull(threadtimeMatcher.group(1)).charAt(0);
            String tag = Objects.requireNonNull(threadtimeMatcher.group(2)).trim();
            String message = Objects.requireNonNull(threadtimeMatcher.group(3)).trim();
            return new LogcatEntry(level, tag, message);
        }

        Matcher briefMatcher = BRIEF_PATTERN.matcher(line);
        if (briefMatcher.matches()) {
            char level = Objects.requireNonNull(briefMatcher.group(1)).charAt(0);
            String tag = Objects.requireNonNull(briefMatcher.group(2)).trim();
            String message = Objects.requireNonNull(briefMatcher.group(3)).trim();
            return new LogcatEntry(level, tag, message);
        }

        return null;
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
        private final MaterialTextView level, tag, message;

        public ViewHolder(View view) {
            super(view);
            this.level = view.findViewById(R.id.level);
            this.tag = view.findViewById(R.id.tag);
            this.message = view.findViewById(R.id.message);
        }
    }

}