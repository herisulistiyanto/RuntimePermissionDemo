package com.android.indie.school.runtimepermissiondemo;

import android.Manifest;

/**
 * Created by herisulistiyanto on 12/11/16.
 */

public interface ConstantPermission {

    int CONST_MY_REQUEST_PERMISSION_CODE = 666;

    String PERMISSION_INTERNET = Manifest.permission.INTERNET;
    String PERMISSION_FINE_LOC = Manifest.permission.ACCESS_FINE_LOCATION;
    String PERMISSION_COARSE_LOC = Manifest.permission.ACCESS_COARSE_LOCATION;
    String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    String PERMISSION_READ_CONTACTS = Manifest.permission.READ_CONTACTS;
    String PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    String PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

}
