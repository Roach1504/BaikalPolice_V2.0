package com.example.turist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);

        public void onLongItemClick(View view, int position);
    }

    GestureDetector mGestureDetector;

    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && mListener != null) {
                    mListener.onLongItemClick(child, recyclerView.getChildAdapterPosition(child));
                }
            }
        });
    }

    @Override public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
            return true;
        }
        return false;
    }

    @Override public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) { }

    @Override
    public void onRequestDisallowInterceptTouchEvent (boolean disallowIntercept){}
}



public class HistoriMapItem extends Fragment {


    String id;
    SharedPreferences sharedPreferences;

    public ArrayList<HashMap<String, String>> bigTracks = new ArrayList<HashMap<String,String>>();
    public ArrayList<HashMap<String, String>> tracks = new ArrayList<HashMap<String,String>>();

    public ProgressDialog loading;


    public void readTracks(final RecyclerView recyclerView){

        loading = new ProgressDialog(getContext());
        loading.setMessage("Загрузка треков");
        loading.setIndeterminate(true);
        loading.setCancelable(false);
        loading.show();



        ArrayList<HashMap<String, String>> tracksH1 = new ArrayList<HashMap<String, String>>();
        class ReadTracksServer extends AsyncTask<Void, Void, Void> {
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
                            .addEncoded("user_id", id)
                            .build();

                    Log.e("ID" ,id);
                    Request request = new Request.Builder()
                            .url("http://109.120.189.141:81/web/api/track/get-my-tracks")
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


                    bigTracks = new ArrayList<HashMap<String,String>>();


                    JSONArray ja = null;
                    try {
                        ja = new JSONArray(message);
                        Log.e("JA", ja.toString());
                        for(int i  = 0; i < ja.length(); i++){
                            HashMap<String,String>  mMap = new HashMap<>();
                            mMap.put("id_track", getStatus("id", ja.get(i).toString()));
                            mMap.put("start_track", getStatus("start", ja.get(i).toString()));
                            mMap.put("finish_track", getStatus("end", ja.get(i).toString()));
                            mMap.put("title", getStatus("title", ja.get(i).toString()));
                            bigTracks.add(mMap);
                        }
                        for(int i=bigTracks.size()-1; i>0;i--){
                            Log.e("Title", bigTracks.get(i).toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.e("JsonArray", bigTracks.toString());



                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                loading.dismiss();
                ContentAdapter adapter = new ContentAdapter(recyclerView.getContext());
                recyclerView.setAdapter(adapter);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


            }
        }

        ReadTracksServer readTracksServer= new ReadTracksServer();
        readTracksServer.execute();


    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);


        View topBar=(View)getActivity().findViewById(R.id.top_bar);
        View topBar1=(View)getActivity().findViewById(R.id.title_bars);
        topBar1.setVisibility(View.VISIBLE);
        topBar.setVisibility(View.VISIBLE);

        sharedPreferences = getContext().getSharedPreferences("authorization", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("id","-1");

        readTracks(recyclerView);
        recyclerView.addOnItemTouchListener( new RecyclerItemClickListener(getContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
            @Override public void onItemClick(View view, int position) {

                Log.e("MyLogs", "Click ");
                SelectTrack selectTrack =new SelectTrack();
                Bundle bundle=new Bundle();

                bundle.putString("id_track",tracks.get(position % tracks.size()).get("id_track"));
                bundle.putString("start_track",tracks.get(position % tracks.size()).get("start_track"));
                bundle.putString("end_track",tracks.get(position % tracks.size()).get("finish_track"));
                bundle.putString("title",tracks.get(position % tracks.size()).get("title"));


                selectTrack.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.track_list,selectTrack).commit();

                //ContentAdapter contentAdapter = new ContentAdapter(getContext());
                //contentAdapter.itemClickAdapter(position);

                //разовый клик
            }

            @Override public void onLongItemClick(View view, int position) {
                //долгое нажатие
             //   ContentAdapter contentAdapter = new ContentAdapter(getContext());
              //  contentAdapter.itemClickAdapter(position);
            }
        }));


        return recyclerView;
    }





    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView trackNum;
        public TextView trackBegin;
        public TextView trackEnd;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_map, parent, false));

            trackNum = (TextView) itemView.findViewById(R.id.numberTrack);
            trackBegin = (TextView) itemView.findViewById(R.id.beginTrack);
            trackEnd = (TextView) itemView.findViewById(R.id.endTrack);
        }
    }


    boolean addDate(String date_input, String dateStart, String dateEnd){

        SimpleDateFormat dateProductFormat = new SimpleDateFormat("yy-MM-dd");
        dateProductFormat.applyPattern("yy-MM-dd");
        Date startDate = new Date();
        Date endDate = new Date();
        Date ddddd = new Date();
        long dateOne;
        long dateTue;
        long dateTree;
        try {
            startDate = dateProductFormat.parse(dateStart);
            dateOne = startDate.getTime();

        } catch (ParseException e) {
            dateOne=0;
            e.printStackTrace();
        }
        try {
            endDate = dateProductFormat.parse(dateEnd);
            dateTue = endDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dateTue = endDate.getTime();
        try {
            ddddd = dateProductFormat.parse(date_input);
            dateTree = ddddd.getTime();
        } catch (ParseException e) {
            dateTree = 0;
            e.printStackTrace();
        }




//        Log.e("addDate", "OT "+dateOne+ " До "+ dateTue + " Текущая "+dateTree);
        if(dateOne>dateTree){
            return false;
        }
        else{
            if(dateTue<dateTree){
                return false;
            }
            else {
                return true;
            }
        }
    }
    /**
     * Adapter to display recycler view.
     */
    public class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        // Set numbers of List in RecyclerView.



        public ContentAdapter(Context context) {
            TextView textStart = (TextView)getActivity().findViewById(R.id.text_start);
            TextView textEnd = (TextView)getActivity().findViewById(R.id.text_end);



            String date_Start = textStart.getText().toString();
            String date_End = textEnd.getText().toString();
            Resources resources = context.getResources();

            if (bigTracks.size() == 0) {
                Toast.makeText(getContext(), "Пока нет ни одного трека", Toast.LENGTH_SHORT).show();
            }
            for(int i = bigTracks.size()-1; i >= 0; i--){
               if(addDate(bigTracks.get(i).get("start_track").split(" ")[0],date_Start, date_End)){
                   tracks.add(bigTracks.get(i));
               }
            }
            Log.e("holder ", tracks.toString());
//            users = metodDB.writeName();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            //holder.track.setText(tracks.get(position % tracks.size()));
            int num = position+1;
            //holder.track.setText("№ "+num + " Begin " +tracks.get(position % tracks.size()).get("start_track"));
            holder.trackNum.setText(tracks.get(position % tracks.size()).get("id_track"));
            holder.trackBegin.setText(tracks.get(position % tracks.size()).get("start_track")+"\n"+tracks.get(position % tracks.size()).get("finish_track"));
            holder.trackEnd.setText(tracks.get(position % tracks.size()).get("title"));

        }

        public void itemClickAdapter(int position) {
//            Log.e("MyLogs", "Click ");
//            SelectTrack selectTrack =new SelectTrack();
//            Bundle bundle=new Bundle();
//
//            bundle.putString("id_track",tracks.get(position % tracks.size()).get("id_track"));
//            bundle.putString("start_track",tracks.get(position % tracks.size()).get("start_track"));
//            bundle.putString("end_track",tracks.get(position % tracks.size()).get("finish_track"));
//
//            selectTrack.setArguments(bundle);
//            FragmentManager fragmentManager = getFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.addToBackStack(null);
//            fragmentTransaction.replace(R.id.track_list,selectTrack).commit();
        }

        @Override
        public int getItemCount() {
            Log.e("SizeHolder", tracks.size()+"");
            return tracks.size();
        }

    }

}
