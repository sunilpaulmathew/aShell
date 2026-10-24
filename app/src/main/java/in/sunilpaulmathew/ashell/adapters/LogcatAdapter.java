package in.sunilpaulmathew.ashell.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.List;
import java.util.Locale;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.serializable.LogcatEntry;
import in.sunilpaulmathew.ashell.utils.Settings;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 18, 2026
 */
public class LogcatAdapter extends RecyclerView.Adapter<LogcatAdapter.ViewHolder> {

    private final List<String> data;

    public LogcatAdapter(List<String> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_logs, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String rawLine = this.data.get(position);
        LogcatEntry log = parseLogcatLine(rawLine);

        if (log == null) {
            log = new LogcatEntry('I', "ADB", rawLine);
        }

        int textColor = log.getTextColor(holder.itemView.getContext());

        holder.level.setText(String.valueOf(log.getLevel()));
        holder.tag.setText(log.getTag());
        holder.message.setText(log.getMessage());

        holder.level.setTextColor(textColor);
        holder.message.setTextColor(textColor);

        Settings.setSlideInAnimation(holder.itemView, position);
    }

    private LogcatEntry parseLogcatLine(String line) {
        if (line == null || line.length() < 3) return null;
        char level = line.charAt(0);
        int tagIndex = line.indexOf('/');
        int colonIndex = line.indexOf(':', tagIndex);

        if (tagIndex != -1 && colonIndex != -1 && tagIndex + 1 < colonIndex) {
            String tag = line.substring(tagIndex + 1, colonIndex).trim();
            String message = line.substring(colonIndex + 1).trim();
            return new LogcatEntry(level, tag, message);
        }
        return null;
    }

    public void addLogs(List<String> newLogs, String searchTxt) {
        if (newLogs.isEmpty()) return;
        int startPosition = data.size();
        int newSize = 0;
        for (String newLine : newLogs) {
            if (searchTxt == null || searchTextIsEmpty(searchTxt) || newLine.trim().toLowerCase(Locale.getDefault()).contains(searchTxt)) {
                data.add(newLine);
                newSize += 1;
            }
        }
        if (newSize > 0) {
            notifyItemRangeInserted(startPosition, newSize);
        }
    }

    private boolean searchTextIsEmpty(String searchTxt) {
        return searchTxt == null || searchTxt.trim().isEmpty();
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