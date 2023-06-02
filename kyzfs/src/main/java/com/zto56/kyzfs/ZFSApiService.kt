package com.zto56.kyzfs

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

/**
 * @author gaolei46@ztoky.cn
 * 2023/5/26 15:49
 */
interface ZFSApiService {
    @GET
    fun getUploadToken(@Url url:String,@Header("refresh-token") refreshToken:String,@Header("Authorization") authorization:String): Call<UploadTokenModel>
}