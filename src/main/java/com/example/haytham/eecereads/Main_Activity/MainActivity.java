package com.example.haytham.eecereads.Main_Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.example.haytham.eecereads.List_All_Books.ListAllBooks;
import com.example.haytham.EECE_Reads.R;


/**
 * Created by haytham on 22/04/18.
 *  = Defines the sections of the main activity
 *  = Defines the alert dialog for user input for various sections
 */

public class MainActivity extends AppCompatActivity {
    private FrameLayout []frameLayouts; // holds the sections of the main activity layout
    private TextView [] textViews;      // holds the titles of the sections
    private TextView [] dummys;         // holds the titles of the sections to make stroke effect

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remove notification bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Enable hardware accleration for android > 5.0
        // Devices with android 5.0 will experience lag in swiping down
        if (Integer.valueOf(android.os.Build.VERSION.SDK) >23) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                                 WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        }
        setContentView(R.layout.activity_home_screen);

        frameLayouts = new FrameLayout[5];
        frameLayouts[0] = (FrameLayout) findViewById(R.id.frame_layout_1);
        frameLayouts[1] = (FrameLayout) findViewById(R.id.frame_layout_2);
        frameLayouts[2] = (FrameLayout) findViewById(R.id.frame_layout_3);
        frameLayouts[3] = (FrameLayout) findViewById(R.id.frame_layout_4);
        frameLayouts[4] = (FrameLayout) findViewById(R.id.frame_layout_5);

        textViews = new TextView[5];
        textViews[0] = (TextView) findViewById(R.id.label_1);
        textViews[1] = (TextView) findViewById(R.id.label_2);
        textViews[2] = (TextView) findViewById(R.id.label_3);
        textViews[3] = (TextView) findViewById(R.id.label_4);
        textViews[4] = (TextView) findViewById(R.id.label_5);

        dummys = new TextView[5];
        dummys[0] = (TextView) findViewById(R.id.dummy_1);
        dummys[1] = (TextView) findViewById(R.id.dummy_2);
        dummys[2] = (TextView) findViewById(R.id.dummy_3);
        dummys[3] = (TextView) findViewById(R.id.dummy_4);
        dummys[4] = (TextView) findViewById(R.id.dummy_5);

        // Get font from assets folder
        Typeface tf = Typeface.createFromAsset(getAssets(),"font.otf");
        for(int i =0; i< 5; i++){
            // Set font to the textviews
            textViews[i].setTypeface(tf);
            dummys[i].setTypeface(tf);
            // Set white shadow behind the textviews
            textViews[i].setShadowLayer(80, 0, 0, Color.WHITE);
            // Add stroke effect
            dummys[i].getPaint().setStrokeWidth(3);
            dummys[i].getPaint().setStyle(Paint.Style.STROKE);
        }
        // List All Activity Selection
        frameLayouts[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()){
                    Intent intent = new Intent(MainActivity.this, ListAllBooks.class);
                    startActivity(intent);
                }
            }
        });

        // Return a borrowed book Selection
        frameLayouts[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()) {
                    try {
                        // Start a background task to fetch all the pins of the books in the library
                        // After fetching the data, BorrowedBookAlert function is called from the
                        //      onPostExecute of the backgroundtask
                        com.example.haytham.eecereads.Main_Activity.BackgroundTask backgroundTask
                                = new com.example.haytham.eecereads.Main_Activity.BackgroundTask(MainActivity.this, 0);
                        backgroundTask.execute("get_all_pins");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // Add a new book Selection
        frameLayouts[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()) {
                    try {
                        // Start a background task to fetch the max pin of the books in the library
                        // After fetching the data, AddBookAlert function is called from the
                        //      onPostExecute of the backgroundtask
                        com.example.haytham.eecereads.Main_Activity.BackgroundTask backgroundTask
                                = new com.example.haytham.eecereads.Main_Activity.BackgroundTask(MainActivity.this, 2);
                        backgroundTask.execute("get_pin");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // Request a new book Selection
        frameLayouts[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Define the layout of the alert box programmatically
                //  1. Add 3 edit texts and set the hints
                final EditText editText_title = new EditText(MainActivity.this);
                editText_title.setHint("Book Title");
                final EditText editText_name = new EditText(MainActivity.this);
                editText_name.setHint("Author Name");
                final EditText editText_type = new EditText(MainActivity.this);
                editText_type.setHint("Type");

                //  2. Define a linear layout holding the edit texts
                LinearLayout parent = new LinearLayout(getApplicationContext());
                parent.setLayoutParams(new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));
                parent.setOrientation(LinearLayout.VERTICAL);
                parent.addView(editText_title);
                parent.addView(editText_name);
                parent.addView(editText_type);

                // Define a sweet alert dialog to inflate the defined linearlayout
                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.NORMAL_TYPE)
                        .setTitleText("Request a book")
                        .setConfirmText("Request Book")
                        .setCustomView(parent) // Inflate the defined linearlayout
                        .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                // Get the entered data and remove all illegal characters
                                String name  = editText_name .getText().toString().replaceAll("[^a-zA-Z0-9 ]","");
                                String title = editText_title.getText().toString().replaceAll("[^a-zA-Z0-9 ]","");
                                String type  = editText_type .getText().toString().replaceAll("[^a-zA-Z0-9 ]","");
                                if (Objects.equals(name, "") || Objects.equals(title, "") || Objects.equals(type, "")) {
                                    // Make sure there is no blank data
                                    Toast.makeText(getApplicationContext(), "Error : All fields are required", Toast.LENGTH_LONG).show();
                                } else {
                                    if(isNetworkAvailable()) {
                                        try {
                                            // Execute a background task that sends the gathered data to the server
                                            com.example.haytham.eecereads.Main_Activity.BackgroundTask backgroundTask
                                                    = new com.example.haytham.eecereads.Main_Activity.BackgroundTask(MainActivity.this, 4);
                                            backgroundTask.execute("request", title, name, type);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        sDialog.dismissWithAnimation();
                                    }
                                }
                            }
                        })
                        .show();
            }
        });

        // Feedback Selection
        frameLayouts[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Define the layout of the alert box programmatically
                //  Add an edit text and set its hint
                final EditText editText_feedback = new EditText(MainActivity.this);
                editText_feedback.setHint("Feedback");
                editText_feedback.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.NORMAL_TYPE)
                            .setTitleText("Your opinion matters to us. Please provide us with what " +
                                    "you like about the application and what needs " +
                                    "improvement. Drop us some new ideas as well if you like.")
                            .setConfirmText("Send Feedback")
                            .setCustomView(editText_feedback)
                            .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    // Get the entered data and remove all illegal characters
                                    String feedback = editText_feedback.getText().toString().replaceAll("[^a-zA-Z0-9 ]","");
                                    if (Objects.equals(feedback, "")) {
                                        // Make sure there is no blank data
                                        Toast.makeText(getApplicationContext(), "Error : All fields are required", Toast.LENGTH_LONG).show();
                                    } else {
                                        if(isNetworkAvailable()) {
                                            try {
                                                // Execute a background task that sends the feedback to the server
                                                com.example.haytham.eecereads.Main_Activity.BackgroundTask backgroundTask
                                                        = new com.example.haytham.eecereads.Main_Activity.BackgroundTask(MainActivity.this, 5);
                                                backgroundTask.execute("feedback", feedback);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            sDialog.dismissWithAnimation();
                                        }
                                    }
                                }
                            })
                            .show();
            }
        });
    }

    /**
     * @brief Parses the JSON string and starts a dialog for adding a new book
     * @param result Data from server in JSON format
     */
    public void AddBookAlert(String result){
        JSONArray jsonArray;
        JSONObject jsonObject;
        int max_pin = 0; // holds the max_pin from the parsed JSON string
        try {
            // Get the JSON of server_response array
            jsonObject = new JSONObject(result);
            jsonArray = jsonObject.getJSONArray("server_response");
            int count = 0;
            while (count < jsonArray.length()) {
                // Get the pin only
                JSONObject jsonObjectTemp = jsonArray.getJSONObject(count);
                max_pin = Integer.valueOf(jsonObjectTemp.getString("Pin"));
                count++;
            }
            // Define the layout of the alert box programmatically
            //  1. Add 4 edit texts and set the hints
            final EditText editText_title = new EditText(MainActivity.this);
            editText_title.setHint("Book Title");
            final EditText editText_name  = new EditText(MainActivity.this);
            editText_name.setHint("Author Name");
            final EditText editText_type  = new EditText(MainActivity.this);
            editText_type.setHint("Type");
            final EditText editText_pin   = new EditText(MainActivity.this);
            editText_pin.setHint("Pin Code");
            //  2. Add a textview that shows the pin that should be written
            final TextView textView_pin = new TextView(MainActivity.this);
            max_pin = max_pin+2;
            textView_pin.setText("Please write this pin on the first page of yours : "+max_pin);
            //  3. Define a linearlayout holding the previous views
            LinearLayout parent = new LinearLayout(getApplicationContext());
            parent.setLayoutParams(new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));
            parent.setOrientation(LinearLayout.VERTICAL);
            parent.addView(editText_title);
            parent.addView(editText_name);
            parent.addView(editText_type);
            parent.addView(textView_pin);
            parent.addView(editText_pin);

            final int finalMax_pin = max_pin;
            // Show a dialog box that inflates the linear layout
            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText("Add a new book")
                    .setContentText("Return Book")
                    .setConfirmText("Add Book")
                    .setCustomView(parent) // inflate the linearlayout
                    .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            // Get entered data and remove all illegal characters
                            String name  = editText_name .getText().toString().replaceAll("[^a-zA-Z0-9 ]","");
                            String title = editText_title.getText().toString().replaceAll("[^a-zA-Z0-9 ]","");
                            String type  = editText_type .getText().toString().replaceAll("[^a-zA-Z0-9 ]","");
                            String pin   = editText_pin  .getText().toString().replaceAll("[^a-zA-Z0-9 ]","");
                            int temp_pin = finalMax_pin;
                            if(Objects.equals(name, "") || Objects.equals(title, "") || Objects.equals(type, "") || Objects.equals(pin, "")){
                                // All fields are required
                                Toast.makeText(getApplicationContext(),"Error : All fields are required",Toast.LENGTH_LONG).show();
                            }
                            else if(android.text.TextUtils.isDigitsOnly(pin)){
                                if(Integer.valueOf(pin) != temp_pin){
                                    // Make sure both pins match
                                    Toast.makeText(getApplicationContext(),"Error : Both pins should match",Toast.LENGTH_LONG).show();
                                }
                                else {
                                    if (isNetworkAvailable()) {
                                        try {
                                            // Execute a background task that adds the new book to the server
                                            com.example.haytham.eecereads.Main_Activity.BackgroundTask backgroundTask
                                                    = new com.example.haytham.eecereads.Main_Activity.BackgroundTask(MainActivity.this, 3);
                                            backgroundTask.execute("add_new", title, name, type, pin);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        Toast.makeText(getApplicationContext(), "Book Added", Toast.LENGTH_LONG).show();
                                        sDialog.dismissWithAnimation();
                                    }
                                }
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Error : Pin contains illegal characters",Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .show();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * @brief Parses the JSON String and views a dialog for the user
     * @param result JSON String that is going to be parsed
     */
    public void BorrowedBookAlert(String result){
        JSONArray jsonArray;
        JSONObject jsonObject;
        final List<String> list_all_pins = new ArrayList<>(); // List holding all the book pins
        try {
            // Get the JSON array of server_response
            jsonObject = new JSONObject(result);
            jsonArray = jsonObject.getJSONArray("server_response");
            int count = 0;
            while (count < jsonArray.length()) {
                JSONObject jsonObjectTemp = jsonArray.getJSONObject(count);
                // Add pins to the list
                list_all_pins.add(jsonObjectTemp.getString("Pin"));
                count++;
            }

            // Define an edit text to gather the pin of the user
            final EditText editText_pin = new EditText(MainActivity.this);
            editText_pin.setHint("Pin Code");
            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText("Enter pin code found on the first page")
                    .setConfirmText("Return Book")
                    .setCustomView(editText_pin)
                    .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            // remove all illegal characters
                            String pin = editText_pin.getText().toString().replaceAll("[^a-zA-Z0-9 ]","");
                            if(Objects.equals(pin, "")){
                                Toast.makeText(getApplicationContext(),"Error : All fields are required",Toast.LENGTH_LONG).show();
                            }
                            else{
                                if(list_all_pins.indexOf(pin) == -1){
                                    Toast.makeText(getApplicationContext(),"Error : No such pin exists",Toast.LENGTH_LONG).show();
                                }
                                else {
                                    if (isNetworkAvailable()) {
                                        try {
                                            // Execute a background task that returns the book to the server
                                            com.example.haytham.eecereads.Main_Activity.BackgroundTask backgroundTask
                                                    = new com.example.haytham.eecereads.Main_Activity.BackgroundTask(MainActivity.this, 1);
                                            backgroundTask.execute("return_with_pin", pin);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        sDialog.dismissWithAnimation();
                                    }
                                }
                            }
                        }
                    })
                    .show();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * @brief checks the network connectivity
     * @return True if network is available, false otherwise
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if(!(activeNetworkInfo != null && activeNetworkInfo.isConnected())){
            Toast.makeText(getApplicationContext(),"Please Check Your Internet Connectivity",Toast.LENGTH_LONG).show();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
