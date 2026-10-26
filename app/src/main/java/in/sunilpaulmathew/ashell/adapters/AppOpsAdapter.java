package in.sunilpaulmathew.ashell.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.dialogs.SingleChoiceBottomDialog;
import in.sunilpaulmathew.ashell.serializable.AppOpsEntry;
import in.sunilpaulmathew.ashell.serializable.MenuEntry;
import in.sunilpaulmathew.ashell.utils.Async;
import in.sunilpaulmathew.ashell.utils.Settings;
import in.sunilpaulmathew.ashell.utils.ShizukuShell;
import in.sunilpaulmathew.ashell.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 25, 2026
 */
public class AppOpsAdapter extends RecyclerView.Adapter<AppOpsAdapter.ViewHolder> {

    private final List<AppOpsEntry> data;
    private final String packageName;

    public AppOpsAdapter(List<AppOpsEntry> data, String packageName) {
        this.data = data;
        this.packageName = packageName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_appops, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppOpsEntry item = this.data.get(position);
        item.load(holder.name, holder.status, holder.description);
        holder.icon.setImageBitmap(Settings.generateAlphabetIcon(String.valueOf(position + 1), 16, holder.itemView.getContext()));
        Settings.setSlideInAnimation(holder.name, position);
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final AppCompatImageButton icon;
        private final MaterialTextView description, name, status;

        public ViewHolder(View view) {
            super(view);
            this.icon = view.findViewById(R.id.icon);
            this.name = view.findViewById(R.id.name);
            this.status = view.findViewById(R.id.status);
            this.description = view.findViewById(R.id.description);

            view.setOnClickListener(v -> {
                Context context = v.getContext();
                List<MenuEntry> menuItems = new ArrayList<>();
                menuItems.add(new MenuEntry(context.getString(R.string.app_ops_allow_title), context.getString(R.string.app_ops_allow_description), 0));
                menuItems.add(new MenuEntry(context.getString(R.string.app_ops_ignore_title), context.getString(R.string.app_ops_ignore_description), 1));
                menuItems.add(new MenuEntry(context.getString(R.string.app_ops_deny_title), context.getString(R.string.app_ops_deny_description), 2));
                menuItems.add(new MenuEntry(context.getString(R.string.app_ops_default_title), context.getString(R.string.app_ops_default_description), 3));
                menuItems.add(new MenuEntry(context.getString(R.string.app_ops_foreground_title), context.getString(R.string.app_ops_foreground_description), 4));

                new SingleChoiceBottomDialog(menuItems, data.get(getBindingAdapterPosition()).getStatus(), context) {
                    @Override
                    public void onItemSelected(int position) {
                        new Async() {
                            private boolean success = false;
                            private String newStatus;
                            @Override
                            public void onPreExecute() {
                            }

                            @Override
                            public void doInBackground() {
                                String[] options = new String[] {
                                        "allow", "ignore", "deny", "default", "foreground"
                                };

                                String result = ShizukuShell.runCommand("cmd appops set " + packageName + " " +
                                        data.get(getBindingAdapterPosition()).getName() + " " + options[position]);

                                if (result == null || result.trim().isEmpty()) {
                                    newStatus = options[position];
                                    success = true;
                                }
                            }

                            @Override
                            public void onPostExecute() {
                                if (success) {
                                    data.get(getBindingAdapterPosition()).setStatus(newStatus);
                                    notifyItemChanged(getBindingAdapterPosition());
                                } else {
                                    Utils.toast(context.getString(R.string.app_ops_failed_toast, data.get(getBindingAdapterPosition()).getName().toUpperCase(Locale.getDefault())), v.getContext()).show();
                                }
                            }
                        }.execute();
                    }
                };
            });
        }
    }

}