package com.example.interceptevents_tvn;

import androidx.annotation.NonNull;

interface onRequestPermissionResult {
    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                     @NonNull int[] grantResults);
}
