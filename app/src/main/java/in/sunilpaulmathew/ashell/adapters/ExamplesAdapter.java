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
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on November 08, 2022
 */
public class ExamplesAdapter extends RecyclerView.Adapter<ExamplesAdapter.ViewHolder> {

    private final List<CommandEntry> data;
    private final OnItemClickListener listener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public ExamplesAdapter(List<CommandEntry> data, OnItemClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExamplesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_examples, parent, false);
        return new ExamplesAdapter.ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamplesAdapter.ViewHolder holder, int position) {
        holder.mTitle.setText(this.data.get(position).getTitle());
        if (this.data.get(position).getSummary() != null) {
            holder.mSummary.setText(this.data.get(position).getSummary());
        }
        holder.mApply.setVisibility(selectedPosition != position ? GONE : VISIBLE);

        holder.mApply.setOnClickListener(v -> listener.onItemClick(this.data.get(position).getExample() != null ? this.data.get(position).getExample() : this.data.get(position).getTitle()));

        Settings.setSlideInAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final MaterialButton mApply;
        private final MaterialTextView mTitle, mSummary;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.mApply = view.findViewById(R.id.apply);
            this.mTitle = view.findViewById(R.id.title);
            this.mSummary = view.findViewById(R.id.summary);
        }



        @Override
        public void onClick(View view) {
            if (selectedPosition != getBindingAdapterPosition()) {
                notifyItemChanged(selectedPosition);
                selectedPosition = getBindingAdapterPosition();
            } else {
                selectedPosition = RecyclerView.NO_POSITION;
            }
            notifyItemChanged(getBindingAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String command);
    }

}