package com.example.turist;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


public class HistoriMap extends Fragment {
    public static boolean hasConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected())
        {

            return true;
        }
        return false;
    }

    ImageView calendarStart;
    ImageView calendarFinish;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.histori_fragment, container, false);


        if(!hasConnection(getContext())){
            Toast.makeText(getContext(), "Подключите интернет", Toast.LENGTH_LONG).show();
        }
        calendarStart = (ImageView)view.findViewById(R.id.data_start);
        calendarFinish = (ImageView)view.findViewById(R.id.data_end);
        calendarStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("title", "Дата от");
                FragmentManager fm = getFragmentManager();
                CalendarDialog calendarDialog= new CalendarDialog();
                calendarDialog.setArguments(bundle);
                calendarDialog.show(fm, "Change");
            }
        });

        calendarFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("title", "Дата до");
                FragmentManager fm = getFragmentManager();
                CalendarDialog calendarDialog= new CalendarDialog();
                calendarDialog.setArguments(bundle);
                calendarDialog.show(fm, "Change");
            }
        });

        HistoriMapItem historiMapItem=new HistoriMapItem();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(historiMapItem).commit();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.track_list,historiMapItem).commit();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.track_list,historiMapItem).commit();




        return view;
    }

}
