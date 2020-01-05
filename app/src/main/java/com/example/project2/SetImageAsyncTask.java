package com.example.project2;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class SetImageAsyncTask extends AsyncTask<String, Void, Boolean> {
    static String method;
    static String surl;

    public SetImageAsyncTask(String method, String url) {
        this.method = method;
        this.surl = url;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        String filename = (String) params[0];
        int bytesAvailable, bytesRead, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1*1024*1024;
        String filedata = "";
        JSONObject jsFile;
        String output;
        int imageSize = 0;

//        try {
//            File file = new File(filename);
//            FileInputStream fileInputStream = new FileInputStream(file);
//
//            bytesAvailable = fileInputStream.available();
//            //System.out.println("avail : "+bytesAvailable);
//
//            bufferSize = Math.min(bytesAvailable, maxBufferSize);
//            buffer = new byte[bufferSize];
//            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//            //System.out.println("read : "+bytesRead);
//            //System.out.println("buffer : "+buffer.toString());
//
//            while (bytesRead > 0) {
////                filedata += new String(buffer);
////                //System.out.println("data : "+filedata);
////                bytesAvailable = fileInputStream.available();
////                //System.out.println("avail : "+bytesAvailable);
////                bufferSize = Math.min(bytesAvailable, maxBufferSize);
////                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
////            }
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
//
//        jsFile = new JSONObject();
//
//        try {
//            jsFile.put("filename", filename);
//            jsFile.put("filedata", filedata);
//        } catch (
//        JSONException e) {
//            e.printStackTrace();
//        }
//        output = jsFile.toString();
//        System.out.println(output);

        HttpURLConnection connection = null;

        try {
            URL url = new URL(surl);

            connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod(method);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            File file = new File(filename);
            FileInputStream fileInputStream = new FileInputStream(file);
            //OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
            DataOutputStream osw = new DataOutputStream(connection.getOutputStream());

            bytesAvailable = fileInputStream.available();
            //System.out.println("avail : "+bytesAvailable);

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            //System.out.println("read : "+bytesRead);
            //System.out.println("buffer : "+buffer.toString());

            while (bytesRead > 0) {
                //filedata += new String(buffer);
                //System.out.println("data : "+filedata);
                osw.write(buffer, 0, bufferSize);
                System.out.println("buffer : "+new String(buffer));
                bytesAvailable = fileInputStream.available();
                //System.out.println("avail : "+bytesAvailable);
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

//            while(imageSize > 0)
//                osw.write(output);
            osw.flush();
            osw.close();

            if(connection.getResponseCode() <205)
            {
                return true;
            }
            else
            {
                return false;
            }

        } catch (Exception e) {
            e.getMessage();
            Log.d("Got error", e.getMessage());
            return false;
        } finally {
            connection.disconnect();
        }
    }
}
