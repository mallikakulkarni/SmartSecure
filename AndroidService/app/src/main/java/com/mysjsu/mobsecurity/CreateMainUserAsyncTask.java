package com.mysjsu.mobsecurity;

import android.os.AsyncTask;
import android.util.Log;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class CreateMainUserAsyncTask extends AsyncTask<String, Void, Boolean> {

    @Override
    protected Boolean doInBackground(String... arg0) {
        try {
            Log.i("message:", "reaching CreateMainUserAsyncTask Task");

            String userJson = arg0[0];
            MainUserQueryBuilder qb = new MainUserQueryBuilder();

            //Creating the URL to MongoDB
            URL url = new URL(qb.buildUserSaveURL());

            //Create Http Connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("content-type", "application/json");
            conn.setDoOutput(true);
            conn.setRequestProperty("Accept", "application/json");
//
//            Log.i("message :", "request URL is " + url);
//            Log.i("message :", "POST data is " + userJson);

            //Sending Data
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(conn.getOutputStream());
            outputStreamWriter.write(userJson);
            outputStreamWriter.flush();
            outputStreamWriter.close();

            int responseCode = conn.getResponseCode();

            if (responseCode < 205) {
                Log.i("message:", "response : success, code is" + responseCode + " message is " + conn.getResponseMessage());
                return true;
            } else {
                Log.i("message:", "response : fail, code is " + responseCode);
                return false;
            }
        } catch (Exception e) {
            Log.i("message:", "Exception occured " + e.toString());
            return false;
        }
    }

}
