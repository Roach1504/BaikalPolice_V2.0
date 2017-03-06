package com.example.turist;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class NameTreakDialog extends DialogFragment {
    String message;
    public void saveNameTreak(final String id, final String name){
        class INSERTtoGps4 extends AsyncTask<Void, Void, Void> {


            protected String getStatus(String key, String strJson) {
                JSONObject dataJsonObj = null;
                String secondName = "";
                try {
                    dataJsonObj = new JSONObject(strJson);
                    secondName = dataJsonObj.getString(key);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return secondName;
            }

            protected Void doInBackground(Void... params) {
                try {

                    OkHttpClient client = new OkHttpClient();
                    RequestBody formBody = new FormBody.Builder()
                            .addEncoded("track_id", id)
                            .addEncoded("track_title", name)
                            .build();


                    Request request = new Request.Builder()
                            .url("http://109.120.189.141:81/web/api/track/change-track-title")
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .post(formBody)
                            .build();

                    okhttp3.Call call = client.newCall(request);
                    Response response = call.execute();
                    Callback callback = new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                        }
                    };

                    callback.onResponse(call, response);
                    message = response.body().string().trim();

                    Log.e("saveNameTreak", message);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                dismiss();
                super.onPostExecute(aVoid);
            }
        }
        INSERTtoGps4 inserTtoGps1=new INSERTtoGps4();
        inserTtoGps1.execute();
    }

    EditText name;
    String idTreak;
    Button accept;
    Button cancel;
    String history="0";
    SharedPreferences sharedPreferences;

    String id_track;
    String start_track;
    String end_track;
    String titles;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.name_traek_dialog, container, false);
        savedInstanceState = this.getArguments();
        getDialog().setTitle("Имя трека");
        if(savedInstanceState !=null){
            idTreak = savedInstanceState.getString("id_treak");
            history = savedInstanceState.getString("history");
            id_track = savedInstanceState.getString("id_track");
            start_track = savedInstanceState.getString("start_track");
            end_track = savedInstanceState.getString("end_track");
            titles = savedInstanceState.getString("title");
        }
        name =(EditText) rootView.findViewById(R.id.nameTreak);
        Log.e("NAME", history+"  fgdfg");
        if(history != null){
            Log.e("test", "dfgdfgdfgadsgfsdfg");
            name.setText(history);
        }
        else {
            Calendar calendar = Calendar.getInstance();
            String dataMessage = calendar.getTime().toString();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dataMessage = dateFormat.format(new Date());
            name.setText("Новый трек " + dataMessage);
        }


        sharedPreferences = getContext().getSharedPreferences("treak", Context.MODE_PRIVATE);

        accept = (Button)rootView.findViewById(R.id.accept);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!idTreak.equals("0")){
                    if(history!=null){
                        SelectTrack selectTrack =new SelectTrack();
                        Bundle bundle=new Bundle();

                        bundle.putString("id_track",id_track);
                        bundle.putString("start_track",start_track);
                        bundle.putString("end_track",end_track);
                        bundle.putString("title",name.getText().toString());


                        selectTrack.setArguments(bundle);
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.replace(R.id.track_list,selectTrack).commit();

                    } else {
                        Log.e("SAVE DIALOG", idTreak + " name= " + name.getText());
                        sharedPreferences.edit().putString("nameTreak", name.getText().toString()).commit();
                    }
                    //  saveNameTreak(idTreak, name.getText().toString());
                    dismiss();
                }
                else{

                    sharedPreferences.edit().putString("nameTreak", name.getText().toString()).commit();
                    dismiss();
                }
            }
        });
        cancel = (Button)rootView.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                String dataMessage = calendar.getTime().toString();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                dataMessage = dateFormat.format(new Date());
                sharedPreferences.edit().putString("nameTreak", dataMessage).commit();
                dismiss();
            }
        });
        return rootView;
    }
}
