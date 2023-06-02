package com.zto56.kyzfs.zfs.model;

import androidx.annotation.Keep;

@Keep
public class GenericResponse<T> {

    public String message;
    public String status;
    public String statusCode;
    public T result;

}
