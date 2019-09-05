package com.hofo.permission;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;

import com.hofo.permission.util.LUtilScreenAdapter;

import java.util.HashMap;
import java.util.List;


public class PermissionActivity extends Activity {

    //权限的请求码
    private static final int REQUEST_PERMISSION_CODE_TAKE_PIC = 9;
    //去设置界面的请求码
    private static final int REQUEST_PERMISSION_SEETING = 8;
    private String[] RequestPermissions;
    //权限的解释集合
    private HashMap<String, String> permissionInfoMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LUtilScreenAdapter.match(this.getApplication(), 320);
        Intent intent = getIntent();
        if (intent != null) {
            RequestPermissions = intent.getStringArrayExtra(Permission.KEY_PERMISSION);
        }

        if (RequestPermissions != null) {
            checkPermiss();
        } else {
            if (Permission.mPermissionResult != null) {
                Permission.mPermissionResult.fail();
            }
            finish();
        }
    }

    /**
     * 请求权限
     */
    private void checkPermiss() {
        PermissionUtil.checkPermission(this, RequestPermissions, new PermissionUtil.PermissionInterface() {
            @Override
            public void success() {
                //请求成功
                finish();
                if (Permission.mPermissionResult != null) {
                    Permission.mPermissionResult.success();
                }
            }

            @Override
            public void fail(List<String> permissions) {
                getPermissionName();
                requestPermission(permissions.toArray(new String[permissions.size()]));
            }
        });
    }

    /**
     * 请求权限
     *
     * @param permissions
     */
    private void requestPermission(final String[] permissions) {
        //请求权限
        PermissionDialog permissionDialog = new PermissionDialog(PermissionActivity.this, R.id.btn_dialog_permission_comfrim);
        permissionDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //请求权限  将已经开启的权限 在点拒绝 会杀死当前进程和在设置界面关闭一样
                PermissionUtil.requestContactsPermissions(PermissionActivity.this, permissions, REQUEST_PERMISSION_CODE_TAKE_PIC);
            }
        });

        StringBuilder sb = new StringBuilder();
        for (String permission : permissions) {
            if (permissionInfoMap != null) {
                String s = permissionInfoMap.get(permission);
                if (!TextUtils.isEmpty(s)) {
                    sb.append(s + " ");
                }
            }
        }
        permissionDialog.setText(R.id.tv_dialog_permission_content, sb.toString());

        permissionDialog.show();
    }

    public void getPermissionName() {
        permissionInfoMap = new HashMap<>();
        permissionInfoMap.put("android.permission.WRITE_CONTACTS", "修改联系人");
        permissionInfoMap.put("android.permission.GET_ACCOUNTS", "访问账户Gmail列表");
        permissionInfoMap.put("android.permission.READ_CONTACTS", "读取联系人");
        permissionInfoMap.put("android.permission.READ_CALL_LOG", "读取通话记录");
        permissionInfoMap.put("android.permission.READ_PHONE_STATE", "读取电话状态");
        permissionInfoMap.put("android.permission.CALL_PHONE", "拨打电话");
        permissionInfoMap.put("android.permission.WRITE_CALL_LOG", "修改通话记录");
        permissionInfoMap.put("android.permission.USE_SIP", "使用SIP视频");
        permissionInfoMap.put("android.permission.PROCESS_OUTGOING_CALLS", "PROCESS_OUTGOING_CALLS");
        permissionInfoMap.put("com.android.voicemail.permission.ADD_VOICEMAIL", "ADD_VOICEMAIL");
        permissionInfoMap.put("android.permission.READ_CALENDAR", "读取日历");
        permissionInfoMap.put("android.permission.WRITE_CALENDAR", "修改日历");
        permissionInfoMap.put("android.permission.CAMERA", "拍照");
        permissionInfoMap.put("android.permission.BODY_SENSORS", "传感器");
        permissionInfoMap.put("android.permission.ACCESS_FINE_LOCATION", "获取精确位置");
        permissionInfoMap.put("android.permission.ACCESS_COARSE_LOCATION", "获取粗略位置");
        permissionInfoMap.put("android.permission.READ_EXTERNAL_STORAGE", "读存储卡");
        permissionInfoMap.put("android.permission.WRITE_EXTERNAL_STORAGE", "修改存储卡");
        permissionInfoMap.put("android.permission.RECORD_AUDIO", "录音");
        permissionInfoMap.put("android.permission.READ_SMS", "读取短信内容");
        permissionInfoMap.put("android.permission.RECEIVE_WAP_PUSH", "接收Wap Push");
        permissionInfoMap.put("android.permission.RECEIVE_MMS", "接收短信");
        permissionInfoMap.put("android.permission.SEND_SMS", "发送短信");
        permissionInfoMap.put("android.permission.READ_CELL_BROADCASTS", "READ_CELL_BROADCASTS");
        permissionInfoMap.put("android.permission.MOUNT_UNMOUNT_FILESYSTEMS", "访问文件系统");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LUtilScreenAdapter.cancelMatch(this.getApplication());
        if (permissionInfoMap != null) {
            permissionInfoMap.clear();
            permissionInfoMap = null;
        }

        RequestPermissions = null;
        Permission.mPermissionResult = null;
    }

    /**
     * 检测权限的回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, final String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CODE_TAKE_PIC) {
            if (PermissionUtil.verifyPermissions(grantResults)) {//有权限
                finish();
                if (Permission.mPermissionResult != null) {
                    Permission.mPermissionResult.success();
                }
            } else {
                //没有权限
                //这个返回false 表示勾选了不再提示
                if (!PermissionUtil.shouldShowPermissions(this, permissions)) {
                    startToSetting();
                } else {
                    //表示没有权限 ,但是没勾选不再提示
                    for (String s : permissions) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                                PermissionActivity.this, s)) {
                            //去掉已经允许的
                            if (permissionInfoMap != null) {
                                permissionInfoMap.remove(s);
                            }
                        }
                    }
                    requestPermission2(permissions);
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            finish();
            if (Permission.mPermissionResult != null) {
                Permission.mPermissionResult.fail();
            }
        }
    }

    /**
     * 请求权限2
     *
     * @param permissions
     */
    private void requestPermission2(final String[] permissions) {
        DeleteDialog deleteDialog = new DeleteDialog(PermissionActivity.this, R.id.btn_dialog_delete_comfrim);
        deleteDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //去掉已经请求过的权限
                List<String> deniedPermissions = PermissionUtil.findDeniedPermissions(PermissionActivity.this, permissions);
                //请求权限
                PermissionUtil.requestContactsPermissions(PermissionActivity.this, deniedPermissions.toArray(new String[deniedPermissions.size()]), REQUEST_PERMISSION_CODE_TAKE_PIC);
            }
        });

        StringBuilder sb = new StringBuilder();
        for (String permission : permissions) {
            if (permissionInfoMap != null) {
                String s = permissionInfoMap.get(permission);
                if (!TextUtils.isEmpty(s)) {
                    sb.append(s + " ");
                }
            }
        }
        deleteDialog.setText(R.id.tv_dialog_delete, "请允许\n" + sb + "权限请求");
        deleteDialog.show();
    }

    private void startToSetting() {
        DeleteDialog deleteDialog = new DeleteDialog(PermissionActivity.this, R.id.btn_dialog_delete_comfrim);
        deleteDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, REQUEST_PERMISSION_SEETING);
            }
        });

        deleteDialog.setText(R.id.tv_dialog_delete, "去设置界面开启权限?");
        deleteDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //如果是从设置界面返回,就继续判断权限
        if (requestCode == REQUEST_PERMISSION_SEETING) {
            checkPermiss();
        } else {
            finish();
            if (Permission.mPermissionResult != null) {

                Permission.mPermissionResult.fail();
            }
        }
    }
}
