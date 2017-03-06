package com.example.turist;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



public class HistoryMessageItem extends Fragment  {
    String id;
    SharedPreferences sharedPreferences;
    ProgressDialog loading;
    public ArrayList<HashMap<String, String>> bigMessages = new ArrayList<HashMap<String,String>>();

    public void readMessages(final RecyclerView recyclerView){

        loading = new ProgressDialog(getContext());
        loading.setMessage("Загрузка cooбщений");
        loading.setIndeterminate(true);
        loading.setCancelable(false);
        loading.show();


        class ReadMessageServer extends AsyncTask<Void, Void, Void> {
            String message;
            ArrayList<HashMap<String, String>> tracksH = new ArrayList<HashMap<String,String>>();

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
                            .addEncoded("id", id)
                            .build();

                    Log.e("ID" ,id);
                    Request request = new Request.Builder()
                            .url("http://109.120.189.141:81/web/api/track/get-my-reports")
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

                    Log.e("Server tracks  ", message);


                    bigMessages = new ArrayList<HashMap<String,String>>();


                    JSONArray ja = null;
                    try {
                        ja = new JSONArray(message);
                        Log.e("JA", ja.toString());
                        for(int i  = 0; i < ja.length(); i++){
                            HashMap<String,String>  mMap = new HashMap<>();
                            mMap.put("point", getStatus("point", ja.get(i).toString()));
                            mMap.put("marker", getStatus("marker", ja.get(i).toString()));
                            mMap.put("date", getStatus("date", ja.get(i).toString()));
                            mMap.put("category", getStatus("category", ja.get(i).toString()));
                            mMap.put("message", getStatus("message", ja.get(i).toString()));
                            mMap.put("name", getStatus("name", ja.get(i).toString()));
                            mMap.put("thumb", getStatus("thumb", ja.get(i).toString()));
                            mMap.put("image", getStatus("image", ja.get(i).toString()));
                            bigMessages.add(mMap);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.e("JsonArray", bigMessages.toString());



                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                loading.dismiss();
                HistoryMessageItem.ContentAdapter adapter = new HistoryMessageItem.ContentAdapter(recyclerView.getContext());
                recyclerView.setAdapter(adapter);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


            }
        }

        ReadMessageServer readTracksServer= new ReadMessageServer();
        readTracksServer.execute();


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
        sharedPreferences = getContext().getSharedPreferences("authorization", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("id","-1");

        readMessages(recyclerView);


        recyclerView.addOnItemTouchListener( new RecyclerItemClickListener(getContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
            @Override public void onItemClick(View view, int position) {
                ContentAdapter contentAdapter = new ContentAdapter(getContext());
                contentAdapter.itemClickAdapter(position);
                //разовый клик
            }

            @Override public void onLongItemClick(View view, int position) {
                //долгое нажатие
               ContentAdapter contentAdapter = new ContentAdapter(getContext());
                contentAdapter.itemClickAdapter(position);
            }
        }));
        return recyclerView;
    }

    public static class ViewHolder1 extends RecyclerView.ViewHolder {

        public TextView textMessage;
        public TextView dateMessage;
        public TextView timeMessage;
        public ImageView imageMessage;
        public TextView typeMessage;

        public ViewHolder1(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_message, parent, false));

            textMessage = (TextView) itemView.findViewById(R.id.text_mes);
           dateMessage = (TextView) itemView.findViewById(R.id.date);
            typeMessage = (TextView) itemView.findViewById(R.id.categ_mes);
            timeMessage = (TextView) itemView.findViewById(R.id.time);
            imageMessage=(ImageView)itemView.findViewById(R.id.logo);
        }
    }

    public class ContentAdapter extends RecyclerView.Adapter<ViewHolder1> {
        // Set numbers of List in RecyclerView.

        private ArrayList<HashMap<String, String>> messages = new ArrayList<HashMap<String, String>>();


        public ContentAdapter(Context context) {
            Resources resources = context.getResources();
            if (bigMessages.size() == 0) {
                Toast.makeText(getContext(), "Пока нет ни одного сообщения", Toast.LENGTH_SHORT).show();
            }
            for (int i = bigMessages.size() - 1; i >= 0; i--) {
                messages.add(bigMessages.get(i));
            }
            Log.e("holder ", messages.toString()+"\n");
//            users = metodDB.writeName();

        }


        @Override
        public ViewHolder1 onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder1(LayoutInflater.from(parent.getContext()), parent);
        }

        String parsDate(String date){
            Log.e("inputDate", date);
            SimpleDateFormat dateMMMFormat = new SimpleDateFormat("yy-MM-dd");
            SimpleDateFormat dateMMMFormat2 = new SimpleDateFormat("dd-MM");
            SimpleDateFormat dateMMMFormat1 = new SimpleDateFormat("dd-MMM");
            SimpleDateFormat dateMMMFormat3 = new SimpleDateFormat("dd-MM-yy");
         //   dateMMMFormat.applyPattern("dd MMM");
//            SimpleDateFormat dateDayFormat = new SimpleDateFormat("yy-MM-dd");
//            dateDayFormat.applyPattern("dd");
            Date startDate = new Date();
            try {
                startDate = dateMMMFormat.parse(date);

            } catch (ParseException e) {
                Log.e("error", "parseDate");
                e.printStackTrace();
            }


            return dateMMMFormat3.format(startDate);
        }

        @Override
        public void onBindViewHolder(ViewHolder1 holder, int position) {

            holder.textMessage.setText("Текст сообщения:"+"\n"+messages.get(position % messages.size()).get("message"));
            holder.dateMessage.setText(parsDate(messages.get(position % messages.size()).get("date").split(" ")[0]));
            //holder.dateMessage.setText("03 Март");
            holder.typeMessage.setText(messages.get(position % messages.size()).get("category"));
            holder.timeMessage.setText(messages.get(position % messages.size()).get("date").split(" ")[1].substring(0,5));
            Log.e("Date", parsDate(messages.get(position % messages.size()).get("date").split(" ")[0]));
            Log.e("Date2", holder.dateMessage.getText().toString());


                Picasso.with(holder.itemView.getContext()).load(messages.get(position % messages.size()).get("image"))
                        .placeholder(R.mipmap.ic_launcher1)
                        .fit()
                        .centerCrop()
                        .into(holder.imageMessage);




        }
        public void itemClickAdapter(int position) {
            SelectMessage selectMessage =new SelectMessage();
            Bundle bundle=new Bundle();


            Log.e("item "+position,messages.get(position).get("message"));
            bundle.putString("points",messages.get(position).get("point"));
            bundle.putString("date",messages.get(position).get("date"));
            bundle.putString("message",messages.get(position ).get("message"));
            bundle.putString("category",messages.get(position).get("category"));
            bundle.putString("image",messages.get(position).get("image"));

            selectMessage.setArguments(bundle);
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.message_list,selectMessage).commit();
        }


        @Override
        public int getItemCount() {
            return messages.size();
        }
    }
}
