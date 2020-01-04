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

public class GetContactsAsyncTask extends AsyncTask<Dictionary, Void, ArrayList<Dictionary>> {
    static String server_output = null;
    static String temp_output = null;

    @Override
    protected ArrayList<Dictionary> doInBackground(Dictionary... arg0) {
        HttpURLConnection conn = null;

        ArrayList<Dictionary> myDicts = new ArrayList<Dictionary>();
        try {
            URL url = new URL("http://192.249.19.251:980/contact");
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
                sb.append(result+"\n");
            }
            is.close();

            server_output = sb.toString();

            JSONObject jsObj = new JSONObject(server_output);
            System.out.println("1: "+jsObj);

            JSONArray contacts = jsObj.getJSONArray("DB_Output");
            System.out.println("2: "+contacts.length());
            for (int i=0;i<contacts.length();i++) {
                JSONObject userObj = contacts.getJSONObject(i);

                Dictionary temp = new Dictionary();
                temp.setPersonId(userObj.getLong("personID"));
                temp.setUser_Name(userObj.getString("name"));
                temp.setUser_phNumber(userObj.getString("phone"));
                myDicts.add(temp);
            }

        }catch (Exception e) {
            e.getMessage();
        } finally {
            conn.disconnect();
        }

        return myDicts;
        //return server_output;
    }
}