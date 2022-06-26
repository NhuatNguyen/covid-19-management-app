package com.example.klcovid.Mainactivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.klcovid.Adapter.AdapterDetailsUser;
import com.example.klcovid.Models.Vaccinlot;
import com.example.klcovid.R;
import com.example.klcovid.Service.CheckConnection;
import com.example.klcovid.Service.Server;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class Details_infor_user extends AppCompatActivity {
    FirebaseRemoteConfig mFirebaseRemoteConfig;
    RecyclerView recyclerInforVaccin;
    TextView txtDetailsInjected, txtDetailsUser;
    ArrayList<Vaccinlot> vaccinlotArrayList;
    AdapterDetailsUser adapterDetailsUser;
    String createatvaccinated, Numberofinjections, namevaccinated, getid,url_one_user;
    public int i;
    ImageView imgbackinfor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_infor_user);

        getid = getIntent().getStringExtra("sendIdUser");
//        Toast.makeText(this, getid.toString(), Toast.LENGTH_LONG).show();

        if(CheckConnection.isConnected(getApplicationContext())){
            Init();
            Connect_url();
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
        getnumberuser();
        imgbackinfor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void Init() {
        recyclerInforVaccin = findViewById(R.id.recyclerInforVaccin);
        txtDetailsUser = findViewById(R.id.txtDetailsUser);
        txtDetailsInjected = findViewById(R.id.txtDetailsInjected);
        imgbackinfor = findViewById(R.id.imgbackinfor);
        vaccinlotArrayList = new ArrayList<>();
        adapterDetailsUser = new AdapterDetailsUser(Details_infor_user.this, vaccinlotArrayList);
        recyclerInforVaccin.setHasFixedSize(true);
        recyclerInforVaccin.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        recyclerInforVaccin.setAdapter(adapterDetailsUser);
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
                            Toast.makeText(Details_infor_user.this, "Fetch failed",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    public void getnumberuser() {
        String make_url = url_one_user + getid;
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, make_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    txtDetailsUser.setText(response.getString("fullName"));
                    JSONArray jsonArray = response.getJSONArray("vaccinated");
                    //txtgetinfor.setText(jsonArray.toString());
                    for (i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Numberofinjections = String.valueOf(i + 1);
                        JSONObject jsonObjectvancin = jsonObject.getJSONObject("vaccineLot");
                        namevaccinated = jsonObjectvancin.getString("name");
                        createatvaccinated = jsonObject.getString("createdAt");
                        txtDetailsInjected.setText("ĐÃ TIÊM " + Numberofinjections + " MŨI");
//                        Toast.makeText(Details_infor_user.this, Numberofinjections + "" +namevaccinated + "" + createatvaccinated, Toast.LENGTH_SHORT).show();
                        vaccinlotArrayList.add(new Vaccinlot(Numberofinjections, namevaccinated, createatvaccinated));
                        adapterDetailsUser.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Details_infor_user.this, error.toString() + "", Toast.LENGTH_LONG).show();
            }
        });
        queue.add(jsonObjectRequest);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        startActivity(new Intent(Details_infor_user.this, Display_Infor_User.class));
        finish();
    }
}