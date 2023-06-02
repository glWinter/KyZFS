package com.zto56.kyzfs.zfs.http;

import androidx.annotation.Keep;

import retrofit2.Call;

@Keep
public class Cancelable {

    Call mCall;

    Cancelable(Call call) {
        mCall = call;
    }

    public void cancel() {
        if(mCall != null) mCall.cancel();
    }

    public boolean isCanceled() {
        if(mCall != null) return mCall.isCanceled();
        return false;
    }

}
