package com.zto56.kyzfs.zfs.http;

import android.text.TextUtils;

import androidx.annotation.Keep;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zto56.kyzfs.zfs.CodeException;
import com.zto56.kyzfs.zfs.ServerException;
import com.zto56.kyzfs.zfs.Utilities;
import com.zto56.kyzfs.zfs.model.GenericResponse;
import com.zto56.kyzfs.zfs.model.UploadRequest;
import com.zto56.kyzfs.zfs.model.UploadResult;
import com.zto56.kyzfs.zfs.model.UploadToken;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Keep
public final class HttpRepository {

    private static int CONNECT_TIMEOUT = 10;
    private static int READ_TIMEOUT = 10;
    private static int WRITE_TIMEOUT = 10;

    private static Environment sEnvironment;
    private static Executor sCustomExecutor;

    private static volatile HttpRepository sInstance = null;
    private static final byte[] INSTANCE_LOCKER = new byte[0];

    private Retrofit mRetrofit;
    private HttpService mService;

    /**
     * 绑定自定义的线程池，需要在其他方法之前调用
     * @param executor
     */
    public static void bindCustomExecutor(Executor executor) {
        sCustomExecutor = executor;
    }

    /**
     * 绑定请求环境，如果没有绑定，默认使用测试环境，需要在其他方法之前调用
     * @param environment
     */
    public static void bindEnvironment(Environment environment) {
        sEnvironment = environment;
    }

    public static HttpRepository getInstance() {
        if (sInstance == null) {
            synchronized (INSTANCE_LOCKER) {
                if (sInstance == null) {
                    sInstance = new HttpRepository();
                }
            }
        }
        return sInstance;
    }

    /**
     * 设置自定义接口超时时间
     * 默认10s
     * @param connectTimeout
     * @param readTimeout
     * @param writeTimeout
     */
    public static void setHttpTimeout(int connectTimeout, int readTimeout, int writeTimeout) {
        CONNECT_TIMEOUT = connectTimeout;
        READ_TIMEOUT = readTimeout;
        WRITE_TIMEOUT = writeTimeout;
    }

    private HttpRepository() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(sEnvironment == Environment.PRODUCTION ? "https://fs.zto.com" : "http://fs.test.ztosys.com")
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .client(getOkHttpClient());
        if(sCustomExecutor != null) builder.callbackExecutor(sCustomExecutor);
        mRetrofit = builder.build();
        mService = mRetrofit.create(HttpService.class);
    }

    public Gson getGson() {
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .setLenient()
                .create();
        return gson;
    }

    private OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .followRedirects(true)
                .hostnameVerifier((hostname, session) -> true);
        return builder.build();
    }

    /**
     * 请求上传的token
     * @param accessToken
     * @param appId
     * @param callback
     * @return
     */
    private Cancelable requestUploadToken(String accessToken, String appId, HttpCallback<UploadToken> callback) {
        Call call = mService.requestUploadToken(accessToken, appId);
        call.enqueue(new CallbackConverter<>(callback));
        return new Cancelable(call);
    }

    /**
     * 上传文件
     * @param accessToken
     * @param appId
     * @param request
     * @param callback
     * @return
     */
    public Cancelable uploadFile(String accessToken, String appId, UploadRequest request, FileUploadCallback<GenericResponse<UploadResult>> callback) {
        final String path = "UploadFile";
        // 如果指定了uploadToken，则不再请求uploadToken
        if(!TextUtils.isEmpty(request.uploadToken)) {
            return uploadFileImpl(path, accessToken, appId, request.uploadToken, request, callback);
        }
        final Cancelable[] cancelable = new Cancelable[1];
        cancelable[0] = requestUploadToken(accessToken, appId, new HttpCallback<UploadToken>() {
            @Override
            public void onSuccess(UploadToken data) {
                Cancelable uploadCancelable = uploadFileImpl(path, accessToken, appId, data.upload_token, request, callback);
                cancelable[0].mCall = uploadCancelable.mCall;
            }

            @Override
            public void onFailure(Throwable error) {
                if(callback != null) callback.onFailure(error);
            }
        });
        return cancelable[0];
    }

    /**
     * 上传加密文件
     * @param accessToken
     * @param appId
     * @param request
     * @param callback
     * @return
     */
    public Cancelable uploadEncryptFile(String accessToken, String appId, UploadRequest request, FileUploadCallback<GenericResponse<UploadResult>> callback) {
        final String path = "UploadEncryptFile";
        // 如果指定了uploadToken，则不再请求uploadToken
        if(!TextUtils.isEmpty(request.uploadToken)) {
            return uploadFileImpl(path, accessToken, appId, request.uploadToken, request, callback);
        }
        final Cancelable[] cancelable = new Cancelable[1];
        cancelable[0] = requestUploadToken(accessToken, appId, new HttpCallback<UploadToken>() {
            @Override
            public void onSuccess(UploadToken data) {
                Cancelable uploadCancelable = uploadFileImpl(path, accessToken, appId, data.upload_token, request, callback);
                cancelable[0].mCall = uploadCancelable.mCall;
            }

            @Override
            public void onFailure(Throwable error) {
                if(callback != null) callback.onFailure(error);
            }
        });
        return cancelable[0];
    }

    private Cancelable uploadFileImpl(String path, String accessToken, String appId, String uploadToken, UploadRequest request, FileUploadCallback<GenericResponse<UploadResult>> callback) {
        String signature;
        try {
            signature = Utilities.makeSignature(request.nonce, request.timestamp, uploadToken);
        }catch (NoSuchAlgorithmException e) {
            if(callback != null) callback.onFailure(e);
            return new Cancelable(null);
        }
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("ext", request.ext);
        builder.addFormDataPart("group", request.isPrivate ? "private" : "public");
        builder.addFormDataPart("appid", appId);
        builder.addFormDataPart("signature", signature);
        builder.addFormDataPart("nonce", request.nonce);
        builder.addFormDataPart("timestamp", request.timestamp);
        // 不传使用服务端文件名
        if (!request.returnServerFileName) {
            builder.addFormDataPart("filename", request.filename);
        }
        if (request.expires != null) {
            builder.addFormDataPart("expires", request.expires.toString());
        }
        builder.addFormDataPart("upload_token", uploadToken);
        //上传文件
        File file = request.getFile();
        UploadFileRequestBody uploadFileRequestBody = new UploadFileRequestBody(file, callback);
        try {
            builder.addFormDataPart("uploadfile", URLEncoder.encode(request.filename, "UTF-8"), uploadFileRequestBody);
        }catch (IOException e) {
            if(callback != null) callback.onFailure(e);
            return new Cancelable(null);
        }
        Call call = mService.uploadFile(path, accessToken, builder.build().parts());
        call.enqueue(new CallbackConverter<>(callback));
        return new Cancelable(call);
    }

    private static class CallbackConverter<T> implements Callback<T> {

        private HttpCallback<T> mCallback;

        public CallbackConverter(HttpCallback<T> callback) {
            mCallback = callback;
        }

        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            if(mCallback == null) return;
            if(!response.isSuccessful()) {
                ServerException serverException = new ServerException(response.code(), response.message());
                mCallback.onFailure(serverException);
                return;
            }
            T data = response.body();
            if(data instanceof GenericResponse) {
                GenericResponse curData = (GenericResponse)data;
                if(!"true".equalsIgnoreCase(curData.status)) {
                    mCallback.onFailure(new CodeException(curData.statusCode, curData.message));
                    return;
                }
            }else if(data instanceof UploadToken) {
                UploadToken curData = (UploadToken)data;
                if(!"true".equalsIgnoreCase(curData.status)) {
                    mCallback.onFailure(new CodeException(curData.statusCode, curData.message));
                    return;
                }
            }
            mCallback.onSuccess(data);
        }

        @Override
        public void onFailure(Call<T> call, Throwable error) {
            if(mCallback == null) return;
            mCallback.onFailure(error);
        }
    }

    @Keep
    public enum Environment {
        TEST, PRODUCTION
    };

}
