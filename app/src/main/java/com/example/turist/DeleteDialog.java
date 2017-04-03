package com.example.turist;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Map;


public class DeleteDialog  extends DialogFragment {
    ListView list;
    Button accept;
    Button cancel;
    JSONArray lists;
    String markers;
    ArrayList<String> listss = new ArrayList<>();
    ArrayList<String> list_return;
    JSONArray backUpPhone;
    JSONArray backUpEmail;
    JSONArray backUpSocial;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        final RecyclerView rootView = (RecyclerView) inflater.inflate(
//                R.layout.recycler_view, container, false);
        View rView = inflater.inflate(R.layout.delete_dialog, container, false);
        savedInstanceState = this.getArguments();
        //list = (ListView)recyclerView.findViewById(R.id.list_type);
        list = (ListView)rView.findViewById(R.id.message_list);
        if(savedInstanceState !=null){
            Log.e("onCreadDialog", "true");
            try {
                lists = new JSONArray(savedInstanceState.getString("list"));
            } catch (JSONException e) {
                Log.e("error", "code 1, delete");
                e.printStackTrace();
            }
            markers = savedInstanceState.getString("title");
            getDialog().setTitle(savedInstanceState.getString("title"));

            try {
                backUpPhone = new JSONArray(savedInstanceState.getString("beakUpPhone"));
                backUpEmail = new JSONArray(savedInstanceState.getString("beakUpEmail"));
                backUpSocial = new JSONArray(savedInstanceState.getString("beakUpSocials"));
            } catch (JSONException e) {
                Log.e("Error", "beakUp");
                e.printStackTrace();
            }

        }
        else{
            Log.e("onCreadDialog", "false");
        }

        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        for (int i = 0; i<lists.length();i++){
            try {
                listss.add(lists.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // listss в список в list_return оставшиеся элементы

Log.e("listss",listss.toString());

        //ImageButton imageButton = (ImageButton) rView.findViewById(R.id.image_item);
        //imageButton.setFocusable(false);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_multiple_choice, listss);

        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                Log.e("click", position+"k");
                SparseBooleanArray chosen = ((ListView) parent).getCheckedItemPositions();
                list_return = new ArrayList<String>();
                for (int i = 0; i < chosen.size(); i++) {
                    if (chosen.valueAt(i)) {
                        list_return.add(listss.get(chosen.keyAt(i)));
                        Log.e("check",(listss.get(chosen.keyAt(i)) + " "));
                    }
                }
            }
        });



        accept = (Button)rView.findViewById(R.id.accept);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.e("return", list_return.toString());
                ArrayList<String> test = new ArrayList<String>();
                JSONArray sos = new JSONArray();
                for(int i = 0; i <listss.size();i++){
                    if(!list_return.contains(listss.get(i))){
                        test.add(listss.get(i));
                        sos.put(listss.get(i));
                    }
                }

                Bundle bundle = new Bundle();
                bundle.putString("marker", markers);
                Log.e("return", sos.toString());
                bundle.putString("body", sos.toString());
                bundle.putString("beakUpPhone", backUpPhone.toString());
                bundle.putString("beakUpEmail", backUpEmail.toString());
                bundle.putString("beakUpSocials", backUpSocial.toString());
                ChangeProfile changeProfile=new ChangeProfile();
                changeProfile.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.first_setting,changeProfile).commit();
                dismiss();
            }
        });
        cancel = (Button)rView.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return rView;
    }

}