package com.example.turist;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class RegistActivity extends AppCompatActivity {
    EditText login;
    EditText pass;
    EditText name;
    EditText surname;
    EditText fname;
    EditText phone;
    TextView nameGroup;
    TextView openGroup;
    TextView marker;

    String nameGroups;
    String checkOpenGroup;
    String markerCreatAddGroup;

    Button reg;
    Button addGroup;
    Button createGroup;
    String url="http://109.120.189.141:81/web/api/track/reg-tourist";
    JSONObject jsonMain=new JSONObject();

    String message;
    Callback callback;
    String cb= "";
    private static final String TAG ="myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        login=(EditText)findViewById(R.id.login);
        pass=(EditText)findViewById(R.id.pass);
        name=(EditText)findViewById(R.id.name);
        surname=(EditText)findViewById(R.id.surname);
        fname=(EditText)findViewById(R.id.fname);
        phone=(EditText)findViewById(R.id.phone);
        marker=(TextView) findViewById(R.id.addgroup);



        nameGroup =(TextView) findViewById(R.id.name_group);
        openGroup =(TextView) findViewById(R.id.checkbox_open);

//        addGroup = (Button)findViewById(R.id.add_group);
//        addGroup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentManager fm = getFragmentManager();
//                DialogRenameFio dialogFragment = new DialogRenameFio();
//                dialogFragment.show(fm, "Error");
//            }
//        });
//

        createGroup = (Button)findViewById(R.id.create_group);
        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                DialogCreateGroup dialogFragment = new DialogCreateGroup ();
                dialogFragment.show(fm, "Error");
            }
        });

        reg=(Button)findViewById(R.id.reg);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameGroups = nameGroup.getText().toString();
                Log.e("Group", nameGroups);
                checkOpenGroup = openGroup.getText().toString();
                Log.e("OpenGroup", checkOpenGroup);
                markerCreatAddGroup = marker.getText().toString();
                Log.e("Marker", markerCreatAddGroup);
                if((login.getText().length()!=0)&&(pass.getText().length()!=0)&&(name.getText().length()!=0)&&(surname.getText().length()!=0)&&(fname.getText().length()!=0)&&(phone.getText().length()!=0)){
                    //магия
                    final String slogin=login.getText().toString();
                    final String spass=pass.getText().toString();
                    final String sname=name.getText().toString();
                    final String ssurname=surname.getText().toString();
                    final String sfname=fname.getText().toString();
                    final String sphone=phone.getText().toString();

//                    try {
//
//                        jsonMain.put("login", login.getText().toString());
//                        jsonMain.put("pass", pass.getText().toString());
//                        jsonMain.put("fio", name.getText().toString() + surname.getText().toString() + fname.getText().toString());
//                        jsonMain.put("tel", phone.getText().toString());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    Log.e(TAG,jsonMain.toString());
//                   APIConnect apiConnect=new APIConnect(url,"",jsonMain,formBody);
//                    apiConnect.execute();

                    class INSERTtoGps extends AsyncTask<Void, Void, Void> {


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


                                Log.e(TAG, "2");
                                OkHttpClient client = new OkHttpClient();

                                Log.e(TAG, "3");
                                RequestBody formBody = new FormBody.Builder()
                                        .addEncoded("login",slogin)
                                        .addEncoded("pass", spass)
                                        .addEncoded("fio", ssurname +" "+ sname +" "+ sfname)
                                        .addEncoded("tel", sphone)

                                        .addEncoded("group_name", "Тест группа 1")
                                        .addEncoded("new_group_name", "")
                                        .addEncoded("open_group", "")
                                        .addEncoded("new_group_flag", "0")
                                        .build();


                                Request request = new Request.Builder()
                                        .url("http://109.120.189.141:81/web/api/track/reg-tourist")
                                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                        .post(formBody)
                                        .build();


                                Log.e("FormBodyRegist ", "login " + slogin + " pass " + spass + " fio " +ssurname +" "+ sname +" "+ sfname + " tel " + sphone + " group_name "+ nameGroups
                                        + " new_group_name " + nameGroups + " open_group " + checkOpenGroup + " new_group_flag " +  markerCreatAddGroup);


                                okhttp3.Call call = client.newCall(request);
                                Response response = call.execute();
                                callback = new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {

                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {

                                    }
                                };

                                callback.onResponse(call, response);
                                message = response.body().string().trim();

                                Log.e(TAG, message);


                                Log.e(TAG, "Вот прям точно ушли");
//                                idreg = getStatus("id",srt);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            cb=getStatus("id", message);
                            Log.e(TAG, "message= " + cb);
                            if(!cb.equals("-1")){
                                Toast.makeText(RegistActivity.this, getStatus("status", message), Toast.LENGTH_LONG).show();
                                Intent intent=new Intent(RegistActivity.this,MainActivity.class);
                                startActivity(intent);
                            }
                            else Toast.makeText(RegistActivity.this, getStatus("status", message), Toast.LENGTH_LONG).show();
                            Log.e(TAG, "cb= " + cb);

                        }
                    }

                    INSERTtoGps in = new INSERTtoGps();
                    in.execute();
                }
                else Toast.makeText(RegistActivity.this, "Заполните все поля", Toast.LENGTH_LONG).show();
            }
        });


    }
}
