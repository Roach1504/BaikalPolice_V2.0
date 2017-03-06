package com.example.turist;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class FirstFragmentSetting extends Fragment{
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.first_setting_fragment, container, false);



        FragmentSetting fragmentSetting=new FragmentSetting();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragmentSetting).commit();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.first_setting,fragmentSetting).commit();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.first_setting,fragmentSetting).commit();
        return view;
    }
}
