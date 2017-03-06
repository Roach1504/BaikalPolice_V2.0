package com.example.turist;

import android.Manifest;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.graphics.Bitmap;

import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.formats.NativeAd;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import okhttp3.Response;
import android.support.v4.app.DialogFragment;


public class FragmentMessage extends Fragment implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, AdapterView.OnItemSelectedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    double gpsmyX;
    double gpsmyY;
    private GoogleApiClient client;
    private boolean mFirst = true;


    String categ;
    String toBase;
    String IDcateg;

    ImageButton message;
    Button camera;
    EditText textMessage;

    DBHellp dbHelper;
    SQLiteDatabase database;
    SharedPreferences sharedPreferences;
   // DialogFragment aD;


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
    public String toBase64() {
        // Получаем изображение из ImageView
        BitmapDrawable drawable = (BitmapDrawable) mPhoto.getDrawable();
        if(drawable != null) {
            Bitmap bitmap = drawable.getBitmap();

            // Записываем изображение в поток байтов.
            // При этом изображение можно сжать и / или перекодировать в другой формат.
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);

            // Получаем изображение из потока в виде байтов
            byte[] bytes = byteArrayOutputStream.toByteArray();

            // Кодируем байты в строку Base64 и возвращаем
            Log.e("64", Base64.encodeToString(bytes, Base64.DEFAULT));

            return Base64.encodeToString(bytes, Base64.DEFAULT);
        }
        //mImageView.setImageResource(R.drawable.ic_camera_alt_black_24dp);
        return null;
    }

    public ProgressDialog aD;
    public void sendMessage(final double gpsX, final double gpsY, final String date, final String text, final String categr, final String id) {
        Log.e("sendMessage ", "start");

//        aD = new PrDialog();
//        FragmentManager fragmentManager = getFragmentManager();
//        aD.show(fragmentManager,"Отправка сообщения");

        aD = new ProgressDialog(getContext());
        aD.setMessage("Отправка сообщения...");
        aD.setIndeterminate(true);
        aD.setCancelable(false);
        aD.show();

        class SendMessageServer extends AsyncTask<Void, Void, Void> {
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
                    JSONArray messages = new JSONArray();
                    JSONObject messageArrayH = new JSONObject();

                    try {
                        messageArrayH.put("x", String.valueOf(gpsX));
                        messageArrayH.put("y", String.valueOf(gpsY));
                        messageArrayH.put("date", date);
                        messageArrayH.put("message", text);
                        messageArrayH.put("user_id", id);
                        messageArrayH.put("category", categr);

                        if(toBase!=null) {
                            messageArrayH.put("image", String.valueOf(toBase.toString()));
                        }
                        Log.e("JSON ", messageArrayH.toString());
                        messages.put(messageArrayH);

                        OkHttpClient client = new OkHttpClient();
                        RequestBody formBody = new FormBody.Builder()
                                .addEncoded("reports", messages.toString())
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

                        Log.e("Server send Message  ", message);

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                  //  mImageView.setImageResource(R.drawable.ic_camera_alt_black_24dp);
                    mImageView.setVisibility(View.VISIBLE);
                    ll.setVisibility(View.GONE);
                    textMessage.setText("");
                    aD.dismiss();

                    Toast.makeText(getContext(), "Сообщение отправленно", Toast.LENGTH_SHORT).show();
                }
            }

            SendMessageServer sendMessageServer = new SendMessageServer();
            sendMessageServer.execute();
            //Log.e("CATEG "," список " +categ.toString());
    }

    public void saveMessage(Context context, double gpsX, double gpsY, String date, String text, String categ, String id){

        if(hasConnection(context)) {
            sendMessage(gpsX, gpsY, date, text, getIdCateg(categ,context), id);
        }
        else{
           // Log.e("TEST MESSAGE ", gpsmyX + " " +gpsmyY+ " " + date+ " " + text+ " " + categ+ " " + id);

            dbHelper = new DBHellp(context);
            database = dbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHellp.DATE_MESSAGE, date);
            contentValues.put(DBHellp.KEY_X_MESSAGE, gpsX);
            contentValues.put(DBHellp.KEY_Y_MESSAGE, gpsY);
            contentValues.put(DBHellp.TEXT_MESSAGE, text);
            contentValues.put(DBHellp.CATEG_MESSAGE, getIdCateg(categ,context));
            contentValues.put(DBHellp.ID_USER_MESSAGE, id);
            contentValues.put(DBHellp.IMAGE_MESSAG, toBase);
            database.insert(DBHellp.TABLE_message, null, contentValues);
            Toast.makeText(getContext(), "Сообщение сохранено ", Toast.LENGTH_SHORT).show();
        }
    }

    public String getIdCateg (String categName, Context context){
        String id="-1";
        DBHellp dbHelper;
        SQLiteDatabase database;
        dbHelper = new DBHellp(context);
        database = dbHelper.getReadableDatabase();
        //Cursor cursor = database.query(dbHelper.TABLE_CATEG,null,null,null,null,null,null);
        Cursor cursor = database.rawQuery("SELECT DISTINCT " +DBHellp.ID_CATEG+" FROM " + dbHelper.TABLE_CATEG + " WHERE " + DBHellp.CATEGRS + " = " + "'"+ categName + "'",null);
        if (cursor.moveToFirst()) {
            int keyName = cursor.getColumnIndex(DBHellp.ID_CATEG);
            do{
                id = cursor.getString(keyName);
            }
            while (cursor.moveToNext());
            Log.e("ID Categ",id);
            cursor.close();
            return id;
        }
        else {
            Log.e("ID DB", "not name");
            cursor.close();
            return id;
        }
    }

    public ArrayList<String> writeDBcateg(Context context){
        ArrayList<String> categ = new ArrayList<>();


        DBHellp dbHelper;
        SQLiteDatabase database;
        dbHelper = new DBHellp(context);
        database = dbHelper.getReadableDatabase();
        //Cursor cursor = database.query(dbHelper.TABLE_CATEG,null,null,null,null,null,null);
        Cursor cursor = database.rawQuery("SELECT DISTINCT " +DBHellp.CATEGRS+","+DBHellp.ID_CATEG+" FROM " + dbHelper.TABLE_CATEG,null);
        if (cursor.moveToFirst()) {
            int keyName = cursor.getColumnIndex(DBHellp.CATEGRS);
            do{
                categ.add(cursor.getString(keyName));

            }
            while (cursor.moveToNext());
            Log.e("categ",categ.toString());
            cursor.close();
            return categ;
        }
        else{
            Log.e("Categ DB", "not name");
            cursor.close();
            return categ;
        }
    }

    Boolean GPSconnect() {
        LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
            return false;
        else
            return true;
    }

    //работа с камерой

    private ImageView mImageView;
    private ImageView mPhoto;
    View ll;

    private static int TAKE_PICTURE = 1;
    private Uri mOutputFileUri;


    private void getThumbnailPicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    private void saveFullImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File file = new File(Environment.getExternalStorageDirectory(),
                "tmp_photo_" + System.currentTimeMillis());
        mOutputFileUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mOutputFileUri);
        startActivityForResult(intent, TAKE_PICTURE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("camera Close", "dd");
        if (requestCode == TAKE_PICTURE) {
            // Проверяем, содержит ли результат маленькую картинку
            if (data != null) {
                if (data.hasExtra("data")) {
                    //Bitmap thumbnailBitmap = data.getParcelableExtra("data");
                    // / TODO Какие-то действия с миниатюрой
                    // mImageView.setImageBitmap(thumbnailBitmap);
                }
            } else {
                // TODO Какие-то действия с полноценным изображением,
                // сохраненным по адресу mOutputFileUri
                Log.e("URL", ""+mOutputFileUri);
                mPhoto.setImageURI(mOutputFileUri);
                ll.setVisibility(View.VISIBLE);
                mImageView.setVisibility(View.INVISIBLE);
                toBase = toBase64();
            }
        }
    }
    TextView loc;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_message, container, false);

        sharedPreferences = getContext().getSharedPreferences("authorization", Context.MODE_PRIVATE);


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        client = new GoogleApiClient.Builder(getContext()).addApi(AppIndex.API).build();


        Spinner spinner = (Spinner) view.findViewById(R.id.categ);
        ArrayList<String> listCateg =writeDBcateg(getContext());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,listCateg);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        textMessage = (EditText)view.findViewById(R.id.textmessage);
        message = (ImageButton)view.findViewById(R.id.message);

        TextView date  = (TextView)view.findViewById(R.id.date);
        Calendar calendar = Calendar.getInstance();
        String dataMessage = calendar.getTime().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dataMessage = dateFormat.format(new Date());
        date.setText(dataMessage);
        loc  = (TextView)view.findViewById(R.id.location);
        loc.setText(String.format("%.4f",gpsmyX)+"\n"+String.format("%.4f",gpsmyY));

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(GPSconnect()){
                   Log.e("LOGs", "press Button");
                   Calendar calendar = Calendar.getInstance();
                   String dataMessage = calendar.getTime().toString();
                   SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                   dataMessage = dateFormat.format(new Date());
                   Log.e("TEST MESSAGE ", gpsmyX + " " +gpsmyY+ " " + dataMessage+ " " + textMessage.getText().toString()+ " " + categ+ " " + sharedPreferences.getString("id","-1"));
                   saveMessage(getContext(), gpsmyX, gpsmyY, dataMessage, textMessage.getText().toString(), categ, sharedPreferences.getString("id","-1"));
               }
            else Toast.makeText(getContext(), "Включите передачу геоданных", Toast.LENGTH_LONG).show();
            }
        });

        //работа с камерой
        mImageView = (ImageView) view.findViewById(R.id.imageadd);
        mPhoto = (ImageView) view.findViewById(R.id.imagephoto);

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFullImage();
            }
        });
        ll = (View) view.findViewById(R.id.llAddphoto);
        ImageView clearPhoto = (ImageView) view.findViewById(R.id.imageclear);

        clearPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageView.setVisibility(View.VISIBLE);
                ll.setVisibility(View.GONE);
            }
        });

        return view;
    }





    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Object item = parent.getItemAtPosition(position);
        categ = item.toString();

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
        //Log.e(TAG, mLastLocation.getLatitude() + " " + mLastLocation.getLongitude());

        if (mFirst) {
            mMap.moveCamera(CameraUpdateFactory.
                    newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 16.0f));
            mFirst = false;
        }
        gpsmyX=mLastLocation.getLatitude();
        gpsmyY=mLastLocation.getLongitude();
        loc.setText(String.format("%.4f",gpsmyX)+"\n"+String.format("%.4f",gpsmyY));
     //   Log.e("GPS message ", gpsmyX + " " + gpsmyY);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setBuildingsEnabled(false);
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (mGoogleApiClient == null) {
            //Log.e(TAG, "googleapi");
            createLocationRequest();
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }

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

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
