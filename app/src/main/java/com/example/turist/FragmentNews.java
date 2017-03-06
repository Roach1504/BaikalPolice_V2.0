package com.example.turist;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RemoteViewsService;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class FragmentNews extends Fragment {

    private List<New> news;
    private RecyclerView rv;
    public ArrayList<String> urlnew = new ArrayList<>();
    public ArrayList<String> datenew = new ArrayList<>();
    public ArrayList<String> titlenew = new ArrayList<>();
    public ArrayList<String> shortnew = new ArrayList<>();
    public ArrayList<String> textnew = new ArrayList<>();
    int newSize;
    String TAG = "myLogs";
    String message;


    class UpdateNews extends AsyncTask<Void, Void, Void> {
        protected ArrayList getNews(String key,String strJson) {
            ArrayList arrayList = new ArrayList();
            try {
                JSONArray messagArrey = new JSONArray(strJson);
                for(int i = 0; i < messagArrey.length(); i++) {
                    arrayList.add(getStatus(key, messagArrey.get(i).toString()));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return arrayList;
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


                Log.e(TAG, "2");
                OkHttpClient client = new OkHttpClient();

                Log.e(TAG, "3");
                RequestBody formBody = new FormBody.Builder()
                        .addEncoded("page","1")
                        .build();
                Log.e(TAG, "4");


                Request request = new Request.Builder()
                        .url("http://109.120.189.141:81/web/api/news/list")
                       // .url("http://195.133.144.16/api/news")
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
                Log.e("BuddistTEST", "message NEWS = " + message);
                Log.e(TAG, "Вот прям точно ушли");
                Log.e(TAG, "URLS: " + getNews("image",message));




            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ArrayList urls = getNews("image",message);
            for (int i = 0; i < urls.size(); i++){
                urlnew.add("http://109.120.189.141:81" + urls.get(i));
                Log.e(TAG,"urls: "+urlnew.get(i));
            }
            datenew = getNews("date",message);
            titlenew = getNews("title",message);
            shortnew = getNews("short",message);
            textnew = getNews("text",message);
            initializeData();
            initializeAdapter();
//
        }
    }//загрузка новостей

    public static boolean hasConnection(Context context) {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recyclerview_activity, container, false);



        rv=(RecyclerView)view.findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);



        if(hasConnection(getContext())){
            UpdateNews updateNews = new UpdateNews();
            updateNews.execute();
        }


        Animation animDown, animUp;
        animDown = AnimationUtils.loadAnimation(getContext(), R.anim.animation_down);
        animDown.setAnimationListener(new Animation.AnimationListener() {
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

        initializeData();
        initializeAdapter();
        return view;
    }
    private void initializeData(){
        news = new ArrayList<>();

        Log.e(TAG,"size = " +titlenew.size());
        for(int i = datenew.size()-1; i >= 0; i--){
            news.add(new New(titlenew.get(i),shortnew.get(i),textnew.get(i),urlnew.get(i),datenew.get(i)));
        }
//        news.add(new New("1Lavery Maisssssssssssss", "25 years olddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddaaaaaa", "ggggggggggggggggggggggggggg", "http://s1.iconbird.com/ico/2013/6/312/w512h5121371844375Download.png", "2016-05-14"));
//        news.add(new New("2Lavery Maiss", "25 years old", "ggggggggggggggggggggggggggg", "https://pp.vk.me/c543103/v543103288/1de4d/1Md30kUItIE.jpg", "2016-05-14"));

    }
    private void initializeAdapter(){
        RVAdapter adapter = new RVAdapter(news);
        rv.setAdapter(adapter);
        //  rv.scrollToPosition(5);
    }
}
class New {
    String header;
    String content;
    String shortText;
    Uri uri;
    String date;

    public int check=0;


    New(String header,String shortText, String content, String uri,String date) {
        this.header = header;
        this.shortText=shortText;
        this.content = content;
        this.uri=Uri.parse(String.valueOf(uri));
        this.date=date;
    }
}
class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder>  {

    List<New> news;
    ArrayList<Integer> pos=new ArrayList<>();


    public static class PersonViewHolder extends RecyclerView.ViewHolder {


        CardView cv;
        TextView newHeader;
        TextView newContent;
        TextView newShortText;
        TextView newDate;
        ImageView newPhoto;
        ImageView newArrow;




        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);

            newHeader = (TextView)itemView.findViewById(R.id.title);
            newShortText=(TextView)itemView.findViewById(R.id.shortText);
            newContent = (TextView)itemView.findViewById(R.id.text);
            newPhoto = (ImageView)itemView.findViewById(R.id.photo);
            newDate=(TextView)itemView.findViewById(R.id.date);
            newContent.setVisibility(View.GONE);
            newArrow=(ImageView)itemView.findViewById(R.id.imageArrow);
            newArrow.setColorFilter(Color.parseColor("#2438ac"));





        }

    }



    RVAdapter(List<New> news){
        this.news = news;
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

    }
    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }
    @Override
    public void onBindViewHolder(final PersonViewHolder personViewHolder, int i) {



        personViewHolder.newHeader.setText(news.get(i).header);
        personViewHolder.newContent.setText(news.get(i).content);
        personViewHolder.newShortText.setText(news.get(i).shortText);
        personViewHolder.newDate.setText(news.get(i).date);
        personViewHolder.newArrow.setRotation(0);

        Picasso.with(personViewHolder.newPhoto.getContext()).load(news.get(i).uri)
                .placeholder(R.drawable.cast_ic_expanded_controller_pause)
//                .resize(, 300)
                .fit()
//                .centerInside()
                .centerCrop()
                .into(personViewHolder.newPhoto);

        if(i==pos.size())
            pos.add(0);
        else pos.set(i,0);
        Log.e("TAG","size "+ String.valueOf(pos.size()));

        //cv.setCardElevation(cv.getCardElevation()-size);
//                    personViewHolder.itemView.getLayoutParams().height= 0;
        personViewHolder.newContent.setVisibility(View.GONE);







        personViewHolder.cv.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                int position = personViewHolder.getAdapterPosition();


                if (position != RecyclerView.NO_POSITION) {

                    itemClick(position);
                    Log.e("TAG","position"+ String.valueOf(position));
                }

            }
            private void itemClick(int position){



                if(pos.get(position)==0){

                    personViewHolder.newContent.setVisibility(View.VISIBLE);
                    personViewHolder.newArrow.setRotation(180);


                    Log.e("TAG", "nom card "+position);

                    pos.set(position, 1);

                }else {
                    pos.set(position, 0);

                    personViewHolder.newContent.setVisibility(View.GONE);
                    personViewHolder.newArrow.setRotation(0);

                }
            }
        });



    }
    @Override
    public int getItemCount() {
        return news.size();
    }
}




