package com.zto56.kyzfs.zfs.http;

import androidx.annotation.Keep;

@Keep
public interface FileUploadCallback<T> extends HttpCallback<T> {

    void onProgressChange(long bytesWritten, long contentLength);

}
