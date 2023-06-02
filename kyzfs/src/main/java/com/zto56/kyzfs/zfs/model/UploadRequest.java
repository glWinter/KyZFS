package com.zto56.kyzfs.zfs.model;

import androidx.annotation.Keep;

import java.io.File;
import java.util.UUID;

@Keep
public class UploadRequest {

    private File mFile;
    /**
     * 文件扩展名，小写，如jpg、docx、zip
     */
    public String ext;
    /**
     * 存储组，公有或私有文件，公有文件指可通过文件路径直接访问
     */
    public boolean isPrivate;
    /**
     * 自定义文件名（不包含扩展名），filename与ext组合标识在各应用内唯一，若不填系统会随机生成文件名返回
     */
    public String filename;
    /**
     * 随机数，签名参数
     */
    public String nonce;
    /**
     * 时间戳，精确到秒，签名参数
     */
    public String timestamp;
    /**
     * 上传token，可以不指定，如果指定，则SDK内部不再获取上传token，以指定的为准
     */
    public String uploadToken;

    /**
     * 文件过期时间
     * 单位s
     */
    public Long expires;

    /**
     * 使用服务端文件名
     */
    public boolean returnServerFileName = false;

    public UploadRequest(File file) {
        this(file, null);
    }

    public UploadRequest(File file, String uploadToken) {
        if(file == null) throw new NullPointerException();
        mFile = file;
        this.ext = "";
        filename = file.getName();
        int index = filename.lastIndexOf(".");
        if(index >= 0) {
            if(index < filename.length() - 1) ext = filename.substring(index + 1);
            filename = filename.substring(0, index);
        }
        isPrivate = true;
        nonce = UUID.randomUUID().toString();
        timestamp = System.currentTimeMillis() / 1000 + "";
        this.uploadToken = uploadToken;
    }

    public File getFile() {
        return mFile;
    }

}
