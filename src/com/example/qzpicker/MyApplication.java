package com.example.qzpicker;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

public class MyApplication extends Application{
	
	 @Override
	    public void onCreate() {
	        super.onCreate();
	        SharedPreferences userSettings = getSharedPreferences("setting", 0);
	        String iscopy = userSettings.getString("iscopy", "");
	        if(iscopy.length()<=1){
	        	new Thread(new Runnable(){
	        		@Override
					public  void run(){
	        			String basePath = Environment.getExternalStorageDirectory().getPath();
	        			Log.d("the base path is", basePath);
	        			FileUtils.copyFilesFromAssets(getApplicationContext(),"fixstars.cat", basePath+"/Android/data/fixstars.cat");
	        			FileUtils.copyFilesFromAssets(getApplicationContext(),"seas_18.se1",basePath+"/Android/data/seas_18.se1");
	        			FileUtils.copyFilesFromAssets(getApplicationContext(),"semo_18.se1",basePath+"/Android/data/semo_18.se1");
	        			FileUtils.copyFilesFromAssets(getApplicationContext(),"sepl_18.se1",basePath+"/Android/data/sepl_18.se1");
	        		}
	        	}).start();
	        
	        	SharedPreferences.Editor editor = userSettings.edit();
	        	editor.putString("iscopy", "iscopy");
	        	editor.commit();
	        }
	        
	 }

}
