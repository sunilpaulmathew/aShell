package in.sunilpaulmathew.ashell.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.ContentValues;
import android.content.Context;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

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

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.adapters.CommandsAdapter;
import in.sunilpaulmathew.ashell.adapters.ShellOutputAdapter;
import in.sunilpaulmathew.ashell.dialogs.AccessUnavilableDialog;
import in.sunilpaulmathew.ashell.dialogs.BookMarkDialog;
import in.sunilpaulmathew.ashell.dialogs.BookMarkEditorDialog;
import in.sunilpaulmathew.ashell.dialogs.ExamplesDialog;
import in.sunilpaulmathew.ashell.serializable.CommandEntry;
import in.sunilpaulmathew.ashell.utils.Async;
import in.sunilpaulmathew.ashell.utils.ButtonAnimator;
import in.sunilpaulmathew.ashell.utils.Commands;
import in.sunilpaulmathew.ashell.utils.Settings;
import in.sunilpaulmathew.ashell.utils.ShizukuShell;
import in.sunilpaulmathew.ashell.utils.Utils;
import rikka.shizuku.Shizuku;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 28, 2022
 */
public class aShellFragment extends BaseFragment {

    private AppCompatImageButton mBookMark;
    private MaterialButton mBookMarksButton, mBottomArrow, mClearButton, mHistoryButton, mSaveButton, mSearchButton, mSendButton, mTopArrow;
    private TextInputEditText mCommand, mSearchWord;
    private RecyclerView mRecyclerViewOutput;
    private ScheduledExecutorService mExecutor = null;
    private ShellOutputAdapter mShellOutputAdapter = null;
    private ShizukuShell mShizukuShell = null;
    private int mPosition = 1, mShownCount = 0;
    private List<String> mHistory = null, mResult = null;
    private String mCommandShared = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments == null) return;

        mCommandShared = arguments.getString("command");
    }

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
        mSearchButton = mRootView.findViewById(R.id.search);
        mBookMark = mRootView.findViewById(R.id.bookmark);
        mBookMarksButton = mRootView.findViewById(R.id.bookmarks);
        mSendButton = mRootView.findViewById(R.id.send);
        mTopArrow = mRootView.findViewById(R.id.top);
        RecyclerView mRecyclerViewCommands = mRootView.findViewById(R.id.recycler_view_commands);
        mRecyclerViewOutput = mRootView.findViewById(R.id.recycler_view_output);
        mRecyclerViewOutput.setItemAnimator(null);
        mRecyclerViewOutput.setLayoutManager(new LinearLayoutManager(requireActivity()));

        if (mCommandShared != null) {
            setCommand(mCommandShared);
        }

        mBookMarksButton.setEnabled(!Utils.getBookmarks(requireActivity()).isEmpty());

        mCommand.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().trim().isEmpty()) {
                    if (s.toString().contains("\n")) {
                        if (!s.toString().endsWith("\n")) {
                            mCommand.setText(s.toString().replace("\n", ""));
                        }
                        initializeShell();
                    } else {
                        if (mShizukuShell != null && mShizukuShell.isBusy()) {
                            return;
                        }
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

                                if (Shizuku.pingBinder() && Shizuku.getVersion() >= 11 && Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                                    mCommandsAdapter = new CommandsAdapter(Commands.getPackageInfo(packageNamePrefix + "."), command -> {
                                        mCommand.setText(splitCommands[0].contains(" ") ? splitPrefix(splitCommands[0], 0) + " " + command : command);
                                        mCommand.setSelection(Objects.requireNonNull(mCommand.getText()).length());
                                        mRecyclerViewCommands.setVisibility(GONE);
                                    });
                                    mRecyclerViewCommands.setItemAnimator(null);
                                    mRecyclerViewCommands.setLayoutManager(new LinearLayoutManager(requireActivity()));
                                    mRecyclerViewCommands.setAdapter(mCommandsAdapter);
                                    mRecyclerViewCommands.setVisibility(VISIBLE);
                                }
                            } else {
                                mCommandsAdapter = new CommandsAdapter(Commands.getCommand(s.toString()), command -> {
                                    if (command.contains(" <")) {
                                        mCommand.setText(command.split("<")[0]);
                                    } else {
                                        mCommand.setText(command);
                                    }
                                    mCommand.setSelection(Objects.requireNonNull(mCommand.getText()).length());
                                });
                                mRecyclerViewCommands.setItemAnimator(null);
                                mRecyclerViewCommands.setLayoutManager(new LinearLayoutManager(requireActivity()));
                                mRecyclerViewCommands.setAdapter(mCommandsAdapter);
                                mRecyclerViewCommands.setVisibility(VISIBLE);
                            }
                        });
                        mSendButton.setIconTint(ColorStateList.valueOf(Settings.getColorAccent(requireActivity())));
                    }
                    requireActivity().runOnUiThread(() -> hideButtons());
                } else {
                    requireActivity().runOnUiThread(() -> {
                        mSendButton.setIcon(Utils.getDrawable(R.drawable.ic_help, requireActivity()));
                        mRecyclerViewCommands.setVisibility(GONE);
                        mBookMark.setVisibility(GONE);
                        showButtons();
                    });
                }
            }
        });

        mSendButton.setOnClickListener(v -> {
            if (mShizukuShell != null && mShizukuShell.isBusy()) {
                mShizukuShell.destroy();
            } else if (mCommand.getText() == null || mCommand.getText().toString().trim().isEmpty()) {
                new ExamplesDialog(requireActivity()) {
                    @Override
                    public void onCommandSelected(String command) {
                        setCommand(command);
                    }
                };
            } else {
                mRecyclerViewCommands.setVisibility(GONE);
                initializeShell();
            }
        });

        mClearButton.setOnClickListener(v -> {
            if (mResult == null) return;
            new ButtonAnimator(mClearButton, getString(R.string.clear_all)) {
                @Override
                public void onItemClicked() {
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
                }
            };
        });

        mSearchButton.setOnClickListener(v -> {
            mHistoryButton.setVisibility(GONE);
            mClearButton.setVisibility(GONE);
            mBookMarksButton.setVisibility(GONE);
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
                String query = s.toString().trim().toLowerCase();

                if (query.isEmpty()) {
                    updateUI(mResult, null);
                } else {
                    List<String> mResultSorted = new CopyOnWriteArrayList<>();
                    for (int i = mPosition; i < mResult.size(); i++) {
                        if (mResult.get(i).toLowerCase().contains(query)) {
                            mResultSorted.add(mResult.get(i));
                        }
                    }
                    updateUI(mResult, mResultSorted);
                }
            }
        });

        mBookMarksButton.setOnClickListener(v -> new ButtonAnimator(mBookMarksButton, getString(R.string.bookmarks)) {
            @Override
            public void onItemClicked() {
                new BookMarkDialog(stringToCommandsEntry(Utils.getBookmarks(requireActivity())), R.drawable.ic_bookmarks, getString(R.string.bookmarks), true, requireActivity()) {
                    @Override
                    public void onCommandSelected(String command, boolean toEdit) {
                        if (toEdit) {
                            new BookMarkEditorDialog(command, requireActivity()) {
                                @Override
                                public void editBookMark(String newCommand) {
                                    Utils.deleteFromBookmark(command, requireActivity());
                                    Utils.addToBookmark(newCommand, requireActivity());
                                    requireActivity().runOnUiThread(() -> mCommand.setText(newCommand));
                                }
                            };
                            return;
                        }
                        mCommand.setText(command);
                        mCommand.setSelection(command.length());
                    }
                };
            }
        });

        mHistoryButton.setOnClickListener(v -> new ButtonAnimator(mHistoryButton, getString(R.string.history)) {
            @Override
            public void onItemClicked() {
                new BookMarkDialog(stringToCommandsEntry(getRecentCommands()), R.drawable.ic_history, getString(R.string.history), false, requireActivity()) {
                    @Override
                    public void onCommandSelected(String command, boolean toEdit) {
                        mCommand.setText(command);
                        mCommand.setSelection(command.length());
                    }
                };
            }
        });

        mSaveButton.setOnClickListener(v -> new ButtonAnimator(mSaveButton, null) {
            @Override
            public void onItemClicked() {
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
            }
        });

        mTopArrow.setOnClickListener(v -> new ButtonAnimator(mTopArrow, "to Top") {
            @Override
            public void onItemClicked() {
                mRecyclerViewOutput.scrollToPosition(0);
            }
        });

        mBottomArrow.setOnClickListener(v -> new ButtonAnimator(mBottomArrow, "to Bottom") {
            @Override
            public void onItemClicked() {
                mRecyclerViewOutput.scrollToPosition(Objects.requireNonNull(
                        mRecyclerViewOutput.getAdapter()).getItemCount() - 1);
            }
        });

        mExecutor = Executors.newSingleThreadScheduledExecutor();
        AtomicInteger lastShownSize = new AtomicInteger(0);
        mExecutor.scheduleWithFixedDelay(() -> {
            if (mResult != null && mResult.size() != lastShownSize.get()) {
                lastShownSize.set(mResult.size());
                new Handler(Looper.getMainLooper()).post(() -> updateUI(mResult, null));
            }
        }, 0, 250, TimeUnit.MILLISECONDS);
        mOnBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (mSearchWord.getVisibility() == VISIBLE) {
                    hideSearchBar();
                    return;
                }

                if (mShizukuShell != null && mShizukuShell.isBusy()) {
                    new MaterialAlertDialogBuilder(requireActivity())
                            .setCancelable(false)
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle(getString(R.string.process_destroy_message))
                            .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                            })
                            .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> mShizukuShell.destroy()
                            ).show();
                    return;
                }
                onBackPressed();
            }
        };

        return mRootView;
    }

    private int lastIndexOf(String s, String splitTxt) {
        return s.lastIndexOf(splitTxt);
    }

    private List<CommandEntry> stringToCommandsEntry(List<String> strings) {
        if (strings == null || strings.isEmpty()) {
            return Collections.emptyList();
        }

        List<CommandEntry> commandsDB = Commands.commandList();

        List<CommandEntry> cleanDb = new ArrayList<>();
        for (CommandEntry entry : commandsDB) {
            String title = entry.getTitle();
            int index = title.indexOf('<');
            String prefix = (index != -1) ? title.substring(0, index).trim() : title.trim();
            cleanDb.add(new CommandEntry(prefix, entry.getSummary()));
        }

        List<CommandEntry> result = new ArrayList<>(strings.size());
        for (String inputCommand : strings) {
            if (inputCommand == null) continue;

            String matchedSummary = null;

            for (CommandEntry pair : cleanDb) {
                if (!pair.getTitle().isEmpty() && inputCommand.startsWith(pair.getTitle())) {
                    matchedSummary = pair.getSummary();
                    break;
                }
            }

            result.add(new CommandEntry(inputCommand, matchedSummary));
        }
        return result;
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

    private void hideButtons() {
        mTopArrow.setVisibility(GONE);
        mBottomArrow.setVisibility(GONE);
        mSaveButton.setVisibility(GONE);
    }

    private void showButtons() {
        if (mResult != null && !mResult.isEmpty()) {
            mSaveButton.setVisibility(VISIBLE);
            if (mResult.size() > 25) {
                mTopArrow.setVisibility(VISIBLE);
                mBottomArrow.setVisibility(VISIBLE);
            }
        }
    }

    private void setCommand(String command) {
        mCommand.setText(command);
        mCommand.setSelection(Objects.requireNonNull(mCommand.getText()).length());
        mBookMark.setVisibility(VISIBLE);
        mBookMark.setImageDrawable(Utils.getDrawable(Utils.isBookmarked(command, requireActivity()) ? R.drawable.ic_starred : R.drawable.ic_star, requireActivity()));
        mSendButton.setIcon(Utils.getDrawable(R.drawable.ic_send, requireActivity()));
        mBookMark.setOnClickListener(v -> bookMark(command));
    }

    private void bookMark(String string) {
        if (Utils.isBookmarked(string, requireActivity())) {
            Utils.deleteFromBookmark(string, requireActivity());
            Utils.toast(getString(R.string.bookmark_removed_message, string), requireActivity()).show();
            mBookMark.setContentDescription("Bookmark command");
        } else {
            Utils.addToBookmark(string, requireActivity());
            Utils.toast(getString(R.string.bookmark_added_message, string), requireActivity()).show();
            mBookMark.setContentDescription(getString(R.string.bookmark_removed_message, string));
        }
        mBookMark.setImageDrawable(Utils.getDrawable(Utils.isBookmarked(string, requireActivity()) ? R.drawable.ic_starred : R.drawable.ic_star, requireActivity()));
        mBookMarksButton.setEnabled(!Utils.getBookmarks(requireActivity()).isEmpty());
    }

    private void clearAll() {
        mResult.clear();
        mRecyclerViewOutput.setAdapter(null);
        mSearchButton.setEnabled(false);
        mClearButton.setEnabled(false);
        mCommand.setHint(getString(R.string.command_hint));
        hideButtons();
        if (!mCommand.isFocused()) mCommand.requestFocus();
    }

    private void hideSearchBar() {
        mSearchWord.setText(null);
        mSearchWord.setVisibility(GONE);
        if (!mCommand.isFocused()) mCommand.requestFocus();
        mBookMarksButton.setVisibility(VISIBLE);
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
            hideSearchBar();
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

        String mTitleText = "<font color=\"" + Settings.getColorAccent(requireActivity()) + "\">shell@" + Utils.getDeviceName() + "</font># <i>" + finalCommand + "</i>";

        if (mResult == null) {
            mResult = new CopyOnWriteArrayList<>();
        } else {
            mResult.add("<i></i>");
        }
        mResult.add(mTitleText);

        new Async() {
            private boolean permissionGranted;
            @Override
            public void onPreExecute() {
            }

            @Override
            public void doInBackground() {
                if (isPermissionGranted()) {
                    permissionGranted = true;
                    mPosition = mResult.size();
                    mShizukuShell = new ShizukuShell(mResult, finalCommand);
                    mShizukuShell.setStatusListener(status -> new Handler(Looper.getMainLooper()).post(() -> {
                        if (!isAdded()) return;
                        switch (status) {
                            case IDLE:
                                if (mCommand != null && !Objects.requireNonNull(mCommand.getText()).toString().trim().isEmpty()) {
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
                } else {
                    permissionGranted = false;
                }
            }

            @Override
            public void onPostExecute() {
                if (permissionGranted) {
                    if (mHistory != null && !mHistory.isEmpty() && !mHistoryButton.isEnabled()) {
                        mHistoryButton.setEnabled(true);
                    }
                    mBookMarksButton.setEnabled(!Utils.getBookmarks(requireActivity()).isEmpty());
                    if (mResult != null && !mResult.isEmpty()) {
                        mClearButton.setEnabled(true);
                        mSearchButton.setEnabled(true);
                        showButtons();
                    }
                } else {
                    requestPermission();
                }
            }
        }.execute();
    }

    private void requestFocus() {
        if (mCommand != null) {
            mCommand.post(() -> {
                if (isAdded() && isVisible()) {
                    mCommand.requestFocus();

                    InputMethodManager imm = (InputMethodManager) requireActivity()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(mCommand, InputMethodManager.SHOW_IMPLICIT);
                    }
                }
            });
        }
    }

    private void clearFocus() {
        if (mCommand != null) {
            mCommand.clearFocus();
        }

        if (isAdded()) {
            View view = requireActivity().getCurrentFocus();
            if (view == null && getView() != null) {
                view = getView();
            }

            if (view != null) {
                InputMethodManager imm = (InputMethodManager) requireActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }
    }

    public void updateCommand(String newCommand) {
        this.mCommandShared = newCommand;

        if (getView() != null) {
            setCommand(this.mCommandShared);
        }
    }

    private void updateUI(List<String> data, List<String> dataFiltered) {
        if (data == null && dataFiltered == null || !isAdded()) return;

        int mCount = dataFiltered != null ? dataFiltered.size() : data.size();

        /*
         * Output only ever gets appended, and swapping in a fresh adapter for that
         * discards every recycled view and relayouts the whole list. Only rebuild
         * when the backing list actually changed, such as when a filter is applied.
         */
        if (dataFiltered == null && mShellOutputAdapter != null
                && mRecyclerViewOutput.getAdapter() == mShellOutputAdapter && mCount > mShownCount) {
            int mInserted = mCount - mShownCount;
            mShellOutputAdapter.setItemCount(mCount);
            mShellOutputAdapter.notifyItemRangeInserted(mShownCount, mInserted);
        } else {
            mShellOutputAdapter = new ShellOutputAdapter(data, dataFiltered);
            mRecyclerViewOutput.setAdapter(mShellOutputAdapter);
        }
        mShownCount = mCount;

        if (mCount > 0) {
            mRecyclerViewOutput.scrollToPosition(mCount - 1);
        }
    }

    @Override
    protected void onPermissionGranted() {
        mResult.add("aShell got access to Shizuku service");
        ShizukuShell.ensureUserService(Commands::loadPackageInfo);
    }

    @Override
    protected void onPermissionDenied() {
        mResult.add(getString(R.string.shizuku_access_denied_title));
    }

    @Override
    protected void onSuccess() {
        Commands.loadPackageInfo();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mExecutor != null) {
            mExecutor.shutdownNow();
            mExecutor = null;
        }
        mShellOutputAdapter = null;
        mShownCount = 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mShizukuShell != null) mShizukuShell.destroy();

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            requestFocus();
        } else {
            clearFocus();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        clearFocus();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isAdded() && isVisible() && isResumed()) {
            requestFocus();
        }
    }

}