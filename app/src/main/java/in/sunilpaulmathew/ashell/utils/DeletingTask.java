package in.sunilpaulmathew.ashell.utils;

import android.content.Context;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.dialogs.ProgressDialog;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on June 24, 2026
 */
public abstract class DeletingTask extends Async {

    private final Context context;
    private final ProgressDialog progressDialog;
    private final String name, path;
    private String result = null;

    public DeletingTask(String name, String path, Context context) {
        this.name = name;
        this.path = path;
        this.context = context.getApplicationContext();
        this.progressDialog = new ProgressDialog(context);
    }

    @Override
    public void onPreExecute() {
        progressDialog.setProgressIcon(R.drawable.ic_delete);
        progressDialog.setProgressStatus(context.getString(R.string.deleting_message, name));
        progressDialog.startDialog();
    }

    @Override
    public void doInBackground() {
        result = ShizukuShell.runCommand("rm -r '" + path + "'");
    }

    @Override
    public void onPostExecute() {
        progressDialog.dismissDialog();
        if (result != null && !result.trim().isEmpty()) {
            new MaterialAlertDialogBuilder(context)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(R.string.app_name)
                    .setMessage(result.trim())
                    .setPositiveButton(R.string.cancel, (dialogInterface, i) -> {
                    }).show();
        } else {
            notifyRemoval();
            Utils.toast(context.getString(R.string.delete_success_toast, name), context).show();
        }
    }

    public abstract void notifyRemoval();
}