package com.example.turist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
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


public class ChangeProfile extends Fragment {
    TextView fio;
    ImageButton fioButton;
    TextView phone;
    ImageButton phoneButton;
    TextView email;
    ImageButton emailButton;
    TextView social;
    ImageButton socialButton;
    SharedPreferences sharedPreferences;
    TextView phoneList;
    TextView emailList;
    TextView socialList;
    ArrayList<String> phones = new ArrayList<>();
    ArrayList<String> emails = new ArrayList<>();
    ArrayList<String> socials = new ArrayList<>();
    ListView ph;
    ListView em;
    ListView soc;
    ImageButton phoneDelete;
    ImageButton emailDelete;
    ImageButton socialDelete;

    Button save;

    ArrayList getList(String massage){
        ArrayList<String> ret = new ArrayList<>();
        try {
            JSONArray mass = new JSONArray(massage);
            for(int i = 0; i< mass.length(); i ++){
                ret.add(mass.getString(i));
            }
        } catch (JSONException e) {
            Log.e("Error", "not list");
            phones.add("");
            emails.add("");
            socials.add("");
            e.printStackTrace();
        }

        return ret;
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


    JSONArray backUpPhone;
    JSONArray backUpEmail;
    JSONArray backUpSocial;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.change_profile_fragment,container,false);

        sharedPreferences = getContext().getSharedPreferences("authorization", Context.MODE_PRIVATE);

        phoneDelete = (ImageButton)view.findViewById(R.id.delete_phone);
        emailDelete = (ImageButton)view.findViewById(R.id.delete_email);
        socialDelete = (ImageButton)view.findViewById(R.id.delete_soc);

        fio = (TextView) view.findViewById(R.id.textfio);
        fio.setText( sharedPreferences.getString("fio","  "));
        phone = (TextView) view.findViewById(R.id.phone);
        email = (TextView) view.findViewById(R.id.textemail);
        social = (TextView) view.findViewById(R.id.textsoc);

        phoneList = (TextView) view.findViewById(R.id.list_phone);

        emailList = (TextView) view.findViewById(R.id.list_email);
        socialList = (TextView) view.findViewById(R.id.list_social);


        savedInstanceState = this.getArguments();
        if (savedInstanceState != null) {
            try {
                backUpPhone = new JSONArray(savedInstanceState.getString("beakUpPhone"));
                backUpEmail = new JSONArray(savedInstanceState.getString("beakUpEmail"));
                backUpSocial = new JSONArray(savedInstanceState.getString("beakUpSocials"));
            } catch (JSONException e) {
                Log.e("Error", "beakUp");
                backUpEmail = null;
                e.printStackTrace();
            }
        }
        Log.e("share", sharedPreferences.getString("contact","shit"));
        if (backUpEmail == null) {
            Log.e("ee", "null");
            phones = getList(getStatus("phone", sharedPreferences.getString("contact", "")));
            emails = getList(getStatus("email", sharedPreferences.getString("contact", "")));
            socials = getList(getStatus("social", sharedPreferences.getString("contact", "")));
        } else {
            Log.e("ee", "backUp");
            phones = getList(backUpPhone.toString());
            emails = getList(backUpEmail.toString());
            socials = getList(backUpSocial.toString());

            Log.e("ee", "backUp " + phones + '\n' + emails + '\n' + socials);
        }
//        emails.add("");
//        socials.add("");

        if(!emails.isEmpty()){
            emailDelete.setVisibility(View.VISIBLE);
            emailList.setText(emails.get(0));
            for(int i = 1; i <emails.size(); i++){
                emailList.setText(emailList.getText()+"\n"+emails.get(i));
            }
        }
        Log.e("socials",socials.size()+"");
        if(!socials.isEmpty()){
            socialDelete.setVisibility(View.VISIBLE);
            socialList.setText(socials.get(0));
            for(int i = 1; i <socials.size(); i++){
                socialList.setText(socialList.getText()+"\n"+socials.get(i));
            }
        }
        if(phones.size()>1) {
            phoneDelete.setVisibility(View.VISIBLE);
        }
        else{
            phoneDelete.setVisibility(View.INVISIBLE);
        }
        phoneList.setText(phones.get(0));
        for (int i = 1; i < phones.size(); i++) {
            phoneList.setText(phoneList.getText() + "\n" + phones.get(i));
        }

        phoneDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("title", "Удаление номера");

                    bundle.putString("list", phones.toString());
                bundle.putString("beakUpPhone", phones.toString());
                bundle.putString("beakUpEmail", emails.toString());
                JSONArray social = new JSONArray();
                for (int i = 0; i < socials.size(); i++) {
                    social.put(socials.get(i));
                }
                bundle.putString("beakUpSocials", social.toString());
                    FragmentManager fm = getFragmentManager();
                    DeleteDialog deleteDialog = new DeleteDialog();
                    deleteDialog.setArguments(bundle);
                    deleteDialog.show(fm, "Change");

            }
        });

        emailDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("title", "Удаление email");
                bundle.putString("list", emails.toString());
                bundle.putString("beakUpPhone", phones.toString());
                bundle.putString("beakUpEmail", emails.toString());
                JSONArray social = new JSONArray();
                for (int i = 0; i < socials.size(); i++) {
                    social.put(socials.get(i));
                }
                bundle.putString("beakUpSocials", social.toString());
                FragmentManager fm = getFragmentManager();
                DeleteDialog deleteDialog= new DeleteDialog();
                deleteDialog.setArguments(bundle);
                deleteDialog.show(fm, "Change");
            }
        });

        socialDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("title", "Удаление соц.Сети");
                JSONArray social = new JSONArray();
                for(int i = 0; i < socials.size(); i++){
                    social.put(socials.get(i));
                }
                bundle.putString("list", social.toString());
                bundle.putString("beakUpPhone", phones.toString());
                bundle.putString("beakUpEmail", emails.toString());
                JSONArray social2 = new JSONArray();
                for (int i = 0; i < socials.size(); i++) {
                    social2.put(socials.get(i));
                }
                bundle.putString("beakUpSocials", social2.toString());
                FragmentManager fm = getFragmentManager();
                DeleteDialog deleteDialog= new DeleteDialog();
                deleteDialog.setArguments(bundle);
                deleteDialog.show(fm, "Change");
            }
        });








        fioButton = (ImageButton) view.findViewById(R.id.imagefio);
        phoneButton = (ImageButton) view.findViewById(R.id.imagephone);
        emailButton = (ImageButton) view.findViewById(R.id.imageemil);
        socialButton = (ImageButton)view.findViewById(R.id.imagesoc);

        fioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("title", "Изменить имя");
                bundle.putString("hint", "Введите имя");
                bundle.putString("beakUpPhone", phones.toString());
                bundle.putString("beakUpEmail", emails.toString());
                JSONArray social = new JSONArray();
                for (int i = 0; i < socials.size(); i++) {
                    social.put(socials.get(i));
                }
                bundle.putString("beakUpSocials", social.toString());
                FragmentManager fm = getFragmentManager();
                DialogRenameFio dialogRenameFio = new DialogRenameFio();
                dialogRenameFio.setArguments(bundle);
                dialogRenameFio.show(fm, "Change");
            }
        });
        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("title", "Добавить телефон");
                bundle.putString("hint", "Введите телефон");
                bundle.putString("beakUpPhone", phones.toString());
                bundle.putString("beakUpEmail", emails.toString());
                JSONArray social = new JSONArray();
                for (int i = 0; i < socials.size(); i++) {
                    social.put(socials.get(i));
                }
                bundle.putString("beakUpSocials", social.toString());
                FragmentManager fm = getFragmentManager();
                DialogRenameFio dialogRenameFio = new DialogRenameFio();
                dialogRenameFio.setArguments(bundle);
                dialogRenameFio.show(fm, "Change");
            }
        });
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("title", "Добавить email");
                bundle.putString("hint", "Введите email");
                bundle.putString("beakUpPhone", phones.toString());
                bundle.putString("beakUpEmail", emails.toString());
                JSONArray social = new JSONArray();
                for (int i = 0; i < socials.size(); i++) {
                    social.put(socials.get(i));
                }
                bundle.putString("beakUpSocials", social.toString());
                FragmentManager fm = getFragmentManager();
                DialogRenameFio dialogRenameFio = new DialogRenameFio();
                dialogRenameFio.setArguments(bundle);
                dialogRenameFio.show(fm, "Change");
            }
        });
        socialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("title", "Добавить соц. сеть");
                bundle.putString("hint", "Введите ссылку");
                bundle.putString("beakUpPhone", phones.toString());
                bundle.putString("beakUpEmail", emails.toString());
                JSONArray social = new JSONArray();
                for (int i = 0; i < socials.size(); i++) {
                    social.put(socials.get(i));
                }
                bundle.putString("beakUpSocials", social.toString());
                FragmentManager fm = getFragmentManager();
                DialogRenameFio dialogRenameFio = new DialogRenameFio();
                dialogRenameFio.setArguments(bundle);
                dialogRenameFio.show(fm, "Change");
            }
        });

        save = (Button)view.findViewById(R.id.save);




        savedInstanceState = this.getArguments();
        if(savedInstanceState!=null){
            save.setVisibility(View.VISIBLE);
            switch (savedInstanceState.getString("marker")) {
                case "Введите имя":{
                    Log.e("fio", savedInstanceState.getString("marker")+" fio " + savedInstanceState.getString("body"));
                    fio.setText(savedInstanceState.getString("body"));
                    break;
                }
                case "Введите телефон":{

                    phoneList.setText(phoneList.getText()+"\n"+savedInstanceState.getString("body"));
                    phones.add(savedInstanceState.getString("body"));

                    break;
                }
                case "Введите email":{
                    emailList.setText(emailList.getText()+"\n"+savedInstanceState.getString("body"));
                    emails.add(savedInstanceState.getString("body"));
                    break;
                }
                case "Введите ссылку":{
                    socialList.setText(socialList.getText()+"\n"+savedInstanceState.getString("body"));
                    socials.add(savedInstanceState.getString("body"));
                    break;
                }
                case "Удаление номера":{
                    try {
                        JSONArray lis = new JSONArray(savedInstanceState.getString("body"));
                        phoneList.setText(lis.get(0).toString());
                        for(int i = 1; i < lis.length(); i++){
                            phoneList.setText(phoneList.getText()+"\n"+lis.get(i));
                        }
                        phones = getList(lis.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case "Удаление email":{
                    try {
                        JSONArray lis = new JSONArray(savedInstanceState.getString("body"));
                        emailList.setText(lis.get(0).toString());
                        for(int i = 1; i < lis.length(); i++){
                            emailList.setText(emailList.getText()+"\n"+lis.get(i));
                        }
                        emails = getList(lis.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        emailList.setText("");
                        emailDelete.setVisibility(View.INVISIBLE);
                    }

                    break;
                }
                case "Удаление соц.Сети":{
                    Log.e("del_return",savedInstanceState.getString("body"));
                    try {
                        JSONArray lis = new JSONArray(savedInstanceState.getString("body"));
                        Log.e("del_return", lis.toString());
                        socialList.setText(lis.get(0).toString());
                        for(int i = 1; i < lis.length(); i++){
                            socialList.setText(socialList.getText()+"\n"+lis.get(i));
                        }
                        socials = getList(lis.toString());
                    } catch (JSONException e) {
                        Log.e("error", "del code 1");
                        socialList.setText("");
                        socialDelete.setVisibility(View.INVISIBLE);
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> ph = new ArrayList<String>();
                ArrayList<String> em = new ArrayList<String>();
                ArrayList<String> so = new ArrayList<String>();
                for(int i = 0; i<phoneList.getText().toString().split("\n").length; i++){
                    if(!phoneList.getText().toString().split("\n")[i].equals("")) {
                        Log.e("phone " + i, phoneList.getText().toString().split("\n")[i]);
                        ph.add(phoneList.getText().toString().split("\n")[i]);
                    }
                }
                for(int i = 0; i<emailList.getText().toString().split("\n").length; i++){
                    if(!emailList.getText().toString().split("\n")[i].equals("")) {
                        Log.e("email " + i, emailList.getText().toString().split("\n")[i]);
                        em.add(emailList.getText().toString().split("\n")[i]);
                    }
                }
                for(int i = 0; i<socialList.getText().toString().split("\n").length; i++){
                    if(!socialList.getText().toString().split("\n")[i].equals("")) {
                        Log.e("social " + i, socialList.getText().toString().split("\n")[i]);
                        so.add(socialList.getText().toString().split("\n")[i]);
                    }
                }
                saveServer(fio.getText().toString(), ph,em,so, sharedPreferences.getString("id","-1"));

            }
        });
        return view;
    }


    void saveServer(final String fio, final ArrayList<String> phones, final ArrayList<String> emails, final ArrayList<String> socials, final String id){
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
                JSONArray tel = new JSONArray(phones);
                JSONArray mail = new JSONArray(emails);
                JSONArray soci = new JSONArray(socials);
                Log.e("setingSave", tel.toString() + " " + mail.toString() + " " + soci.toString());




                try {

                    OkHttpClient client = new OkHttpClient();
                    RequestBody formBody = new FormBody.Builder()
                            .addEncoded("id", id)
                            .addEncoded("fio", fio)
                            .addEncoded("phone", tel.toString())
                            .addEncoded("email", mail.toString())
                            .addEncoded("social", soci.toString())
                            .build();

                    Request request = new Request.Builder()
                            .url("http://109.120.189.141:81/web/api/track/change-profile")
                            //.url("http://192.168.0.103/web/api/track/change-profile")
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .post(formBody)
                            .build();

                    Log.e("request", request + " :request");
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
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if(!getStatus("status",message).isEmpty()){
                    Log.e("fio=", fio);

                    sharedPreferences.edit().putString("fio",fio).commit();

//                    phones = getList(getStatus("phone",sharedPreferences.getString("contact","")));
//                    emails = getList(getStatus("email",sharedPreferences.getString("contact","")));
//                    socials = getList(getStatus("social",sharedPreferences.getString("contact","")));

                //my         {"email":"[111111111@gmail.com, 222222222@gmail.com, @gmail.com]","phone":"[89503845823]","social":"[vk.com\/id=11111111, vk.com\/id=22222222, vk.com\/id=33333333]"}
                    //server {"email":["111111111@gmail.com","222222222@gmail.com","@gmail.com"],"phone":["89503845823","0000000000"],"social":["vk.com\/id=11111111","vk.com\/id=22222222","vk.com\/id=33333333"]}
                    JSONObject udDate = new JSONObject();
                    try {



                    JSONArray ph = new JSONArray();
                    JSONArray em = new JSONArray();

                    JSONArray soc = new JSONArray();

                    for(int i = 0; i < emails.size(); i++){
                        em.put(emails.get(i));
                    }
                    for(int i = 0; i < phones.size(); i++){
                        ph.put(phones.get(i));
                    }
                    for(int i = 0; i < socials.size(); i++){
                        soc.put(socials.get(i));
                    }
                        udDate.put("email", em);
                        udDate.put("phone", ph);
                        udDate.put("social", soc);

                    } catch (JSONException e) {
                        Log.e("error", "code 1");
                        e.printStackTrace();
                    }



                    Log.e("sharer UP", udDate.toString());
                    sharedPreferences.edit().putString("contact",udDate.toString()).commit();



                    Toast.makeText(getContext(), getStatus("status", message), Toast.LENGTH_SHORT).show();
                    save.setVisibility(View.INVISIBLE);
                    FragmentSetting fragmentSetting=new FragmentSetting();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.remove(fragmentSetting).commit();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.add(R.id.first_setting,fragmentSetting).commit();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.first_setting,fragmentSetting).commit();

                    Intent intent=new Intent(getActivity(),MenuActivity.class);
                    startActivity(intent);

                }
                else{
                    Toast.makeText(getContext(), "Ошибка сохранения данных"+"\n"+ "Возможно вы не ввели имя полностью", Toast.LENGTH_LONG).show();
                }

            }
        }
        SendMessageServer sendMessageServer = new SendMessageServer();
        sendMessageServer.execute();

    }
}
