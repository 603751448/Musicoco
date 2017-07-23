package com.duan.musicoco.app.manager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.duan.musicoco.R;
import com.duan.musicoco.shared.DialogProvider;

import static android.R.attr.dial;
import static android.R.attr.targetSdkVersion;

/**
 * Created by DuanJiaNing on 2017/5/25.
 */

public final class PermissionManager {

    private Context context;

    private static PermissionManager mInstance;

    private PermissionManager(Context context) {
        this.context = context;
    }

    public static PermissionManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PermissionManager(context);
        }
        return mInstance;
    }

    public static final class PerMap {

        public static final int CATEGORY_MEDIA_READ = 0x1;

        //权限名称
        private String name;

        //说明
        private String des;

        //类别，同时作为 activity onRequestPermissionsResult 方法的 requestCode
        private int category;

        //权限
        private String[] permissions;

        /**
         * 构造要申请的权限
         *
         * @param name        权限的名称（弹出请求权限对话框时将作为标题）
         * @param des         对权限用途的描述
         * @param category    权限所属类别
         * @param permissions
         */
        public PerMap(String name, String des, int category, String[] permissions) {
            this.name = name;
            this.des = des;
            this.category = category;
            this.permissions = permissions;
        }
    }

    //检查权限是否获取
    public boolean checkPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int re = ContextCompat.checkSelfPermission(context, permission);
            return re == PackageManager.PERMISSION_GRANTED;
        } else
            return false;
    }

    //检查权限是否获取
    public boolean checkPermission(String... permission) {

        boolean nr = true;

        for (int i = 0; i < permission.length; i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (targetSdkVersion >= Build.VERSION_CODES.M) {
                    // targetSdkVersion >= Android M, we can
                    // use Context#checkSelfPermission
                    nr = context.checkSelfPermission(permission[i])
                            == PackageManager.PERMISSION_GRANTED;
                } else {
                    // targetSdkVersion < Android M, we have to use PermissionChecker
                    nr = PermissionChecker.checkSelfPermission(context, permission[i])
                            == PermissionChecker.PERMISSION_GRANTED;
                }

                if (!nr) {
                    break;
                }
            }
        }
        return nr;
    }

    //检查权限是否获取

    public boolean checkPermission(PerMap perMap) {
        return checkPermission(perMap.permissions);
    }

    public interface OnPermissionRequestRefuse {
        void onRefuse();
    }

    //请求权限
    public void showPermissionRequestTip(final PerMap perMap, final Activity activity, final OnPermissionRequestRefuse refuse) {

        if (!checkPermission(perMap)) {

            DialogProvider provider = new DialogProvider(activity);
            final Dialog dialog = provider.createPromptDialog(
                    perMap.name,
                    perMap.des
            );
            provider.setOnPositiveButtonListener(activity.getString(R.string.continue_), new DialogProvider.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    ActivityCompat.requestPermissions(activity, perMap.permissions, perMap.category);
                }
            });
            provider.setOnNegativeButtonListener(activity.getString(R.string.cancel), new DialogProvider.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    refuse.onRefuse();
                }
            });
            dialog.setCancelable(false);
            dialog.show();
        }
    }

}
