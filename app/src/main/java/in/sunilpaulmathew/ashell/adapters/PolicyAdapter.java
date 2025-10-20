package in.sunilpaulmathew.ashell.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.serializable.CommandEntry;
import in.sunilpaulmathew.ashell.utils.Settings;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 21, 2022
 */
public class PolicyAdapter extends RecyclerView.Adapter<PolicyAdapter.ViewHolder> {

    private final List<CommandEntry> data;

    public PolicyAdapter(List<CommandEntry> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public PolicyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(in.sunilpaulmathew.ashell.R.layout.recycler_view_policy, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull PolicyAdapter.ViewHolder holder, int position) {
        holder.mTitle.setText(data.get(position).getTitle());
        holder.mText.setText(data.get(position).getSummary());
        holder.mText.setTextColor(holder.mText.getHintTextColors());

        Settings.setSlideInAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final MaterialTextView mText, mTitle;

        public ViewHolder(View view) {
            super(view);
            this.mText = view.findViewById(R.id.text);
            this.mTitle = view.findViewById(R.id.title);
            view.setOnClickListener(v -> {
                if (mText.getMaxLines() == 1) {
                    mText.setSingleLine(false);
                } else {
                    mText.setMaxLines(1);
                }
            });
        }
    }

}