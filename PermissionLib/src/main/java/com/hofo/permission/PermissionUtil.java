package com.hofo.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Android 6.0 动态权限工具类
 */
public class PermissionUtil {

    /**
     * 检查权限
     *
     * @param activity
     * @param permissions //Requested permission group
     */
    public static void checkPermission(Activity activity, String[] permissions, PermissionInterface permissionInterface) {
        if (Build.VERSION.SDK_INT < 23) {
            permissionInterface.success();
            return;
        }
        List<String> deniedPermissions = findDeniedPermissions(activity, permissions);

        if (deniedPermissions != null && deniedPermissions.size() > 0) {
            permissionInterface.fail(deniedPermissions);
        } else if (deniedPermissions != null) {
            permissionInterface.success();
        }
    }

    /**
     * 找到没有授权的权限
     *
     * @param activity
     * @param permission
     * @return
     */
    public static List<String> findDeniedPermissions(Activity activity, String... permission) {
        List<String> denyPermissions = new ArrayList<>();
        for (String value : permission) {
            if (ContextCompat.checkSelfPermission(activity, value) != PackageManager.PERMISSION_GRANTED) {
                if (!TextUtils.isEmpty(value)) {
                    denyPermissions.add(value);
                }
            }
        }
        return denyPermissions;
    }

    /**
     * 请求权限
     */
    public static void requestContactsPermissions(Activity activity, String[] permissions, int requestCode) {
        //默认值为false，但用户拒绝一次该权限，则为true
        if (shouldShowPermissions(activity, permissions)) {
            //需要向用户展示提示界面，指导用户开启权限（给用户洗脑）
        } else {
            //无需提示用户界面，直接请求权限，如果用户点不再被询问，即使请求没有权限请求权限对话框
        }
        ActivityCompat.requestPermissions(
                activity, permissions, requestCode);
    }

    /**
     * 检测这些权限是否不需要授权（提示用户）
     *
     * @param activity
     * @param permission
     * @return
     */
    public static boolean shouldShowPermissions(Activity activity, String... permission) {
        for (String value : permission) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity, value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 确定所请求的权限是否成功
     *
     * @param grantResults
     * @return
     */
    public static boolean verifyPermissions(int[] grantResults) {
        // 必须至少检查一个结果。
        if (grantResults.length < 1) {
            return false;
        }

        // 验证是否已授予每个必需权限，否则返回false。
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public interface PermissionInterface {
        void success();

        void fail(List<String> permissions);
    }
}
