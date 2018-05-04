package edu.ggc.lutz.pixabay;

import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

/**
 * This class starts the setting activity
 *
 * @author  Jacob, Jessica, and Afeefa
 * @version 1.0
 * @since   2018-05-04
 */
public class SettingActivity extends PreferenceActivity {

    static final String TAG = BuildConfig.TAG;

    /**
     * This method is used to set the activity setting content
     * @param savedInstanceState takes in a Bundle object
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.v(TAG, "Setting Activity onCreate initialized...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        addPreferencesFromResource(R.xml.app_preferences);
    }
}