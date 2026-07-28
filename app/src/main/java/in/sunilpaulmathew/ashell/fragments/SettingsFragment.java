package in.sunilpaulmathew.ashell.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import in.sunilpaulmathew.ashell.BuildConfig;
import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.adapters.SettingsAdapter;
import in.sunilpaulmathew.ashell.serializable.SettingsEntry;
import in.sunilpaulmathew.ashell.utils.Settings;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on July 27, 2026
 */
public class SettingsFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.layout_recycler_view, container, false);

        RecyclerView mRecyclerView = mRootView.findViewById(R.id.recycler_view);

        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        mRecyclerView.setAdapter(new SettingsAdapter(getData(), requireActivity()));

        mOnBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Settings.navigateToFragment(1, null, requireActivity());
            }
        };

        return mRootView;
    }

    private List<SettingsEntry> getData() {
        List<SettingsEntry> mData = new ArrayList<>();
        mData.add(new SettingsEntry(getString(R.string.user_interface)));
        mData.add(new SettingsEntry(1, R.mipmap.ic_launcher, getString(R.string.app_version, BuildConfig.VERSION_NAME), getString(R.string.copyright_text)));
        mData.add(new SettingsEntry(2, R.drawable.ic_theme, getString(R.string.app_theme), Settings.getAppTheme(requireActivity())));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && Settings.isDarkTheme(requireActivity())) {
            mData.add(new SettingsEntry(3, R.drawable.ic_amoled_theme, getString(R.string.app_theme_amoled), getString(R.string.app_theme_amoled_description), true, Settings.isAmoledBlackEnabled(requireActivity())));
        }
        mData.add(new SettingsEntry(4, R.drawable.ic_language, getString(R.string.language), Settings.getLanguageDescription(requireActivity())));
        mData.add(new SettingsEntry(getString(R.string.general)));
        mData.add(new SettingsEntry(5, R.drawable.ic_help, getString(R.string.examples), getString(R.string.examples_description)));
        mData.add(new SettingsEntry(6, R.drawable.ic_learn, getString(R.string.shizuku_learn), getString(R.string.shizuku_learn_description)));
        mData.add(new SettingsEntry(getString(R.string.miscellaneous)));
        mData.add(new SettingsEntry(7, R.drawable.ic_translate, getString(R.string.translations), getString(R.string.translations_description)));
        mData.add(new SettingsEntry(8, R.drawable.ic_privacy, getString(R.string.privacy_policy), getString(R.string.privacy_policy_description)));
        mData.add(new SettingsEntry(9, R.drawable.ic_email, getString(R.string.developer_contact), getString(R.string.developer_contact_description)));
        return mData;
    }

    @Override
    protected void onPermissionGranted() {
    }

    @Override
    protected void onPermissionDenied() {
    }

    @Override
    protected void onSuccess() {
    }

}