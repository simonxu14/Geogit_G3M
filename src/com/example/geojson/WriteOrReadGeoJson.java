package com.example.geojson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.glob3.mobile.generated.JSONArray;
import org.glob3.mobile.generated.JSONBaseObject;
import org.glob3.mobile.generated.JSONGenerator;
import org.glob3.mobile.generated.JSONObject;
import org.glob3.mobile.specific.JSONParser_Android;




import android.view.*;



public class WriteOrReadGeoJson {
	
	private BufferedReader file;
	
	//simon read file
	public String ReadFile(String filePath) throws FileNotFoundException{
		String path=filePath;
	    file = new BufferedReader(new FileReader(path));
	    String returnStr ="";
	    try{
	 	    String tempString=null;
		    while ((tempString = file.readLine()) != null){
			    returnStr = returnStr+tempString;
	    }      
	    }catch(IOException e){
	    }finally{
	    	if (file != null){
	            try{
	            	file.close();
	            }catch (IOException e1) {
	            }
	        }
	    }
	    return returnStr;
	 }
	
	//simon write file
	 public static boolean WriteFile(String filePath,String tempcon) throws FileNotFoundException
	 {
		 boolean result=false;
	     try{
		     String path = filePath;
	         PrintWriter pw = new PrintWriter(new FileOutputStream(path));
	         pw.println(tempcon);
	         pw.close();
	         result=true;
	     }catch(IOException e) {
	     }
	     return result;
	  }
	 
//	 public static String updateJsonFile(String path, String newdatajson, String...conditionList) throws FileNotFoundException{
//		 String str = readCommentJson(path);
//		 JSONObject dataJson = JSONObject.fromObject(str);
//		 JSONObject newData = JSONObject.fromObject(newdatajson);
//		 
//		 JSONArray ja = dataJson.getJSONArray("rows");
//		 for(int i = 0;i<ja.size();i++){
//			 JSONObject item = ja.getJSONObject(i);
//			 boolean dely = false;
//			 for(String key : conditionList){
//				 if(item.get(key).equals(newData.get(key))){
//					 dely = true;
//				 }
//				 else{
//					 dely = false;
//				 }
//			 }
//			 if(dely){
//				 newData.put("creationTime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
//				 ja.set(i, newData);
//			 }
//		 }
//		 dataJson.put("row", ja);
//		 WriteFile(path, dataJson.toString());
//		 return dataJson.toString();
//	 }
	 
	 //simon add json
//	 public static String addJson(String path,String addjson){
//		 String str = readCommentJson(path);//with comment
//		 JSONObject dataJson = JSONObject.fromObject(str);//past information
//		 JSONObject addData = JSONObject.fromObject(addjson);//the information needed to be added in
//		 addData.put("creationTime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
//		 
////	     System.out.println("****");
////		 System.out.println(dataJson.toString());
////		 System.out.println(addData.toString());
////		 System.out.println("****");
//		 
//		 JSONArray ja = dataJson.getJSONArray("rows");
//		 
////		 System.out.println("****");
////		 System.out.println(dataJson.toString());
////		 System.out.println(ja.toString());
////		 System.out.println("****");
//		 
//		 Integer total = (Integer)dataJson.get("total");
//		 total = total+1;
//		 dataJson.put("total", total);
//		 ja.add(addData);
//		 
////		 System.out.println("****");
////		 System.out.println(dataJson.toString());
////		 System.out.println(ja.toString());
////		 System.out.println("****");
//		 
//		 writeJson(path,dataJson.toString());
//		 return dataJson.toString();
//     }
	 
	 //simon delete json
	 public static String deleteGeoJson(String path,Double id){
		 
//		 System.out.println("***");
		 String str = readCommentJson(path);
//		 System.out.println(str);
//		 System.out.println(id);
//		 System.out.println("***");
		 
		 JSONBaseObject jsonBaseObject = new JSONParser_Android().parse(str, true);
		 JSONObject jsonObject = (JSONObject) jsonBaseObject;
	
		 
		 JSONArray ja = jsonObject.getAsArray("features");
		 
//		 System.out.println(ja.size());
		 for(int i=0;i<ja.size();i++){
			 
			 JSONObject item = ja.getAsObject(i);
//			 System.out.println(item.toString());

//			 System.out.println(item.getAsObject("properties").getAsNumber("osm_id").value());
			 Double del = item.getAsObject("properties").getAsNumber("osm_id").value();
			 if(id.equals(del))
				 ja.getAsObject(i).put("type", "");
//			 item = ja.getAsObject(i);
//			 System.out.println(item.toString());
		 }
		 String result = JSONGenerator.generate(jsonBaseObject);
//		 result.substring("{\"type\":\"\",", "}},");
		 int start = result.indexOf("{\"type\":\"\",");
//		 System.out.println(start);
		 
		 int end = result.indexOf("}},",start);
//		 System.out.println(end);
		 
		 int size = result.length();
		 String a = result.substring(0, start);
		 String b = result.substring(end+3,size);
		 
		 String real = a.concat(b);
//		 System.out.println(real);
		 
		 writeJson(path, real);
		 return real;
	 }
	 
	 
	 //simon write GeoJson file
	 public static void writeJson(String path,String content){
		 
		 File file = null;
		 FileOutputStream out = null;
		 OutputStreamWriter osw = null;
		 BufferedWriter bufferw = null;
		 
		 try{
			 
			 file = new File(path);
			 out = new FileOutputStream(file);
			 osw = new OutputStreamWriter(out,"utf-8");
			 bufferw = new BufferedWriter(osw);
			 bufferw.write(content);
			 
		 }catch(Exception e){
			 e.printStackTrace();
		 }finally{
			 if(bufferw != null){
				 try{
					 bufferw.close();
				 }catch (IOException e){
					 e.printStackTrace();
				 }
			 }
			 if(osw != null){
				 try {
					 osw.close();
				 }catch (IOException e) {
					 e.printStackTrace();
				 }  
			 }
			 if(out != null){
				 try{
					 out.close();
				 }catch (IOException e){
					 e.printStackTrace();
				 }
			 }
			 if(file != null){
				 file = null;
			 }
		 } 
	 }
	 
	 
	 //simon read GeoJson file without commend
	 public static String readJson(String path,String codeFormat){
		 
	     String str = "",json ="";
	     String encoding = "GBK";
	     
	     File file = null;
	     FileInputStream fis = null;
	     InputStreamReader isr = null; 
	     BufferedReader br = null;
	     
	     try {
	    	 file = new File(path);
	    	 if(file.isFile()){
	    		 fis = new FileInputStream(file); 
	    		 isr = new InputStreamReader(fis,"UTF-8");
	    		 encoding = isr.getEncoding();
	    		 br = new BufferedReader(isr);
	    		 while((str = br.readLine()) != null){
	    			 str  = str.trim();
	    			 int i = str.indexOf("//");
	    			 int j1 = str.indexOf("/*"),j2=str.indexOf("*"),j3=str.indexOf("*/");
	    			 if(i==0 ||(j1==0 && j3 < 1) || (j2==0 && j3 < 1)){
	    				 continue;
	    			 }
	    			 if(i>0){
	    				 str = str.substring(0,i).trim();
	    			 }
	    			 if((j1>0 || j2>0 ) && j2 >0){
	    				 String[] strs = str.split("\\/\\*");
	    				 String stri = "";
	    				 for(String s : strs){
	    					 int k = s.indexOf("*/");
	    					 if(k < 0){
	    						 stri += s.trim();
	    					 }else{
	    						 stri += s.substring(k+2).trim();
	    					 }
	    				 }
	    				 str = stri.trim();
	    			 }
	    			 json += str.trim();
	    		 }
	    	 }
	     }catch(UnsupportedEncodingException e1) {
	    	 e1.printStackTrace();
	     }catch(IOException e) {
	    	 e.printStackTrace();
	     }finally{
	    	 if(br!=null){
	    		 try {
	    			 br.close();
	    		 } catch (IOException e) {
	    			 e.printStackTrace();
	    		 }
	    	 }
	    	 if(isr!=null){
	    		 try {
	    			 isr.close();
	    		 }catch (IOException e) {
	    			 e.printStackTrace();
	    		 }
	    	 }
	    	 if(fis!=null){
	    		 try {
	    			 fis.close();
	    		 }catch (IOException e) {
	    			 e.printStackTrace();
	    		 }
	    	 }
	    	 if(file!=null){
	    		 file = null;
	    	 }
	     }
	     try {
	    	 if(!"UTF8".equals(encoding)){
	    		 json = new String(json.getBytes(encoding),codeFormat);
	    	 }
	     }catch (UnsupportedEncodingException e) {
	    	 e.printStackTrace();
	     }
	     return json;
	 }
	 
	 
	 
	 
	 public static String readCommentJson(String path){
		 return readCommentJson(path,"UTF8");
	 }
	 
	//simon read GeoJson file with commend
	 public static String readCommentJson(String path,String codeFormat){
		 String  str = "",json ="";
		 String encoding = "GBK";
		 File file = null;
		 FileInputStream fis = null;
		 InputStreamReader isr = null; 
		 BufferedReader br = null;
		 try {
			 file = new File(path);
			 if(file.isFile()){
				 fis = new FileInputStream(file); 
				 isr = new InputStreamReader(fis,"UTF-8");
				 encoding = isr.getEncoding();
				 br = new BufferedReader(isr);
				 while((str = br.readLine())!=null){
					 str=str+"\n ";
					 json +=str;
				 }
			 }
		 }catch(UnsupportedEncodingException e1) {
			 e1.printStackTrace();
		 }catch(IOException e) {
			 e.printStackTrace();
		 }finally{
			 if(br!=null){
				 try {
					 br.close();
				 }catch (IOException e) {
					 e.printStackTrace();
				 }
			 }
			 if(isr!=null){
				 try {
					 isr.close();
				 }catch (IOException e) {
					 e.printStackTrace();
				 }
			 }
			 if(fis!=null){
				 try {
					 fis.close();
				 }catch (IOException e) {
					 e.printStackTrace();
				 }
			 }
			 if(file!=null){
				 file = null;
			 }
		 }
		 try {
			 if(!"UTF8".equals(encoding)){
				 json = new String(json.getBytes(encoding),codeFormat);
			 }
		 }catch (UnsupportedEncodingException e) {
			 e.printStackTrace();
		 }
		 return json;
	 }
}
