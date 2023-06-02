package com.zto56.kyzfs.zfs;

import androidx.annotation.Keep;

@Keep
public class ServerException extends Exception {

    private int mServerCode;

    public ServerException(int serverCode, String message) {
        super(message);
        mServerCode = serverCode;
    }

    public int getCode() {
        return mServerCode;
    }

    public String getCodeAndMessage() {
        String msg = getLocalizedMessage();
        return (msg == null ? "" :msg) + "[" + mServerCode + "]";
    }

    @Override
    public String toString() {
        String name = getClass().getName();
        return name + ": " + getCodeAndMessage();
    }

}
