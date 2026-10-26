package in.sunilpaulmathew.ashell.serializable;

import java.io.Serializable;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 25, 2026
 */
public class PermissionsEntry implements Serializable {

    private boolean isGranted;
    /*
     0 = RUNTIME: Toggleable by user
     1 = INSTALL: Normal / Non-toggleable (always granted)
     2 = REQUESTED: Declared / Requested only
     */
    private final int permissionType;
    private final String permissionName;

    public PermissionsEntry(String permissionName, boolean isGranted, int permissionType) {
        this.permissionName = permissionName;
        this.isGranted = isGranted;
        this.permissionType = permissionType;
    }

    public boolean isGranted() {
        return this.isGranted;
    }

    public boolean isToggleable() {
        return this.permissionType != 1;
    }

    public String getPermissionName() {
        if (permissionName == null) return "";

        int lastDotIndex = permissionName.lastIndexOf('.');
        if (lastDotIndex != -1 && lastDotIndex < permissionName.length() - 1) {
            return permissionName.substring(lastDotIndex + 1);
        }
        return permissionName;
    }

    public String getPermissionDescription() {
        return this.permissionName;
    }

    public void setGranted(boolean granted) {
        this.isGranted = granted;
    }

}