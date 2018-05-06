package com.example.haytham.eecereads.List_All_Books;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.haytham.eecereads.Main_Activity.MainActivity;
import com.example.haytham.EECE_Reads.R;

/**
 * Created by haytham on 25/04/18.
 * Defines the dialog boxes that is shown when any item is clicked
 */

public class DialogAlert extends BottomSheetDialogFragment {
    public DialogAlert(){

    }
    private Context context;                        // Context of the fragment calling this class
    private FoundFragment foundFragment;            // Instance of FoundFragment
    private BorrowedFragment borrowedFragment;      // Instance of BorrowedFragment
    private RequestedFragment requestedFragment;    // Instance of RequestedFragment
    private String title;                           // Title of the book
    private String name;                            // Author name
    private String maxPin;                          // Max pin fetched from the server

    @SuppressLint("ValidFragment")
    public DialogAlert(Context context,FoundFragment foundFragment,String title,String name){
        this.context=context;
        this.foundFragment=foundFragment;
        this.title=title;
        this.name=name;
    }
    @SuppressLint("ValidFragment")
    public DialogAlert(Context context,BorrowedFragment borrowedFragment,String title,String name){
        this.context=context;
        this.borrowedFragment=borrowedFragment;
        this.title=title;
        this.name=name;
    }

    @SuppressLint("ValidFragment")
    public DialogAlert(Context context,RequestedFragment requestedFragment,String title,String name,String maxPin){
        this.context=context;
        this.requestedFragment=requestedFragment;
        this.title=title;
        this.name=name;
        this.maxPin = maxPin;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.view_custom_dialog, container, false);
        getDialog().setTitle("Simple Dialog");

        TextView textView           = (TextView) rootView.findViewById(R.id.book_info);
        TextView pinText            = (TextView) rootView.findViewById(R.id.text_pin);
        TextView dialogTitle        = (TextView) rootView.findViewById(R.id.dialog_title);
        final Button dismiss        = (Button)   rootView.findViewById(R.id.cancel);
        Button left_btn             = (Button)   rootView.findViewById(R.id.borrow);
        final EditText editText_pin = (EditText) rootView.findViewById(R.id.get_pin);

        // If foundFragment is using this class, view a dialog to borrow a book
        if(foundFragment!=null){
            dialogTitle.setText("Borrow a book");
            textView.setText(title+"\n"+name);
            left_btn.setText("Borrow");
            left_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String pin = editText_pin.getText().toString();
                    if(isNetworkAvailable()) {
                        try {
                            // Execute a background to borrow a book from the server
                            BackgroundTaskNoDialog backgroundTask = new BackgroundTaskNoDialog((Activity) context, foundFragment);
                            backgroundTask.execute("borrow", title, name, pin);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        getDialog().dismiss();
                    }
                }
            });
            dismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDialog().dismiss();
                }
            });
        }

        // If borrowedFragment is using this class, view a dialog to return a book
        else if(borrowedFragment!=null){
            dialogTitle.setText("Return a book");
            textView.setText(title+" \n"+name);
            left_btn.setText("Return");
            left_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String pin = editText_pin.getText().toString();
                    if(isNetworkAvailable()) {
                        try {
                            // Execute a background to return a book from the server
                            BackgroundTaskNoDialog backgroundTask = new BackgroundTaskNoDialog((Activity) context, borrowedFragment);
                            backgroundTask.execute("return", title, name, pin);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        getDialog().dismiss();
                    }
                }
            });
            dismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDialog().dismiss();
                }
            });
        }

        // If requestedFragment is using this class, view a dialog to add a book
        else if(requestedFragment!=null){
            dialogTitle.setText("Add a book");
            final int value = Integer.valueOf(maxPin)+5;
            pinText.setText("Please write this pin on your first page : "+value);
            pinText.setVisibility(View.VISIBLE);
            textView.setText(title+"\n"+name);
            left_btn.setText("Respond");
            left_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String pin = editText_pin.getText().toString();
                    if(pin.isEmpty()){
                        Toast.makeText(getContext(),"Please fill in the field",Toast.LENGTH_LONG).show();
                    }
                    else if(android.text.TextUtils.isDigitsOnly(pin)){
                        if(Integer.valueOf(pin) == value){
                            if(isNetworkAvailable()) {
                                try {
                                    // Execute a background to add a new book to the server
                                    BackgroundTaskNoDialog backgroundTask = new BackgroundTaskNoDialog((Activity) context, requestedFragment);
                                    backgroundTask.execute("respond_to_request", title, name, String.valueOf(value));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                getDialog().dismiss();
                            }
                        }
                        else{
                            Toast.makeText(getContext(),"Pins don't match, try again",Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Toast.makeText(getContext(),"Pins don't match, try again",Toast.LENGTH_LONG).show();
                        getDialog().dismiss();

                    }
                }
            });
            dismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDialog().dismiss();
                }
            });
        }
        return rootView;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if(!(activeNetworkInfo != null && activeNetworkInfo.isConnected())){
            Toast.makeText(getContext(),"Please Check Your Internet Connectivity",Toast.LENGTH_LONG).show();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
