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

public class GetImageAsyncTask extends AsyncTask<Dictionary, Void, ArrayList<String>> {
    static String server_output = null;
    static String temp_output = null;
    static ArrayList<String> result = new ArrayList<String>();

    @Override
    protected ArrayList<String> doInBackground(Dictionary... arg0) {
        HttpURLConnection conn = null;

        try {
            URL url = new URL("http://192.249.19.251:980/gallery");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            //conn.setRequestProperty("Accept", "application/json");
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

            server_output = sb.toString();

        }catch (Exception e) {
            e.getMessage();
        } finally {
            conn.disconnect();
        }
        result.add(server_output);

        //return myDicts;
        return result;
    }
}