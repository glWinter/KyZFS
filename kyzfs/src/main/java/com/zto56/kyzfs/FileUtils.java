package com.zto56.kyzfs;

import android.graphics.Bitmap;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author gaolei46@ztoky.cn
 * 2023/5/5 11:11
 */
public class FileUtils {

    public static File saveBitmap2FileAsQuality(Bitmap bitmap, String filepath, int quality) {
        File file = new File(filepath);//将要保存图片的路径
        makeDir(file);
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
        } catch (IOException e) {
            e.printStackTrace();
            file = null;
        } finally {
            if (bos != null) {
                try {
                    bos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //        }
        return file;
    }
    private static void makeDir(File file) {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void deleteFiles(String filesPath) {
        File file = new File(filesPath);
        if(file.exists()){
            file.delete();
        }
    }
    public static void deleteFiles(File file) {
        if(file.exists()){
            file.delete();
        }
    }

    public static void deleteDirectory(File file){
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File file1 : files) {
                    FileUtils.deleteFiles(file1);
                }
            }
        }
    }

    public static String imageForBase64(File file) {

        if (file.exists()) {
            byte[] buffer;
            FileInputStream in;
            try {
                in = new FileInputStream(file);
                buffer = new byte[(int) file.length() + 100];
                int length;
                length = in.read(buffer);
                String data = android.util.Base64.encodeToString(buffer, 0, length, android.util.Base64.DEFAULT);
                in.close();
                return data;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "";
    }

}
