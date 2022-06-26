package com.example.klcovid.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.klcovid.Models.Vaccinlot;
import com.example.klcovid.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdapterDetailsUser extends RecyclerView.Adapter<AdapterDetailsUser.ItemHolder>{


    Context context;
    List<Vaccinlot> vaccineLotList;
    Date date;
    String formateddate;

    public AdapterDetailsUser(Context context, List<Vaccinlot> vaccineLotList) {
        this.context = context;
        this.vaccineLotList = vaccineLotList;
    }
    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_adapter_details_user,null);
        ItemHolder itemHolder = new ItemHolder(view);
        return itemHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        Vaccinlot vaccineLot = vaccineLotList.get(position);
        holder.txtDesignNameVaccin.setText("Tên: " + vaccineLot.getName());
        //String strDate = "2013-05-15T10:00:00-0700";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            date = dateFormat.parse(vaccineLot.getCreatedAt());
            formateddate = DateFormat.getDateInstance().format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.txtDesignDatetime.setText("Thời Gian Tiêm: " + formateddate.replace("thg","/").replace(",","/").replace(" ",""));
        holder.txtDesignInjected.setText("Mũi: " + vaccineLot.getSomui());
    }

    @Override
    public int getItemCount() {
        return vaccineLotList.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder{
        TextView txtDesignNameVaccin,txtDesignDatetime,txtDesignInjected;
        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            txtDesignNameVaccin = itemView.findViewById(R.id.txtDesignNameVaccin);
            txtDesignDatetime = itemView.findViewById(R.id.txtDesignDatetime);
            txtDesignInjected = itemView.findViewById(R.id.txtDesignInjected);
        }
    }
}
