package com.example.klcovid.Mainactivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.klcovid.Models.Place;
import com.example.klcovid.R;
import com.example.klcovid.Service.Server;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Scanner_QR extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    FirebaseRemoteConfig mFirebaseRemoteConfig;
    ArrayList<Place> placeArrayList;
    ZXingScannerView scannerView;
    Place place;
    boolean j = false;
    String getid,id,url_place,url_check_place;
//    int REQUEST_CODE_CAMERA = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(Scanner_QR.this);
        setContentView(scannerView);
        getid = getIntent().getStringExtra("sendIdUser");
        placeArrayList = new ArrayList<>();
        Init();
        Connect_url();
        Get_place();
        Dexter.withContext(Scanner_QR.this).withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        scannerView.startCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    private void Init() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(10)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.defaults_value);

        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            boolean updated = task.getResult();
//                            Toast.makeText(MainActivity.this, "Fetch and activate succeeded",
//                                    Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(Scanner_QR.this, "Fetch failed",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void Connect_url() {
        url_place = mFirebaseRemoteConfig.getString("place");
        Toast.makeText(this, "" + url_place, Toast.LENGTH_SHORT).show();
        url_check_place = mFirebaseRemoteConfig.getString("check_place");
        Toast.makeText(this, "" + url_check_place, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void handleResult(Result rawResult) {
        String data = rawResult.getText().toString();
        Check_idplace(data);
        onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //startActivity(new Intent(Scanner_QR.this,Display_Infor_User.class));
        scannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(Scanner_QR.this);
        scannerView.startCamera();
    }

    public void Get_place(){
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url_place, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response != null){
                    for(int i = 0;i<response.length();i++){
                        try {
                            JSONObject object =response.getJSONObject(i);
                            id = object.getString("id");
                            placeArrayList.add(new Place(id));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Scanner_QR.this,"Không Nhận Được Dữ Liệu" + error.toString() , Toast.LENGTH_LONG).show();
            }
        });
        queue.add(jsonArrayRequest);
    }
    public void Check_idplace(String iddata){
        for(int i = 0;i < placeArrayList.size();i++){
            place = placeArrayList.get(i);
            Log.d("Arr: ", place.id+"");
            if(iddata.equals(place.id)){
                Log.d("Arr2: ", place.id+"");
                j = true;
                break;
            }
        }
        if(j == true){
            Update_Check_Place(iddata);
        }else{
            Toast.makeText(this, "Không Tồn Tại", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Scanner_QR.this, Display_Infor_User.class);
            intent.putExtra("sendIdUser", getid);
            startActivity(intent);
            finish();
        }
    }
    private void Update_Check_Place(String data) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        final HashMap<String, String> params = new HashMap<>();
        params.put("id", getid);
        params.put("placeId", data);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url_check_place, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                startActivity(new Intent(Scanner_QR.this,Display_Infor_User.class));
                //Default.txtQR.setText(data);
                Toast.makeText(Scanner_QR.this, "Đã quét thành công", Toast.LENGTH_SHORT).show();
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Scanner_QR.this, "Lỗi: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(objectRequest);
    }
}