package in.sunilpaulmathew.ashell.adapters;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

import in.sunilpaulmathew.ashell.R;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on June 07, 2026
 */
public class TitleAdapter extends RecyclerView.Adapter<TitleAdapter.ViewHolder> {

    private final List<String> data;
    private static ClickListener mClickListener;

    public TitleAdapter(List<String> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public TitleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_title, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull TitleAdapter.ViewHolder holder, int position) {
        holder.mTitle.setText(data.get(position));
        if (position < data.size() - 1) {
            holder.mTitle.setTypeface(null, Typeface.BOLD_ITALIC);
            holder.mButton.setVisibility(VISIBLE);
        } else {
            holder.mTitle.setTypeface(null, Typeface.BOLD);
            holder.mButton.setVisibility(GONE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final MaterialButton mButton, mTitle;

        public ViewHolder(View view) {
            super(view);
            this.mButton = view.findViewById(R.id.icon);
            this.mTitle = view.findViewById(R.id.title);

            view.setOnClickListener(v -> {
                if (getBindingAdapterPosition() != getItemCount() - 1) {
                    mClickListener.onItemClick(getBindingAdapterPosition());
                }
            });
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        TitleAdapter.mClickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position);
    }

}