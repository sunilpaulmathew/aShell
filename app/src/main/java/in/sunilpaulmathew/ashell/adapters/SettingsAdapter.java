package in.sunilpaulmathew.ashell.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import in.sunilpaulmathew.ashell.dialogs.ExtrasDialog;
import in.sunilpaulmathew.ashell.dialogs.PolicyDialog;
import in.sunilpaulmathew.ashell.dialogs.SingleChoiceDialog;
import in.sunilpaulmathew.ashell.serializable.ExtrasChooserEntry;
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
        SettingsEntry entry = this.data.get(position);
        Context context = holder.itemView.getContext();

        holder.mTitle.setText(entry.geTitle());
        if (entry.getDescription() != null) {
            holder.mDescription.setText(entry.getDescription());
            holder.mDescription.setVisibility(View.VISIBLE);
        } else {
            holder.mDescription.setVisibility(View.GONE);
        }

        if (entry.getIcon() != Integer.MIN_VALUE) {
            holder.mIcon.setImageResource(entry.getIcon());
            holder.mIcon.setVisibility(View.VISIBLE);
            holder.mIcon.setContentDescription(entry.geTitle());
            holder.mIcon.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
            int color = entry.getIconColor(context);
            if (color != Integer.MIN_VALUE) {
                holder.mIcon.setColorFilter(entry.getIconColor(context));
            } else {
                holder.mIcon.clearColorFilter();
            }
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
            holder.mTitle.setTextColor(Settings.getColorAccent(context));
        } else {
            holder.mTitle.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            holder.mTitle.setTextColor(Settings.getColorText(context));
        }

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
            Context context = view.getContext();
            if (position == 1) {
                Intent settings = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                settings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                settings.setData(uri);
                context.startActivity(settings);
            } else if (position == 2) {
                new SingleChoiceDialog(R.drawable.ic_theme, context.getString(R.string.app_theme),
                        Settings.getAppThemeMenu(context), Settings.getAppThemePosition(context), context) {

                    @Override
                    public void onItemSelected(int itemPosition) {
                        if (itemPosition == Settings.getAppThemePosition(context)) {
                            return;
                        }
                        switch (itemPosition) {
                            case 2:
                                Utils.saveInt("appTheme", 2, context);
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                                break;
                            case 1:
                                Utils.saveInt("appTheme", 1, context);
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                                break;
                            default:
                                Utils.saveInt("appTheme", 0, context);
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                                break;
                        }
                        data.get(getBindingAdapterPosition()).setDescription(Settings.getAppTheme(context));
                        notifyItemChanged(getBindingAdapterPosition());
                    }
                }.show();
            } else if (position == 3) {
                Utils.saveBoolean("amoledTheme", !Utils.getBoolean("amoledTheme", false, context), context);
                Settings.restartApp(activity);
            } else if (position == 4) {
                Settings.setAppLanguage(activity);
            } else if (position == 5) {
                new ExtrasDialog(activity) {
                    @Override
                    public void onApplied(List<ExtrasChooserEntry> entries) {
                        new Thread(() -> {
                            boolean extrasEnabled = Settings.isExtrasEnabled(activity);
                            for (ExtrasChooserEntry entry : entries) {
                                Utils.saveBoolean("extras_" + entry.getId(), entry.isEnabled(), activity);
                            }
                            activity.runOnUiThread(() -> {
                                if (extrasEnabled) {
                                    if (!Settings.isExtrasEnabled(activity)) {
                                        Settings.loadUI(activity);
                                    } else {
                                        Settings.reloadExtras(activity);
                                    }
                                } else {
                                    if (!Settings.isExtrasEnabled(activity)) {
                                        Settings.showShell(null, activity);
                                    } else {
                                        Settings.loadUI(activity);
                                        Settings.showShell(null, activity);
                                    }
                                }
                            });
                        }).start();
                    }
                };
            } else if (position == 6) {
                new ExamplesDialog(activity) {
                    @Override
                    public void onCommandSelected(String command) {
                        if (Settings.isExtrasEnabled(activity)) {
                            Settings.navigateToFragment(0, command, activity);
                        } else {
                            Settings.showShell(command, activity);
                        }
                    }
                };
            } else if (position == 7) {
                Utils.loadUrl("https://shizuku.rikka.app/", context);
            } else if (position == 8) {
                Utils.loadUrl("https://poeditor.com/join/project/20PSoEAgfX", context);
            } else if (position == 9) {
                new PolicyDialog(context);
            } else if (position == 10) {
                Utils.loadUrl("mailto:smartpack.org@gmail.com", context);
            }
        }
    }

}