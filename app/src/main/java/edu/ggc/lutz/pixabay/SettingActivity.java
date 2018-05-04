package edu.ggc.lutz.pixabay;

import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

public class SettingActivity extends PreferenceActivity {

    static final String TAG = BuildConfig.TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.v(TAG, "Setting Activity onCreate initialized...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        addPreferencesFromResource(R.xml.app_preferences);
    }
}