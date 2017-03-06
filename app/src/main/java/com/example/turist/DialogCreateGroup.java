package com.example.turist;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;


public class DialogCreateGroup extends DialogFragment {
    DelayAutoCompleteTextView bookTitle;

    TextView textView;
    TextView textView2;
    TextView open;
    CheckBox mCheckBox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_create_group, container, false);
        getDialog().setTitle("Добавление к группе");
        mCheckBox = (CheckBox) rootView.findViewById(R.id.open_group);

        bookTitle = (DelayAutoCompleteTextView) rootView.findViewById(R.id.book_title);
        bookTitle.setThreshold(3);
        bookTitle.setAdapter(new GroupAutoCompleteAdapter(rootView.getContext()));
        bookTitle.setLoadingIndicator((ProgressBar) rootView.findViewById(R.id.progress_bar));

        textView=(TextView)getActivity().findViewById(R.id.name_group);
        textView2=(TextView)getActivity().findViewById(R.id.addgroup);
        open=(TextView)getActivity().findViewById(R.id.checkbox_open);

        bookTitle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String group = (String) adapterView.getItemAtPosition(position);
                bookTitle.setText(group);
            }
        });
        Button accept =(Button)rootView.findViewById(R.id.accept);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText(bookTitle.getText().toString());
                Log.e("log",bookTitle.getText().toString());
                textView.setVisibility(View.VISIBLE);
                textView2.setText("1");
                if(mCheckBox.isChecked()){
                    open.setText("1");
                }
                else {
                    open.setText("0");
                }
                dismiss();
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