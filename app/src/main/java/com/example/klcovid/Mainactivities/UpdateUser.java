package com.example.klcovid.Mainactivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.klcovid.R;
import com.example.klcovid.Service.CheckConnection;
import com.example.klcovid.Service.Server;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class UpdateUser extends AppCompatActivity {
    FirebaseRemoteConfig mFirebaseRemoteConfig;
    EditText edtname, edtphone, edtidphone, edtaddress;
    String fullname, address,format_edtname,format_address;
    Button btnUpdateUser;
    String getid,url_one_user;
    ImageView imgbackinfor;
    String [] arr_name,arr_address;
    AutoCompleteTextView auto_complete_update_male;
    ArrayList<String> array_male = new ArrayList<>();
    ArrayAdapter arrayAdapter;
    String input_male;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);
        getid = getIntent().getStringExtra("sendIdUser");
        //Toast.makeText(this, getid.toString(), Toast.LENGTH_LONG).show();
        if(CheckConnection.isConnected(getApplicationContext())){
            Init();
            array_male.add("Nam");
            array_male.add("Nữ");
            array_male.add("Khác");
            arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,array_male);
            auto_complete_update_male.setThreshold(5);
            auto_complete_update_male.setAdapter(arrayAdapter);
            Connect_url();
            getUser();
            Act();
        }else{
            Toast.makeText(this, "Chưa kết nối intenet", Toast.LENGTH_SHORT).show();
        }
    }
    private void Connect_url() {
        url_one_user = mFirebaseRemoteConfig.getString("user");
        //Toast.makeText(this, "" + url_one_user, Toast.LENGTH_SHORT).show();
    }
    private void Act() {
        btnUpdateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Input();
            }
        });
        imgbackinfor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getUser() {
        String make_urlgetone = url_one_user + getid;
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, make_urlgetone, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                Toast.makeText(UpdateUser.this, response.toString(), Toast.LENGTH_LONG).show();
                try {
                    edtname.setText(response.getString("fullName"));
                    edtphone.setText(response.getString("phoneNumber"));
//                    edtidphone.setText(response.getString("idNumber"));
                    auto_complete_update_male.setText(response.getString("idNumber"));
                    edtaddress.setText(response.getString("address"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                finish();
            }
        });
        queue.add(jsonObjectRequest);
    }

    private void UpdateData() {
        Toast.makeText(this, "Bắt Đầu Cập Nhật", Toast.LENGTH_SHORT).show();
        RequestQueue queue = Volley.newRequestQueue(this);

        final HashMap<String, String> params = new HashMap<>();
        params.put("fullName", format_edtname.trim());
        params.put("idNumber",input_male);
        params.put("address", format_address.trim());

        String Update_User = Server.user + getid;

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.PUT, Update_User, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //startActivity(new Intent(UpdateUser.this, Display_Infor_User.class));
                Toast.makeText(UpdateUser.this, "Cập Nhật Thành Công", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(UpdateUser.this, "Cập Nhật Thất Bại: " + error.toString(), Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });
        int socketTime = 9000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTime, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        objectRequest.setRetryPolicy(policy);
        queue.add(objectRequest);
    }
    private void Input(){
        //list male

        fullname = edtname.getText().toString();
        address = edtaddress.getText().toString();
        input_male = auto_complete_update_male.getText().toString();
        //input format name
        format_edtname = "";
        arr_name = fullname.split(" ");
        for(String s : arr_name){
            format_edtname += String.valueOf(s.charAt(0)).toUpperCase() +s.substring(1) + " ";
        }
        //input format address
        format_address = "";
        arr_address = address.split(" ");
        for(String s : arr_address){
            format_address += String.valueOf(s.charAt(0)).toUpperCase() +s.substring(1) + " ";
        }

        if (format_edtname.isEmpty() || format_address.isEmpty() || input_male.isEmpty()) {
            Toast.makeText(UpdateUser.this, "Không được để trống", Toast.LENGTH_SHORT).show();
        } else {
            UpdateData();
            onBackPressed();
        }
    }
    private void Init() {
        edtname = findViewById(R.id.edtUpdateFullname);
        edtphone = findViewById(R.id.edtUpdateNumberphone);
        edtaddress = findViewById(R.id.edtUpdateAddress);
        btnUpdateUser = findViewById(R.id.btnUpdateUser);
        imgbackinfor= findViewById(R.id.imgbackinfor);
        auto_complete_update_male = findViewById(R.id.auto_complete_update_male);

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
                            Toast.makeText(UpdateUser.this, "Fetch failed",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
    @Override
    protected void onPause() {
        super.onPause();
//        startActivity(new Intent(UpdateUser.this, Display_Infor_User.class));
        finish();
    }
}