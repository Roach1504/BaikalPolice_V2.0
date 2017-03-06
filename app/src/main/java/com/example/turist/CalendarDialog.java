package com.example.turist;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;


public class CalendarDialog extends DialogFragment {

    String selectedDate;
    String head;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_calendar, container, false);

        savedInstanceState = this.getArguments();
        if(savedInstanceState !=null){
            head = savedInstanceState.getString("title");
            Log.e("bandel", savedInstanceState.getString("title"));
            getDialog().setTitle(savedInstanceState.getString("title"));
        }
        else {
            Log.e("error", "2");
        }


        CalendarView calendarView = (CalendarView) rootView.findViewById(R.id.calendarView1);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year,
                                            int month, int dayOfMonth) {
                int mYear = year;
                int mMonth = month+1;
                int mDay = dayOfMonth;
                if(mMonth<10) {
                    if(mDay<10){
                        selectedDate = new StringBuilder().append(mYear)
                                .append("-").append("0" + mMonth).append("-").append("0"+mDay)
                                .append(" ").toString();
                    }
                    else{
                        selectedDate = new StringBuilder().append(mYear)
                                .append("-").append("0" + mMonth).append("-").append(mDay)
                                .append(" ").toString();
                    }
                }
                else{
                    if(mDay<10){
                        selectedDate = new StringBuilder().append(mYear)
                                .append("-").append(mMonth).append("-").append("0"+mDay)
                                .append(" ").toString();
                    }
                    else{
                        selectedDate = new StringBuilder().append(mYear)
                                .append("-").append(mMonth).append("-").append(mDay)
                                .append(" ").toString();
                    }
                }
                Log.e("date", selectedDate);
            }
        });

        Button accept =(Button)rootView.findViewById(R.id.accept);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle= new Bundle();
                bundle.putString("date", selectedDate);
                bundle.putString("titles", head);
                TextView textStart = (TextView)getActivity().findViewById(R.id.text_start);
                TextView textEnd = (TextView)getActivity().findViewById(R.id.text_end);
                if(head.equals("Дата от")){
                    textStart.setText(selectedDate);
                }
                else{
                    textEnd.setText(selectedDate);
                }

                HistoriMapItem historiMapItem=new HistoriMapItem();
                historiMapItem.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(historiMapItem).commit();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.track_list,historiMapItem).commit();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.track_list,historiMapItem).commit();
                dismiss();
            }
        });

        Button cancel=(Button)rootView.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textStart = (TextView)getActivity().findViewById(R.id.text_start);
                TextView textEnd = (TextView)getActivity().findViewById(R.id.text_end);
                if(head.equals("Дата от")){
                    textStart.setText("От: ");
                }
                else{
                    textEnd.setText("До: ");
                }
                dismiss();
            }
        });


        return rootView;
    }
}
