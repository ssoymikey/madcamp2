package com.example.project2;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GetImageAsyncTask extends AsyncTask<Void, Void, ArrayList<String>> {
    static String server_output = null;
    static ArrayList<String> images = new ArrayList<String>();
    static String buffer;

    @Override
    protected ArrayList<String> doInBackground(Void... arg0) {
        HttpURLConnection conn = null;

        try {
            URL url = new URL("http://192.249.19.251:980/gallery");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoInput(true);        //데이터를 읽어올지 설정

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            InputStream is = conn.getInputStream();
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));

            String result;
            while((result = br.readLine())!=null){
                sb.append(result);
            }
            is.close();

            //내가 좀 건드리다가 말아썽 나의 메커니즘은
            //서버에서 받아오는 형식은 json string 이니까
            //이거를 json object로 만들어서 filedata 추출하구 (이때 string 형태로 db 저장되므로 string으로 받음)
            //다시 byte array 로 변환하여 사용하려구 해써!
            server_output = sb.toString();
            JSONObject jsObj = new JSONObject(server_output);
            JSONArray jsArr = jsObj.getJSONArray("DB_Output");
            for (int i=0;i<jsArr.length();i++) {
                JSONObject userObj = jsArr.getJSONObject(i);
                String temp = userObj.getString("filedata");
                System.out.println(temp);

                images.add(temp);
            }
            //buffer = jsObj.getString("filedata");
            //image = buffer.getBytes();
            //System.out.println(buffer);

        }catch (Exception e) {
            e.getMessage();
        } finally {
            conn.disconnect();
        }
        //result.add(server_output);

        return images;
    }
}