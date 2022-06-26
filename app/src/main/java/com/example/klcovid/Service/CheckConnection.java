package com.example.klcovid.Service;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class CheckConnection {
    public static boolean isConnected(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null) {
            if (ni.getType() == ConnectivityManager.TYPE_WIFI)
                if (ni.isConnected())
                    return true;
            if (ni.getType() == ConnectivityManager.TYPE_MOBILE)
                if (ni.isConnected())
                    return true;
            if (ni.getType() == ConnectivityManager.TYPE_ETHERNET)
                if (ni.isConnected())
                    return true;
        }
        return false; //none of connections available
    }
    public static  void ShowToast(Context context,String notifications){
        Toast.makeText(context, notifications, Toast.LENGTH_SHORT).show();
    }
}


