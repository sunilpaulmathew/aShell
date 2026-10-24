package in.sunilpaulmathew.ashell.fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.adapters.LogcatAdapter;
import in.sunilpaulmathew.ashell.utils.Settings;
import in.sunilpaulmathew.ashell.utils.ShizukuShell;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 18, 2026
 */
public class LogcatFragment extends Fragment {

    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private final List<String> results = new CopyOnWriteArrayList<>();
    private final LogcatAdapter adapter = new LogcatAdapter(new CopyOnWriteArrayList<>());
    private int pauseColor, resumeColor;
    private MaterialButton stop;
    private RecyclerView recyclerView;
    private ShizukuShell shizukuShell;
    private String searchText = null;
    private Thread logcatThread;
    private volatile boolean isStreaming = false, darkTheme;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_logcat, container, false);

        MaterialButton back = rootView.findViewById(R.id.back);
        stop = rootView.findViewById(R.id.stop);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        TextInputEditText editText = rootView.findViewById(R.id.search_word);

        if (getContext() != null) {
            darkTheme = Settings.isDarkTheme(getContext());
        }
        pauseColor = Color.parseColor(darkTheme ? "#FFB74D" : "#E65100");
        resumeColor = Color.parseColor(darkTheme ? "#4FD8EB" : "#006874");

        if (getContext() != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        }
        recyclerView.setAdapter(adapter);

        stop.setOnClickListener(v -> {
            if (isStreaming) {
                stop.setIconResource(R.drawable.ic_resume);
                stop.setIconTint(ColorStateList.valueOf(resumeColor));
                stop.setTextColor(resumeColor);
                stopLogcatStream();
            } else {
                stop.setIconResource(R.drawable.ic_pause);
                stop.setIconTint(ColorStateList.valueOf(pauseColor));
                stop.setTextColor(pauseColor);
                startLogcatStream();
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                searchText = s.toString().trim().toLowerCase();
            }
        });

        back.setOnClickListener(v -> backToMain());

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backToMain();
            }
        });

        return rootView;
    }

    private final Runnable flushRunnable = new Runnable() {
        @Override
        public void run() {
            if (!results.isEmpty() && isAdded()) {
                List<String> batch = new ArrayList<>(results);
                results.clear();
                adapter.addLogs(batch, searchText);
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            }
            uiHandler.postDelayed(this, 500);
        }
    };

    private void backToMain() {
        stopLogcatStream();
        Fragment parent = getParentFragment();
        if (parent instanceof ExtrasFragment) {
            ((ExtrasFragment) parent).showExtrasGrid();
        }
    }

    private void startLogcatStream() {
        if (isStreaming) return;
        isStreaming = true;
        uiHandler.postDelayed(flushRunnable, 500);
        stop.setIconResource(R.drawable.ic_pause);
        stop.setIconTint(ColorStateList.valueOf(pauseColor));
        stop.setTextColor(pauseColor);

        logcatThread = new Thread(() -> {
            shizukuShell = new ShizukuShell(results, "logcat -v brief");
            if (!shizukuShell.isBusy()) {
                shizukuShell.exec();
            }

            if (isAdded() && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (isAdded()) {
                        adapter.addLogs(results, searchText);
                    }
                });
            } else {
                isStreaming = false;
            }
        });
        logcatThread.start();
    }

    private void stopLogcatStream() {
        isStreaming = false;
        uiHandler.removeCallbacks(flushRunnable);
        stop.setIconResource(R.drawable.ic_resume);
        stop.setIconTint(ColorStateList.valueOf(resumeColor));
        stop.setTextColor(resumeColor);
        results.clear();
        if (shizukuShell != null && shizukuShell.isBusy()) {
            shizukuShell.destroy();
        }
        if (logcatThread != null) {
            logcatThread.interrupt();
            logcatThread = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLogcatStream();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLogcatStream();
    }

    @Override
    public void onResume() {
        super.onResume();
        startLogcatStream();
    }
}