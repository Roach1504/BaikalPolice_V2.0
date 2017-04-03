package com.example.turist;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

public class DialogRenameFio extends DialogFragment{
    EditText name;
    Button accept;
    Button cancel;
    JSONArray backUpPhone;
    JSONArray backUpEmail;
    JSONArray backUpSocial;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_rename, container, false);
        savedInstanceState = this.getArguments();
        name =(EditText) rootView.findViewById(R.id.book_title);
        if(savedInstanceState !=null){
            Log.e("onCreadDialog", "true");
            name.setHint(savedInstanceState.getString("hint"));
            getDialog().setTitle(savedInstanceState.getString("title"));
            if(savedInstanceState.getString("hint").equals("Введите телефон")){
                name.setInputType(InputType.TYPE_CLASS_PHONE);
            }
            if(savedInstanceState.getString("hint").equals("Введите email")){
                name.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            }

            try {
                backUpPhone = new JSONArray(savedInstanceState.getString("beakUpPhone"));
                backUpEmail = new JSONArray(savedInstanceState.getString("beakUpEmail"));
                backUpSocial = new JSONArray(savedInstanceState.getString("beakUpSocials"));
            } catch (JSONException e) {
                Log.e("Error", "beakUp");
                e.printStackTrace();
            }
        }
        else{
            Log.e("onCreadDialog", "false");
        }

        accept = (Button)rootView.findViewById(R.id.accept);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("name, ", name.getText().toString()+"hhh");
                if (name.getText().toString().length() < 5) {
                    Toast.makeText(getContext(), "Должно быть более 5 символов", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (name.getText().toString().equals("")) {
                        //Toast.makeText(getContext(), "Вы ни чего не ввели", Toast.LENGTH_SHORT).show();
                    } else {
                        Bundle bundle = new Bundle();
                        Log.e("marker", name.getHint().toString());
                        bundle.putString("marker", name.getHint().toString());
                        bundle.putString("body", name.getText().toString());
                        bundle.putString("beakUpPhone", backUpPhone.toString());
                        bundle.putString("beakUpEmail", backUpEmail.toString());
                        bundle.putString("beakUpSocials", backUpSocial.toString());

                        ChangeProfile changeProfile = new ChangeProfile();
                        changeProfile.setArguments(bundle);
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.replace(R.id.first_setting, changeProfile).commit();

                        dismiss();
                    }
                }
            }
        });
        cancel = (Button)rootView.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        return rootView;
    }
}
