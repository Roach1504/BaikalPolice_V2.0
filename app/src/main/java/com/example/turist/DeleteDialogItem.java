package com.example.turist;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


public class DeleteDialogItem extends Fragment {
    JSONArray lists;
    String markers;
    ArrayList<String> listss = new ArrayList<>();
    ArrayList<String> list_return = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final RecyclerView rootView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
        if(savedInstanceState !=null){
            Log.e("onCreadDialog", "true");
            try {
                lists = new JSONArray(savedInstanceState.getString("list"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            markers = savedInstanceState.getString("title");
           // getDialog().setTitle(savedInstanceState.getString("title"));
        }
        else{
            Log.e("onCreadDialog", "false");
        }
        for (int i = 0; i<lists.length();i++){
            try {
                listss.add(lists.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        DeleteDialogItem.ContentAdapter adapter = new DeleteDialogItem.ContentAdapter(getContext());
        rootView.setAdapter(adapter);
        rootView.setHasFixedSize(true);
        rootView.setLayoutManager(new LinearLayoutManager(getActivity()));

        rootView.addOnItemTouchListener( new RecyclerItemClickListener(getContext(), rootView ,new RecyclerItemClickListener.OnItemClickListener() {
            @Override public void onItemClick(View view, int position) {
                DeleteDialogItem.ContentAdapter contentAdapter = new DeleteDialogItem.ContentAdapter(getContext());
                contentAdapter.itemClickAdapter(position);
                DeleteDialogItem.ContentAdapter adapter = new DeleteDialogItem.ContentAdapter(getContext());
                rootView.setAdapter(adapter);
                rootView.setHasFixedSize(true);
                rootView.setLayoutManager(new LinearLayoutManager(getActivity()));
                //разовый клик
            }

            @Override public void onLongItemClick(View view, int position) {
                //долгое нажатие
                DeleteDialogItem.ContentAdapter contentAdapter = new DeleteDialogItem.ContentAdapter(getContext());
                contentAdapter.itemClickAdapter(position);
            }
        }));

        return rootView;
    }
    public static class ViewHolder1 extends RecyclerView.ViewHolder {

        public TextView text_list;
        public ImageButton imag_list;


        public ViewHolder1(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_list_delete, parent, false));
            text_list = (TextView) itemView.findViewById(R.id.text_item);
            imag_list = (ImageButton)itemView.findViewById(R.id.image_item);

        }
    }
    public class ContentAdapter extends RecyclerView.Adapter<DeleteDialogItem.ViewHolder1> {
        // Set numbers of List in RecyclerView.

        private ArrayList<String> messages = new ArrayList<String>();


        public ContentAdapter(Context context) {
            Resources resources = context.getResources();
            for (int i = listss.size() - 1; i >= 0; i--) {
                messages.add(listss.get(i));
            }
            Log.e("holder ", messages.toString()+"\n");
//            users = metodDB.writeName();

        }



        @Override
        public DeleteDialogItem.ViewHolder1 onCreateViewHolder(ViewGroup parent, int viewType) {
            return new DeleteDialogItem.ViewHolder1(LayoutInflater.from(parent.getContext()), parent);
        }


        @Override
        public void onBindViewHolder(DeleteDialogItem.ViewHolder1 holder, int position) {
            holder.text_list.setText(messages.get(position % messages.size()));
            if(holder.text_list.getText().toString().equals(list_return.get(position))){
                holder.imag_list.setVisibility(View.INVISIBLE);
            }

        }
        public void itemClickAdapter(int position) {
            list_return.add(messages.get(position));

        }


        @Override
        public int getItemCount() {
            return messages.size();
        }
    }
}
