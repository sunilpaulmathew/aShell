package in.sunilpaulmathew.ashell.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.adapters.FilesAdapter;
import in.sunilpaulmathew.ashell.adapters.FoldersAdapter;
import in.sunilpaulmathew.ashell.adapters.TitleAdapter;
import in.sunilpaulmathew.ashell.dialogs.AccessUnavilableDialog;
import in.sunilpaulmathew.ashell.dialogs.ProgressDialog;
import in.sunilpaulmathew.ashell.serializable.FilesEntry;
import in.sunilpaulmathew.ashell.utils.Async;
import in.sunilpaulmathew.ashell.utils.Settings;
import in.sunilpaulmathew.ashell.utils.ShizukuShell;
import in.sunilpaulmathew.ashell.utils.Utils;
import rikka.shizuku.Shizuku;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on June 07, 2026
 */
public class FilesFragment extends BaseFragment {

    private FilesAdapter mFilesAdapter;
    private FoldersAdapter mFoldersAdapter;
    private MaterialButton mBack;
    private RecyclerView mRecyclerViewTitle;
    private TitleAdapter mTitleAdapter;
    private final List<String> mTitles = new CopyOnWriteArrayList<>();
    private static String mPath = "/";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_files, container, false);

        mBack = mRootView.findViewById(R.id.back);
        mRecyclerViewTitle = mRootView.findViewById(R.id.recycler_view_title);
        RecyclerView mRecyclerViewFiles = mRootView.findViewById(R.id.recycler_view_files);
        RecyclerView mRecyclerViewFolders = mRootView.findViewById(R.id.recycler_view_folders);

        mFoldersAdapter = new FoldersAdapter(new CopyOnWriteArrayList<>(), filePath -> loadUI(filePath).execute());
        mFilesAdapter = new FilesAdapter(new CopyOnWriteArrayList<>(), newPath -> loadUI(newPath).execute());
        mTitleAdapter = new TitleAdapter(new CopyOnWriteArrayList<>(), position -> loadUI(position == 0 ? "/" : "/" + String.join("/", mTitles.subList(1, position + 1))).execute());

        mRecyclerViewTitle.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        mRecyclerViewFolders.setLayoutManager(new GridLayoutManager(requireActivity(), getSpanSize()));
        mRecyclerViewFiles.setLayoutManager(new GridLayoutManager(requireActivity(), getSpanSize()));

        mRecyclerViewTitle.setAdapter(mTitleAdapter);
        mRecyclerViewFolders.setAdapter(mFoldersAdapter);
        mRecyclerViewFiles.setAdapter(mFilesAdapter);

        if (Shizuku.pingBinder()) {
            if (isPermissionGranted()) {
                onSuccess();
            } else {
                requestPermission();
            }
        } else {
            new AccessUnavilableDialog(requireActivity()).show();
        }

        mRecyclerViewTitle.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        Settings.enableViewPagerSwiping(false, requireActivity());
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        Settings.enableViewPagerSwiping(true, requireActivity());
                        break;
                }
                return false;
            }
        });

        mBack.setOnClickListener(v -> backToMain());

        mOnBackPressedCallback = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                if (Objects.equals(mPath, "/")) {
                    backToMain();
                } else {
                    loadUI(String.join("/", mTitles.subList(1, mTitles.size() - 1))).execute();
                }
            }
        };

        return mRootView;
    }

    private Async loadUI(String parentPath) {
        return new Async() {
            private final Activity activity = requireActivity();
            private final List<FilesEntry> files = new CopyOnWriteArrayList<>(), folders = new CopyOnWriteArrayList<>();
            private boolean failed = false, permissionGranted;
            private ProgressDialog progressDialog = null;
            @Override
            public void onPreExecute() {
                progressDialog = new ProgressDialog(activity);
                progressDialog.setProgressIcon(R.drawable.ic_folder);
                progressDialog.setProgressStatus(R.string.loading);
                progressDialog.startDialog();
            }

            @Override
            public void doInBackground() {
                mTitles.clear();
                mTitles.add(getString(R.string.root));

                String normalized = parentPath;

                while (normalized.startsWith("/")) {
                    normalized = normalized.substring(1);
                }

                if (!normalized.isEmpty()) {
                    Collections.addAll(mTitles, normalized.split("/"));
                }

                mPath = "/" + normalized;

                if (isPermissionGranted()) {
                    permissionGranted = true;

                    final String result = ShizukuShell.runCommand("ls -l " + mPath);
                    if (!result.trim().isEmpty()) {
                        failed = false;
                        for (String lines : result.split("\\r?\\n")) {
                            if (lines != null && !lines.isEmpty()) {

                                String[] parts = lines.trim().split("\\s+", 8);
                                try {
                                    if (parts.length >= 8) {
                                        FilesEntry entry = new FilesEntry(
                                                parts[7], parts[2], parts[3],
                                                parts[5] + " " + parts[6],
                                                mPath, parts[0],
                                                Long.parseLong(parts[4]), false
                                        );

                                        if (parts[0].charAt(0) == 'd') {
                                            folders.add(entry);
                                        } else {
                                            files.add(entry);
                                        }
                                    }
                                } catch (Exception ignored) {
                                }
                            }
                        }
                    } else {
                        failed = true;
                    }
                } else {
                    permissionGranted = false;
                }
            }

            @SuppressLint("StringFormatMatches")
            @Override
            public void onPostExecute() {
                try {
                    progressDialog.dismissDialog();
                } catch (Exception ignored) {
                }
                if (permissionGranted) {
                    if (failed) {
                        Utils.toast(getString(R.string.file_system_access_error_toast), activity).show();
                        return;
                    }

                    mFilesAdapter.updateData(files);
                    mFoldersAdapter.updateData(folders);
                    mTitleAdapter.updateData(mTitles);

                    mRecyclerViewTitle.scrollToPosition(mTitles.size() - 1);
                    mRecyclerViewTitle.setVisibility(mTitleAdapter.getItemCount() > 1 ? VISIBLE : GONE);
                    mBack.setVisibility(mTitleAdapter.getItemCount() > 1 ? VISIBLE : GONE);
                } else {
                    requestPermission();
                }
            }
        };
    }

    private int getSpanSize() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return 4;
        } else {
            return 2;
        }
    }

    private void backToMain() {
        Fragment parent = getParentFragment();
        if (parent instanceof ExtrasFragment) {
            ((ExtrasFragment) parent).showExtrasGrid();
        }
    }

    @Override
    protected void onPermissionGranted() {
        ShizukuShell.ensureUserService(() -> loadUI(mPath).execute());
    }

    @Override
    protected void onPermissionDenied() {
        Utils.toast(getString(R.string.shizuku_access_denied_title), requireActivity()).show();
    }

    @Override
    protected void onSuccess() {
        ShizukuShell.ensureUserService(() -> loadUI(mPath).execute());
    }

}