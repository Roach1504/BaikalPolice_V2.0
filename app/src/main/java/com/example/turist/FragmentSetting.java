package com.example.turist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FragmentSetting extends FirstFragmentSetting implements AdapterView.OnItemSelectedListener {
    private TextView selection;
    SharedPreferences sharedPreferences;
    SharedPreferences sharedPreferences1;
    Button redProf;
    Button exit;
    CheckBox avt;
    Button changePass;
    int key = 0;
    String perod;
    String message;

    public void chengeSetting(final String period, final String group_flag, final String id){

        if(!hasConnection(getContext())){
            Toast.makeText(getContext(), "Для сохранение настроек подключите интернет", Toast.LENGTH_SHORT).show();
        }
        class ChengePasswordServer extends AsyncTask<Void, Void, Void> {


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


                    Log.e("START CONNECT", "ff");
                    OkHttpClient client = new OkHttpClient();


                    RequestBody formBody = new FormBody.Builder()
                            .addEncoded("id",id)
                            .addEncoded("period",period)
                            .addEncoded("group_flag",group_flag)
                            .build();




                    Request request = new Request.Builder()
                            .url("http://109.120.189.141:81/web/api/track/change-settings")
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
                    Log.e("ответ от сервера ", message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if(message!=null){
                    Toast.makeText(getContext(), getStatus("status",message), Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getContext(), "Для сохранение настроек подключите интернет", Toast.LENGTH_SHORT).show();
                }
            }
        }


        ChengePasswordServer in1 = new ChengePasswordServer();
        in1.execute();


    }
    public static boolean hasConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected()) {

            return true;
        }
        return false;
    }

    int marker = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_setting, container, false);



        sharedPreferences = getContext().getSharedPreferences("authorization", Context.MODE_PRIVATE);
        sharedPreferences1 = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);


        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.period, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        exit=(Button)view.findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sharedPreferences.edit().clear().commit();
                sharedPreferences1.edit().clear().commit();
                Intent intent=new Intent(getContext(),MainActivity.class);
                startActivity(intent);
            }
        });

        redProf = (Button)view.findViewById(R.id.redProf);
        redProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasConnection(getContext())){
                    ChangeProfile changeProfile=new ChangeProfile();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.first_setting,changeProfile).commit();
                }
                else{
                    Toast.makeText(getContext(), "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
                }

            }
        });
        avt = (CheckBox)view.findViewById(R.id.avtoNew);
        if(sharedPreferences1.getInt("autoNews", 0)==0){
            avt.setChecked(false);
        }
        else avt.setChecked(true);


        Log.e("share", sharedPreferences.getString("contact","shit"));

        avt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(avt.isChecked()) {
                    if(perod.isEmpty()){
                        sharedPreferences1.edit().putInt("autoNews", 1).commit();
                        chengeSetting(perod,"1",sharedPreferences.getString("id","-1"));
                        Log.e("myLog","check true");
                    }
                    else {
                        sharedPreferences1.edit().putInt("autoNews", 1).commit();
                        chengeSetting("60","1",sharedPreferences.getString("id","-1"));
                        Log.e("myLog","check true");
                    }

                }
                else {
                    if(perod.isEmpty()){
                         sharedPreferences1.edit().putInt("autoNews", 0).commit();
                        chengeSetting(perod,"0",sharedPreferences.getString("id","-1"));
                        Log.e("myLog","check true");
                    }
                    else {
                        sharedPreferences1.edit().putInt("autoNews", 0).commit();
                        chengeSetting("60","0",sharedPreferences.getString("id","-1"));
                        Log.e("myLog","check true");
                    }
                }
            }
        });

        changePass=(Button)view.findViewById(R.id.changePass);
        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!hasConnection(getContext())){
                    Toast.makeText(getContext(), "Для изменения пароля подключите интернет", Toast.LENGTH_SHORT).show();
                }
                else {
                    FragmentManager fm = getFragmentManager();
                    ChangePassDialog changePassDialog = new ChangePassDialog();
                    changePassDialog.show(fm, "Change");
                }
            }
        });


        return view;
    }
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // Получаем выбранный объект
        Object item = parent.getItemAtPosition(pos);
        perod= item.toString();
        if(marker!=0) {
            chengeSetting(perod, sharedPreferences.getString("autoNews", "0"), sharedPreferences.getString("id", "-1"));
            sharedPreferences.edit().remove("period").putInt("period", Integer.parseInt(item.toString())).commit();
        }
        marker = 1;
    }

    public void onNothingSelected(AdapterView<?> parent) {

    }

}
