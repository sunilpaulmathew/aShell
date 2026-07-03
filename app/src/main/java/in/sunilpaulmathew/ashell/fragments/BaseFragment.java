package in.sunilpaulmathew.ashell.fragments;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.utils.ShizukuPermissionChecker;
import in.sunilpaulmathew.ashell.utils.ShizukuShell;
import in.sunilpaulmathew.ashell.utils.Utils;
import rikka.shizuku.Shizuku;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on June 29, 2026
 */
public abstract class BaseFragment extends Fragment {

    private boolean mExit;
    protected OnBackPressedCallback mOnBackPressedCallback;
    private final Handler mHandler = new Handler();
    private final Shizuku.OnRequestPermissionResultListener REQUEST_PERMISSION_RESULT_LISTENER = this::onRequestPermissionsResult;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), mOnBackPressedCallback);
    }

    private void onRequestPermissionsResult(int requestCode, int grantResult) {
        if (requestCode == 0 && grantResult == PackageManager.PERMISSION_GRANTED) {
            onPermissionGranted();
        } else {
            onPermissionDenied();
        }
        Shizuku.removeRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER);
    }

    protected boolean isPermissionGranted() {
        return Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED;
    }

    protected void onBackPressed() {
        if (mExit) {
            mExit = false;
            requireActivity().finish();
        }
        Utils.toast(getString(R.string.press_back), requireActivity()).show();
        mExit = true;
        mHandler.postDelayed(() -> mExit = false, 2000);
    }

    protected void addPermissionListener() {
        Shizuku.addRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER);
    }

    protected void requestPermission() {
        new ShizukuPermissionChecker(requireActivity()) {
            @Override
            public void onRequestingPermission() {
                addPermissionListener();
                Shizuku.requestPermission(0);
            }

            @Override
            public void onFinished() {
                ShizukuShell.ensureUserService(() -> onSuccess());
            }
        };
    }

    protected abstract void onPermissionGranted();

    protected abstract void onPermissionDenied();

    protected abstract void onSuccess();

    @Override
    public void onDestroy() {
        super.onDestroy();
        Shizuku.removeRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (mOnBackPressedCallback != null) {
            mOnBackPressedCallback.setEnabled(!hidden);
        }
    }

}