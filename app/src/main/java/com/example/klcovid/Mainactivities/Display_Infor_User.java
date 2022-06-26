package com.example.klcovid.Mainactivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Display_Infor_User extends AppCompatActivity {
    FirebaseRemoteConfig mFirebaseRemoteConfig;
    String getid,url_one_user,formateddate;
    ImageView imgQRUser;
    ConstraintLayout ConstrainUpdate,ConstraintdownloadQR,ConstraintScannerQR,ConstraintViewInfor,constraintlogout,constraint_background;
    TextView txtNameUser,txt_notification_sick;
    SharedPreferenceClass sharedPreferenceClass;
    SimpleDateFormat dateFormat;
    Date date;
//    ProgressBar progress_display_fullname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display__infor__user);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        sharedPreferenceClass = new SharedPreferenceClass(Display_Infor_User.this);
        getid = sharedPreferenceClass.getValue_string("sendIdUser");
        //getid = getIntent().getStringExtra("sendIdUser");
        if(CheckConnection.isConnected(Display_Infor_User.this)){
            Init();
            Connect_url();
            GetUserId();
            Create_QR();
            Act();
        }else{
            Toast.makeText(this, "Bạn chưa kết nối Intenet", Toast.LENGTH_SHORT).show();
        }
    }

    private void Connect_url() {
        url_one_user = mFirebaseRemoteConfig.getString("user");
        //Toast.makeText(this, "" + url_one_user, Toast.LENGTH_SHORT).show();
    }

    private void GetUserId() {
        String make_url = url_one_user + getid;
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, make_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    txtNameUser.setText("Xin Chào "+response.getString("fullName").toUpperCase());
                    if(response.getBoolean("alert") == true){
                        txt_notification_sick.setMaxLines(2);
                        txt_notification_sick.setEllipsize(TextUtils.TruncateAt.END);
                        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        try {
                            date = dateFormat.parse(response.getString("dateCheck"));
                            formateddate = DateFormat.getDateInstance().format(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        txt_notification_sick.setText("Bạn đã tiếp xúc gần với người bệnh, xin hãy cách ly ở nhà đến ngày " +
                                formateddate.replace("thg","/").replace(",","/").replace(" ",""));
                        constraint_background.setBackgroundResource(R.color.yellow);
                    }else{
                        txt_notification_sick.setText("");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Display_Infor_User.this, "Không nhận được dữ liệu ", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        queue.add(jsonObjectRequest);
    }


    private void Create_QR() {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        StringBuilder textToSend = new StringBuilder();
        textToSend.append(getid);
        try {
            BitMatrix bitMatrix = multiFormatWriter.
                    encode(textToSend.toString(), BarcodeFormat.QR_CODE,500,500);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imgQRUser.setImageBitmap(bitmap);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void Act() {
        ConstrainUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Display_Infor_User.this,UpdateUser.class);
                intent.putExtra("sendIdUser",getid);
                startActivity(intent);
            }
        });
        ConstraintdownloadQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Display_Infor_User.this,Download_QR.class);
                intent.putExtra("sendIdUser",getid);
                startActivity(intent);
            }
        });
        ConstraintScannerQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent intent = new Intent(Display_Infor_User.this,Scanner_QR.class);
            intent.putExtra("sendIdUser",getid);
            startActivity(intent);
            }
        });
        ConstraintViewInfor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Display_Infor_User.this,Details_infor_user.class);
                intent.putExtra("sendIdUser",getid);
                startActivity(intent);
            }
        });
        constraintlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                sharedPreferenceClass.Logout();
//                startActivity(new Intent(Display_Infor_User.this,VerifyNumberPhone.class));
//                startActivity(new Intent(Display_Infor_User.this,Check_NumberPhone.class));
//                finish();
                Toast.makeText(Display_Infor_User.this, "Khong the hien thi", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void Init() {
        imgQRUser = findViewById(R.id.imgQRUser);
        ConstrainUpdate = findViewById(R.id.UpdateUser);
        ConstraintdownloadQR = findViewById(R.id.constraintdownloadQR);
        ConstraintScannerQR = findViewById(R.id.ConstraintScannerQR);
        txtNameUser = findViewById(R.id.txtNameUser);
        ConstraintViewInfor = findViewById(R.id.ConstraintViewInfor);
        constraintlogout= findViewById(R.id.constraintlogout);
        txt_notification_sick = findViewById(R.id.txt_notification_sick);
        constraint_background = findViewById(R.id.constraint_background);
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
                            Toast.makeText(Display_Infor_User.this, "Fetch failed",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
//        progress_display_fullname = findViewById(R.id.progress_display_fullname);
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        startActivity(getIntent());
        finish();
    }
}