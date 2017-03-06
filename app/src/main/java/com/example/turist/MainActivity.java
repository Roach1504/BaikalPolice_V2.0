package com.example.turist;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {


    Button next;
    EditText login;
    EditText pass;
    Button reg;
    SharedPreferences sharedPreferences;
    SharedPreferences sharedPreferences1;
    JSONObject jsonMain = new JSONObject();
    String url = "http://109.120.189.141:81/web/api/track/login";
    String id = new String();

    final String TAG = "myLogs";
    String message;
    Callback callback;
    String cb;
    String period;
    DBHellp dbHelper;


    public void writeCateg(Context context) {
        Log.e("Cate ", "1");
        final ArrayList<String> categ = new ArrayList<>();
        final ArrayList<String> idCateg = new ArrayList<>();


        dbHelper = new DBHellp(context);

        class AddCategServer extends AsyncTask<Void, Void, Void> {
            String message;


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
                            .build();

                    Request request = new Request.Builder()
                            .url("http://109.120.189.141:81/web/api/track/get-categories-reports")
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

                    Log.e("Server add Categ ", message);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                JSONArray jsonArray = new JSONArray();
                //ArrayList<String> list = new ArrayList<String>();
                try {
                    jsonArray = new JSONArray(message);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Log.e("getStatus", "title " + i + "= " + getStatus("title", jsonArray.getString(i)) + " id " + i + "= " + getStatus("id", jsonArray.getString(i)));
                        categ.add(getStatus("title", jsonArray.getString(i)));
                        idCateg.add(getStatus("id", jsonArray.getString(i)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("categ", categ.toString());
                Log.e("id", idCateg.toString());


                SQLiteDatabase database;
                if (!categ.isEmpty()) {
                    database = dbHelper.getWritableDatabase();
                    for (int i = 0; i < categ.size(); i++) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(DBHellp.ID_CATEG, idCateg.get(i));
                        contentValues.put(DBHellp.CATEGRS, categ.get(i));
                        Log.e("contentValues", contentValues.toString());
                        database.insert(DBHellp.TABLE_CATEG, null, contentValues);
                    }
                }
            }
        }

        AddCategServer addCategServer = new AddCategServer();
        addCategServer.execute();
    }

    @Override
    protected void onPostResume() {
        Log.e("OnResum", "ok");

        if (!sharedPreferences.getString("id", "").isEmpty()) {
            Log.e("OnResum", sharedPreferences.getString("id", "null"));
            Intent intent = new Intent(MainActivity.this, MenuActivity.class);
            startActivity(intent);
        }
        super.onPostResume();
    }

    @Override
    public void onBackPressed() {
        Log.e("Back", "click");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("onCreateMain", "ok");

        setContentView(R.layout.activity_main);

        sharedPreferences1 = getSharedPreferences("treak", Context.MODE_PRIVATE);
        sharedPreferences = getSharedPreferences("authorization", Context.MODE_PRIVATE);

        Log.e(TAG,"share"+sharedPreferences.getString("id",""));
        if(!sharedPreferences.getString("id","").isEmpty()){
            Intent intent = new Intent(MainActivity.this, MenuActivity.class);
            startActivity(intent);
        }

        login=(EditText)findViewById(R.id.login);
        pass=(EditText)findViewById(R.id.pass);

        next = (Button) findViewById(R.id.next);
        reg=(Button)findViewById(R.id.reg);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasConnection(MainActivity.this)) {
                    Intent intent=new Intent(MainActivity.this,RegistActivity.class);
                    startActivity(intent);
                }
                else Toast.makeText(MainActivity.this, "Нет подключения к интернету", Toast.LENGTH_LONG).show();

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ((login.getText().length()!=0) && (pass.getText().length()!=0)){
                    final String slogin=login.getText().toString();
                    final String spass=pass.getText().toString();
                    Log.e("Login", slogin);
                    Log.e("password", spass);

                    if (hasConnection(MainActivity.this)) {
                        writeCateg(MainActivity.this);
                    }
                    else Toast.makeText(MainActivity.this, "Нет подключения к интернету", Toast.LENGTH_LONG).show();

                    if (hasConnection(MainActivity.this)) {
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
                                            //.addEncoded('photo', photo)
                                            .build();
                                    Log.e(TAG, "4");


                                    Request request = new Request.Builder()
                                            .url("http://109.120.189.141:81/web/api/track/login")
                                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                            .post(formBody)
                                            .build();

                                    Log.e("Request", request.toString());
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

                                    Log.e(TAG, "message avtor= " + message);
                                    Log.e(TAG, "message avtor id= " + getStatus("id", message));

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

                               // cb = "69";//// TODO: 01.02.2017

                                if(!cb.isEmpty()){
                                    Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                                    sharedPreferences1.edit().putString("id", cb).commit();
                                    sharedPreferences.edit().putString("id", cb).commit();
                                    sharedPreferences.edit().putString("fio", getStatus("fio",message)).commit();

                                    period = getStatus("period", message);
                                    sharedPreferences.edit().putInt("period", Integer.parseInt(period)).commit();
                                    Log.e("contact",  getStatus("contact",message)+" cont");
                                    sharedPreferences.edit().putString("contact", getStatus("contact",message)).commit();
                                    startActivity(intent);
                                }
                                else Toast.makeText(MainActivity.this, "Неверный логин или пароль", Toast.LENGTH_LONG).show();
                            }
                        }
                        INSERTtoGps in = new INSERTtoGps();
                        in.execute();
                    }
                    else Toast.makeText(MainActivity.this, "Нет подключения к интернету", Toast.LENGTH_LONG).show();

                } else
                    Toast.makeText(MainActivity.this, "Введите логин и пароль", Toast.LENGTH_LONG).show();

            }
        });
    }
    public static boolean hasConnection(final Context context) {
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
}
