package com.zto56.kyzfs.zfs.http;

import androidx.annotation.Keep;

@Keep
public interface HttpCallback<T> {

    void onSuccess(T data);

    void onFailure(Throwable error);

}
