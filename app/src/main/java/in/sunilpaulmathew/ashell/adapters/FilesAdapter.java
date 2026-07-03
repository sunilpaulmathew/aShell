package in.sunilpaulmathew.ashell.adapters;

import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.dialogs.BottomMenuDialog;
import in.sunilpaulmathew.ashell.dialogs.FileInfoDialog;
import in.sunilpaulmathew.ashell.dialogs.ProgressDialog;
import in.sunilpaulmathew.ashell.serializable.FilesEntry;
import in.sunilpaulmathew.ashell.serializable.MenuEntry;
import in.sunilpaulmathew.ashell.utils.Async;
import in.sunilpaulmathew.ashell.utils.DeletingTask;
import in.sunilpaulmathew.ashell.utils.ShizukuShell;
import in.sunilpaulmathew.ashell.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on June 07, 2026
 */
public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.ViewHolder> {

    private final List<FilesEntry> data;
    private static ClickListener mClickListener;

    public FilesAdapter(List<FilesEntry> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public FilesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_files, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull FilesAdapter.ViewHolder holder, int position) {
        FilesEntry item = data.get(position);

        item.loadFileInfo(holder.imageIcon,
                holder.fileName,
                holder.fileSize);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final AppCompatImageView imageIcon;
        private final MaterialTextView fileName, fileSize;
        public ViewHolder(View view) {
            super(view);
            this.imageIcon = view.findViewById(R.id.image);
            this.fileName = view.findViewById(R.id.name);
            this.fileSize = view.findViewById(R.id.size);


            view.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                FilesEntry filesEntry = data.get(position);
                if (filesEntry.isSymbolicLink()) {
                    mClickListener.onItemClick(filesEntry.decodeSymLink());
                } else {
                    List<MenuEntry> menuEntry = new CopyOnWriteArrayList<>();
                    if (!filesEntry.hasExtension() || filesEntry.isTextFile()) {
                        menuEntry.add(new MenuEntry(v.getContext().getString(R.string.open), R.drawable.ic_open, 0));
                    }
                    menuEntry.add(new MenuEntry(v.getContext().getString(R.string.copy_storage), R.drawable.ic_copy, 1));
                    menuEntry.add(new MenuEntry(v.getContext().getString(R.string.copy_path), R.drawable.ic_clipboard, 2));
                    menuEntry.add(new MenuEntry(v.getContext().getString(R.string.get_info), R.drawable.ic_info, 3));
                    if (filesEntry.canWrite()) {
                        menuEntry.add(new MenuEntry(v.getContext().getString(R.string.delete), R.drawable.ic_delete, 4));
                    }
                    new BottomMenuDialog(menuEntry,
                            imageIcon.getDrawable(),
                            filesEntry.getName(),
                            filesEntry.getAbsolutePath(),
                            v.getContext()) {
                        @Override
                        public void onMenuItemClicked(int menuID) {
                            if (menuID == 0) {
                                new Async() {
                                    private ProgressDialog progressDialog;
                                    private String result = null;
                                    @Override
                                    public void onPreExecute() {
                                        progressDialog = new ProgressDialog(v.getContext());
                                        progressDialog.setProgressIcon(R.drawable.ic_open);
                                        progressDialog.setProgressStatus(v.getContext().getString(R.string.opening_message, filesEntry.getName()));
                                        progressDialog.startDialog();
                                    }

                                    @Override
                                    public void doInBackground() {
                                        result = ShizukuShell.runCommand("cat " + filesEntry.getAbsolutePath());
                                    }

                                    @Override
                                    public void onPostExecute() {
                                        progressDialog.dismissDialog();
                                        if (result.startsWith("cat: ") && result.contains(filesEntry.getAbsolutePath())) {
                                            Utils.toast(result, view.getContext()).show();
                                            return;
                                        }
                                        new MaterialAlertDialogBuilder(view.getContext())
                                                .setIcon(imageIcon.getDrawable())
                                                .setTitle(filesEntry.getName())
                                                .setMessage(result.trim())
                                                .setPositiveButton(R.string.cancel, (dialogInterface, i) -> {
                                                })
                                                .show();
                                    }
                                }.execute();
                            } else if (menuID == 1) {
                                new Async() {
                                    private boolean success = false;
                                    private ProgressDialog progressDialog;
                                    @Override
                                    public void onPreExecute() {
                                        progressDialog = new ProgressDialog(v.getContext());
                                        progressDialog.setProgressIcon(R.drawable.ic_copy);
                                        progressDialog.setProgressStatus(v.getContext().getString(R.string.copying_message, filesEntry.getName()));
                                        progressDialog.startDialog();
                                    }

                                    public InputStream pathToInputStream(String filePath) throws Exception {
                                        ParcelFileDescriptor pfd = ParcelFileDescriptor.open(
                                                new File(filePath),
                                                ParcelFileDescriptor.MODE_READ_ONLY
                                        );
                                        return new ParcelFileDescriptor.AutoCloseInputStream(pfd);
                                    }

                                    @Override
                                    public void doInBackground() {
                                        try {
                                            success = Utils.copy(pathToInputStream(filesEntry.getAbsolutePath()), new File(Utils.getExportPath(v.getContext()), filesEntry.getName()));
                                        } catch (Exception ignored) {
                                        }
                                    }

                                    @Override
                                    public void onPostExecute() {
                                        progressDialog.dismissDialog();
                                        Utils.toast(v.getContext().getString(success? R.string.copying_succeed_toast : R.string.copying_failed_toast, filesEntry.getName()), v.getContext()).show();
                                    }
                                }.execute();
                            } else if (menuID == 2) {
                                Utils.copyToClipboard(filesEntry.getAbsolutePath(), v.getContext());
                            } else if (menuID == 3) {
                                new FileInfoDialog(filesEntry, v.getContext());
                            } else if (menuID == 4) {
                                new DeletingTask(filesEntry.getName(), filesEntry.getAbsolutePath(), v.getContext()) {
                                    @Override
                                    public void notifyRemoval() {
                                        data.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, getItemCount());
                                    }
                                }.execute();
                            }
                        }
                    };
                }
            });
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        FilesAdapter.mClickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(String string);
    }

}