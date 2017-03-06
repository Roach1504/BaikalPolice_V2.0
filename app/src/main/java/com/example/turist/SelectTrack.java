package com.example.turist;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonLineString;
import com.google.maps.android.data.geojson.GeoJsonMultiPoint;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SelectTrack extends Fragment implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    String id_track;
    String start_track;
    String end_track;
    String titles;

    String server;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    double gpsmyX;
    double gpsmymX;
    SharedPreferences sharedPreferences;
    private GoogleApiClient client;

    JSONArray geoJson;
    JSONArray geoArray;
    TextView begin;
    TextView end;
    TextView name;
    ImageView change;


    ArrayList<HashMap<String,String>> pointS = new ArrayList<HashMap<String,String>>();

    public ProgressDialog loading;


    public int fireadd = 0;
    PolylineOptions rectOptions1 = new PolylineOptions();
    ProgressBar progressBar;
    ImageView fire;

    public void addFire(final String dataStart, final String dataEnd) {
        Log.e("date's", dataStart+ " " +dataEnd);

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
                String dataOne = dataStart;
                if(dataStart.equals(dataEnd)){
                    String dateBackYear = dataStart.split("-")[0];
                    String dateBackMonth = dataStart.split("-")[1];
                    String dateBackDay = dataStart.split("-")[2];
                    int day= Integer.parseInt(dateBackDay)-1;
                    if(day<10){
                        dataOne = dateBackYear+"-"+dateBackMonth+"-"+"0"+day;
                    }
                    else{
                        dataOne = dateBackYear+"-"+dateBackMonth+"-"+day;
                    }
                    Log.e("Back Date ", dateBackYear+"-"+dateBackMonth+"-"+"0"+day);
                }

                try {

                    OkHttpClient client = new OkHttpClient();
                    RequestBody formBody = new FormBody.Builder()
                            .addEncoded("query", "DateTime" + " >= " + "'" + dataOne + "' and " + "DateTime <= " + "'" + dataEnd + "'")
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
                       // Polyline polyline = mMap.addPolyline(rectOptions1.width(5).color(Color.GREEN));
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


    public void readTrackServer(){

        loading = new ProgressDialog(getContext());
        loading.setMessage("Загрузка трекa");
        loading.setIndeterminate(true);
        loading.setCancelable(false);
        loading.show();

        ArrayList<HashMap<String, String>> tracksH1 = new ArrayList<HashMap<String, String>>();
        class ReadTrackServer extends AsyncTask<Void, Void, Void> {
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



            protected Void doInBackground(Void... bitmam) {

                try {

                    OkHttpClient client = new OkHttpClient();
                    RequestBody formBody = new FormBody.Builder()
                            .addEncoded("track_id", id_track)
                            .build();

                    Log.e("ID_treack" ,id_track);
                    Request request = new Request.Builder()
                            .url("http://109.120.189.141:81/web/api/track/get-points-android" + "?" +"track_id="+id_track )
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .get()
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

                    Log.e("Server tracks  ", message);
                    geoArray = new JSONArray(message);
                    server = message;

                    //geoJson = mesJson;
                    Log.e("GeoJSON ", geoArray.toString());


                } catch (IOException e) {
                    e.printStackTrace();
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);


                JSONArray dataJsonObj = null;
                try {
                    dataJsonObj = new JSONArray(server);
                    } catch (JSONException e) {
                    e.printStackTrace();

                }
                Log.e("Server", dataJsonObj.toString());
                for(int i = 0; i <dataJsonObj.length(); i++){
                    try {
                        HashMap<String,String> mHash = new HashMap<>();
                      //  Log.e(i +"Points", dataJsonObj.get(i).toString());
                       // Log.e(i +"x", getStatus("x",dataJsonObj.get(i).toString()));
                       // Log.e(i +"y", getStatus("y",dataJsonObj.get(i).toString()));
                        mHash.put("x", getStatus("x",dataJsonObj.get(i).toString()));
                        mHash.put("y", getStatus("y",dataJsonObj.get(i).toString()));
                        pointS.add(mHash);
                    } catch (JSONException e) {
                        Log.e("Error", "code(1)");
                        e.printStackTrace();
                    }
                }
                Log.e("Posnt's",pointS.toString());
                Polyline polyline;
                PolylineOptions rectOptions = new PolylineOptions();
                for(int i = 0; i < pointS.size(); i++){
                    rectOptions.add(new LatLng(Double.parseDouble(pointS.get(i).get("x")), Double.parseDouble(pointS.get(i).get("y"))));
                }
                polyline = mMap.addPolyline(rectOptions.width(5).color(R.color.colorPrimary));
                mMap.moveCamera(CameraUpdateFactory.
                        newLatLngZoom(new LatLng(Double.parseDouble(pointS.get(pointS.size()/2).get("x")), Double.parseDouble(pointS.get(pointS.size()/2).get("y"))), 12.0f));

                loading.dismiss();
            }
        }

        ReadTrackServer readTracksServer= new ReadTrackServer();
        readTracksServer.execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            id_track = bundle.getString("id_track");
            start_track = bundle.getString("start_track");
            end_track = bundle.getString("end_track");
            titles = bundle.getString("title");

        }

        View view = inflater.inflate(R.layout.select_track, container, false);

        View topBar=(View)getActivity().findViewById(R.id.top_bar);
        View topBar1=(View)getActivity().findViewById(R.id.title_bars);
        topBar1.setVisibility(View.GONE);
        topBar.setVisibility(View.GONE);


        change = (ImageView)view.findViewById(R.id.change);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("id_treak", id_track);
                bundle.putString("history", titles);
                bundle.putString("id_track",id_track);
                bundle.putString("start_track",start_track);
                bundle.putString("end_track",end_track);
                bundle.putString("title",titles);
                FragmentManager fm = getFragmentManager();
                NameTreakDialog nameTreakDialog = new NameTreakDialog();
                nameTreakDialog.setArguments(bundle);
                nameTreakDialog.show(fm,"Change");
            }
        });

        begin = (TextView) view.findViewById(R.id.beginTrack);
        begin.setText("Дата начала: " + start_track);
        name = (TextView) view.findViewById(R.id.titles);
        name.setText(titles);
        end = (TextView) view.findViewById(R.id.endTrack);
        end.setText("Дата завершения: " + end_track);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar3);

        fire = (ImageButton) view.findViewById(R.id.fireButton);

        fire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fire.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(v.VISIBLE);

                addFire(start_track.split(" ")[0],end_track.split(" ")[0]);
            }
        });
                sharedPreferences = getContext().getSharedPreferences("authorization", Context.MODE_PRIVATE);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        client = new GoogleApiClient.Builder(getContext()).addApi(AppIndex.API).build();




        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
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
        mLastLocation = location;
//        if(pointS.size()>0){
//        mMap.moveCamera(CameraUpdateFactory.
//                newLatLngZoom(new LatLng(Double.parseDouble(pointS.get(pointS.size()/2).get("x")), Double.parseDouble(pointS.get(pointS.size()/2).get("y"))), 12.0f));
//        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.setBuildingsEnabled(false);
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (mGoogleApiClient == null) {
            createLocationRequest();
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }

        readTrackServer();


        mMap.setMyLocationEnabled(true);
    }
    private void createLocationRequest() {
        //Log.e(TAG, "createLocationRequest");

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        setIntervalTime(100);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }
    private void setIntervalTime(int time) {
        mLocationRequest.setFastestInterval(time);
    }
    private void startLocationUpdates() {
        //Log.e(TAG, "startLocationUpdates");
        if (ActivityCompat.checkSelfPermission(getContext(),android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,this);
    }
    private void stopLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            //   stopLocationUpdates();
            mGoogleApiClient.disconnect();
        }
    }

}
