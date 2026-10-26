package in.sunilpaulmathew.ashell.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.adapters.ApplicationsAdapter;
import in.sunilpaulmathew.ashell.dialogs.APKDetailsDialog;
import in.sunilpaulmathew.ashell.dialogs.AppOpsDialog;
import in.sunilpaulmathew.ashell.dialogs.BottomMenuDialog;
import in.sunilpaulmathew.ashell.dialogs.PermissionsDialog;
import in.sunilpaulmathew.ashell.dialogs.ProgressDialog;
import in.sunilpaulmathew.ashell.dialogs.SortOptionsDialog;
import in.sunilpaulmathew.ashell.serializable.AppOpsEntry;
import in.sunilpaulmathew.ashell.serializable.MenuEntry;
import in.sunilpaulmathew.ashell.serializable.PackageDetailsEntry;
import in.sunilpaulmathew.ashell.serializable.PackageEntry;
import in.sunilpaulmathew.ashell.serializable.PackageHeaderEntry;
import in.sunilpaulmathew.ashell.serializable.PermissionsEntry;
import in.sunilpaulmathew.ashell.utils.Async;
import in.sunilpaulmathew.ashell.utils.Packages;
import in.sunilpaulmathew.ashell.utils.ShizukuShell;
import in.sunilpaulmathew.ashell.utils.Utils;
import rikka.shizuku.Shizuku;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 18, 2026
 */
public class ApplicationsFragment extends Fragment {

    private ApplicationsAdapter adapter;
    private String searchTxt = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_applications, container, false);

        MaterialButton back = rootView.findViewById(R.id.back);
        MaterialButton sort = rootView.findViewById(R.id.sort);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        TextInputEditText searchWord = rootView.findViewById(R.id.search_word);

        recyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), getSpanSize()));

        adapter = new ApplicationsAdapter(new CopyOnWriteArrayList<>(), packageEntry -> {
            List<MenuEntry> menuEntries = new ArrayList<>();
            menuEntries.add(new MenuEntry(getString(R.string.get_info), R.drawable.ic_info_outlined, 0));
            menuEntries.add(new MenuEntry(getString(R.string.app_manager_clear_data), R.drawable.ic_refresh, 1));
            menuEntries.add(new MenuEntry(getString(R.string.app_manager_force_close), R.drawable.ic_close, 2));
            menuEntries.add(new MenuEntry(getString(R.string.app_ops_manage), R.drawable.ic_appops, 3));
            menuEntries.add(new MenuEntry(getString(R.string.permissions_manage), R.drawable.ic_permissions, 4));
            menuEntries.add(new MenuEntry(getString(packageEntry.isDisabled() ? R.string.app_manager_enable : R.string.app_manager_disable), packageEntry.isDisabled() ? R.drawable.ic_enable : R.drawable.ic_cancel, 5));
            menuEntries.add(new MenuEntry(getString(R.string.app_manager_export), R.drawable.ic_download, 6));
            if (packageEntry.isSystemApp()) {
                menuEntries.add(new MenuEntry(getString(R.string.app_manager_uninstall), R.drawable.ic_delete, 7));
            }
            menuEntries.add(new MenuEntry(getString(R.string.settings), R.drawable.ic_settings, 8));

            new BottomMenuDialog(menuEntries, packageEntry.getAppIcon(), packageEntry.getAppLabel(), packageEntry.getPackageName(), requireActivity()) {
                @Override
                public void onMenuItemClicked(int menuID) {
                    switch (menuID) {
                        case 8:
                            Intent settings = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            settings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            Uri uri = Uri.fromParts("package", packageEntry.getPackageName(), null);
                            settings.setData(uri);
                            startActivity(settings);
                            break;
                        case 7:
                            if (Shizuku.pingBinder() && Shizuku.getVersion() >= 11 &&
                                    Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                                uninstallApp(packageEntry);
                            }
                            break;
                        case 6:
                            exportApp(packageEntry);
                            break;
                        case 5:
                            changeAppState(packageEntry);
                            break;
                        case 4:
                            managePermissions(packageEntry);
                            break;
                        case 3:
                            manageAppOps(packageEntry);
                            break;
                        case 2:
                            forceCloseApp(packageEntry);
                            break;
                        case 1:
                            clearAppData(packageEntry);
                            break;
                        default:
                            getInfo(packageEntry);
                            break;
                    }
                }
            };

        });

        recyclerView.setAdapter(adapter);

        searchWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                loadPackages(s.toString().trim().toLowerCase(Locale.getDefault()), Utils.getInt("appType", 1, requireActivity()), false);
            }
        });

        loadPackages();

        back.setOnClickListener(v -> backToMain());

        sort.setOnClickListener(v -> new SortOptionsDialog(v.getContext()) {
            @Override
            public void onItemClicked() {
                loadPackages();
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backToMain();
            }
        });

        return rootView;
    }

    private int getSpanSize() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return 6;
        } else {
            return 3;
        }
    }

    private void backToMain() {
        Fragment parent = getParentFragment();
        if (parent instanceof ExtrasFragment) {
            ((ExtrasFragment) parent).showExtrasGrid();
        }
    }

    private void clearAppData(PackageEntry packageEntry) {
        new Async() {
            private final Activity activity = requireActivity();
            private ProgressDialog progressDialog;
            private String result;

            @Override
            public void onPreExecute() {
                progressDialog = new ProgressDialog(activity);
                progressDialog.setProgressStatus(R.string.applying);
                progressDialog.startDialog();
            }

            @Override
            public void doInBackground() {
                result = ShizukuShell.runCommand("pm clear --user " + Utils.getUserID() + " " + packageEntry.getPackageName());
            }

            @Override
            public void onPostExecute() {
                if (!isAdded() || isDetached() || activity.isDestroyed() || activity.isFinishing()) return;
                progressDialog.dismissDialog();
                if (result != null && !result.trim().isEmpty() && !result.trim().equalsIgnoreCase("success")) {
                    new MaterialAlertDialogBuilder(activity)
                            .setIcon(packageEntry.getAppIcon())
                            .setTitle(packageEntry.getAppLabel())
                            .setMessage(result)
                            .setPositiveButton(R.string.cancel, (dialogInterface, i) -> {
                            }).show();
                }
            }
        }.execute();
    }

    private void changeAppState(PackageEntry packageEntry) {
        new Async() {
            private final Activity activity = requireActivity();
            private ProgressDialog progressDialog;
            private String result;

            @Override
            public void onPreExecute() {
                progressDialog = new ProgressDialog(activity);
                progressDialog.setProgressStatus(R.string.applying);
                progressDialog.startDialog();
            }

            @Override
            public void doInBackground() {
                if (packageEntry.isDisabled()) {
                    result = ShizukuShell.runCommand("pm enable --user " + Utils.getUserID() + " " + packageEntry.getPackageName());
                } else {
                    result = ShizukuShell.runCommand("pm disable-user --user " + Utils.getUserID() + " " + packageEntry.getPackageName());
                }
            }

            @Override
            public void onPostExecute() {
                if (!isAdded() || isDetached() || activity.isDestroyed() || activity.isFinishing()) return;
                progressDialog.dismissDialog();
                if (result != null && (result.contains("new state: disabled") || result.contains("new state: enabled"))) {
                    loadPackages(searchTxt, Utils.getInt("appType", 1, activity), true);
                } else {
                    new MaterialAlertDialogBuilder(activity)
                            .setIcon(packageEntry.getAppIcon())
                            .setTitle(packageEntry.getAppLabel())
                            .setMessage(result)
                            .setPositiveButton(R.string.cancel, (dialogInterface, i) -> {
                            }).show();
                }
            }
        }.execute();
    }

    private void exportApp(PackageEntry packageEntry) {
        new Async() {
            private final Activity activity = requireActivity();
            private ProgressDialog progressDialog;

            @Override
            public void onPreExecute() {
                progressDialog = new ProgressDialog(activity);
                progressDialog.setProgressStatus(R.string.applying);
                progressDialog.startDialog();
            }

            @Override
            public void doInBackground() {
                if (packageEntry.isAppBundle()) {
                    File exportDir = new File(Utils.getExportPath(activity), packageEntry.getPackageName());
                    exportDir.mkdirs();
                    for (File apks : packageEntry.splitAPKs()) {
                        try (FileInputStream inputStream = new FileInputStream(apks)) {
                            Utils.copy(inputStream, new File(exportDir, apks.getName()));
                        } catch (IOException ignored) {
                        }
                    }
                } else {
                    try (FileInputStream inputStream = new FileInputStream(packageEntry.getApkPath())) {
                        Utils.copy(inputStream, new File(Utils.getExportPath(activity), packageEntry.getAppLabel() + " " + packageEntry.getVersionName() + ".apk"));
                    } catch (IOException ignored) {
                    }
                }
            }

            @Override
            public void onPostExecute() {
                if (!isAdded() || isDetached() || activity.isDestroyed() || activity.isFinishing()) return;
                progressDialog.dismissDialog();
            }
        }.execute();
    }

    private void forceCloseApp(PackageEntry packageEntry) {
        new Async() {
            private final Activity activity = requireActivity();
            private ProgressDialog progressDialog;
            private String result;

            @Override
            public void onPreExecute() {
                progressDialog = new ProgressDialog(activity);
                progressDialog.setProgressStatus(R.string.applying);
                progressDialog.startDialog();
            }

            @Override
            public void doInBackground() {
                result = ShizukuShell.runCommand("am force-stop " + packageEntry.getPackageName());
            }

            @Override
            public void onPostExecute() {
                if (!isAdded() || isDetached() || activity.isDestroyed() || activity.isFinishing()) return;
                progressDialog.dismissDialog();
                if (result != null && !result.trim().isEmpty()) {
                    new MaterialAlertDialogBuilder(activity)
                            .setIcon(packageEntry.getAppIcon())
                            .setTitle(packageEntry.getAppLabel())
                            .setMessage(result)
                            .setPositiveButton(R.string.cancel, (dialogInterface, i) -> {
                            }).show();
                }
            }
        }.execute();
    }

    private void getInfo(PackageEntry packageEntry) {
        new Async() {
            private final Activity activity = requireActivity();
            private ProgressDialog progressDialog;
            private final List<PackageHeaderEntry> header = new CopyOnWriteArrayList<>();
            private final List<PackageDetailsEntry> details = new CopyOnWriteArrayList<>();

            @Override
            public void onPreExecute() {
                progressDialog = new ProgressDialog(activity);
                progressDialog.setProgressStatus(R.string.loading);
                progressDialog.startDialog();
            }

            private String getFileSize(long size) {
                if (size <= 0) return "Unknown";
                final String[] units = {"B", "KB", "MB", "GB", "TB"};
                int unitIndex = 0;
                double readableSize = size;
                while (readableSize >= 1024 && unitIndex < units.length - 1) {
                    readableSize /= 1024;
                    unitIndex++;
                }
                return String.format(Locale.getDefault(), "%.2f %s", readableSize, units[unitIndex]);
            }

            private void generatePackageInfo() {
                long totalSize = 0;
                for (File apkFile : packageEntry.splitAPKs()) {
                    totalSize += apkFile.length();
                }

                String apkSize = getFileSize(totalSize);

                header.add(new PackageHeaderEntry(packageEntry.getVersionName(), R.drawable.ic_apks));
                header.add(new PackageHeaderEntry(apkSize, R.drawable.ic_storage));
                header.add(new PackageHeaderEntry(Packages.sdkToAndroidVersion(packageEntry.getMinSDK()) + " +", R.drawable.ic_device));

                details.add(new PackageDetailsEntry(getString(R.string.app_version), packageEntry.getVersionName()));
                if (packageEntry.getVersionCode() != null) {
                    details.add(new PackageDetailsEntry(getString(R.string.version_code), packageEntry.getVersionCode()));
                }
                if (packageEntry.getCompileSDK() != -1) {
                    details.add(new PackageDetailsEntry(getString(R.string.sdk_version_compiled), Packages.sdkToAndroidVersion(packageEntry.getCompileSDK())));
                }
                if (packageEntry.getMinSDK() != -1) {
                    details.add(new PackageDetailsEntry(getString(R.string.sdk_version_min), Packages.sdkToAndroidVersion(packageEntry.getMinSDK())));
                }
                if (packageEntry.getTargetSDK() != -1) {
                    details.add(new PackageDetailsEntry(getString(R.string.sdk_version_target), Packages.sdkToAndroidVersion(packageEntry.getTargetSDK())));
                }
                details.add(new PackageDetailsEntry(getString(packageEntry.isAppBundle() ? R.string.size_bundle : R.string.size_apk), apkSize));
                if (packageEntry.isAppBundle()) {
                    details.add(new PackageDetailsEntry(getString(R.string.split_apks), getString(R.string.split_apks_count, packageEntry.splitAPKs().size()), packageEntry.splitAPKs()));
                }
            }

            @Override
            public void doInBackground() {
                generatePackageInfo();
            }

            @Override
            public void onPostExecute() {
                progressDialog.dismissDialog();
                new APKDetailsDialog(packageEntry.getAppIcon(), packageEntry.getAppLabel(), packageEntry.getPackageName(), header, details, activity);
            }
        }.execute();
    }

    private void loadPackages() {
        loadPackages(searchTxt, Utils.getInt("appType", 1, requireActivity()), false);
    }

    private void loadPackages(String text, int tabPosition,  boolean reload) {
        new Async() {
            private final Activity activity = requireActivity();
            private List<PackageEntry> data;
            private ProgressDialog progressDialog;
            @Override
            public void onPreExecute() {
                progressDialog = new ProgressDialog(activity);
                progressDialog.setProgressIcon(R.drawable.ic_apps);
                progressDialog.setProgressStatus(R.string.loading);
                progressDialog.startDialog();
            }

            @Override
            public void doInBackground() {
                data = getData(tabPosition);
                searchTxt = text;
            }

            private List<PackageEntry> getData(int tabPosition) {
                if (reload) {
                    Packages.acquireData();
                }
                List<PackageEntry> data = new ArrayList<>();
                boolean selected;

                for (PackageEntry packageEntry : Packages.getPackages()) {
                    if (tabPosition == 3) {
                        selected = packageEntry.isDisabled();
                    } else if (tabPosition == 2) {
                        selected = !packageEntry.isSystemApp() && !packageEntry.isDisabled();
                    } else if (tabPosition == 1) {
                        selected = packageEntry.isSystemApp() && !packageEntry.isDisabled();
                    } else {
                        selected = true;
                    }
                    if (selected) {
                        if (text == null || packageEntry.getPackageName().toLowerCase(Locale.getDefault()).contains(text)) {
                            data.add(packageEntry);
                        }
                    }

                    if (Utils.getBoolean("reverse_order", false, activity)) {
                        Collections.reverse(data);
                    }
                }
                return data;
            }

            @Override
            public void onPostExecute() {
                if (!isAdded() || isDetached() || activity.isDestroyed() || activity.isFinishing()) return;
                try {
                    if (progressDialog != null) progressDialog.dismissDialog();
                } catch (Exception ignored) {
                }
                adapter.updateData(data);
            }
        }.execute();
    }

    private void manageAppOps(PackageEntry packageEntry)  {
        new Async() {
            private final Activity activity = requireActivity();
            private final List<AppOpsEntry> appOps = new CopyOnWriteArrayList<>();
            private ProgressDialog progressDialog;

            @Override
            public void onPreExecute() {
                progressDialog = new ProgressDialog(activity);
                progressDialog.setProgressStatus(R.string.loading);
                progressDialog.startDialog();
            }

            @Override
            public void doInBackground() {
                String[] appOpsList = ShizukuShell.runCommand("cmd appops get " + packageEntry.getPackageName()).trim().split("\\r?\\n");
                for (String line : appOpsList) {
                    if (line.contains(":")) {
                        String[] splitOp = line.split(":");
                        String name = splitOp[0].trim();
                        String status = splitOp[1].trim();

                        if (line.contains("time=") && !line.equals("No operations.") && !name.equals("Uid mode")) {
                            appOps.add(new AppOpsEntry(name, status));
                        }
                    }
                }
            }

            @Override
            public void onPostExecute() {
                if (!isAdded() || isDetached() || activity.isDestroyed() || activity.isFinishing()) return;
                progressDialog.dismissDialog();
                if (!appOps.isEmpty()) {
                    new AppOpsDialog(packageEntry, appOps, activity);
                } else {
                    Utils.toast(getString(R.string.app_ops_empty_toast), activity).show();
                }
            }
        }.execute();
    }

    private void managePermissions(PackageEntry packageEntry) {
        new Async() {
            private final Activity activity = requireActivity();
            private List<PermissionsEntry> permissions;
            private ProgressDialog progressDialog;
            private String result;

            @Override
            public void onPreExecute() {
                progressDialog = new ProgressDialog(activity);
                progressDialog.setProgressStatus(R.string.loading);
                progressDialog.startDialog();
            }

            private List<PermissionsEntry> getPermissions(String dumpsysOutput) {
                List<PermissionsEntry> permissionsList = new ArrayList<>();
                boolean insideRuntime = false;
                boolean insideInstall = false;
                boolean insideRequested = false;

                Pattern statePattern = Pattern.compile("^\\s*([a-zA-Z0-9._]+):\\s*granted=(true|false)");
                Pattern nameOnlyPattern = Pattern.compile("^\\s*([a-zA-Z0-9._]+)$");

                String[] lines = dumpsysOutput.split("\\r?\\n");
                for (String line : lines) {
                    if (line.contains("runtime permissions:")) {
                        insideRuntime = true;
                        insideInstall = false;
                        insideRequested = false;
                        continue;
                    } else if (line.contains("install permissions:")) {
                        insideRuntime = false;
                        insideInstall = true;
                        insideRequested = false;
                        continue;
                    } else if (line.contains("requested permissions:")) {
                        insideRuntime = false;
                        insideInstall = false;
                        insideRequested = true;
                        continue;
                    } else if (line.contains("declared permissions:") || line.contains("permissions:")) {
                        if (!line.contains("install permissions") && !line.contains("runtime permissions")) {
                            insideRuntime = false;
                            insideInstall = false;
                            insideRequested = false;
                        }
                    }

                    if (insideRuntime) {
                        Matcher matcher = statePattern.matcher(line);
                        if (matcher.find()) {
                            String name = matcher.group(1);
                            boolean granted = Boolean.parseBoolean(matcher.group(2));
                            addOrUpdatePermission(permissionsList, name, granted, 0);
                        }
                    } else if (insideInstall) {
                        Matcher matcher = statePattern.matcher(line);
                        if (matcher.find()) {
                            String name = matcher.group(1);
                            boolean granted = Boolean.parseBoolean(matcher.group(2));
                            addOrUpdatePermission(permissionsList, name, granted, 1);
                        }
                    } else if (insideRequested) {
                        Matcher matcher = nameOnlyPattern.matcher(line);
                        if (matcher.find()) {
                            String name = matcher.group(1);
                            if (!existsInList(permissionsList, name)) {
                                permissionsList.add(new PermissionsEntry(name, false, 2));
                            }
                        }
                    }
                }

                return permissionsList;
            }

            private void addOrUpdatePermission(List<PermissionsEntry> list, String name, boolean granted, int type) {
                for (int i = 0; i < list.size(); i++) {
                    PermissionsEntry entry = list.get(i);
                    if (entry.getPermissionName().equals(name)) {
                        list.set(i, new PermissionsEntry(name, granted, type));
                        return;
                    }
                }
                list.add(new PermissionsEntry(name, granted, type));
            }

            private boolean existsInList(List<PermissionsEntry> list, String name) {
                for (PermissionsEntry entry : list) {
                    if (entry.getPermissionName().equals(name)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void doInBackground() {
                result = ShizukuShell.runCommand("dumpsys package " + packageEntry.getPackageName() + " | grep -E \"permission\"");
                permissions = getPermissions(result);
            }

            @Override
            public void onPostExecute() {
                if (!isAdded() || isDetached() || activity.isDestroyed() || activity.isFinishing()) return;
                progressDialog.dismissDialog();
                if (result != null && permissions != null && !permissions.isEmpty()) {
                    new PermissionsDialog(packageEntry, permissions, activity);
                } else {
                    Utils.toast(R.string.app_ops_empty_toast, activity).show();
                }
            }
        }.execute();
    }

    private void uninstallApp(PackageEntry packageEntry) {
        new Async() {
            private final Activity activity = requireActivity();
            private ProgressDialog progressDialog;
            private String result;

            @Override
            public void onPreExecute() {
                progressDialog = new ProgressDialog(activity);
                progressDialog.setProgressStatus(R.string.applying);
                progressDialog.startDialog();
            }

            @Override
            public void doInBackground() {
                result = ShizukuShell.runCommand("pm uninstall --user " + Utils.getUserID() + " " + packageEntry.getPackageName());
            }

            @Override
            public void onPostExecute() {
                if (!isAdded() || isDetached() || activity.isDestroyed() || activity.isFinishing()) return;
                progressDialog.dismissDialog();
                if (result != null && !result.trim().isEmpty() && !result.trim().equalsIgnoreCase("success")) {
                    new MaterialAlertDialogBuilder(activity)
                            .setIcon(packageEntry.getAppIcon())
                            .setTitle(packageEntry.getAppLabel())
                            .setMessage(result)
                            .setPositiveButton(R.string.cancel, (dialogInterface, i) -> {
                            }).show();
                } else {
                    loadPackages(searchTxt, Utils.getInt("appType", 1, activity), true);
                }
            }
        }.execute();
    }

}