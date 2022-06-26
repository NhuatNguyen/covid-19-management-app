package com.example.klcovid.Mainactivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.klcovid.R;
import com.example.klcovid.Service.CheckConnection;
import com.example.klcovid.Service.Server;
import com.example.klcovid.Service.SharedPreferenceClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

public class Create_User extends AppCompatActivity {
    FirebaseRemoteConfig mFirebaseRemoteConfig;
    EditText edtfullname, edtphonenumber, edtaddress;
    Button btnCreateUser;
    String fullname, phonenumber, address, format_edtname, format_address,dateCheck,getnumbernew, input_male,url_one_user;
    String[] arr_name, arr_address;
    SharedPreferenceClass sharedPreferenceClass;
    AutoCompleteTextView auto_complete_male;
    ArrayList<String> array_male = new ArrayList<>();
    ArrayAdapter arrayAdapter;
    Boolean alert = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create__user);

        sharedPreferenceClass = new SharedPreferenceClass(Create_User.this);
        getnumbernew = sharedPreferenceClass.getValue_string("phone_number");
//        getnumbernew = getIntent().getStringExtra("phone_number");
        Toast.makeText(this, getnumbernew, Toast.LENGTH_SHORT).show();
        if(CheckConnection.isConnected(Create_User.this)){
            Init();
            //List date of male
            array_male.add("Nam");
            array_male.add("Nữ");
            array_male.add("Khác");
            arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, array_male);
            auto_complete_male.setAdapter(arrayAdapter);
            Connect_url();
            Act();
        }else{
            Toast.makeText(this, "Bạn chưa kết nối với intenet", Toast.LENGTH_SHORT).show();
        }

    }
    private void Connect_url() {
        url_one_user = mFirebaseRemoteConfig.getString("user");
        //Toast.makeText(this, "" + url_one_user, Toast.LENGTH_SHORT).show();
    }
    private void Act(){


        edtphonenumber.setText(getnumbernew);
        btnCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullname = edtfullname.getText().toString();
                phonenumber = edtphonenumber.getText().toString();
                address = edtaddress.getText().toString();
                dateCheck = "";
                //input format name
                format_edtname = "";
                arr_name = fullname.split(" ");
                for (String s : arr_name) {
                    format_edtname += String.valueOf(s.charAt(0)).toUpperCase() + s.substring(1) + " ";
                }
                //input format address
                format_address = "";
                arr_address = address.split(" ");
                for (String s : arr_address) {
                    format_address += String.valueOf(s.charAt(0)).toUpperCase() + s.substring(1) + " ";
                }
                input_male = auto_complete_male.getText().toString();
                if ((format_edtname.isEmpty()) || (input_male.isEmpty()) || (phonenumber.isEmpty()) || (format_address.isEmpty())) {
                    Toast.makeText(Create_User.this, "Không bỏ trống", Toast.LENGTH_SHORT).show();
                } else {
                    CreateUser();
                }
            }
        });
    }
    private void Init() {
        edtfullname = findViewById(R.id.edtAddFullname);
        edtaddress = findViewById(R.id.edtAddaddress);
        edtphonenumber = findViewById(R.id.edtAddphonenumber);
        btnCreateUser = findViewById(R.id.btnCreateUser);
        auto_complete_male = findViewById(R.id.auto_complete_male);

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
                            Toast.makeText(Create_User.this, "Fetch failed",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void CreateUser() {
        final HashMap<String, String> params = new HashMap<>();
        params.put("fullName", format_edtname.trim());
        params.put("phoneNumber", phonenumber);
        params.put("idNumber", input_male);
        params.put("address", format_address.trim());
        params.put("alert", String.valueOf(alert));
        params.put("dateCheck",dateCheck);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url_one_user, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Toast.makeText(Create_User.this, "Tạo mới thành công", Toast.LENGTH_SHORT).show();
                sharedPreferenceClass.setValue_String("phone_number", getnumbernew);
                Intent intent = new Intent(Create_User.this, Check_NumberPhone.class);
                startActivity(intent);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        JSONObject object = new JSONObject(res);
                        Toast.makeText(Create_User.this, object.getString("Không thành công"), Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    } catch (JSONException | UnsupportedEncodingException je) {
                        je.printStackTrace();
                    }
                }
            }
        });

        int socketTime = 9000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTime, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    protected void onPause() {
        super.onPause();
        onBackPressed();
    }
}