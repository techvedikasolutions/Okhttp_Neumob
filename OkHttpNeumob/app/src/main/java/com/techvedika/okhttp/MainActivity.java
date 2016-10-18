package com.techvedika.okhttp;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.neumob.api.Neumob;
import com.techvedika.okhttpneumob.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import cz.msebera.android.httpclient.entity.StringEntity;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "NEUMOB";
    private static int bytesdownload;
    private static final String NEUMOB_CLIENT_KEY = "QPBbwUEiSUZWLcsy";
    private static final String GET_JSON = "http://pratikbutani.x10.mx/json_data.json";
    private static final String JSON_URL = "http://54.214.42.193/vip/index.php?tag=companyExtension";
    private String IMAGE_URL = "http://serviceapi.skholingua.com/images/skholingua_image.png";
    public static String URL_Login = "https://mvisitor.techvedika.com/BusinessCard/user/login";

    private RadioGroup mRadioGroup_OkhttpSelection;
    private String selectedChoice = "okhttp";
    private Button mbtn_getData;
    private Button mbtn_getImage;
    private Button mbtn_PostData;
    private TextView mtxt_bytesDownloaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Neumob.initialize(getApplicationContext(), NEUMOB_CLIENT_KEY, new Runnable() {
            @Override
            public void run() {
                if (Neumob.isInitialized()) {
                    boolean isAccelerated = Neumob.isAccelerated();
                    Log.d(TAG, "isAccelerated" + isAccelerated);
                } else if (Neumob.isAuthenticated()) {
                    Log.d(TAG, "isAuthenticated" + Neumob.isAuthenticated());
                }
            }
        });
        mRadioGroup_OkhttpSelection = (RadioGroup) findViewById(R.id.rg_okhttpSelection);
        mbtn_getData = (Button) findViewById(R.id.btn_GetJsonData);
        mbtn_getImage = (Button) findViewById(R.id.btn_GetImageFile);
        mbtn_PostData = (Button) findViewById(R.id.btn_PostJsonData);
        mtxt_bytesDownloaded = (TextView) findViewById(R.id.txt_BytesDownloaded);
        mbtn_getData.setOnClickListener(this);
        mbtn_getImage.setOnClickListener(this);
        mbtn_PostData.setOnClickListener(this);
        mRadioGroup_OkhttpSelection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedID) {
                if (checkedID == R.id.rb_okhttp) {
                    selectedChoice = "okhttp";
                } else if (checkedID == R.id.rb_okhttp2) {
                    selectedChoice = "okhttp2";
                } else {
                    selectedChoice = "okhttp3";
                }
                Toast.makeText(getApplicationContext(), selectedChoice, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_GetJsonData) {
            OkHttpHandler handler = new OkHttpHandler(1);

            byte[] text = new byte[0];
            try {
                text = handler.execute(JSON_URL).get();
                if (text != null && text.length > 0) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(text, 0, text.length);
                    // imageView.setImageBitmap(bitmap);
                    mtxt_bytesDownloaded.setText("Total Json data download: " + text.length);
                    Log.e("Images", ":" + text.length);
                }
            } catch (Exception e) {
                mtxt_bytesDownloaded.setText("sorry, something went wrong!");
            }

        } else if (view.getId() == R.id.btn_GetImageFile) {
            OkHttpHandler handler = new OkHttpHandler(1);

            byte[] image = new byte[0];
            try {
                image = handler.execute(IMAGE_URL).get();
                if (image != null && image.length > 0) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                    // imageView.setImageBitmap(bitmap);
                    mtxt_bytesDownloaded.setText("Total Image Data download: " + image.length);
                    Log.e("Images", ":" + image.length);
                }
            } catch (Exception e) {
                mtxt_bytesDownloaded.setText("sorry, something went wrong!");
            }
        } else if (view.getId() == R.id.btn_PostJsonData) {
            getloginRequestUsingOKHTTP3();
        }

    }

    public class OkHttpHandler extends AsyncTask<String, Void, byte[]> {

        OkHttpClient client = Neumob.createOkHttp3Client();
        int choice = 1;

        public OkHttpHandler(int i) {
            choice = i;
        }

        @Override
        protected byte[] doInBackground(String... strings) {
            if (choice == 1 || choice == 2) {
                Request.Builder builder = new Request.Builder();
                builder.url(strings[0]);
                Request request = builder.build();

                try {
                    Response response = client.newCall(request).execute();
                    return response.body().bytes();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            } else if (choice == 3) {

                return null;
            } else {
                return null;
            }
        }

    }

    private void getloginRequestUsingOKHTTP3() {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                StringEntity entity = null;
                byte[] result = null;
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("site", "1");
                    jsonObject.put("deviceType", "ANDROID");
                    jsonObject.put("email", "testapp@gmail.com");
                    jsonObject.put("deviceToken", "0123456789");
                    jsonObject.put("password", "12345");
                    System.out.println("jsonObject" + jsonObject);
                    entity = new StringEntity(jsonObject.toString());
                    Headers.Builder builder = new Headers.Builder();
                    builder.add("Accept", "application/json");
                    builder.add("siteID", "1");
                    builder.add("charset", "utf-8");
                    result = postJson(URL_Login, jsonObject.toString(), builder);
                    Log.e("result", ":" + result.length);
                    bytesdownload = result.length;
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }


        }.execute();

        mtxt_bytesDownloaded.setText("Successfully Posted json data " );
    }

    private static byte[] postJson(String url, String json, Headers.Builder headers) throws IOException {
        MediaType type = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(type, json);
        Request request = new Request.Builder().url(url).post(body).headers(headers.build()).build();
        OkHttpClient client = Neumob.createOkHttp3Client();
        Response response = client.newCall(request).execute();

        return response.body().bytes();
    }
    public static MultipartBody uploadRequestBody(String title, String imageFormat, String token, File file) {

        MediaType MEDIA_TYPE = MediaType.parse("image/" + imageFormat); // e.g. "image/png"
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("action", "upload")
                .addFormDataPart("format", "json")
                .addFormDataPart("filename", title + "." + imageFormat) //e.g. title.png --> imageFormat = png
                .addFormDataPart("file", "...", RequestBody.create(MEDIA_TYPE, file))
                .addFormDataPart("token", token)
                .build();
    }

}

