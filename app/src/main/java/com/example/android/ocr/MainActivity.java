package com.example.android.ocr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.logging.Logger.global;

public class MainActivity extends AppCompatActivity {

    private Button snapBtn;
    private Button detectBtn;
    private ImageView imageView;
    private EditText editText;
    private Bitmap imageBitmap;
    private Button uploadBtn;
    String Registration_Plates;
    Bitmap bitmap;
    String urlUpload = "http://sachdevanikharwork.000webhostapp.com/OCR/upload.php";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        snapBtn = findViewById(R.id.snapBtn);
        detectBtn = findViewById(R.id.detectBtn);
        imageView = findViewById(R.id.imageView);
        editText = findViewById(R.id.editText);
        uploadBtn = findViewById(R.id.uploadBtn);

        dispatchTakePictureIntent();

        /*snapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
                //detectBtn.performClick();
            }
        }); */

        detectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detectTxt();
            }
        });


    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra("android.intent.extra.quickCapture",true);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);

            detectTxt();
        }
    }

    private void detectTxt() {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextDetector detector = FirebaseVision.getInstance().getVisionTextDetector();
        detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                processTxt(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                uploadBtn.performClick();
                dispatchTakePictureIntent();

                            }
        });

    }

    public void uploadtophp (View view) {
        Registration_Plates = editText.getText().toString().trim();

        BackgroundTask backgroundTask = new BackgroundTask(this );

        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlUpload , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();

            }

        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error){
                Toast.makeText(getApplicationContext(),"Error"+error.toString(),Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String > params = new HashMap<>();
                String imageData = imageToString(bitmap);
                params.put("image", imageData);
                params.put("user",Registration_Plates);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue( MainActivity.this);
        requestQueue.add(stringRequest);

    }

/* This code is commented */

       /*public void uploadtophp (View view) {
            Registration_Plates = editText.getText().toString().trim();

            String method = "register";
            BackgroundTask backgroundTask = new BackgroundTask(this);
            backgroundTask.execute(method,Registration_Plates);

        }*/

    private void processTxt(FirebaseVisionText text) {
        List<FirebaseVisionText.Block> blocks = text.getBlocks();
        if (blocks.size() == 0) {
            uploadBtn.performClick();
            dispatchTakePictureIntent();
            Toast.makeText(MainActivity.this, "No Text Detected But Uploaded Successfully", Toast.LENGTH_LONG).show();
            return;
        }
        for (FirebaseVisionText.Block block : text.getBlocks()) {
            String txt = block.getText();
            txt = txt.replaceAll("\\s","");
            editText.setTextSize(18);
            editText.setText(txt);

            uploadBtn.performClick();
            dispatchTakePictureIntent();

        }
    }
    public String imageToString(Bitmap bitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.JPEG,75,outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        String encodedImage = Base64.encodeToString(imageBytes,Base64.DEFAULT);
        return encodedImage;
    }

}
