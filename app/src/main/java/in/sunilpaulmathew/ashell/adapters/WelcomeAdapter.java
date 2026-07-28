package in.sunilpaulmathew.ashell.adapters;

import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.serializable.CommandEntry;
import in.sunilpaulmathew.ashell.utils.Settings;
import rikka.shizuku.Shizuku;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 16, 2025
 */
public class WelcomeAdapter extends RecyclerView.Adapter<WelcomeAdapter.ViewHolder> {

    private final List<CommandEntry> data;
    private final OnItemClickListener clickListener;

    public WelcomeAdapter(List<CommandEntry> data, OnItemClickListener clickListener) {
        this.data = data;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public WelcomeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_welcome, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull WelcomeAdapter.ViewHolder holder, int position) {
        holder.mTitle.setText(data.get(position).getTitle());
        holder.mText.setText(data.get(position).getSummary());
        holder.mText.setTextColor(holder.mText.getHintTextColors());

        if (position == 1 && Shizuku.pingBinder() && Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
            holder.mButton.setVisibility(View.GONE);
            holder.mPermission.setVisibility(View.VISIBLE);
        } else {
            holder.mButton.setVisibility(View.VISIBLE);
            holder.mPermission.setVisibility(View.GONE);
        }

        holder.mPermission.setOnClickListener(v -> {
            if (Shizuku.pingBinder() && Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
                clickListener.onItemClick();
            }
        });

        Settings.setSlideInAnimation(holder.itemView, position);
        holder.mButton.setContentDescription(holder.mText.getMaxLines() == 1 ?
                "Expand" : "Collapse");
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final AppCompatImageButton mButton;
        private final MaterialButton mPermission;
        private final MaterialTextView mText, mTitle;

        public ViewHolder(View view) {
            super(view);
            this.mButton = view.findViewById(R.id.button);
            this.mPermission = view.findViewById(R.id.permission);
            this.mText = view.findViewById(R.id.text);
            this.mTitle = view.findViewById(R.id.title);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (getBindingAdapterPosition() == 1 && Shizuku.pingBinder() && Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
                clickListener.onItemClick();
            } else {
                if (mText.getMaxLines() == 1) {
                    mText.setSingleLine(false);
                    mButton.setRotation(180);
                    mButton.setContentDescription("Collapse");
                } else {
                    mButton.setRotation(0);
                    mText.setMaxLines(1);
                    mButton.setContentDescription("Expand");
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick();
    }

}