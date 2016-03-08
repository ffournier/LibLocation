package com.library.android.liblocation.exception;

public class ExceptionPermissionNotGranted extends SecurityException {

    public ExceptionPermissionNotGranted() {
        super("Permission Location has not granted for this application");
    }
}
