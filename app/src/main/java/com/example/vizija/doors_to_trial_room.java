package com.example.vizija;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

public class doors_to_trial_room extends AppCompatActivity {
    int disease=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doors_to_trial_room);
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        final int picture = extras.getInt("productId");
        disease = extras.getInt("disease");
        Utils.change((RelativeLayout) findViewById(R.id.parentLayout), disease, this);
        findViewById(R.id.trial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(doors_to_trial_room.this, UploadActivity.class);
                intent.putExtra("productId", picture);
                intent.putExtra("disease",disease);
                startActivity(intent);
            }
        });
    }
}