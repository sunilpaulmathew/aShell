package in.sunilpaulmathew.ashell.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import in.sunilpaulmathew.ashell.BuildConfig;
import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.dialogs.ExamplesDialog;
import in.sunilpaulmathew.ashell.dialogs.PolicyDialog;
import in.sunilpaulmathew.ashell.dialogs.SingleChoiceDialog;
import in.sunilpaulmathew.ashell.serializable.SettingsEntry;
import in.sunilpaulmathew.ashell.utils.Settings;
import in.sunilpaulmathew.ashell.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 21, 2022
 */
public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> {

    private final Activity activity;
    private final List<SettingsEntry> data;

    public SettingsAdapter(List<SettingsEntry> data, Activity activity) {
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
            holder.mIcon.setContentDescription(data.get(position).geTitle());
            holder.mIcon.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
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
            holder.mTitle.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            holder.mTitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            holder.mTitle.setTextColor(Settings.getColorAccent(holder.mTitle.getContext()));
        } else {
            holder.mTitle.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            holder.mTitle.setTextColor(Settings.getColorText(holder.mTitle.getContext()));
        }

        holder.mCheckBox.setOnClickListener(v -> {
            if (data.get(position).getPosition() == 2) {
                Utils.saveBoolean("amoledTheme", !Utils.getBoolean("amoledTheme", false, holder.mCheckBox.getContext()), holder.mCheckBox.getContext());
                Settings.restartApp(activity);
            }
        });

        Settings.setSlideInAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final AppCompatImageButton mIcon;
        private final MaterialCheckBox mCheckBox;
        private final MaterialTextView mTitle, mDescription;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.mIcon = view.findViewById(R.id.icon);
            this.mTitle = view.findViewById(R.id.title);
            this.mDescription = view.findViewById(R.id.description);
            this.mCheckBox = view.findViewById(R.id.checkbox);
        }

        @SuppressLint("StringFormatMatches")
        @Override
        public void onClick(View view) {
            int position = data.get(getBindingAdapterPosition()).getPosition();
            if (position == 1) {
                Intent settings = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                settings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                settings.setData(uri);
                view.getContext().startActivity(settings);
            } else if (position == 2) {
                new SingleChoiceDialog(R.drawable.ic_theme, view.getContext().getString(R.string.app_theme),
                        Settings.getAppThemeMenu(view.getContext()), Settings.getAppThemePosition(view.getContext()), view.getContext()) {

                    @Override
                    public void onItemSelected(int itemPosition) {
                        if (itemPosition == Settings.getAppThemePosition(view.getContext())) {
                            return;
                        }
                        switch (itemPosition) {
                            case 2:
                                Utils.saveInt("appTheme", 2, view.getContext());
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                                break;
                            case 1:
                                Utils.saveInt("appTheme", 1, view.getContext());
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                                break;
                            default:
                                Utils.saveInt("appTheme", 0, view.getContext());
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                                break;
                        }
                        data.get(getBindingAdapterPosition()).setDescription(Settings.getAppTheme(view.getContext()));
                        notifyItemChanged(getBindingAdapterPosition());
                    }
                }.show();
            } else if (position == 3) {
                Utils.saveBoolean("amoledTheme", !Utils.getBoolean("amoledTheme", false, view.getContext()), view.getContext());
                Settings.restartApp(activity);
            } else if (position == 4) {
                Settings.setAppLanguage(activity);
            } else if (position == 5) {
                new ExamplesDialog(true, activity) {
                    @Override
                    public void onCommandSelected(String command) {
                        // To-do
                    }
                };
            } else if (position == 6) {
                Utils.loadUrl("https://shizuku.rikka.app/", view.getContext());
            } else if (position == 7) {
                Utils.loadUrl("https://poeditor.com/join/project/20PSoEAgfX", view.getContext());
            } else if (position == 8) {
                new PolicyDialog(view.getContext());
            } else if (position == 9) {
                Utils.loadUrl("mailto:smartpack.org@gmail.com", view.getContext());
            }
        }
    }

}