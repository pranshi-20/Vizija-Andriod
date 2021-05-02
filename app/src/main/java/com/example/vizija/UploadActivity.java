package com.example.vizija;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.example.vizija.Utils.toByteArray;

public class UploadActivity extends AppCompatActivity {

    int possible = 0;
    Bitmap b1, b2, b3;
    int productId;
    ImageView imageview;
    ProgressBar progressBar;

    private void dispatchTakePictureIntent(int REQUEST_IMAGE_CAPTURE) {
/*
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
        }

*/
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_CAPTURE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            assert data != null;
            Uri imageUri = data.getData();
            Bitmap imageBitmap = null;
            try {
                assert imageUri != null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    imageBitmap = ImageDecoder.decodeBitmap(
                            ImageDecoder.createSource(
                                    getContentResolver(),
                                    imageUri));

                } else {
                    imageBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (imageBitmap != null)
                switch (requestCode) {
                    case 1:
                        possible |= 1;
                        b1 = imageBitmap;
                        ((AppCompatImageView) findViewById(R.id.image1)).setImageBitmap(imageBitmap);
                        break;
                    case 2:
                        possible |= 2;
                        b2 = imageBitmap;
                        ((AppCompatImageView) findViewById(R.id.image2)).setImageBitmap(imageBitmap);
                        break;
                    case 3:
                        possible |= 4;
                        b3 = imageBitmap;
                        ((AppCompatImageView) findViewById(R.id.image3)).setImageBitmap(imageBitmap);
                        break;
                }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        int disease = extras.getInt("disease");
        Utils.change((ScrollView) findViewById(R.id.parentLayout), disease, this);

//        Utils.change((ScrollView) findViewById(R.id.parentLayout), 2, this); // try this after filling all the colours iske ander ke
        productId = extras.getInt("productId");
        findViewById(R.id.upload1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent(1);
            }
        });
        findViewById(R.id.upload2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent(2);
            }
        });
        progressBar = findViewById(R.id.progress_circular);
        findViewById(R.id.upload3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent(3);
            }
        });
        findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadMulti();
            }
        });
    }

    public void uploadMulti() {
        if (possible != 7) {
            return;
        }
        final VolleyMultipartRequest.DataPart[] parts = new VolleyMultipartRequest.DataPart[4];
        parts[0] = new VolleyMultipartRequest.DataPart("image0.jpg", toByteArray(b1, false), "image/jpeg");
        parts[1] = new VolleyMultipartRequest.DataPart("image1.jpg", toByteArray(b2, false), "image/jpeg");
        parts[2] = new VolleyMultipartRequest.DataPart("image2.jpg", toByteArray(b3, false), "image/jpeg");
        parts[3] = new VolleyMultipartRequest.DataPart("image3.jpg", toByteArray(BitmapFactory.decodeResource(getResources(), productId), true), "image/jpeg");

        VolleyMultipartRequest req = new VolleyMultipartRequest(Request.Method.POST, "http://192.168.1.240:5000/api/android", new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                try {
                    progressBar.setVisibility(View.GONE);
                    imageview = (ImageView) findViewById(R.id.withProductImage);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(response.data, 0, response.data.length);
                    imageview.setImageBitmap(decodedByte);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VolleyError", error.toString());
            }
        }) {
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("0", parts[0]);
                params.put("1", parts[1]);
                params.put("2", parts[2]);
                params.put("3", parts[3]);

                return params;
            }
        };
        req.setShouldCache(false);
        req.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                0,
                1));
        Volley.newRequestQueue(getApplicationContext()).add(req);
    }

    private final String twoHyphens = "--";
    private final String lineEnd = "\r\n";
    private final String boundary = "apiclient-" + System.currentTimeMillis();


    private void dataParse(DataOutputStream dataOutputStream, VolleyMultipartRequest.DataPart[] data) throws IOException {
        for (int i = 0; i < 4; i++)
            buildDataPart(dataOutputStream, data[i], "image" + i);
    }

    private void buildDataPart(DataOutputStream dataOutputStream, VolleyMultipartRequest.DataPart dataFile, String inputName) throws IOException {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" +
                inputName + "\"; filename=\"" + dataFile.getFileName() + "\"" + lineEnd);
        if (dataFile.getType() != null && !dataFile.getType().trim().isEmpty()) {
            dataOutputStream.writeBytes("Content-Type: " + dataFile.getType() + lineEnd);
        }
        dataOutputStream.writeBytes(lineEnd);

        ByteArrayInputStream fileInputStream = new ByteArrayInputStream(dataFile.getContent());
        int bytesAvailable = fileInputStream.available();
        int maxBufferSize = 1024 * 1024;
        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
        byte[] buffer = new byte[bufferSize];
        int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

        while (bytesRead > 0) {
            dataOutputStream.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }
        dataOutputStream.writeBytes(lineEnd);
    }
}