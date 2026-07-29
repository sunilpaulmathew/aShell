package in.sunilpaulmathew.ashell.adapters;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.serializable.CommandEntry;
import in.sunilpaulmathew.ashell.utils.Settings;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on July 29, 2026
 */
public class BookmarksAdapter extends RecyclerView.Adapter<BookmarksAdapter.ViewHolder> {

    private final boolean editable;
    private final List<CommandEntry> data;
    private final OnItemClickListener clickListener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public BookmarksAdapter(List<CommandEntry> data, OnItemClickListener clickListener, boolean editable) {
        this.data = data;
        this.clickListener = clickListener;
        this.editable = editable;
    }

    @NonNull
    @Override
    public BookmarksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_examples, parent, false);
        return new BookmarksAdapter.ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarksAdapter.ViewHolder holder, int position) {
        CommandEntry entry = this.data.get(position);

        holder.title.setText(entry.getTitle());

        if (editable) {
            holder.action.setVisibility(selectedPosition != position ? GONE : VISIBLE);

            if (entry.getSummary() != null) {
                holder.summary.setText(entry.getSummary());
                holder.summary.setVisibility(VISIBLE);
            } else {
                holder.summary.setVisibility(GONE);
            }

            holder.action.setIconResource(R.drawable.ic_edit);
            holder.action.setText(R.string.edit);
        } else {
            holder.summary.setVisibility(GONE);
            holder.action.setVisibility(GONE);
        }

        holder.action.setOnClickListener(v -> clickListener.onItemClick(this.data.get(holder.getBindingAdapterPosition()).getTitle(), true));

        Settings.setSlideInAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final MaterialButton action;
        private final MaterialTextView title, summary;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.action = view.findViewById(R.id.apply);
            this.title = view.findViewById(R.id.title);
            this.summary = view.findViewById(R.id.summary);

            if (editable) {
                view.setOnLongClickListener(v -> {
                    if (selectedPosition != getBindingAdapterPosition()) {
                        notifyItemChanged(selectedPosition);
                        selectedPosition = getBindingAdapterPosition();
                    } else {
                        selectedPosition = RecyclerView.NO_POSITION;
                    }
                    notifyItemChanged(getBindingAdapterPosition());
                    return true;
                });
            }
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(data.get(getBindingAdapterPosition()).getTitle(), false);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String command, boolean edit);
    }

}