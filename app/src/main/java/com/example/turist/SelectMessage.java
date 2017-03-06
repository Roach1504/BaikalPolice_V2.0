package com.example.turist;

import android.*;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;


public class SelectMessage extends Fragment implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    String point;
    String date;
    String text;
    String category;
    String image;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private GoogleApiClient client;
    TextView text_mes;
    TextView date_mes;
    TextView categ_mes;
    ImageView mImageView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            point = bundle.getString("points");


            date = bundle.getString("date");
            text = bundle.getString("message");
            category = bundle.getString("category");
            image = bundle.getString("image");
        }


        View view = inflater.inflate(R.layout.select_message, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        client = new GoogleApiClient.Builder(getContext()).addApi(AppIndex.API).build();
        text_mes = (TextView) view.findViewById(R.id.select_mes);
        date_mes = (TextView) view.findViewById(R.id.select_date);
        categ_mes = (TextView) view.findViewById(R.id.select_type_mes);
        mImageView = (ImageView)view.findViewById(R.id.select_imge);

        text_mes.setText("Текст сообщения:"+"\n"+text);
        date_mes.setText(date);
        categ_mes.setText("Категория:"+"\n"+category);
        Log.e("image", image);
        if(!image.isEmpty()){
            Picasso.with(mImageView.getContext()).load(image)
                    .placeholder(R.drawable.ic_camera_alt_black_24dp)
                    .fit()
                    .centerCrop()
                    .into(mImageView);
        }
        return view;
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

        Double x = 0.0;
        Double y = 0.0;
        JSONArray points = new JSONArray();
        try {
            points = new JSONArray(point);
            Log.e("Point 0 ", Double.parseDouble(points.getString(0)) + "");
            Log.e("Point 1 ", points.getString(1));

            x = Double.parseDouble(points.getString(0));
            y = Double.parseDouble(points.getString(1));

//            mMap.addMarker(new MarkerOptions()
//                    .position(new LatLng(Double.parseDouble(points.getString(0)), Double.parseDouble(points.getString(1))))
//                    .title("Место отправки"));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("x", x+"");
        Log.e("y", y+"");
        mMap.moveCamera(CameraUpdateFactory.
                newLatLngZoom(new LatLng(y, x), 12.0f));

        mMap.setMyLocationEnabled(true);
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(y, x))
                .title("Hello world"));
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
        if (ActivityCompat.checkSelfPermission(getContext(),android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
