package edu.ggc.lutz.pixabay;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class AboutActivity extends AppCompatActivity {

    static final String TAG = BuildConfig.TAG;
    ImageView pixabayLogo;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.v(TAG, "AboutActivity onCreate initialized...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        pixabayLogo = findViewById(R.id.pixabayLogo);
        uri = Uri.parse("https://pixabay.com/");

        pixabayLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.v(TAG, "AboutActivity onClick initialized...");

                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });


    }


}