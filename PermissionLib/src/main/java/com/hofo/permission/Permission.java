package com.hofo.permission;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import java.util.List;


public class Permission {

    public static PermissionResult mPermissionResult;
    public static String KEY_PERMISSION = "KEY_PERMISSION";
    public static Application sApplication;

    public static void checkPermisson(Activity activity, String[] permissions, PermissionResult permissionResult) {
        mPermissionResult = permissionResult;
        sApplication = activity.getApplication();

        PermissionUtil.checkPermission(activity, permissions, new PermissionUtil.PermissionInterface() {
            @Override
            public void success() {
                if (Permission.mPermissionResult != null) {
                    Permission.mPermissionResult.success();
                }
            }

            @Override
            public void fail(List<String> permission) {
                Intent intent = new Intent(sApplication, PermissionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(KEY_PERMISSION, permission.toArray(new String[permission.size()]));
                sApplication.startActivity(intent);
            }
        });

    }
}