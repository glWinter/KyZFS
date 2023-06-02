package com.zto56.kyzfs

import com.zto56.kyzfs.Const.Companion.ERROR_GET_UPLOAD_TOKEN
import com.zto56.kyzfs.RetrofitUtils.service
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * @author gaolei46@ztoky.cn
 * 2023/5/26 16:01
 */
object ZFSRequest {
    suspend fun getUploadToken(url: String, refreshToken:String,authorization: String): UploadTokenModel =
        suspendCancellableCoroutine { cont ->
            service.getUploadToken(url,refreshToken, authorization)
                .enqueue(object : retrofit2.Callback<UploadTokenModel> {
                    override fun onResponse(
                        call: Call<UploadTokenModel>, response: Response<UploadTokenModel>
                    ) {
                        if (response.body() != null && response.body()!!.result != null) {
                            cont.resume(response.body()!!)
                        } else {
                            cont.resumeWithException(
                                MyRuntimeException(
                                    ERROR_GET_UPLOAD_TOKEN, "获取token失败"
                                )
                            )
                        }
                    }

                    override fun onFailure(call: Call<UploadTokenModel>, t: Throwable) {
                        cont.resumeWithException(
                            MyRuntimeException(
                                ERROR_GET_UPLOAD_TOKEN, "获取token失败"
                            )
                        )
                    }
                })
        }

}