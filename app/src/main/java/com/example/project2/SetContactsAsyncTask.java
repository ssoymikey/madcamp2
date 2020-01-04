package com.example.project2;

import android.os.AsyncTask;
import android.util.Log;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class SetContactsAsyncTask extends AsyncTask<String, Void, Boolean> {
    static String method;
    static String surl;

    public SetContactsAsyncTask(String method, String url) {
        this.method = method;
        this.surl = url;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        String contact = (String) params[0];
        Log.d("contact", ""+contact);
        HttpURLConnection connection = null;

        try {
            URL url = new URL(surl);

            connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod(method);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());

            osw.write(contact);
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
