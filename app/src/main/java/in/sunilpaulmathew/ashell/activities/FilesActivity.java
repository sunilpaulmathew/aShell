package in.sunilpaulmathew.ashell.activities;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.adapters.FilesAdapter;
import in.sunilpaulmathew.ashell.adapters.FoldersAdapter;
import in.sunilpaulmathew.ashell.adapters.TitleAdapter;
import in.sunilpaulmathew.ashell.dialogs.ProgressDialog;
import in.sunilpaulmathew.ashell.serializable.FilesEntry;
import in.sunilpaulmathew.ashell.utils.Async;
import in.sunilpaulmathew.ashell.utils.ShizukuShell;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on June 07, 2026
 */
public class FilesActivity extends BaseActivity {

    private FilesAdapter mFilesAdapter;
    private FoldersAdapter mFoldersAdapter;
    private RecyclerView mRecyclerViewFiles, mRecyclerViewFolders, mRecyclerViewTitle;
    private TitleAdapter mTitleAdapter;
    private final List<String> mTitles = new CopyOnWriteArrayList<>();
    private static String mPath = "/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_files, R.id.layout_main);

        mRecyclerViewTitle = findViewById(R.id.recycler_view_title);
        mRecyclerViewFiles = findViewById(R.id.recycler_view_files);
        mRecyclerViewFolders = findViewById(R.id.recycler_view_folders);

        loadUI(mPath).execute();

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (Objects.equals(mPath, "/")) {
                    finish();
                } else {
                    loadUI(String.join("/", mTitles.subList(1, mTitles.size() - 1))).execute();
                }
            }
        });
    }

    private Async loadUI(String parentPath) {
        return new Async() {
            private final Activity activity = FilesActivity.this;
            private ProgressDialog progressDialog = null;
            @Override
            public void onPreExecute() {
                mRecyclerViewFolders.setVisibility(GONE);
                mRecyclerViewFiles.setVisibility(GONE);
                progressDialog = new ProgressDialog(activity);
                progressDialog.setProgressIcon(R.drawable.ic_folder);
                progressDialog.setProgressStatus(R.string.loading);
                progressDialog.startDialog();
            }

            @Override
            public void doInBackground() {
                mTitles.clear();
                mTitles.add("root");

                String normalized = parentPath;

                while (normalized.startsWith("/")) {
                    normalized = normalized.substring(1);
                }

                if (!normalized.isEmpty()) {
                    Collections.addAll(mTitles, normalized.split("/"));
                }

                mPath = "/" + normalized;

                List<FilesEntry> files = new CopyOnWriteArrayList<>();
                List<String> folders = new CopyOnWriteArrayList<>();
                final String result = ShizukuShell.runCommand("ls -l " + mPath);
                if (!result.trim().isEmpty()) {
                    for (String lines : result.split("\\r?\\n")) {
                        if (lines != null && !lines.isEmpty()) {
                            String[] parts = lines.split("\\s+", 8);
                            try {
                                if (parts.length >= 8 && parts[0].charAt(0) == 'd') {
                                    folders.add(parts[7]);
                                } else {
                                    files.add(new FilesEntry(parts[7], parts[2], parts[3], normalized, Long.parseLong(parts[4]), parts[0].charAt(0), false));
                                }
                            } catch (Exception ignored) {
                            }
                        }
                    }
                }

                mFoldersAdapter = new FoldersAdapter(folders, normalized);
                mFilesAdapter = new FilesAdapter(files);
                mTitleAdapter = new TitleAdapter(mTitles);
            }

            @SuppressLint("StringFormatMatches")
            @Override
            public void onPostExecute() {
                try {
                    progressDialog.dismissDialog();
                } catch (Exception ignored) {
                }
                mFoldersAdapter.setOnItemClickListener((String newPath) -> loadUI(newPath).execute());
                mFilesAdapter.setOnItemClickListener((String newPath) -> loadUI(newPath).execute());
                mTitleAdapter.setOnItemClickListener((int position) -> loadUI(position == 0 ? "/" : "/" + String.join("/", mTitles.subList(1, position + 1))).execute());
                mRecyclerViewTitle.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
                mRecyclerViewTitle.setAdapter(mTitleAdapter);
                mRecyclerViewTitle.scrollToPosition(mTitles.size() - 1);
                mRecyclerViewFolders.setLayoutManager(new GridLayoutManager(activity, getSpanSize()));
                mRecyclerViewFolders.setAdapter(mFoldersAdapter);
                mRecyclerViewFiles.setLayoutManager(new GridLayoutManager(activity, getSpanSize()));
                mRecyclerViewFiles.setAdapter(mFilesAdapter);
                mRecyclerViewTitle.setVisibility(mTitleAdapter.getItemCount() > 1 ? VISIBLE : GONE);
                mRecyclerViewFolders.setVisibility(VISIBLE);
                mRecyclerViewFiles.setVisibility(VISIBLE);
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

}