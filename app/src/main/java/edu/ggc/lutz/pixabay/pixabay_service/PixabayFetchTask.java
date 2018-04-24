package edu.ggc.lutz.pixabay.pixabay_service;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.ggc.lutz.pixabay.BuildConfig;
import edu.ggc.lutz.pixabay.CompositeResults;
import edu.ggc.lutz.pixabay.MainActivity;
import edu.ggc.lutz.pixabay.OnTaskCompleted;

public class PixabayFetchTask extends AsyncTask<URL, String, CompositeResults> {

    private static final String TAG = BuildConfig.TAG;
    private static final String PIXABAY_API_KEY = BuildConfig.PIXABAY_API_KEY;
    private static final String PIXABAY_API_URL = BuildConfig.PIXABAY_API_URL;
    private static final String QUERY = "&editor_choice=true&safesearch=true&image_type=photo";

    private final String url;
    private CompositeResults compositeResults;
    private OnTaskCompleted listener;

    public PixabayFetchTask(Activity activity, CompositeResults compositeResults) {
        this.url = PIXABAY_API_URL + "/?key=" + PIXABAY_API_KEY + QUERY;
        this.compositeResults = compositeResults;
        this.listener = (OnTaskCompleted) activity;
    }

    @Override
    protected CompositeResults doInBackground(URL... params) {
        Log.v(TAG, "entering PixabayFetchTask.doInBackground(...)");
        CompositeResults results = null;
        try {
            String line;
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder json = new StringBuilder();
            while ((line = reader.readLine()) != null) json.append(line);
            Log.i(TAG, "json=" + json.toString());
            results = new CompositeResults(json.toString(), (MainActivity) listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    @Override
    protected void onPostExecute(CompositeResults results) {
        super.onPostExecute(results);
        listener.onTaskCompleted(results);
    }
}
