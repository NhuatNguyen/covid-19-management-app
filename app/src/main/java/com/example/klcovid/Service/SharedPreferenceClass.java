package com.example.klcovid.Service;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceClass {
    private SharedPreferences appShared;
    private SharedPreferences.Editor prefsEditor;
    public SharedPreferenceClass(Context context){
        appShared = context.getSharedPreferences("datalogin", Activity.MODE_PRIVATE);
        this.prefsEditor = appShared.edit();
    }
    public String getValue_string(String key){
        return appShared.getString(key,"");
    }

    public void setValue_String(String key,String value){
        prefsEditor.putString(key,value).commit();
    }
    public void Logout(){
        prefsEditor.clear().commit();
    }
}
