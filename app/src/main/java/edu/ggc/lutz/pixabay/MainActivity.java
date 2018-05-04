package edu.ggc.lutz.pixabay;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.services.vision.v1.model.EntityAnnotation;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import edu.ggc.lutz.pixabay.cloudvision.LabelDetectionTask;
import edu.ggc.lutz.pixabay.json.Hit;
import edu.ggc.lutz.pixabay.pixabay_service.PixabayBitmapFetchTask;
import edu.ggc.lutz.pixabay.pixabay_service.PixabayFetchTask;

/**
 * This class starts the program
 *
 * @author  Jacob, Jessica, and Afeefa
 * @version 1.0
 * @since   2018-05-04
 */
public class MainActivity extends AppCompatActivity implements OnTaskCompleted {

    static final String TAG = BuildConfig.TAG;

    private ImageView image;
    private TextView tvTags;
    private TextView tvLabels;
    private SharedPreferences prefs;
    private boolean showTags;
    private boolean showLabels;
    private CompositeResults compositeResults;
    private int currentPick;
    private TextToSpeech ttobj;

    /**
     * This method is used to initialize the activity
     * @param savedInstanceState takes in a Bundle object
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.v(TAG, "Main Activity onCreate initialized...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        image = findViewById(R.id.ivPhoto);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        tvTags = findViewById(R.id.tvTags);
        tvLabels = findViewById(R.id.tvLabels);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        compositeResults = null;

        PixabayFetchTask fetch = new PixabayFetchTask(MainActivity.this, compositeResults);
        fetch.execute();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> { stageNewImage(); });

        PreferenceManager.setDefaultValues(this, R.xml.app_preferences, false);


        // Set up Text to Speech Object
        ttobj = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                ttobj.setLanguage(Locale.ENGLISH);
            }
        });
    }

    /**
     * This method stage new images
     */
    void stageNewImage() {

        Log.v(TAG, "Main Activity stageNewImage initialized...");

        tvTags.setText(R.string.pixabay_tag_na);
        tvLabels.setText(R.string.cloud_vision_labels_na);
        image.setImageResource(R.drawable.loadingpuppycircle);

        if (compositeResults == null) return;

        currentPick = (int) getRandomLong(0L, compositeResults.size());
        final Hit hit = compositeResults.getHit(currentPick);
        Log.i(TAG, hit.getWebformatURL());
        final long id = hit.getId(); // convert response's index to Pixabay's id
        URL url = null;
        try {
            url = new URL(hit.getWebformatURL());
            if (compositeResults.getBitmaps().get(id) == null) {
                PixabayBitmapFetchTask fetchTask =
                        new PixabayBitmapFetchTask(this, compositeResults, hit);
                fetchTask.execute(url);
            }
            LongSparseArray<List<EntityAnnotation>> labels = compositeResults.getLabels();
            if (labels.get(id) == null)           // lazy fetch
                new LabelDetectionTask(this, compositeResults, id).execute();
            else {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        render();
    }

    /**
     * This method renders images
     */
    public void render() {
        // Do not add verbose logging here -- that would be way too chatty!

        Hit hit = compositeResults.getHit(currentPick);
        final long id = hit.getId(); // convert response's index to Pixabay's id
        List<EntityAnnotation> label = compositeResults.getLabels().get(id);
        Bitmap bitmap = compositeResults.getBitmaps().get(id);

        if (bitmap == null || (hit == null && tvTags.getVisibility() == View.VISIBLE) ||
                (label == null && tvLabels.getVisibility() == View.VISIBLE)) {
            return;
        }

        if (tvTags.getVisibility() == View.VISIBLE) {
            String s = getString(R.string.pixabay_tags, hit.getTags());
            Log.i(TAG, s);
            if(prefs.getBoolean("pixabay_tags", true)){
                tvTags.setText(s);
            }
            if(prefs.getBoolean("tts_sound", true)){
                ttobj.speak(s, TextToSpeech.QUEUE_FLUSH, null, "ttsTags");
            }
            // TODO previous line outputs to logcat, need to format and place s in tvTags
        }

        if (tvLabels.getVisibility() == View.VISIBLE) {
            String l =getString(R.string.cloud_vision_labels, labelsToString(label));
            Log.i(TAG, l);
            if(prefs.getBoolean("cloud_vision_labels", true)){
                tvLabels.setText(l);
            }
            // TODO previous line outputs to logcat, need to format and place l in tvLabels
        }

        image.setImageBitmap(bitmap);
    }

    /**
     * This method is a string builder that shows the probabilities along with the description
     */
    private String labelsToString(List<EntityAnnotation> labels) {

        Log.v(TAG, "Main Activity labelsToString initialized...");

        StringBuilder message = new StringBuilder();
        for (EntityAnnotation label : labels) {
            if (message.length() > 0) message.append(", ");
                message.append(prefs.getBoolean("show_probabilities", true) ?
                        String.format(Locale.US, "%s (%.2f)", label.getDescription(), label.getScore()) :
                        String.format(Locale.US, "%s", label.getDescription()));
        }
        return message.toString();
    }

    /**
     * This method gives the composite result and calls the render method
     */
    public void onTaskCompleted(CompositeResults results) {

        Log.v(TAG, "Main Activity onTaskCompleted initialized...");

        compositeResults = results;
        render();
    }

    /**
     * This method create the options menu when the user opens the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Log.v(TAG, "Main Activity onCreateOptionsMenu initialized...");

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * This method allows the user to select items from the option menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.v(TAG, "Main Activity onOptionsItemSelected initialized...");

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_about) {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        }
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is for when the activity is paused
     */
    @Override
    protected void onPause() {
        Log.v(TAG, "Main Activity onPause initialized...");
        super.onPause();
    }

    /**
     * This method is for when the activity is running
     */
    @Override
    protected void onResume() {
        Log.v(TAG, "Main Activity onResume initialized...");
        super.onResume();
    }

    /**
     * This method returns a random number
     */
    private static long getRandomLong(long minimum, long maximum) {
        Log.v(TAG, "Main Activity getRandomLong initialized...");
        return ((long) (Math.random() * (maximum - minimum))) + minimum;
    }
}
