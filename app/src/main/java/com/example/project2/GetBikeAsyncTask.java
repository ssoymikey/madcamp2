package com.example.project2;

import android.os.AsyncTask;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GetBikeAsyncTask extends AsyncTask<String, Void, ArrayList<Bicycle>> {
    static String server_output = null;
    static String temp_output = null;

    @Override
    protected ArrayList<Bicycle> doInBackground(String... arg0) {
        HttpURLConnection conn = null;
        String start = arg0[0];

        ArrayList<Bicycle> myBikes = new ArrayList<Bicycle>();
        try {
            URL url = new URL("http://192.249.19.251:980/bike");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoInput(true);        //데이터를 읽어올지 설정
            conn.setDoOutput(true);
            conn.setRequestProperty("content-type", "text/html");

            String input = start;
            System.out.println(input);

            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
            osw.write(input);
            osw.flush();
            osw.close();

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

                Bicycle temp = new Bicycle();
                temp.setUser_Name(userObj.getString("name"));
                temp.setUser_phNumber(userObj.getString("phone"));
                temp.setStart(userObj.getString("startpoint"));
                temp.setEnd(userObj.getString("endpoint"));
                temp.setPwd(userObj.getString("password"));
                temp.setAvail(userObj.getString("available"));
                myBikes.add(temp);
            }

        }catch (Exception e) {
            e.getMessage();
        } finally {
            conn.disconnect();
        }

        return myBikes;
        //return server_output;
    }
}