package com.zto56.kyzfs;

import java.io.Serializable;

/**
 * @author gaolei46@ztoky.cn
 * 2023/5/18 15:40
 */


public class UploadTokenModel implements Serializable{
    private Result result;

    private String statusCode;

    private int code;

    private boolean success;

    private String message;

    public void setResult(Result result) {
        this.result = result;
    }

    public Result getResult() {
        return this.result;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusCode() {
        return this.statusCode;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean getSuccess() {
        return this.success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public class Result implements Serializable {
        private String uploadToken;

        private String appId;

        public void setUploadToken(String uploadToken) {
            this.uploadToken = uploadToken;
        }

        public String getUploadToken() {
            return this.uploadToken;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getAppId() {
            return this.appId;
        }
    }
}
