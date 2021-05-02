package com.example.vizija;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.text.HtmlCompat;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private SwitchCompat colorBlindSwitch;
    private static final int ISHIARA_TEST_RESULT_CODE = 1000;
    boolean resultEvaluated = false;
    boolean flag = false;
    int disease;
    int RecordAudioRequestCode = 1000;
    SpeechRecognizer speechRecognizer;
    boolean colorblind;
    int[] women = {R.drawable.womenicon, R.drawable.women_d, R.drawable.women_p, R.drawable.women_t};
    int[] men = {R.drawable.menicon, R.drawable.men_d, R.drawable.men_p, R.drawable.men_t};

    int[] e = {R.drawable.e, R.drawable.e_d, R.drawable.e_p, R.drawable.e_t};
    int[] h = {R.drawable.h, R.drawable.h_d, R.drawable.h_p, R.drawable.h_t};
    int[] g = {R.drawable.g, R.drawable.g_d, R.drawable.g_p, R.drawable.g_t};
    int[] c = {R.drawable.c, R.drawable.c_d, R.drawable.c_p, R.drawable.c_t};
    int[] b = {R.drawable.b, R.drawable.b_d, R.drawable.b_p, R.drawable.b_t};
    int[] f = {R.drawable.f, R.drawable.f_d, R.drawable.f_p, R.drawable.f_t};

    int[] vid1={R.raw.orig_1,R.raw.deut_1,R.raw.prot_1,R.raw.trit_1};
    int[] vid2={R.raw.orig_2,R.raw.deut_2,R.raw.protan_2,R.raw.trit_2};

    int[] item1={R.drawable.item1, R.drawable.item1_d, R.drawable.item1_p, R.drawable.item1_t};
    int[] item4={R.drawable.item4, R.drawable.item4_d, R.drawable.item4_p, R.drawable.item4_t};
    int[] item5={R.drawable.item5, R.drawable.item5_d, R.drawable.item5_p, R.drawable.item5_t};

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RecordAudioRequestCode);
        }
    }

    Intent speechRecognizerIntent;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        special();
    }

    public void special(){
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        boolean blind = extras.getBoolean("blind");
        disease = extras.getInt("disease");
        Utils.change((RelativeLayout) findViewById(R.id.parentLayout), disease, this);

        if (blind) {
            tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        Utils.speak(tts, "Which category you want to choose? Men or the Women");
                    } else {
                        Toast.makeText(getApplicationContext(), "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }, "com.google.android.tts");
            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {

                }

                @Override
                public void onDone(String utteranceId) {
                    tts.stop();
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                            "Speak Up");
                    try {
                        startActivityForResult(intent, RecordAudioRequestCode);
                    } catch (ActivityNotFoundException a) {
                        Toast.makeText(getApplicationContext(),
                                "Sorry, your device doesn't support speech input.",
                                Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onError(String utteranceId) {
                }
            });

        }
        VideoView videoView = findViewById(R.id.video);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" +
                R.raw.orig_1));
                videoView.start();
        VideoView videoView2 = findViewById(R.id.video2);
        MediaController mediaController2 = new MediaController(this);
        mediaController2.setAnchorView(videoView2);
        videoView2.setMediaController(mediaController2);
        videoView2.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" +
                R.raw.orig_2));
        videoView2.start();



        findViewById(R.id.male).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean t = colorBlindSwitch.isChecked();
                Intent i = new Intent(MainActivity.this, Male_dress1.class);
                i.putExtra("flag", flag);
                i.putExtra("disease", disease);
                i.putExtra("colorblind", t);
                startActivity(i);
            }
        });
        findViewById(R.id.female).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean t = colorBlindSwitch.isChecked();
                Intent i = new Intent(MainActivity.this, Dress1.class);
                i.putExtra("flag", flag);
                i.putExtra("disease", disease);
                i.putExtra("colorblind", t);
                startActivity(i);


            }
        });
        colorBlindSwitch = findViewById(R.id.colorBlindSwitch);
        colorBlindSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startActivityForResult(new Intent(MainActivity.this, Ishihara.class), ISHIARA_TEST_RESULT_CODE);
//
                } else {
                    disease = 0;
                    setContentView(R.layout.activity_main);
                    special();
                    ColorDrawable colorDrawable
                            = new ColorDrawable(getResources().getColor(R.color.colorPrimary, null));
                    getSupportActionBar().setTitle(Html.fromHtml("<font color=#FFFFFF>Vizija</font>", HtmlCompat.FROM_HTML_MODE_LEGACY));
                    getSupportActionBar().setBackgroundDrawable(colorDrawable);
/*
                    ((ImageView) findViewById(R.id.male)).setImageResource(men[disease]);
                    ((ImageView) findViewById(R.id.female)).setImageResource(women[disease]);
                    ((ImageButton) findViewById(R.id.e)).setImageResource(e[disease]);
                    ((ImageButton) findViewById(R.id.h)).setImageResource(h[disease]);
                    ((ImageButton) findViewById(R.id.g)).setImageResource(g[disease]);
                    ((ImageButton) findViewById(R.id.c)).setImageResource(c[disease]);
                    ((ImageButton) findViewById(R.id.b)).setImageResource(b[disease]);
                    ((ImageButton) findViewById(R.id.f)).setImageResource(f[disease]);
                    ((ImageButton) findViewById(R.id.dup_b)).setImageResource(b[disease]);
                    ((ImageButton) findViewById(R.id.dup_g)).setImageResource(g[disease]);
*/

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (tts != null)
            tts.stop();
        super.onBackPressed();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ISHIARA_TEST_RESULT_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    int score = data.getIntExtra("result", 0);
                    disease = data.getIntExtra("disease", 0);
                    Utils.change((RelativeLayout)findViewById(R.id.parentLayout), disease, this);
                    Toast.makeText(MainActivity.this, "Result Received" + score, Toast.LENGTH_LONG).show();
                    resultEvaluated = true;
                    ((ImageView) findViewById(R.id.male)).setImageResource(men[disease]);
                    ((ImageView) findViewById(R.id.female)).setImageResource(women[disease]);
                    ((ImageButton) findViewById(R.id.e)).setImageResource(e[disease]);
                    ((ImageButton) findViewById(R.id.h)).setImageResource(h[disease]);
                    ((ImageButton) findViewById(R.id.g)).setImageResource(g[disease]);
                    ((ImageButton) findViewById(R.id.c)).setImageResource(c[disease]);
                    ((ImageButton) findViewById(R.id.b)).setImageResource(b[disease]);
                    ((ImageButton) findViewById(R.id.f)).setImageResource(f[disease]);
                    ((ImageButton) findViewById(R.id.dup_b)).setImageResource(b[disease]);
                    ((ImageButton) findViewById(R.id.dup_g)).setImageResource(g[disease]);
                    ((ImageButton) findViewById(R.id.item1)).setImageResource(item1[disease]);
                    ((ImageButton) findViewById(R.id.item4)).setImageResource(item4[disease]);
                    ((ImageButton) findViewById(R.id.item5)).setImageResource(item5[disease]);
                    ((VideoView) findViewById(R.id.video2)).setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" +
                            vid2[disease]));

                    ((VideoView) findViewById(R.id.video)).setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" +
                            vid1[disease]));
                }
            } else {
                colorBlindSwitch.setChecked(false);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        boolean blind = extras.getBoolean("blind");
        if (blind == true && requestCode == RecordAudioRequestCode && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//            Log.d("RESULT", result.get(0));
            if (result.get(0).toLowerCase().contains("women") || result.get(0).toLowerCase().contains("female") || result.get(0).toLowerCase().contains("ladki") || result.get(0).toLowerCase().contains("girl")) {
                Intent intent = new Intent(MainActivity.this, Dress1.class);
                intent.putExtra("blind", true);
                intent.putExtra("disease", disease);
                finish();
                startActivity(intent);
            } else if (result.get(0).toLowerCase().contains("men") || result.get(0).toLowerCase().contains("male") || result.get(0).toLowerCase().contains("boy") || result.get(0).toLowerCase().contains("ladke")) {
                Intent intent = new Intent(MainActivity.this, Male_dress1.class);
                intent.putExtra("blind", true);
                intent.putExtra("disease", disease);
                finish();
                startActivity(intent);
            }
        }

    }


    TextToSpeech tts;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_options, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_1:
                return true;
            case R.id.menu_2:
                return true;
            case R.id.menu_3:
                return true;
            case R.id.menu_4:
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}