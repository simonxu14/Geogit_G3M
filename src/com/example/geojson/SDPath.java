package com.example.geojson;

import android.os.Environment;

public class SDPath {
	 public String getSDPath(){ 
	     	String sdDir = null; 
	     	boolean sdCardExist = Environment.getExternalStorageState() 
	     	.equals(android.os.Environment.MEDIA_MOUNTED);
	     	if (sdCardExist) 
	     	{ 
	     	sdDir = Environment.getExternalStorageDirectory().getAbsolutePath();
	     	} 
	     	return sdDir; 
	     }


}
