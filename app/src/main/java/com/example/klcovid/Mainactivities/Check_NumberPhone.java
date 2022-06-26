package com.example.klcovid.Mainactivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.klcovid.Models.User;
import com.example.klcovid.R;
import com.example.klcovid.Service.CheckConnection;
import com.example.klcovid.Service.Server;
import com.example.klcovid.Service.SharedPreferenceClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Check_NumberPhone extends AppCompatActivity {
    FirebaseRemoteConfig mFirebaseRemoteConfig;
    ArrayList<User> userArrayList;
    LinearLayout progress_check_numberphone;
    SharedPreferenceClass sharedPreferenceClass;
    String getnumber,url_one_user;
    User user;
    Boolean j = false;
    Boolean Check_array = false;
    CountDownTimer Timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check__number_phone);
        progress_check_numberphone = findViewById(R.id.progress_check_numberphone);
        sharedPreferenceClass = new SharedPreferenceClass(Check_NumberPhone.this);
        getnumber = sharedPreferenceClass.getValue_string("phone_number");
        //getnumber = "002345678";
        Init();
        Connect_url();
        getnumberuser();
        Timer = new CountDownTimer(12000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                progress_check_numberphone.setVisibility(View.INVISIBLE);
                Toast.makeText(Check_NumberPhone.this, "Thành Công", Toast.LENGTH_SHORT).show();
                Check_Number(getnumber);
            }
        }.start();
    }

    private void Connect_url() {
        url_one_user = mFirebaseRemoteConfig.getString("user");
        //Toast.makeText(this, "" + url_one_user, Toast.LENGTH_SHORT).show();
    }
    private void Init() {
        //firebase
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
                            Toast.makeText(Check_NumberPhone.this, "Fetch failed",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    public void getnumberuser() {
        userArrayList = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url_one_user, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response != null) {
                    String phonenumber = "";
                    String id = "";
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject object = response.getJSONObject(i);
                            phonenumber = object.getString("phoneNumber");
                            id = object.getString("id");
                            userArrayList.add(new User(id, phonenumber));
                            Log.d("Number: ", phonenumber + " " + id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Check_NumberPhone.this, "Không nhận được dữ liệu", Toast.LENGTH_LONG).show();
                Timer.cancel();
                onBackPressed();
            }
        });
        int socketTime = 12000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTime, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonArrayRequest.setRetryPolicy(policy);
        queue.add(jsonArrayRequest);
    }

    private void Check_Number(String data_number) {
        for (int i = 0; i < userArrayList.size(); i++) {
            user = userArrayList.get(i);
            Log.d("Arr: ", user.phoneNumber + "");
            if (data_number.equals(user.phoneNumber)) {
                Log.d("Arr2: ", user.phoneNumber + "");
                j = true;
                break;
            }
        }
        if (j == true) {
            sharedPreferenceClass.setValue_String("sendIdUser", user.getId());
            Intent intent = new Intent(Check_NumberPhone.this, Display_Infor_User.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Không tồn tại số điện thoại này! Vui lòng tạo", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Check_NumberPhone.this, Create_User.class);
//            intent.putExtra("phone_number",getnumber);
            startActivity(intent);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        //Check_Number(getnumber);
        SharedPreferences admin_Nhuat = getSharedPreferences("datalogin", MODE_PRIVATE);
        if (admin_Nhuat.contains("sendIdUser")) {
            startActivity(new Intent(Check_NumberPhone.this, Display_Infor_User.class));
            Timer.cancel();
            finish();
        }
    }
}