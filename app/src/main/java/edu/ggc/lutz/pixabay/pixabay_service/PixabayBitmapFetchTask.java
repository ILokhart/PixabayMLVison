package edu.ggc.lutz.pixabay.pixabay_service;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LongSparseArray;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import edu.ggc.lutz.pixabay.BuildConfig;
import edu.ggc.lutz.pixabay.CompositeResults;
import edu.ggc.lutz.pixabay.OnTaskCompleted;
import edu.ggc.lutz.pixabay.json.Hit;

public class PixabayBitmapFetchTask extends AsyncTask<URL, Void, Bitmap> {

    private static final String TAG = BuildConfig.TAG;

    private final CompositeResults compositeResults;
    private Hit hit;
    private final long id;
    //private ImageView imageView;
    private final OnTaskCompleted listener;

    public PixabayBitmapFetchTask(Activity activity, CompositeResults compositeResults, Hit hit) {
        this.hit = hit;
        this.id = hit.getId(); // convert response's index to Pixabay's id
        //this.imageView = imageView;
        this.compositeResults = compositeResults;
        this.listener = (OnTaskCompleted) activity;
    }

    @Override
    protected Bitmap doInBackground(URL... urls) {
        LongSparseArray<Bitmap> bitmaps = compositeResults.getBitmaps();
        try {
            InputStream stream = urls[0].openStream();
            bitmaps.put(hit.getId(), BitmapFactory.decodeStream(stream));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmaps.get(id);
    }

    protected void onPostExecute(Bitmap bitmap) {
        listener.onTaskCompleted(compositeResults);
    }
}
