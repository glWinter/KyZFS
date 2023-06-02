package com.zto56.kyzfs.zfs.http;

import com.zto56.kyzfs.zfs.model.GenericResponse;
import com.zto56.kyzfs.zfs.model.UploadResult;
import com.zto56.kyzfs.zfs.model.UploadToken;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface HttpService {

    @GET("GetWebUploadST")
    Call<UploadToken> requestUploadToken(@Header("X-Access-Token") String accessToken, @Query("appid") String appId);

    @Multipart
    @POST("{path}")
    Call<GenericResponse<UploadResult>> uploadFile(@Path("path") String path, @Header("X-Access-Token") String accessToken, @Part List<MultipartBody.Part> partList);

}
