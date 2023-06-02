package com.zto56.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zto56.kyzfs.KyZFS
import com.zto56.kyzfs.KyZFSCb
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KyZFS.build {
            setAuthorization("")
        }
        KyZFS(this).apply {
            type = 0
            isPrivate = false
            maxSize = 70*1024
            maxItem = 3
            chooseVideo = false
            isShowWaterMaker = false
            authorization = ""
            refreshToken = ""
            url = ""
            selectedSize = 0
            waterMaker = ""
        }.start(object:KyZFSCb{
            override fun success(list: List<JSONObject>) {
            }
        })
    }
}