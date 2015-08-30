package se.leanbit.sats.repositories.services;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebService extends AsyncTask<String, String, String>
{
    final private String LOG_TAG = "WEB_SERVICE";
    protected String doInBackground(String... params)
    {
        String result = "";
        BufferedReader reader = null;
        StringBuilder data = new StringBuilder();
        try
        {
            URL url = new URL(params[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("accept", "Application/json");
            Log.e(LOG_TAG, "Request GET from: "+params[0]+"\nSTATUS: "+connection.getResponseCode());

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null)
            {
                data.append(line);
            }
            result = data.toString();
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG, "EXCEPTION: ", e);
        }
        finally
        {
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (Exception e)
                {
                    Log.e(LOG_TAG, "Could not close reader", e);
                }
            }
        }
        return result;
    }
}