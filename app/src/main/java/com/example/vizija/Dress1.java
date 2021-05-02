package com.example.vizija;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;
import java.util.Objects;

public class Dress1 extends AppCompatActivity {
    int disease = 0;
    boolean colorblind;
    int[] imgID = {R.drawable.femaletwo, R.drawable.tshirt_d, R.drawable.tshirt_p, R.drawable.tshirt_t};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dress1);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        boolean flag = extras.getBoolean("flag");
        boolean blind = extras.getBoolean("blind");
        disease = extras.getInt("disease");
        Utils.change((LinearLayout) findViewById(R.id.parentLayout), disease, this);

        findViewById(R.id.tryIt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dress1.this, doors_to_trial_room.class);
                intent.putExtra("productId", imgID[disease]);
                intent.putExtra("disease",disease);
                startActivity(intent);
            }
        });
        if (flag || blind) {
            AudioMode();
        }
            ((ImageView) findViewById(R.id.frame)).setImageResource(imgID[disease]);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dress_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_settings:
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    @Override
    public void onBackPressed() {
        if (tts != null)
            tts.stop();
        super.onBackPressed();
    }

    private void AudioMode() {
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    initTTs();
                } else {
                    Toast.makeText(getApplicationContext(), "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
                }
            }
        }, "com.google.android.tts");


    }

    TextToSpeech tts;

    public void initTTs() {
        int ttsLang = tts.setLanguage(Locale.US);

        if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.e("TTS", "The Language is not supported!");
        } else {
            Log.i("TTS", "Language Supported.");
        }
        tts.speak("The product displayed is a women grey printed v-neck t-shirt. The print consists of four colorful cactus images. It's material is cotton,elastane and can be machine washed. The dress from Adibas is both stylish and durable, making it a must have item. When you are going through an art gallery opening or a theater, wear this piece with platform heels and trendy clutch. It is available in in 5 sizes- small, medium, large, extra large and extra extra large. It's current price is 839 rupees inclusive of all taxes.",
                TextToSpeech.QUEUE_FLUSH, null, "InitText");
        Log.i("TTS", "Initialization success.");
    }
}