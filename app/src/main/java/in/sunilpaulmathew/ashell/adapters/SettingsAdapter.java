package in.sunilpaulmathew.ashell.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import in.sunilpaulmathew.ashell.BuildConfig;
import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.activities.ExamplesActivity;
import in.sunilpaulmathew.ashell.serializable.SettingsItems;
import in.sunilpaulmathew.ashell.utils.Settings;
import in.sunilpaulmathew.ashell.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 21, 2022
 */
public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> {

    private final Activity activity;
    private final List<SettingsItems> data;

    public SettingsAdapter(List<SettingsItems> data, Activity activity) {
        this.data = data;
        this.activity = activity;
    }

    @NonNull
    @Override
    public SettingsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_settings, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsAdapter.ViewHolder holder, int position) {
        holder.mTitle.setText(data.get(position).geTitle());
        if (data.get(position).getDescription() != null) {
            holder.mDescription.setText(data.get(position).getDescription());
            holder.mDescription.setVisibility(View.VISIBLE);
        } else {
            holder.mDescription.setVisibility(View.GONE);
        }

        if (data.get(position).getIcon() != Integer.MIN_VALUE) {
            holder.mIcon.setImageDrawable(Utils.getDrawable(data.get(position).getIcon(), holder.mIcon.getContext()));
            holder.mIcon.setVisibility(View.VISIBLE);
        } else {
            holder.mIcon.setVisibility(View.GONE);
        }

        if (data.get(position).isSwitch()) {
            holder.mCheckBox.setVisibility(View.VISIBLE);
            holder.mCheckBox.setChecked(data.get(position).isEnabled());
        } else {
            holder.mCheckBox.setVisibility(View.GONE);
        }

        if (data.get(position).getPosition() == 0) {
            holder.mDivider.setVisibility(View.VISIBLE);
            holder.mTitle.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            holder.mTitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            holder.mTitle.setTextColor(Settings.getColorAccent(holder.mTitle.getContext()));
        } else {
            holder.mTitle.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            holder.mTitle.setTextColor(Settings.getColorText(holder.mTitle.getContext()));
            holder.mDivider.setVisibility(View.GONE);
        }

        holder.mCheckBox.setOnClickListener(v -> {
            if (data.get(position).getPosition() == 2) {
                Utils.saveBoolean("amoledTheme", !Utils.getBoolean("amoledTheme", false, holder.mCheckBox.getContext()), holder.mCheckBox.getContext());
                Settings.restartApp(activity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final AppCompatImageButton mIcon;
        private final MaterialCheckBox mCheckBox;
        private final MaterialTextView mTitle, mDescription;
        private final View mDivider;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.mIcon = view.findViewById(R.id.icon);
            this.mTitle = view.findViewById(R.id.title);
            this.mDescription = view.findViewById(R.id.description);
            this.mCheckBox = view.findViewById(R.id.checkbox);
            this.mDivider = view.findViewById(R.id.divider);
        }

        @SuppressLint("StringFormatMatches")
        @Override
        public void onClick(View view) {
            int position = data.get(getBindingAdapterPosition()).getPosition();
            if (position == 1) {
                Settings.setAppTheme(view.getContext());
            } else if (position == 2) {
                Utils.saveBoolean("amoledTheme", !Utils.getBoolean("amoledTheme", false, view.getContext()), view.getContext());
                Settings.restartApp(activity);
            } else if (position == 3) {
                Settings.setAppLanguage(activity);
            } else if (position == 4) {
                Intent examples = new Intent(view.getContext(), ExamplesActivity.class);
                view.getContext().startActivity(examples);
            } else if (position == 5) {
                Utils.loadUrl("https://shizuku.rikka.app/", view.getContext());
            } else if (position == 6) {
                Utils.loadUrl("https://poeditor.com/join/project/20PSoEAgfX", view.getContext());
            } else if (position == 7) {
                LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());
                View policyLayout = layoutInflater.inflate(R.layout.layout_privacy_policy, null);
                MaterialTextView version = policyLayout.findViewById(R.id.title);
                RecyclerView recyclerView = policyLayout.findViewById(R.id.recycler_view);
                version.setText(view.getContext().getString(R.string.app_version, BuildConfig.VERSION_NAME));
                recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                recyclerView.setAdapter(new PolicyAdapter(Settings.getPolicyData()));
                new MaterialAlertDialogBuilder(view.getContext())
                        .setView(policyLayout)
                        .setPositiveButton(R.string.cancel, (dialog, id) -> {
                        }).show();
            } else if (position == 8) {
                Utils.loadUrl("mailto:smartpack.org@gmail.com", view.getContext());
            }
        }
    }

}