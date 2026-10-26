package in.sunilpaulmathew.ashell.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.dialogs.ProgressDialog;
import in.sunilpaulmathew.ashell.serializable.PermissionsEntry;
import in.sunilpaulmathew.ashell.utils.Async;
import in.sunilpaulmathew.ashell.utils.Settings;
import in.sunilpaulmathew.ashell.utils.ShizukuShell;
import in.sunilpaulmathew.ashell.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 25, 2026
 */
public class PermissionsAdapter extends RecyclerView.Adapter<PermissionsAdapter.ViewHolder> {

    private final List<PermissionsEntry> data;
    private final String packageName;

    public PermissionsAdapter(List<PermissionsEntry> data, String packageName) {
        this.data = data;
        this.packageName = packageName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_permissions, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PermissionsEntry entry = this.data.get(position);
        holder.checkBox.setChecked(entry.isGranted());
        holder.title.setText(entry.getPermissionName());
        holder.description.setText(entry.getPermissionDescription());
        holder.icon.setImageBitmap(Settings.generateAlphabetIcon(String.valueOf(position + 1), 16, holder.itemView.getContext()));
        holder.checkBox.setEnabled(entry.isToggleable());

        holder.checkBox.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            int currentPosition = holder.getBindingAdapterPosition();
            PermissionsEntry currentEntry = this.data.get(currentPosition);
            if (currentPosition == RecyclerView.NO_POSITION) return;
            toggleState(currentEntry, currentPosition, context);
        });

        Settings.setSlideInAnimation(holder.title, position);
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    private void toggleState(PermissionsEntry currentEntry, int currentPosition, Context context) {
        new Async() {
            private boolean success = false;
            private ProgressDialog progressDialog;

            @Override
            public void onPreExecute() {
                progressDialog = new ProgressDialog(context);
                progressDialog.setProgressStatus(R.string.applying);
                progressDialog.startDialog();
            }

            @Override
            public void doInBackground() {
                String result = ShizukuShell.runCommand("pm " + (currentEntry.isGranted() ? "revoke " : "grant ") + packageName + " " + currentEntry.getPermissionDescription());
                if (result == null || result.trim().isEmpty()) {
                    success = true;
                    currentEntry.setGranted(!currentEntry.isGranted());
                }
            }

            @Override
            public void onPostExecute() {
                progressDialog.dismissDialog();
                if (!success) {
                    Utils.toast(context.getString(R.string.app_ops_failed_toast, currentEntry.getPermissionDescription()), context).show();
                } else {
                    notifyItemChanged(currentPosition);
                }
            }
        }.execute();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final AppCompatImageButton icon;
        private final MaterialCheckBox checkBox;
        private final MaterialTextView title, description;

        public ViewHolder(View view) {
            super(view);
            this.icon = view.findViewById(R.id.icon);
            this.checkBox = view.findViewById(R.id.checkbox);
            this.title = view.findViewById(R.id.title);
            this.description = view.findViewById(R.id.description);

            view.setOnClickListener(v -> {
                if (!this.checkBox.isEnabled()) {
                    Utils.toast(R.string.permissions_not_tweakable_toast, v.getContext()).show();
                }
            });
        }
    }

}