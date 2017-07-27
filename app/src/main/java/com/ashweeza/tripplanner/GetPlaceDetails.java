package com.ashweeza.tripplanner;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Ashweeza on 2/25/2017.
 */

public class GetPlaceDetails extends AsyncTask<String, Void, PlaceDetails> {
    DetailActivity activity;
    Boolean errorFlag;
    ProgressDialog progressDialog;

    public GetPlaceDetails(DetailActivity activity) {
        this.activity = activity;
        errorFlag = false;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Loading Place Details");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected PlaceDetails doInBackground(String... params) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        InputStream inputStream;
        StringBuilder buffer = null;
        try {
            //Log.v(TAG, "FINAL URL inside Async Task:" + FINAL_URL);
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            inputStream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            buffer = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            // String finalJson = buffer.toString();
            //Log.v(TAG, finalJson);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null)
                connection.disconnect();
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        try {
            return PlaceDetailsParser.PlaceDetailsJsonParser.getFeedArrayList(buffer.toString());
        } catch (JSONException e) {
            errorFlag = true;
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(PlaceDetails placeDatas) {
        progressDialog.dismiss();
        if (errorFlag || placeDatas == null) {
            Toast.makeText(activity, "No cities match your query", Toast.LENGTH_SHORT).show();
            activity.finish();
        } else {
            //activity.placeDataFetched = placeDatas;
            activity.showDetails(placeDatas);
        }
    }

}
