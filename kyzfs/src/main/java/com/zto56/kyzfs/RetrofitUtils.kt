package com.zto56.kyzfs

import com.zto56.kyzfs.interceptor.LoggingInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author gaolei46@ztoky.cn
 * 2023/5/26 15:49
 */
object RetrofitUtils {
    private const val baseUrl = "https://www.baidu.com"
    private var retrofit: Retrofit? = null

    val service: ZFSApiService by lazy {
        getRetrofit().create(ZFSApiService::class.java)
    }

    private fun getRetrofit(): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder().baseUrl(baseUrl).client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create()).build()
        }
        return retrofit!!
    }

    private fun getOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient().newBuilder()
        builder.run {
            addInterceptor(LoggingInterceptor())
            connectTimeout(8, TimeUnit.SECONDS)
            readTimeout(8, TimeUnit.SECONDS)
            writeTimeout(8, TimeUnit.SECONDS)
        }
        val client = builder.build()
        client.dispatcher().maxRequestsPerHost = 10
        return client
    }

}