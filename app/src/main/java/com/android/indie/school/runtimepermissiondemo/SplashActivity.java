package com.android.indie.school.runtimepermissiondemo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity implements ConstantPermission{

    @BindView(R.id.tvSplash)
    TextView tvSplash;
    @BindView(R.id.activity_splash)
    View activitySplash;

    private SharedPreferences sharedPreferences;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected;
    private ArrayList<String> permissionsRequired;

    private final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        sharedPreferences = this.getSharedPreferences(SplashActivity.this.getString(R.string.shared_key), Context.MODE_PRIVATE);

        if (permissionsRequired != null) {
            permissionsRequired = null;
        }

        permissionsRequired = new ArrayList<>();
        permissionsRequired = addPermissionToRequest(permissionsRequired);

        if (BuildConfig.DEBUG) {
            Log.e(TAG, "PERMISSION SIZE : " + permissionsRequired.size());
            for (int i = 0; i < permissionsRequired.size(); i++) {
                Log.e(TAG, "PERMISSION REQUIRED : " + permissionsRequired.get(i));
            }
        }
        permissionsToRequest = getUnAskedPermissions(permissionsRequired);
        permissionsRejected = getRejectedPermissions(permissionsRequired);

        if (permissionsToRequest.isEmpty() && permissionsRejected.size() > 0) {
            permissionsToRequest.addAll(permissionsRejected);
        }

        permissionsRequestHandler(permissionsToRequest);

        goToNextScreen();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void goToNextScreen() {
        if (canProceedToNextScreen(permissionsRequired)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent menuIntent = new Intent(SplashActivity.this, MenuActivity.class);
                    startActivity(menuIntent);
                    finish();
                }
            }, 1000);
        }
    }

    private boolean isAboveLolipop() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    private boolean isHasPermission(final String permission) {
        if (isAboveLolipop()) {
            return (ContextCompat.checkSelfPermission(SplashActivity.this, permission) == PackageManager.PERMISSION_GRANTED);
        }
        return true;
    }

    private ArrayList<String> addPermissionToRequest(ArrayList<String> permissions) {

        if (!isHasPermission(PERMISSION_INTERNET)) {
            permissions.add(PERMISSION_INTERNET);
        }

        if (!isHasPermission(PERMISSION_CAMERA)) {
            permissions.add(PERMISSION_CAMERA);
        }

        if (!isHasPermission(PERMISSION_COARSE_LOC)) {
            permissions.add(PERMISSION_COARSE_LOC);
        }

        if (!isHasPermission(PERMISSION_FINE_LOC)) {
            permissions.add(PERMISSION_FINE_LOC);
        }

        if (!isHasPermission(PERMISSION_READ_CONTACTS)) {
            permissions.add(PERMISSION_READ_CONTACTS);
        }

        if (!isHasPermission(PERMISSION_READ_EXTERNAL_STORAGE)) {
            permissions.add(PERMISSION_READ_EXTERNAL_STORAGE);
        }

        if (!isHasPermission(PERMISSION_WRITE_EXTERNAL_STORAGE)) {
            permissions.add(PERMISSION_WRITE_EXTERNAL_STORAGE);
        }

        return permissions;
    }

    private ArrayList<String> getUnAskedPermissions(ArrayList<String> permissions) {
        ArrayList<String> results = new ArrayList<>();

        for (String perm : permissions) {
            if (!isHasPermission(perm) && shouldAskPermission(perm)) {
                results.add(perm);
            }
        }
        return results;
    }

    private ArrayList<String> getRejectedPermissions(ArrayList<String> permissions) {
        ArrayList<String> results = new ArrayList<>();

        for (String perm : permissions) {
            if (!isHasPermission(perm) && !shouldAskPermission(perm)) {
                results.add(perm);
            }
        }
        return results;
    }

    private boolean shouldAskPermission(String permission) {
        return (sharedPreferences.getBoolean(permission, true));
    }

    private void markAskedPermission(String permission) {
        sharedPreferences.edit().putBoolean(permission, false).apply();
    }

    private void clearMarkAskedPermission(String permission) {
        sharedPreferences.edit().putBoolean(permission, true).apply();
    }

    private void permissionsRequestHandler(ArrayList<String> permissions) {
        if (permissions.size() > 0) {
            ActivityCompat.requestPermissions(SplashActivity.this,
                    permissions.toArray(new String[permissions.size()]),
                    CONST_MY_REQUEST_PERMISSION_CODE);

            for (String perm : permissions) {
                markAskedPermission(perm);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CONST_MY_REQUEST_PERMISSION_CODE) {
            for (String perm : permissions) {
                if (!isHasPermission(perm)) {
                    permissionsRejected.add(perm);
                    clearMarkAskedPermission(perm);
                }
            }

            if (permissionsRejected.size() > 0) {
                showDialogRequest(SplashActivity.this.getString(R.string.dialog_msg));
            }

            goToNextScreen();
        }
    }

    private void showDialogRequest(final String msg) {
        new AlertDialog.Builder(SplashActivity.this)
                .setMessage(msg)
                .setPositiveButton(SplashActivity.this.getString(R.string.grant), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        permissionsToRequest.clear();
                        permissionsToRequest.addAll(permissionsRejected);
                        permissionsRequestHandler(permissionsToRequest);
                        permissionsRejected.clear();
                    }
                })
                .setNegativeButton(SplashActivity.this.getString(R.string.close_app), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .create()
                .show();
    }

    private boolean canProceedToNextScreen(ArrayList<String> required) {
        for (String perm : required) {
            if (!isHasPermission(perm)) {
                return false;
            }
        }
        return true;
    }
}
