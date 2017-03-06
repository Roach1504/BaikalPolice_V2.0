package com.example.turist;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
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

public class ChangePassDialog extends DialogFragment  {
    EditText oldPass;
    EditText newPass;
    EditText repeatNewPass;
    String message;
    public void chengePassword(final String oldPassword, final String newPassword, final String id){

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
                            .addEncoded("pass",oldPassword)
                            .addEncoded("pass_new",newPassword)
                            .build();




                    Request request = new Request.Builder()
                            .url("http://109.120.189.141:81/web/api/track/change-password")
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
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                dismiss();
            }
        }


        ChengePasswordServer in1 = new ChengePasswordServer();
        in1.execute();


    }

    SharedPreferences sharedPreferences;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.change_pass_dialog, container, false);

        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Dialog);
        getDialog().setTitle("Изменение пароля");

        sharedPreferences = getContext().getSharedPreferences("authorization", Context.MODE_PRIVATE);


        oldPass=(EditText)rootView.findViewById(R.id.oldPass);
        newPass=(EditText)rootView.findViewById(R.id.newPass);
        repeatNewPass=(EditText)rootView.findViewById(R.id.repeatNewPass);

        Button accept =(Button)rootView.findViewById(R.id.accept);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("pass", oldPass.getText().toString()+" "+newPass.getText().toString()+ " "+ repeatNewPass.getText());
                if(newPass.getText().toString().equals(repeatNewPass.getText().toString())){
                    if(newPass.getText().length()<5){
                        Toast.makeText(getContext(), "пароль должен быть длинее 4 символов", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if(newPass.getText().equals(oldPass.getText())){
                            Toast.makeText(getContext(), "ваш новый пароль не может быть вашим старым паролем", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            chengePassword(oldPass.getText().toString(),newPass.getText().toString(), sharedPreferences.getString("id","-1"));
                        }
                    }
                }
                else{
                    Toast.makeText(getContext(), "пароли не совпадают", Toast.LENGTH_SHORT).show();
                }

            }
        });

        Button cancel=(Button)rootView.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return rootView;
    }


}
