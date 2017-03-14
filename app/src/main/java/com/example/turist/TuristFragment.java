package com.example.turist;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPointStyle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class TuristFragment extends Fragment implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private Location mLastLocation1;
    double gpsmyX;
    double gpsmymX;

    final String TAG = "myLogs";
    public LatLng sek;
    ImageButton send;
    ImageButton pause;
    ImageView fire;

    TextView textX;
    TextView textY;
    TextView text3;

    int markers = 0;
    int markersPaint = 0;

    private boolean mFirst = true;

    int idtreak = 0;
    int idtreakS;
    String markerS;

    private double gpspojarX;
    double gpsmyY;
    double gpsmymY;
    private double gpspojarY;
    private String datapojar = new String();
    private GoogleApiClient client;
    private MarkerOptions mMarkerOptions;
    private double gpsStartX;
    private double gpsStartY;
    private String dataStart = new String();
    private String dataStart1 = new String();
    private String dataS = new String();
    private double gpsFinX;
    private double gpsFinY;
    private int keyT;
    int flag = 0;
    String period;

    private ArrayList<String> geosY = new ArrayList<String>();
    private ArrayList<String> dates = new ArrayList<String>();

    private JSONObject jsonMain = new JSONObject();
    private JSONObject jsonMain2 = new JSONObject();
    JSONObject geoArray;
    JSONArray Geo;

    private JSONArray arrayHellp = new JSONArray();
    private JSONArray hellp = new JSONArray();
    private JSONObject arrayHellpName = new JSONObject();

    SharedPreferences sharedPreferences;
    String message;
    Chronometer mChronometer;
    SQLiteDatabase database;

    DBHellp dbHelper;

    private Timer mTimer;
    private MyTimerTask mMyTimerTask;

    private RecyclerView rv1;
    public String markerButton;

    PolylineOptions rectOptions1 = new PolylineOptions();

    Boolean GPSconnect() {
        LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
            return false;
        else
            return true;
    }

    public boolean hasConnection(Context context) {
        if(context != null) {
            ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
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
        else {
            return false;
        }
    }


    public int fireadd = 0;
    ProgressBar progressBar;
    ProgressDialog pD;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_turist, container, false);


       // sharedPreferences = getContext().getSharedPreferences("authorization", Context.MODE_PRIVATE);
        sharedPreferences = getContext().getSharedPreferences("treak", Context.MODE_PRIVATE);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(getContext()).addApi(AppIndex.API).build();

        textX = (TextView) view.findViewById(R.id.textX);
        textY = (TextView) view.findViewById(R.id.textY);
        text3 = (TextView) view.findViewById(R.id.textView3);
        pause = (ImageButton) view.findViewById(R.id.pause);
        send = (ImageButton) view.findViewById(R.id.send);
        fire = (ImageButton) view.findViewById(R.id.fireButton);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar2);

        fire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fire.setVisibility(v.INVISIBLE);
                progressBar.setVisibility(v.VISIBLE);
                addFire();

            }
        });
        //gps = (ImageView) view.findViewById(R.id.imageView2);

        dbHelper = new DBHellp(getContext());
        database = dbHelper.getReadableDatabase();

        Cursor cursor = database.query(dbHelper.TABLE_TREAKS, null, null, null, null, null, null);
        if (sharedPreferences.getInt("mark", 0) != 0) {
            Log.e(TAG, "Записи есть");
            markerButton = "продолжить";
            pause.setVisibility(View.VISIBLE);
            send.setImageResource(R.drawable.ic_stop_black_24dp);

            // send.setText("Завершить прогулку");
        } else {
            Log.e(TAG, "Данных нет");
            markerButton = "начать";
            send.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);
            pause.setVisibility(View.GONE);

        }
        cursor.close();

        idtreak = sharedPreferences.getInt("track_id", 0);

        mChronometer = (Chronometer) view.findViewById(R.id.chronometer);
        mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long elapsedMillis = SystemClock.elapsedRealtime()
                        - mChronometer.getBase();
            }
        });


        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GPSconnect()) {
                    if ((gpsmyX != 0.0) && (gpsmyY != 0.0)) {
                        //mChronometer.setBase(SystemClock.elapsedRealtime());

                        markers = 1;
                        database = dbHelper.getReadableDatabase();
                        Cursor cursor = database.query(dbHelper.TABLE_TREAKS, null, null, null, null, null, null);
                        if (cursor.moveToFirst()) {
                            Log.e(TAG, "input");
                        } else {
                            Log.e(TAG, "no input");
                        }
                        mTimer = new Timer();
                        mMyTimerTask = new MyTimerTask();
                        Log.e(TAG, "period= " + String.valueOf(sharedPreferences.getInt("period", 0)));
                        mTimer.schedule(mMyTimerTask, 1000, sharedPreferences.getInt("period", 5) * 1000);
                        flag = 1;

                        mChronometer.start();


                        pause.setVisibility(view.GONE);
                    } else
                        Toast.makeText(getContext(), "Мы не можем вас найти", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(getContext(), "Включите передачу геоданных", Toast.LENGTH_LONG).show();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GPSconnect()) {
                    if ((gpsmyX != 0.0) && (gpsmyY != 0.0)) {
                        if (markerButton.equals("начать")) {

                            Bundle bundle = new Bundle();
                            bundle.putString("id_treak", String.valueOf(sharedPreferences.getInt("track_id", 0)));
                            FragmentManager fm = getFragmentManager();
                            NameTreakDialog nameTreakDialog = new NameTreakDialog();
                            nameTreakDialog.setCancelable(false);
                            nameTreakDialog.setArguments(bundle);
                            nameTreakDialog.show(fm, "Change");

                            database.delete(DBHellp.TABLE_paint, null, null);
                            markerButton = "продолжить";
                            send.setImageResource(R.drawable.ic_stop_black_24dp);
                            sharedPreferences.edit().putInt("mark", 1).commit();
                            Log.e(TAG, "начали прогулку id= " + String.valueOf(sharedPreferences.getInt("track_id", 0)));
                            pause.setVisibility(View.INVISIBLE);
                            mChronometer.setBase(SystemClock.elapsedRealtime());
                            mChronometer.start();
                            mTimer = new Timer();
                            mMyTimerTask = new MyTimerTask();
                            Log.e(TAG, "period= " + String.valueOf(sharedPreferences.getInt("period", 0)));
                            mTimer.schedule(mMyTimerTask, 1000, sharedPreferences.getInt("period", 5) * 1000);


                        } else {
                            if (hasConnection(getContext())) {
                                send.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);
                                markerButton = "начать";

                                markersPaint = 0;
                                mMap.clear();
                                Log.e(TAG, "завершили прогулку id= " + String.valueOf(sharedPreferences.getInt("track_id", 0)));
                                if (sharedPreferences.getInt("track_id", 0) == 0) {
                                    markerEND = 1;
                                    INSERTtoGps1 in1 = new INSERTtoGps1();
                                    in1.execute();
                                }
                                    INSERTtoGps2 in2 = new INSERTtoGps2();
                                    in2.execute();

                                    INSERTtoGps3 in3 = new INSERTtoGps3();
                                    in3.execute();


                                if (flag == 1) {
                                    mTimer.cancel();
                                    flag = 0;
                                }
                                mChronometer.stop();
                                mChronometer.setBase(SystemClock.elapsedRealtime());

                                pause.setVisibility(View.GONE);



//                        dbHelper.close();
                            } else {
                                Toast.makeText(getContext(), "Подключите интернет что бы завершить маршрут", Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        pD = new ProgressDialog(getContext());
                        pD.setMessage("Поиск спутников...");
                        pD.setIndeterminate(true);
                        pD.setCancelable(true);
                        pD.show();

                        Toast.makeText(getContext(), "Мы не можем вас найти", Toast.LENGTH_LONG).show();
                    }
                } else
                    Toast.makeText(getContext(), "Включите передачу геоданных", Toast.LENGTH_LONG).show();

            }

        });

        return view;
    }
    int markerEND=0;

    class INSERTtoGps1 extends AsyncTask<Void, Void, Void> {


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
                        .addEncoded("tourist_id", sharedPreferences.getString("id", ""))
                        .addEncoded("track_title", "")
                        .build();
                Log.e(TAG, "4");


                Request request = new Request.Builder()
                        .url("http://109.120.189.141:81/web/api/track/new")
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

                Log.e(TAG, message);

                Log.e(TAG, "message GPS1 = " + message);
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
//                                if(!message.isEmpty())
            if (message != null) {
                idtreak = Integer.parseInt(getStatus("track_id", message));
                period = getStatus("period", message);
                sharedPreferences.edit().putInt("period", Integer.parseInt(period)).commit();
                Log.e(TAG, "idtreak = " + String.valueOf(idtreak));
                sharedPreferences.edit().putInt("track_id", idtreak).commit();
                markerEND = 0;

                if (!sharedPreferences.getString("nameTreak", "null").equals("null")) {
                    Log.e("Name True and ID", sharedPreferences.getInt("track_id", 0) + " name " + sharedPreferences.getString("nameTreak", "null"));
                    saveNameTreak(String.valueOf(sharedPreferences.getInt("track_id", 0)), sharedPreferences.getString("nameTreak", "null"));
                }

                if (idtreak != 0) {
                    flag = 1;
                } else
                    Toast.makeText(getContext(), "Ошибка", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Проверте соеденения с интернетом", Toast.LENGTH_LONG).show();
            }
        }

    }//начало прогулки

    class INSERTtoGps2 extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            geoArray = new JSONObject();
            Geo = new JSONArray();
        }

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

                database = dbHelper.getReadableDatabase();
                Cursor cursor = database.query(dbHelper.TABLE_TREAKS, null, null, null, null, null, null);
                if (cursor.moveToFirst()) {
                    int helpKey1 = cursor.getColumnIndex(DBHellp.KEY_X);
                    int helpKey2 = cursor.getColumnIndex(DBHellp.KEY_Y);
                    int helpKey4 = cursor.getColumnIndex(DBHellp.KEY_DATE);
                    int helpKey5 = cursor.getColumnIndex(DBHellp.KEY_MARKER);
                    Geo = new JSONArray();
                    do {
                        gpsmymX = cursor.getDouble(helpKey1);
                        gpsmymY = cursor.getDouble(helpKey2);
                        dataS = cursor.getString(helpKey4);
                        markerS = cursor.getString(helpKey5);


                        Log.e(TAG, "X = " + gpsmymX);
                        Log.e(TAG, "Y = " + gpsmymY);
                        Log.e(TAG, "Data = " + dataS);
                        Log.e(TAG, "idtreak = " + idtreakS);
                        Log.e(TAG, "markers = " + markerS);

                        geoArray = new JSONObject();

                        try {
                            geoArray.put("track_id", sharedPreferences.getInt("track_id", 0));
                            geoArray.put("x", gpsmymX);
                            geoArray.put("y", gpsmymY);
                            geoArray.put("date", dataS);
                            geoArray.put("break", markerS);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Geo.put(geoArray);
                    }
                    while (cursor.moveToNext());
                    Log.e(TAG, String.valueOf(Geo));
                    OkHttpClient client = new OkHttpClient();
                    RequestBody formBody = new FormBody.Builder()
                            .addEncoded("points", Geo.toString())
                            .build();
                    Request request = new Request.Builder()
                            .url("http://109.120.189.141:81/web/api/track/add-point")
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .post(formBody)
                            .build();
                    Call call = client.newCall(request);
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
                    Log.e(TAG, "messageGPS 2(точки есть) и их "+Geo.length()+" = " + message);

                } //else
                {
                    geoArray = new JSONObject();
                    Geo = new JSONArray();


                    Calendar calendar = Calendar.getInstance();
                    String dataMessage = calendar.getTime().toString();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    dataStart = dateFormat.format(new Date());

                    Log.e(TAG, "Данных нет об ID");
                    try {
                        geoArray.put("track_id", idtreak);
                        geoArray.put("x", gpsmyX);
                        geoArray.put("y", gpsmyY);
                        geoArray.put("date", dataStart);
                        geoArray.put("break", "0");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Geo.put(geoArray);
                    Log.e(TAG, "json GPS2 = " + Geo.toString());
                    OkHttpClient client = new OkHttpClient();
                    RequestBody formBody = new FormBody.Builder()
                            .addEncoded("points", Geo.toString())
                            .build();
                    Request request = new Request.Builder()
                            .url("http://109.120.189.141:81/web/api/track/add-point")
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .post(formBody)
                            .build();
                    Call call = client.newCall(request);
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
                    Log.e(TAG, message);
                    Log.e(TAG, "messageGPS 2 (точек нет)= " + message);
                }

//                                idreg = getStatus("id",srt);
                cursor.close();
                database.delete(DBHellp.TABLE_TREAKS, null, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {


            if(markONE==0){
                markONE=1;
                Log.e("Diolog Name Treak", "id True");
//                Bundle bundle = new Bundle();
//                bundle.putString("id_treak", String.valueOf(sharedPreferences.getInt("track_id", 0)));
//                FragmentManager fm = getFragmentManager();
//                NameTreakDialog nameTreakDialog = new NameTreakDialog();
//                nameTreakDialog.setArguments(bundle);
//                nameTreakDialog.show(fm,"Change");
            }
            else{
                if(markONE==1) {
                    markONE=2;
                    //Log.e("Name SAVE and ID", sharedPreferences.getInt("track_id", 0) + " name " + sharedPreferences.getString("nameTreak", "null"));
                    if(!sharedPreferences.getString("nameTreak","ok").equals("ok")) {
                        saveNameTreak(String.valueOf(sharedPreferences.getInt("track_id", 0)), sharedPreferences.getString("nameTreak", "null"));
                        Log.e("Name SAVE and ID GPS 2", sharedPreferences.getInt("track_id", 0) + " name " + sharedPreferences.getString("nameTreak", "null"));
                    }
                    else {
                        Log.e("Name", "pizdos");
                    }
                }
            }


            super.onPostExecute(aVoid);


        }
    }//отправка точек
    public int markONE=0;

    class INSERTtoGps3 extends AsyncTask<Void, Void, Void> {


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

                Log.e(TAG, "GPS 3 id = " + String.valueOf(idtreak));
                RequestBody formBody = new FormBody.Builder()
                        .addEncoded("track_id", String.valueOf(idtreak))
                        .build();
                Log.e(TAG, "4");


                Request request = new Request.Builder()
                        .url("http://109.120.189.141:81/web/api/track/end")
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

                Log.e(TAG, message);

                Log.e(TAG, "messageGPS 3 = " + message);
                Log.e(TAG, response.message() + ":code");

//                                idreg = getStatus("id",srt);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if(markONE==0){

                    saveNameTreak(String.valueOf(sharedPreferences.getInt("track_id", 0)), sharedPreferences.getString("nameTreak", "null"));

            }
            String backup = sharedPreferences.getString("id", "");
            int backup2 = sharedPreferences.getInt("period", 60);
            sharedPreferences.edit().clear().commit();
            sharedPreferences.edit().putString("id", backup).commit();
            sharedPreferences.edit().putInt("period", backup2).commit();
            markONE=0;
            super.onPostExecute(aVoid);
            super.onPostExecute(aVoid);

//
        }
    }//завершение прогулки

    public void addFire() {
        class FireServer extends AsyncTask<Void, Void, Void> {
            String message;
            // TODO: 06.02.2017
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


            protected Void doInBackground(Void... bitmam) {

                try {

                    Calendar calendar = Calendar.getInstance();
                    String dataFire = calendar.getTime().toString();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    dataFire = dateFormat.format(new Date());

                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, -1);
                    SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
                    String dataFire1 = dateFormat1.format(cal.getTime());
                    Log.e("вчера", dataFire1);

                    OkHttpClient client = new OkHttpClient();
                    RequestBody formBody = new FormBody.Builder()
                            .addEncoded("query", "DateTime" + " >= " + "'" + dataFire1 + "' and " + "DateTime <= " + "'" + dataFire + "'")
                            .addEncoded("api_key", "5723BIAKLC")
                            .build();

                    Request request = new Request.Builder()
                            .url("http://maps.kosmosnimki.ru/rest/ver1/layers/F2840D287CD943C4B1122882C5B92565/search")
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

                    Log.e("FIRE", "ok");
                    Log.e("Server fire  ", message);


                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                try {
                    GeoJsonLayer layer = new GeoJsonLayer(mMap, new JSONObject(message + " dfsdf"));
                    if (fireadd == 0) {
                        layer = new GeoJsonLayer(mMap, new JSONObject(message));
                        GeoJsonPointStyle pointStyle = layer.getDefaultPointStyle();
                        pointStyle.setDraggable(true);
                        pointStyle.setTitle("Очаг возгорания");
                        layer.addLayerToMap();
                        fireadd = 1;
                        fire.setColorFilter(R.color.Gray);

                    } else {
                        fireadd = 0;

                        fire.setColorFilter(R.color.org, PorterDuff.Mode.DST);

                        mMap.clear();
                        Polyline polyline = mMap.addPolyline(rectOptions1.width(5).color(Color.GREEN));
                    }

                } catch (JSONException e) {
                    Log.e("Error", "not Object");
                    e.printStackTrace();
                }
                fire.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
        }

        FireServer fireServer = new FireServer();
        fireServer.execute();
    }


    public void sendMessageDB(Context context) {
        class SendMessageServer extends AsyncTask<Void, Void, Void> {

            protected JSONArray getMessage(Context context1) {
                JSONArray messages = new JSONArray();


                HashMap<String, String> map = new HashMap<>();
                database = dbHelper.getReadableDatabase();
                Cursor cursor = database.rawQuery("SELECT " + DBHellp.KEY_X_MESSAGE + ", " + DBHellp.KEY_Y_MESSAGE + ", " + DBHellp.DATE_MESSAGE +
                        ", " + DBHellp.TEXT_MESSAGE + ", " + DBHellp.ID_USER_MESSAGE + ", " + DBHellp.CATEG_MESSAGE + ", " + DBHellp.IMAGE_MESSAG
                        + " FROM " + DBHellp.TABLE_message, null);

                if (cursor.moveToFirst()) {
                    int keyX = cursor.getColumnIndex(DBHellp.KEY_X_MESSAGE);
                    int keyY = cursor.getColumnIndex(DBHellp.KEY_Y_MESSAGE);
                    int keyDate = cursor.getColumnIndex(DBHellp.DATE_MESSAGE);
                    int keyUserID = cursor.getColumnIndex(DBHellp.ID_USER_MESSAGE);
                    int keyText = cursor.getColumnIndex(DBHellp.TEXT_MESSAGE);
                    int keyCateg = cursor.getColumnIndex(DBHellp.CATEG_MESSAGE);
                    int keyImage = cursor.getColumnIndex(DBHellp.IMAGE_MESSAG);
                    do {
                        JSONObject messageH = new JSONObject();
                        try {
                            messageH.put("x", cursor.getString(keyX));
                            messageH.put("y", cursor.getString(keyY));
                            messageH.put("date", cursor.getString(keyDate));
                            messageH.put("message", cursor.getString(keyText));
                            messageH.put("user_id", cursor.getString(keyUserID));
                            messageH.put("category", cursor.getString(keyCateg));
                            messageH.put("image", cursor.getString(keyImage));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        messages.put(messageH);
                    }
                    while (cursor.moveToNext());
                    Log.e("categ", messages.toString());
                    cursor.close();
                    return messages;
                } else {
                    Log.e("Categ DB", "not name");
                }

                return messages;
            }

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
                            .addEncoded("reports", getMessage(getContext()).toString())
                            .build();

                    Request request = new Request.Builder()
                            .url("http://109.120.189.141:81/web/api/track/add-report")
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

                    Log.e("Server send DB ", message);

                } catch (IOException e) {
                    e.printStackTrace();

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                database.delete(DBHellp.TABLE_message, null, null);
            }
        }

        SendMessageServer sendMessageServer = new SendMessageServer();
        sendMessageServer.execute();
    }

    public void saveNameTreak(final String id, final String name){
        class SaveTitle extends AsyncTask<Void, Void, Void> {


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
                super.onPostExecute(aVoid);
            }
        }
        SaveTitle saveTitle = new SaveTitle();
        saveTitle.execute();
    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            markersPaint = 1;

            Log.e(TAG, "Цикл пошёл1");
            if (hasConnection(getContext())) {
                database = dbHelper.getReadableDatabase();
                Cursor cursor = database.query(dbHelper.TABLE_message, null, null, null, null, null, null);
                if (cursor.moveToFirst()) {
                    sendMessageDB(getContext());
                }
            }
            Calendar calendar = Calendar.getInstance();
            String dataMessage = calendar.getTime().toString();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dataStart = dateFormat.format(new Date());

            JSONArray geoArrayH = new JSONArray();
            geoArrayH.put(String.valueOf(gpsmyX));
            geoArrayH.put(String.valueOf(gpsmyY));
            gpsmymX = gpsmyX;
            gpsmymY = gpsmyY;
            dataS = dataStart;
            idtreakS = idtreak;


            if (markers == 1) {
                database = dbHelper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();

                contentValues.put(DBHellp.KEY_Y, gpsmyY);
                contentValues.put(DBHellp.KEY_X, gpsmyX);
                contentValues.put(DBHellp.KEY_DATE, dataStart);
                contentValues.put(DBHellp.KEY_MARKER, "1");

                database.insert(DBHellp.TABLE_TREAKS, null, contentValues);
                markers = 0;
            }
            if ((gpsmyX != 0.0) && (gpsmyY != 0.0)) {
                if (hasConnection(getContext())) {

//                    database = dbHelper.getWritableDatabase();
//                    ContentValues contentValues = new ContentValues();
//                    contentValues.put(DBHellp.KEY_Y_PAINT, gpsmyY);
//                    contentValues.put(DBHellp.KEY_X_PAINT, gpsmyX);
//                    database.insert(DBHellp.TABLE_TREAKS, null, contentValues);


                    Log.e(TAG, "Network true-connect");
                    Log.e(TAG, gpsmyX + " " + gpsmyY);
                    Log.e(TAG, String.valueOf(sharedPreferences.getInt("track_id", 0)));
                    if (sharedPreferences.getInt("track_id", 0) == 0) {
                        INSERTtoGps1 in1 = new INSERTtoGps1();
                        in1.execute();
                        Log.e(TAG, "иду за id");
                    }

                    INSERTtoGps2 in2 = new INSERTtoGps2();
                    in2.execute();

                } else {
                    Log.e(TAG, "Network falsе-connect");

                    if(markONE==0) {
//                        Bundle bundle = new Bundle();
//                        bundle.putString("id_treak", String.valueOf(sharedPreferences.getInt("track_id", 0)));
//                        FragmentManager fm = getFragmentManager();
//                        NameTreakDialog nameTreakDialog = new NameTreakDialog();
//                        nameTreakDialog.setArguments(bundle);
//                        nameTreakDialog.show(fm, "Change");
                        markONE=1;
                    }

                    database = dbHelper.getWritableDatabase();
                    ContentValues contentValues = new ContentValues();

                    contentValues.put(DBHellp.KEY_Y, gpsmyY);
                    contentValues.put(DBHellp.KEY_X, gpsmyX);
                    contentValues.put(DBHellp.KEY_DATE, dataStart);
                    contentValues.put(DBHellp.KEY_MARKER, "0");

                    database.insert(DBHellp.TABLE_TREAKS, null, contentValues);

                    database = dbHelper.getReadableDatabase();
                    Cursor cursor = database.query(dbHelper.TABLE_TREAKS, null, null, null, null, null, null);
                    if (cursor.moveToFirst()) {
                        Log.e(TAG, "input");

                        int helpKey1 = cursor.getColumnIndex(DBHellp.KEY_X);
                        int helpKey2 = cursor.getColumnIndex(DBHellp.KEY_Y);
                        int helpKey4 = cursor.getColumnIndex(DBHellp.KEY_DATE);
                        int helpKey5 = cursor.getColumnIndex(DBHellp.KEY_MARKER);
                        Geo = new JSONArray();
                        do {
                            gpsmymX = cursor.getDouble(helpKey1);
                            gpsmymY = cursor.getDouble(helpKey2);
                            dataS = cursor.getString(helpKey4);
                            markerS = cursor.getString(helpKey5);

                            Log.e(TAG, "X = " + gpsmymX);
                            Log.e(TAG, "Y = " + gpsmymY);
                            Log.e(TAG, "Data = " + dataS);
                            Log.e(TAG, "idtreak = " + idtreakS);
                            Log.e(TAG, "markers = " + markerS);


                            geoArray = new JSONObject();

                            try {
                                geoArray.put("track_id", sharedPreferences.getInt("track_id", 0));
                                geoArray.put("x", gpsmymX);
                                geoArray.put("y", gpsmymY);
                                geoArray.put("date", dataS);
                                geoArray.put("break", markerS);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Geo.put(geoArray);
                        }
                        while (cursor.moveToNext());
                        Log.e(TAG, String.valueOf(Geo));

                    } else {
                        Log.e(TAG, "no input");
                    }

                }
            } else {
                // TODO: 08.02.2017 не можем вас найти
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Сохраните состояние UI в переменную savedInstanceState.
        // Она будет передана в метод onCreate при закрытии и
        // повторном запуске процесса.
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onPause() {
        if (sharedPreferences.getInt("mark", 0) == 1) {
            mTimer.cancel();
            Log.e(TAG, "timer stop;");
        }
        super.onPause();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e(TAG, "onConnected");

        startLocationUpdates();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onLocationChanged(Location location) {
        //Log.e(TAG, "onLocationChanged");


        mLastLocation = location;
        //  Log.e(TAG, mLastLocation.getLatitude() + " " + mLastLocation.getLongitude());
        //LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)

        if (mFirst) {
            mMap.moveCamera(CameraUpdateFactory.
                    newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 16.0f));
            mFirst = false;
        }


//        Log.e("GPS", mLastLocation.getTime()+" time");
//        Log.e("GPS", mLastLocation.getSpeed()+" speed");
//        Log.e("GPS", mLastLocation.getLatitude()+ " "+ mLastLocation.getLongitude()+" points");

        gpsmyX = mLastLocation.getLatitude();
        gpsmyY = mLastLocation.getLongitude();
        if ((gpsmyX != 0.0) && (gpsmyY != 0.0)) {
            if (pD != null) {
                pD.dismiss();
            }
        }
//        textX.setText("X: "+gpsmyX);
        //      textY.setText("Y: "+gpsmyY);


        Polyline polyline;

        if (markersPaint == 1) {

            ContentValues contentValues = new ContentValues();

            contentValues.put(DBHellp.KEY_Y_PAINT, gpsmyY);
            contentValues.put(DBHellp.KEY_X_PAINT, gpsmyX);

            database.insert(DBHellp.TABLE_paint, null, contentValues);

            database = dbHelper.getReadableDatabase();
            Cursor cursor1 = database.query(dbHelper.TABLE_paint, null, null, null, null, null, null);
            String paintX;
            String paintY;
            PolylineOptions rectOptions = new PolylineOptions();
            if (cursor1.moveToFirst()) {
                int helpKeyX = cursor1.getColumnIndex(DBHellp.KEY_X_PAINT);
                int helpKeyY = cursor1.getColumnIndex(DBHellp.KEY_Y_PAINT);
                do {
                    paintX = cursor1.getString(helpKeyX);
                    paintY = cursor1.getString(helpKeyY);
                    // Log.e(TAG,"paints" + paintX+ paintY);
                    rectOptions1.add(new LatLng(Double.parseDouble(paintX), Double.parseDouble(paintY)));
                    rectOptions.add(new LatLng(Double.parseDouble(paintX), Double.parseDouble(paintY))); // Closes the polyline.

                }
                while (cursor1.moveToNext());

            } else {
                Log.e(TAG, "paints is not");
            }
            polyline = mMap.addPolyline(rectOptions.width(5).color(R.color.colorPrimary));

        } else {
            // mMap.clear();
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e("error", googleMap.toString() + "googleMap Null");

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setBuildingsEnabled(false);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (mGoogleApiClient == null) {
            Log.e(TAG, "googleapi");
            createLocationRequest();
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        } else {
            Log.e("Marker", "not");
        }

        mMap.setMyLocationEnabled(true);


        init();


    }

    private void init() {

        mMarkerOptions = new MarkerOptions();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override

            public void onMapClick(LatLng latLng) {
                Animation mFadeInAnimation, mFadeOutAnimation;
                mFadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.animation_in);
                mFadeOutAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.animation_out);
                mFadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                mFadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                if (mChronometer.getVisibility() == View.VISIBLE) {
//                    pause.setVisibility(View.INVISIBLE);

                    mChronometer.startAnimation(mFadeOutAnimation);
                    mChronometer.setVisibility(View.INVISIBLE);
                    send.startAnimation(mFadeOutAnimation);
                    send.setVisibility(View.INVISIBLE);
//                    textX.startAnimation(mFadeOutAnimation);
                    //                  textX.setVisibility(View.INVISIBLE);
                    //                textY.startAnimation(mFadeOutAnimation);
                    //              textY.setVisibility(View.INVISIBLE);
                    //    gps.startAnimation(mFadeOutAnimation);
                    //  gps.setVisibility(View.INVISIBLE);
                    //text3.startAnimation(mFadeOutAnimation);
                    //text3.setVisibility(View.INVISIBLE);
                } else {
//                    pause.setVisibility(View.VISIBLE);
                    mChronometer.startAnimation(mFadeInAnimation);
                    mChronometer.setVisibility(View.VISIBLE);
                    send.startAnimation(mFadeInAnimation);
                    send.setVisibility(View.VISIBLE);
                    //      textX.startAnimation(mFadeInAnimation);
                    //    textX.setVisibility(View.VISIBLE);
                    //  textY.startAnimation(mFadeInAnimation);
                    // textY.setVisibility(View.VISIBLE);
                    //   gps.startAnimation(mFadeInAnimation);
                    //  gps.setVisibility(View.VISIBLE);
                    //text3.startAnimation(mFadeInAnimation);
                    //text3.setVisibility(View.VISIBLE);
                }


                Log.e(TAG, "5");
                Log.d(TAG, "onMapClick: " + latLng.latitude + "," + latLng.longitude);


            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng latLng) {
                Log.d(TAG, "onMapLongClick: " + latLng.latitude + "," + latLng.longitude);
            }
        });

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

            @Override
            public void onCameraChange(CameraPosition camera) {
                Log.d(TAG, "onCameraChange: " + camera.target.latitude + "," + camera.target.longitude);
            }
        });
    }

    private void createLocationRequest() {
        Log.e(TAG, "createLocationRequest");

        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(10000);
        setIntervalTime(100);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    private void setIntervalTime(int time) {
        mLocationRequest.setFastestInterval(time);
    }

    private void startLocationUpdates() {
        Log.e(TAG, "startLocationUpdates");
        mGoogleApiClient.connect();

        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            Log.e("PERMISSION", "false");
            return;
        }
        if (mGoogleApiClient != null) {
            //  Log.e("error Null", mGoogleApiClient.toString()+" =");
        }
        if (mLocationRequest != null) {
            //  Log.e("error Null", mLocationRequest.toString()+" =");

        }
        Log.e("res", String.valueOf(LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)) + ":ответ");
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


        //mLastLocation1 = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        //Log.e("Test GPS x ",location.getLatitude()+" = x");
        // onLocationChanged(location);
        //  mLastLocation = location;
        // Log.e("CONECT",  LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,this).toString());

    }

    private void stopLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            //   stopLocationUpdates();
            mGoogleApiClient.disconnect();
        }
    }
}
