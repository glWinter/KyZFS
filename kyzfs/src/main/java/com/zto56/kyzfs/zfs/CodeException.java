package com.zto56.kyzfs.zfs;

import androidx.annotation.Keep;

@Keep
public class CodeException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String code = null;

    public CodeException(String code) {
        if (code == null)
            throw new NullPointerException();
        this.code = code;
    }

    public CodeException(String code, String detailMessage) {
        super(detailMessage);
        if (code == null)
            throw new NullPointerException();
        this.code = code;
    }

    public CodeException(String code, Throwable throwable) {
        super(throwable);
        if (code == null)
            throw new NullPointerException();
        this.code = code;
    }

    public CodeException(String code, String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
        if (code == null)
            throw new NullPointerException();
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getCodeAndMessage() {
        String msg = getLocalizedMessage();
        return (msg == null ? "" :msg) + "[" + code + "]";
    }

    @Override
    public String toString() {
        String name = getClass().getName();
        return name + ": " + getCodeAndMessage();
    }

}
