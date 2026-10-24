package in.sunilpaulmathew.ashell.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.serializable.ExtrasChooserEntry;
import in.sunilpaulmathew.ashell.utils.Settings;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 18, 2026
 */
public class ChooseExtrasAdapter extends RecyclerView.Adapter<ChooseExtrasAdapter.ViewHolder> {

    private final List<ExtrasChooserEntry> data;

    public ChooseExtrasAdapter(List<ExtrasChooserEntry> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_choose_extras, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExtrasChooserEntry extrasEntry = this.data.get(position);
        extrasEntry.load(holder.cardView, holder.checkBox, holder.icon, holder.title, holder.description);

        if (position == 0) {
            holder.itemView.setEnabled(false);
            holder.itemView.setClickable(false);
            holder.checkBox.setEnabled(false);
        } else {
            holder.itemView.setEnabled(true);
            holder.itemView.setClickable(true);
            holder.checkBox.setEnabled(true);
        }

        Settings.setSlideInAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public List<ExtrasChooserEntry> getData() {
        return this.data;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final AppCompatImageButton icon;
        private final MaterialCardView cardView;
        private final MaterialCheckBox checkBox;
        private final MaterialTextView title, description;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.icon = view.findViewById(R.id.icon);
            this.cardView = view.findViewById(R.id.main_card);
            this.checkBox = view.findViewById(R.id.checkbox);
            this.title = view.findViewById(R.id.title);
            this.description = view.findViewById(R.id.description);
        }

        @Override
        public void onClick(View view) {
            if (getBindingAdapterPosition() ==0) return;
            ExtrasChooserEntry entry = data.get(getBindingAdapterPosition());
            entry.setEnabled(!entry.isEnabled());
            notifyItemChanged(getBindingAdapterPosition());
        }
    }

}