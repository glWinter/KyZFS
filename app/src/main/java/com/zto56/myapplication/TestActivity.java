package com.zto56.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zto56.kyzfs.KyZFS;
import com.zto56.kyzfs.KyZFSCb;

import org.json.JSONObject;

import java.util.List;

/**
 * @author gaolei46@ztoky.cn
 * 2023/6/2 14:56
 */
public class TestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KyZFS zfs = new KyZFS.Builder().setAuthorization("")
                .setChooseVideo(false)
                .build(this);
        zfs.start(new KyZFSCb() {
            @Override
            public void success(@NonNull List<? extends JSONObject> list) {

            }
        });
    }
}
