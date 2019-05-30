package com.example.android.ocr;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SearchRecentSuggestionsProvider;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;


public class BackgroundTask extends AsyncTask<String,Void,String> {
    AlertDialog alertDialog;
    Context ctx;
    BackgroundTask(Context ctx  )
    {
        this.ctx=ctx;
    }

    @Override
    protected void onPreExecute() {
       // alertDialog = new AlertDialog.Builder(ctx).create();
       // alertDialog.setTitle("Login Information");
    }

    @Override
    protected String doInBackground(String... params) {

        String reg_url = "http://sachdevanikharwork.000webhostapp.com/OCR/upload.php";


            String Registration_Plates = params[0];

            try {
                URL url = new URL(reg_url);
                HttpURLConnection httpURLConnection =(HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));

                String data = URLEncoder.encode("user","UTF-8")+"="+URLEncoder.encode(Registration_Plates,"UTF-8");
                Log.e("data" , data);
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS = httpURLConnection.getInputStream();
                IS.close();
                return "Number Plate Uploaded Successfully";
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }



        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {

    }

    @Override
    protected void onPostExecute(String result) {

        if (result.equals("Number Plate Uploaded Successfully"))
        {
            Toast.makeText(ctx,result,Toast.LENGTH_SHORT).show();
        }
        else
        {
            alertDialog.setMessage(result);
            alertDialog.show();

        }
    }

}