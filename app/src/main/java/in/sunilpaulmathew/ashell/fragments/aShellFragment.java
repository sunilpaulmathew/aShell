package in.sunilpaulmathew.ashell.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import in.sunilpaulmathew.ashell.BuildConfig;
import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.activities.ExamplesActivity;
import in.sunilpaulmathew.ashell.activities.SettingsActivity;
import in.sunilpaulmathew.ashell.adapters.CommandsAdapter;
import in.sunilpaulmathew.ashell.adapters.ShellOutputAdapter;
import in.sunilpaulmathew.ashell.dialogs.AccessUnavilableDialog;
import in.sunilpaulmathew.ashell.utils.Async;
import in.sunilpaulmathew.ashell.utils.Commands;
import in.sunilpaulmathew.ashell.utils.ShizukuPermissionChecker;
import in.sunilpaulmathew.ashell.utils.ShizukuShell;
import in.sunilpaulmathew.ashell.utils.Settings;
import in.sunilpaulmathew.ashell.utils.Utils;
import rikka.shizuku.Shizuku;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 28, 2022
 */
public class aShellFragment extends Fragment {

    private AppCompatAutoCompleteTextView mCommand;
    private AppCompatImageButton mBookMark;
    private MaterialButton mBookMarksButton, mBottomArrow, mClearButton, mHistoryButton, mInfoButton, mSaveButton, mSearchButton, mSendButton, mSettingsButton, mTopArrow;
    private TextInputEditText mSearchWord;
    private RecyclerView mRecyclerViewOutput;
    private ShizukuShell mShizukuShell = null;
    private boolean mExit, mPermissionGranted = false;
    private final Handler mHandler = new Handler();
    private final Shizuku.OnRequestPermissionResultListener REQUEST_PERMISSION_RESULT_LISTENER = this::onRequestPermissionsResult;
    private int mPosition = 1;
    private List<String> mHistory = null, mResult = null;
    private String mCommandShared = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments == null) return;

        mCommandShared = arguments.getString("command");
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_ashell, container, false);

        mCommand = mRootView.findViewById(R.id.shell_command);
        mSearchWord = mRootView.findViewById(R.id.search_word);
        mSaveButton = mRootView.findViewById(R.id.save_button);
        mBottomArrow = mRootView.findViewById(R.id.bottom);
        mClearButton = mRootView.findViewById(R.id.clear);
        mHistoryButton = mRootView.findViewById(R.id.history);
        mInfoButton = mRootView.findViewById(R.id.info);
        mSettingsButton = mRootView.findViewById(R.id.settings);
        mSearchButton = mRootView.findViewById(R.id.search);
        mBookMark = mRootView.findViewById(R.id.bookmark);
        mBookMarksButton = mRootView.findViewById(R.id.bookmarks);
        mSendButton = mRootView.findViewById(R.id.send);
        mTopArrow = mRootView.findViewById(R.id.top);
        mRecyclerViewOutput = mRootView.findViewById(R.id.recycler_view_output);
        mRecyclerViewOutput.setItemAnimator(null);
        mRecyclerViewOutput.setLayoutManager(new LinearLayoutManager(requireActivity()));

        mCommand.requestFocus();

        if (mCommandShared != null) {
            mCommand.setText(mCommandShared);
            mBookMark.setVisibility(VISIBLE);
            mBookMark.setImageDrawable(Utils.getDrawable(Utils.isBookmarked(mCommandShared, requireActivity()) ? R.drawable.ic_starred : R.drawable.ic_star, requireActivity()));
            mSendButton.setIcon(Utils.getDrawable(R.drawable.ic_send, requireActivity()));
            mBookMark.setOnClickListener(v -> bookMark(mCommandShared));
        }

        mBookMarksButton.setEnabled(!Utils.getBookmarks(requireActivity()).isEmpty());

        mCommand.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().contains("\n")) {
                    if (!s.toString().endsWith("\n")) {
                        mCommand.setText(s.toString().replace("\n", ""));
                    }
                    initializeShell();
                } else {
                    if (mShizukuShell != null && mShizukuShell.isBusy()) {
                        return;
                    }
                    RecyclerView mRecyclerViewCommands = mRootView.findViewById(R.id.recycler_view_commands);
                    if (!s.toString().trim().isEmpty()) {
                        mSendButton.setIcon(Utils.getDrawable(R.drawable.ic_send, requireActivity()));
                        mBookMark.setImageDrawable(Utils.getDrawable(Utils.isBookmarked(s.toString().trim(), requireActivity()) ? R.drawable.ic_starred : R.drawable.ic_star, requireActivity()));
                        mBookMark.setVisibility(VISIBLE);
                        mBookMark.setOnClickListener(v -> bookMark(s.toString().trim()));
                        new Handler(Looper.getMainLooper()).post(() -> {
                            CommandsAdapter mCommandsAdapter;
                            if (s.toString().matches(".*\\b(pm|am|appops|cmd)\\b.*") && s.toString().contains(".")) {
                                String[] splitCommands =  {
                                        s.toString().substring(0, lastIndexOf(s.toString(), ".")), s.toString().substring(lastIndexOf(s.toString(), "."))
                                };

                                String packageNamePrefix;
                                if (splitCommands[0].contains(" ")) {
                                    packageNamePrefix = splitPrefix(splitCommands[0], 1);
                                } else {
                                    packageNamePrefix = splitCommands[0];
                                }

                                mCommandsAdapter = new CommandsAdapter(Commands.getPackageInfo(packageNamePrefix + "."));
                                mRecyclerViewCommands.setItemAnimator(null);
                                mRecyclerViewCommands.setLayoutManager(new LinearLayoutManager(requireActivity()));
                                mRecyclerViewCommands.setAdapter(mCommandsAdapter);
                                mRecyclerViewCommands.setVisibility(VISIBLE);
                                mCommandsAdapter.setOnItemClickListener((command, v) -> {
                                    mCommand.setText(splitCommands[0].contains(" ") ? splitPrefix(splitCommands[0], 0) + " " + command : command);
                                    mCommand.setSelection(mCommand.getText().length());
                                    mRecyclerViewCommands.setVisibility(GONE);
                                });
                            } else {
                                mCommandsAdapter = new CommandsAdapter(Commands.getCommand(s.toString()));
                                mRecyclerViewCommands.setItemAnimator(null);
                                mRecyclerViewCommands.setLayoutManager(new LinearLayoutManager(requireActivity()));
                                mRecyclerViewCommands.setAdapter(mCommandsAdapter);
                                mRecyclerViewCommands.setVisibility(VISIBLE);
                                mCommandsAdapter.setOnItemClickListener((command, v) -> {
                                    if (command.contains(" <")) {
                                        mCommand.setText(command.split("<")[0]);
                                    } else {
                                        mCommand.setText(command);
                                    }
                                    mCommand.setSelection(mCommand.getText().length());
                                });
                            }
                        });
                    } else {
                        mSendButton.setIcon(Utils.getDrawable(R.drawable.ic_help, requireActivity()));
                        mBookMark.setVisibility(GONE);
                        mRecyclerViewCommands.setVisibility(GONE);
                    }
                    mSendButton.setIconTint(ColorStateList.valueOf(Settings.getColorAccent(requireActivity())));
                }
            }
        });

        mSendButton.setOnClickListener(v -> {
            if (mShizukuShell != null && mShizukuShell.isBusy()) {
                mShizukuShell.destroy();
            } else if (mCommand.getText() == null || mCommand.getText().toString().trim().isEmpty()) {
                Intent examples = new Intent(requireActivity(), ExamplesActivity.class);
                startActivity(examples);
            } else {
                initializeShell();
            }
        });

        mInfoButton.setOnClickListener(v -> {
            LayoutInflater mLayoutInflator = LayoutInflater.from(v.getContext());
            View aboutLayout = mLayoutInflator.inflate(R.layout.layout_about, null);
            MaterialTextView mAppTile = aboutLayout.findViewById(R.id.title);
            mAppTile.setText(v.getContext().getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME);

            new MaterialAlertDialogBuilder(v.getContext())
                    .setView(aboutLayout).show();
        });

        mSettingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), SettingsActivity.class);
            startActivity(intent);
        });

        mClearButton.setOnClickListener(v -> {
            if (mResult == null) return;
            if (Utils.getBoolean("clearAllMessage", true, requireActivity())) {
                new MaterialAlertDialogBuilder(requireActivity())
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle(getString(R.string.clear_all_message))
                        .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                        })
                        .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                            Utils.saveBoolean("clearAllMessage", false, requireActivity());
                            clearAll();
                        }).show();
            } else {
                clearAll();
            }
        });

        mSearchButton.setOnClickListener(v -> {
            mHistoryButton.setVisibility(GONE);
            mClearButton.setVisibility(GONE);
            mBookMarksButton.setVisibility(GONE);
            mInfoButton.setVisibility(GONE);
            mSettingsButton.setVisibility(GONE);
            mSearchButton.setVisibility(GONE);
            mSearchWord.setVisibility(VISIBLE);
            mSearchWord.requestFocus();
            mCommand.setText(null);
            mCommand.setHint(null);
        });

        mSearchWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || s.toString().trim().isEmpty()) {
                    updateUI(mResult).execute();
                } else {
                    List<String> mResultSorted = new CopyOnWriteArrayList<>();
                    for (int i = mPosition; i < mResult.size(); i++) {
                        if (mResult.get(i).toLowerCase().contains(s.toString().toLowerCase())) {
                            mResultSorted.add(mResult.get(i));
                        }
                    }
                    updateUI(mResultSorted).execute();
                }
            }
        });

        mBookMarksButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(requireContext(), mCommand);
            Menu menu = popupMenu.getMenu();
            for (int i = 0; i < Utils.getBookmarks(requireActivity()).size(); i++) {
                menu.add(Menu.NONE, i, Menu.NONE, Utils.getBookmarks(requireActivity()).get(i));
            }
            popupMenu.setOnMenuItemClickListener(item -> {
                mCommand.setText(Utils.getBookmarks(requireActivity()).get(item.getItemId()));
                mCommand.setSelection(mCommand.getText().length());
                return false;
            });
            popupMenu.show();
        });

        mHistoryButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(requireContext(), mCommand);
            Menu menu = popupMenu.getMenu();
            for (int i = 0; i < getRecentCommands().size(); i++) {
                menu.add(Menu.NONE, i, Menu.NONE, getRecentCommands().get(i));
            }
            popupMenu.setOnMenuItemClickListener(item -> {
                mCommand.setText(getRecentCommands().get(item.getItemId()));
                mCommand.setSelection(mCommand.getText().length());
                return false;
            });
            popupMenu.show();
        });

        mSaveButton.setOnClickListener(v -> {
            StringBuilder sb = new StringBuilder();
            for (int i = mPosition; i < mResult.size(); i++) {
                if (!mResult.get(i).equals("aShell: Finish") && !mResult.get(i).equals("<i></i>")) {
                    sb.append(mResult.get(i)).append("\n");
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.MediaColumns.DISPLAY_NAME, mHistory.get(mHistory.size() - 1)
                            .replace("/", "-").replace(" ", "") + ".txt");
                    values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
                    values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                    Uri uri = requireActivity().getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
                    OutputStream outputStream = requireActivity().getContentResolver().openOutputStream(Objects.requireNonNull(uri));
                    Objects.requireNonNull(outputStream).write(sb.toString().getBytes());
                    outputStream.close();
                } catch (IOException ignored) {
                }
            } else {
                if (requireActivity().checkCallingOrSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(), new String[] {
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, 0);
                    return;
                }
                Utils.create(sb.toString(), new File(Environment.DIRECTORY_DOWNLOADS, mHistory.get(mHistory.size() - 1)
                        .replace("/", "-").replace(" ", "") + ".txt"));
            }
            new MaterialAlertDialogBuilder(requireActivity())
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(getString(R.string.shell_output_saved_message, Environment.DIRECTORY_DOWNLOADS))
                    .setPositiveButton(getString(R.string.cancel), (dialogInterface, i) -> {
                    }).show();
        });

        mTopArrow.setOnClickListener(v -> mRecyclerViewOutput.scrollToPosition(0));

        mBottomArrow.setOnClickListener(v -> mRecyclerViewOutput.scrollToPosition(Objects.requireNonNull(
                mRecyclerViewOutput.getAdapter()).getItemCount() - 1));

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        AtomicInteger lastShownSize = new AtomicInteger(0);
        executor.scheduleWithFixedDelay(() -> {
            if (mResult != null && mResult.size() != lastShownSize.get()) {
                lastShownSize.set(mResult.size());
                new Handler(Looper.getMainLooper()).post(() -> updateUI(mResult).execute());
            }
        }, 0, 250, TimeUnit.MILLISECONDS);

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (mSearchWord.getVisibility() == VISIBLE) {
                    hideSearchBar();
                } else if (mShizukuShell != null && mShizukuShell.isBusy()) {
                    new MaterialAlertDialogBuilder(requireActivity())
                            .setCancelable(false)
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle(getString(R.string.process_destroy_message))
                            .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                            })
                            .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> mShizukuShell.destroy()
                            ).show();
                } else if (mExit) {
                    mExit = false;
                    requireActivity().finish();
                } else {
                    Utils.toast(getString(R.string.press_back), requireActivity()).show();
                    mExit = true;
                    mHandler.postDelayed(() -> mExit = false, 2000);
                }
            }
        });

        return mRootView;
    }

    private int lastIndexOf(String s, String splitTxt) {
        return s.lastIndexOf(splitTxt);
    }

    private List<String> getRecentCommands() {
        List<String> mRecentCommands = new ArrayList<>(mHistory);
        Collections.reverse(mRecentCommands);
        return mRecentCommands;
    }

    private String splitPrefix(String s, int i) {
        String[] splitPrefix = {
                s.substring(0, lastIndexOf(s, " ")), s.substring(lastIndexOf(s, " "))
        };
        return splitPrefix[i].trim();
    }

    private void bookMark(String string) {
        if (Utils.isBookmarked(string, requireActivity())) {
            Utils.deleteFromBookmark(string, requireActivity());
            Utils.toast(getString(R.string.bookmark_removed_message, string), requireActivity()).show();
        } else {
            Utils.addToBookmark(string, requireActivity());
            Utils.toast(getString(R.string.bookmark_added_message, string), requireActivity()).show();
        }
        mBookMark.setImageDrawable(Utils.getDrawable(Utils.isBookmarked(string, requireActivity()) ? R.drawable.ic_starred : R.drawable.ic_star, requireActivity()));
        mBookMarksButton.setEnabled(!Utils.getBookmarks(requireActivity()).isEmpty());
    }

    private void clearAll() {
        mResult.clear();
        mRecyclerViewOutput.setAdapter(null);
        mSearchButton.setEnabled(false);
        mSaveButton.setVisibility(GONE);
        mClearButton.setEnabled(false);
        mCommand.setHint(getString(R.string.command_hint));
        mTopArrow.setVisibility(GONE);
        mBottomArrow.setVisibility(GONE);
        if (!mCommand.isFocused()) mCommand.requestFocus();
    }

    private void hideSearchBar() {
        mSearchWord.setText(null);
        mSearchWord.setVisibility(GONE);
        if (!mCommand.isFocused()) mCommand.requestFocus();
        mBookMarksButton.setVisibility(VISIBLE);
        mInfoButton.setVisibility(VISIBLE);
        mSettingsButton.setVisibility(VISIBLE);
        mHistoryButton.setVisibility(VISIBLE);
        mClearButton.setVisibility(VISIBLE);
        mSearchButton.setVisibility(VISIBLE);
    }

    private void initializeShell() {
        if (!Shizuku.pingBinder()) {
            new AccessUnavilableDialog(requireActivity()).show();
            return;
        }
        if (mCommand.getText() == null || mCommand.getText().toString().trim().isEmpty()) {
            return;
        }
        if (mShizukuShell != null && mShizukuShell.isBusy()) {
            new MaterialAlertDialogBuilder(requireActivity())
                    .setCancelable(false)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(getString(R.string.app_name))
                    .setMessage(getString(R.string.app_working_message))
                    .setPositiveButton(getString(R.string.cancel), (dialogInterface, i) -> {
                    }).show();
            return;
        }
        runShellCommand(mCommand.getText().toString().replace("\n", ""));
    }

    private void runShellCommand(String command) {
        mCommand.setText(null);
        mCommand.setHint(null);
        mCommand.clearFocus();
        if (mSearchWord.getVisibility() == VISIBLE) {
            mSearchWord.setText(null);
            mSearchWord.setVisibility(GONE);
        }

        if (mTopArrow.getVisibility() == VISIBLE) {
            mTopArrow.setVisibility(GONE);
            mBottomArrow.setVisibility(GONE);
        }

        String finalCommand;
        if (command.startsWith("adb shell ")) {
            finalCommand = command.replace("adb shell ", "").trim();
        } else if (command.startsWith("adb -d shell ")) {
            finalCommand = command.replace("adb -d shell ", "").trim();
        } else {
            finalCommand = command.trim();
        }

        if (finalCommand.equals("clear")) {
            if (mResult != null) {
                clearAll();
            }
            return;
        }

        if (finalCommand.equals("exit")) {
            new MaterialAlertDialogBuilder(requireActivity())
                    .setCancelable(false)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(getString(R.string.quit_app_message))
                    .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                    })
                    .setPositiveButton(getString(R.string.quit), (dialogInterface, i) -> requireActivity().finish()).show();
            return;
        }

        if (finalCommand.startsWith("su")) {
            Utils.toast(getString(R.string.su_warning_message), requireActivity()).show();
            return;
        }

        if (mHistory == null) {
            mHistory = new ArrayList<>();
        }
        mHistory.add(finalCommand);

        mSaveButton.setVisibility(GONE);

        mHistoryButton.setEnabled(false);
        mBookMarksButton.setEnabled(false);
        mClearButton.setEnabled(false);
        mSearchButton.setEnabled(false);
        mInfoButton.setEnabled(false);
        mSettingsButton.setEnabled(false);

        String mTitleText = "<font color=\"" + Settings.getColorAccent(requireActivity()) + "\">shell@" + Utils.getDeviceName() + "</font># <i>" + finalCommand + "</i>";

        if (mResult == null) {
            mResult = new CopyOnWriteArrayList<>();
        } else {
            mResult.add("<i></i>");
        }
        mResult.add(mTitleText);

        new Async() {
            @Override
            public void onPreExecute() {
            }

            @Override
            public void doInBackground() {
                mPermissionGranted = Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED;
                if (mPermissionGranted) {
                    mPosition = mResult.size();
                    mShizukuShell = new ShizukuShell(mResult, finalCommand);
                    mShizukuShell.setStatusListener(status -> new Handler(Looper.getMainLooper()).post(() -> {
                        if (!isAdded()) return;
                        switch (status) {
                            case IDLE:
                                if (mCommand != null && !mCommand.getText().toString().trim().isEmpty()) {
                                    mSendButton.setIcon(Utils.getDrawable(R.drawable.ic_send, requireActivity()));
                                } else {
                                    mSendButton.setIcon(Utils.getDrawable(R.drawable.ic_help, requireActivity()));
                                }
                                mSendButton.setIconTint(ColorStateList.valueOf(Settings.getColorAccent(requireActivity())));
                                break;
                            case RUNNING:
                                mSendButton.setIcon(Utils.getDrawable(R.drawable.ic_stop, requireActivity()));
                                mSendButton.setIconTint(ColorStateList.valueOf(Utils.getColor(R.color.colorRed, requireActivity())));
                                break;
                        }
                    }));
                    mShizukuShell.exec();
                }
            }

            @Override
            public void onPostExecute() {
                if (mPermissionGranted) {
                    if (mHistory != null && !mHistory.isEmpty() && !mHistoryButton.isEnabled()) {
                        mHistoryButton.setEnabled(true);
                    }
                    mInfoButton.setEnabled(true);
                    mSettingsButton.setEnabled(true);
                    mBookMarksButton.setEnabled(!Utils.getBookmarks(requireActivity()).isEmpty());
                    if (mResult != null && !mResult.isEmpty()) {
                        mClearButton.setEnabled(true);
                        mSaveButton.setVisibility(VISIBLE);
                        mSearchButton.setEnabled(true);
                        if (mResult.size() > 25) {
                            mTopArrow.setVisibility(VISIBLE);
                            mBottomArrow.setVisibility(VISIBLE);
                        }
                    }
                } else {
                    new ShizukuPermissionChecker(requireActivity()) {
                        @Override
                        public void onRequestingPermission() {
                            // Request permission
                            Shizuku.addRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER);
                            Shizuku.requestPermission(0);
                        }

                        @Override
                        public void onFinished() {
                            Commands.loadPackageInfo();
                        }
                    };
                }
            }
        }.execute();
    }

    private Async updateUI(List<String> data) {
        return new Async() {
            private ShellOutputAdapter mShellOutputAdapter;
            @Override
            public void onPreExecute() {
            }

            @Override
            public void doInBackground() {
                if (data == null || data.isEmpty()) return;
                mShellOutputAdapter = new ShellOutputAdapter(mResult);
            }

            @Override
            public void onPostExecute() {
                if (isAdded()) {
                    if (data != null && !data.isEmpty()) {
                        mRecyclerViewOutput.setAdapter(mShellOutputAdapter);
                        mRecyclerViewOutput.scrollToPosition(mResult.size() - 1);
                    }
                }
            }
        };
    }

    private void onRequestPermissionsResult(int requestCode, int grantResult) {
        if (requestCode == 0 && grantResult == PackageManager.PERMISSION_GRANTED) {
            mResult.add("aShell got access to Shizuku service");
            ShizukuShell.ensureUserService(() -> {
                Commands.loadPackageInfo();
            });
        } else {
            mResult.add(getString(R.string.shizuku_access_denied_title));
        }
        Shizuku.removeRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Shizuku.removeRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER);
        if (mShizukuShell != null) mShizukuShell.destroy();
    }

}