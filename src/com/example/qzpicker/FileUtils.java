package com.example.qzpicker;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by shenhua on 1/17/2017.
 * Email shenhuanet@126.com
 */
public class FileUtils {

	public static void copyFilesFromAssets(Context context, String assetsPath, String savePath){
	       try {
	           String fileNames[] = context.getAssets().list(assetsPath);// 获取assets目录下的所有文件及目录名
	           if (fileNames.length > 0) {// 如果是目录
	               File file = new File(savePath);
	               file.mkdirs();// 如果文件夹不存在，则递归
	               for (String fileName : fileNames) {
	                   copyFilesFromAssets(context, assetsPath + "/" + fileName,
	                           savePath + "/" + fileName);
	               }
	           } else {// 如果是文件
	               InputStream is = context.getAssets().open(assetsPath);
	               FileOutputStream fos = new FileOutputStream(new File(savePath));
	               byte[] buffer = new byte[1024];
	               int byteCount = 0;
	               while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取
	                   // buffer字节
	                   fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
	               }
	               fos.flush();// 刷新缓冲区
	               is.close();
	               fos.close();
	           }
	       } catch (Exception e) {
	           // TODO Auto-generated catch block
	           e.printStackTrace();
	       }
	   }

}

