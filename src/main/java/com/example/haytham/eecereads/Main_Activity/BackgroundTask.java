package com.example.haytham.eecereads.Main_Activity;

import android.os.AsyncTask;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Objects;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by haytham on 22/04/18.
 *  = Serves the requests of the main activity only.
 *  = All the data is encoded in UTF-8
 *  = Status :
 *      - 1 >> Book Available for borrowing
 *      - 2 >> Borrowed Book
 *      - 3 >> Requested Book
 */

public class BackgroundTask extends AsyncTask<String,Void,String> {

    private MainActivity mainActivity=null;
    /**
     * 0 : get_all_pins    >> Frame [1]         >> return borrowed book
     * 1 : return_with_pin >> BorrowedBookAlert >> return borrowed book
     * 2 : get_pin         >> Frame [2]         >> Add New Book
     * 3 : add_new         >> AlertBookAlert    >> Add New Book
     * 4 : request         >> Frame [3]         >> Request a book
     * 5 : feedback        >> Frame [4]         >> Feedback
     * */
    private int choice;
    private SweetAlertDialog sweetAlertDialog; // Loading dialog

    BackgroundTask(MainActivity mainActivity,int choice){
        this.mainActivity =mainActivity;
        this.choice = choice;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        // Show the loading box for choices 0 and 2 only
        if(choice == 0 || choice == 2 ){
            sweetAlertDialog = new SweetAlertDialog(mainActivity, SweetAlertDialog.PROGRESS_TYPE);
            sweetAlertDialog.setTitle("Loading Please Wait");
            sweetAlertDialog.show();
        }
    }

    @Override
    protected String doInBackground(String... params) {
        String get_pin_url         = "http://ekra2.000webhostapp.com/get_pin.php";
        String add_new_url         = "http://ekra2.000webhostapp.com/add.php";
        String get_all_pins_url    = "http://ekra2.000webhostapp.com/get_all_pins.php";
        String return_with_pin_url = "http://ekra2.000webhostapp.com/return_with_pin.php";
        String feedback_url        = "http://ekra2.000webhostapp.com/feedback.php";

        // Gets the max pin from the server
        if(Objects.equals(params[0], "get_pin")){
            String json_data;
            try {
                URL url = new URL(get_pin_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                while ((json_data = bufferedReader.readLine()) != null) {
                    stringBuilder.append(json_data + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Adds a new book to the server
        else if(params[0].equals("add_new"))
        {
            String title = params[1];
            String name  = params[2];
            String type  = params[3];
            String pin   = params[4];
            try {
                URL url = new URL(add_new_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String encoded_data =
                        URLEncoder.encode("Title"   ,"UTF-8") + "="
                    +   URLEncoder.encode(title        ,"UTF-8") + "&"
                    +   URLEncoder.encode("Name"    ,"UTF-8") + "="
                    +   URLEncoder.encode(name         ,"UTF-8") + "&"
                    +   URLEncoder.encode("Type"    ,"UTF-8") + "="
                    +   URLEncoder.encode(type         ,"UTF-8") + "&"
                    +   URLEncoder.encode("Status"  ,"UTF-8") + "="
                    +   URLEncoder.encode("1"       ,"UTF-8") + "&"
                    +   URLEncoder.encode("Pin"     ,"UTF-8") + "="
                    +   URLEncoder.encode(pin          ,"UTF-8");

                bufferedWriter.write(encoded_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                // Get response from server
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String returnValue = bufferedReader.readLine();
                inputStream.close();
                return returnValue;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Get all pins from the server
        else if(Objects.equals(params[0], "get_all_pins")){
            String json_data;
            try {
                URL url = new URL(get_all_pins_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                while ((json_data = bufferedReader.readLine()) != null) {
                    stringBuilder.append(json_data + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Return book having a specific pin
        else if(params[0].equals("return_with_pin"))
        {
            String pin   = params[1];
            try {
                URL url = new URL(return_with_pin_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String encoded_data =
                                URLEncoder.encode("Pin","UTF-8") + "="
                            +   URLEncoder.encode(pin,"UTF-8");

                bufferedWriter.write(encoded_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                // Get response from server
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String returnValue = bufferedReader.readLine();
                inputStream.close();
                return returnValue;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Add a new request to the server
        else if(params[0].equals("request"))
        {
            String title   = params[1];
            String name    = params[2];
            String type    = params[3];
            try {
                URL url = new URL(add_new_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String encoded_data =
                        URLEncoder.encode("Title" ,"UTF-8") + "="
                    +   URLEncoder.encode(title      ,"UTF-8") + "&"
                    +   URLEncoder.encode("Name"  ,"UTF-8") + "="
                    +   URLEncoder.encode(name       ,"UTF-8") + "&"
                    +   URLEncoder.encode("Type"  ,"UTF-8") + "="
                    +   URLEncoder.encode(type       ,"UTF-8") + "&"
                    +   URLEncoder.encode("Status","UTF-8") + "="
                    +   URLEncoder.encode("3"     ,"UTF-8") + "&"
                    +   URLEncoder.encode("Pin"   ,"UTF-8") + "="
                    +   URLEncoder.encode("0"     ,"UTF-8");

                bufferedWriter.write(encoded_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                // Get response from server
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String returnValue = bufferedReader.readLine();
                inputStream.close();
                return returnValue;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Sends feedback to the server
        else if(params[0].equals("feedback"))
        {
            String feedback   = params[1];
            try {
                URL url = new URL(feedback_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String encoded_data =
                        URLEncoder.encode("Feedback","UTF-8") + "="
                    +   URLEncoder.encode(feedback     ,"UTF-8");

                bufferedWriter.write(encoded_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                /* Get response from server */
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String returnValue = bufferedReader.readLine();
                inputStream.close();
                return returnValue;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values){
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);
        if(choice == 0){
            sweetAlertDialog.dismissWithAnimation();
            mainActivity.BorrowedBookAlert(result);
        }
        else if(choice == 1){
            if(Objects.equals(result, "Success")){
                Toast.makeText(mainActivity,"Pin found : Book returned",Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(mainActivity,"Pin not found : Book not returned",Toast.LENGTH_LONG).show();
            }
        }
        else if(choice == 2){
            sweetAlertDialog.dismissWithAnimation();
            mainActivity.AddBookAlert(result);
        }
        else if(choice == 3){
            if(Objects.equals(result, "Success")){
                Toast.makeText(mainActivity,"Success : Request sent",Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(mainActivity,"Error : Request not sent",Toast.LENGTH_LONG).show();
            }

        }
        else if(choice == 4){
            if(Objects.equals(result, "Success")){
                Toast.makeText(mainActivity,"Success : Book Added.",Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(mainActivity,"Error : Book not added",Toast.LENGTH_LONG).show();
            }
        }
        else if(choice == 5){
            if(Objects.equals(result, "Success")){
                Toast.makeText(mainActivity,"Feedback sent. Thank you.",Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(mainActivity,"Error occurred",Toast.LENGTH_LONG).show();
            }
        }
    }
}


