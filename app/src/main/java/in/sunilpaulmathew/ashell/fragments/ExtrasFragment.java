package in.sunilpaulmathew.ashell.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.adapters.ExtrasAdapter;
import in.sunilpaulmathew.ashell.serializable.ExtrasEntry;
import in.sunilpaulmathew.ashell.utils.Settings;
import in.sunilpaulmathew.ashell.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 18, 2026
 */
public class ExtrasFragment extends BaseFragment {

    private ExtrasAdapter adapter;
    private Fragment activeFragment;
    private FragmentManager childManager;
    private FragmentContainerView subContainerView;
    private RecyclerView recyclerView;
    private static final String TAG_APPS = "apps_fragment";
    private static final String TAG_BATTERY = "battery_fragment";
    private static final String TAG_FILES = "files_fragment";
    private static final String TAG_CPU = "cpu_fragment";
    private static final String TAG_LOGS = "logs_fragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_extras, container, false);

        subContainerView = rootView.findViewById(R.id.sub_fragment_container);
        recyclerView = rootView.findViewById(R.id.recycler_view);

        childManager = getChildFragmentManager();

        if (savedInstanceState != null) {
            String savedTag = savedInstanceState.getString("savedTag");
            if (savedTag != null) {
                Fragment restoredFragment = childManager.findFragmentByTag(savedTag);
                if (restoredFragment != null) {
                    showSubPage(savedTag, restoredFragment);
                }
            }
        } else {
            activeFragment = null;
            showExtrasGrid();
        }

        adapter = new ExtrasAdapter(new CopyOnWriteArrayList<>(), id -> {
            switch (id) {
                case 4:
                    openLogsFragment();
                    break;
                case 3:
                    openBatteryFragment();
                    break;
                case 2:
                    openCPUFragment();
                    break;
                case 1:
                    openAppsFragment();
                    break;
                default:
                    openFilesFragment();
                    break;
            }
        });

        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), getSpanSize()));

        recyclerView.setAdapter(adapter);

        loadUI();

        mOnBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Settings.navigateToFragment(0, null, requireActivity());
            }
        };

        return rootView;
    }

    private int getSpanSize() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return 3;
        } else {
            return 2;
        }
    }

    private List<ExtrasEntry> getData() {
        List<ExtrasEntry> data = new ArrayList<>();
        for (ExtrasEntry entry : Settings.getExtras()) {
            if (Utils.getBoolean("extras_" + entry.getId(), false, requireActivity())) {
                data.add(entry);
            }
        }
        return data;
    }

    public void loadUI() {
        adapter.updateData(getData());
    }

    void showExtrasGrid() {
        if (activeFragment != null) {
            childManager.beginTransaction().hide(activeFragment).commit();
            activeFragment = null;
        }
        subContainerView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void openAppsFragment() {
        Fragment existing = childManager.findFragmentByTag(TAG_APPS);
        if (existing == null) {
            existing = new ApplicationsFragment();
        }
        showSubPage(TAG_APPS, existing);
    }

    private void openBatteryFragment() {
        Fragment existing = childManager.findFragmentByTag(TAG_BATTERY);
        if (existing == null) {
            existing = new BatteryMonitorFragment();
        }
        showSubPage(TAG_BATTERY, existing);
    }

    private void openCPUFragment() {
        Fragment existing = childManager.findFragmentByTag(TAG_CPU);
        if (existing == null) {
            existing = new CPUMonitorFragment();
        }
        showSubPage(TAG_CPU, existing);
    }

    private void openFilesFragment() {
        Fragment existing = childManager.findFragmentByTag(TAG_FILES);
        if (existing == null) {
            existing = new FilesFragment();
        }
        showSubPage(TAG_FILES, existing);
    }

    private void openLogsFragment() {
        Fragment existing = childManager.findFragmentByTag(TAG_LOGS);
        if (existing == null) {
            existing = new LogcatFragment();
        }
        showSubPage(TAG_LOGS, existing);
    }

    private void showSubPage(String tag, Fragment fragment) {
        recyclerView.setVisibility(View.GONE);
        subContainerView.setVisibility(View.VISIBLE);

        FragmentTransaction transaction = childManager.beginTransaction();

        if (activeFragment != null && activeFragment != fragment) {
            transaction.hide(activeFragment);
        }

        if (!fragment.isAdded()) {
            transaction.add(R.id.sub_fragment_container, fragment, tag);
        } else {
            transaction.show(fragment);
        }

        transaction.commit();

        activeFragment = fragment;
    }

    @Override
    protected void onPermissionGranted() {
    }

    @Override
    protected void onPermissionDenied() {
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (activeFragment != null) {
            outState.putString("savedTag", activeFragment.getTag());
        }
    }

    @Override
    protected void onSuccess() {
    }

}