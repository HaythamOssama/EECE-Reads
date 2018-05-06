package com.example.haytham.eecereads.List_All_Books;

import android.app.Activity;
import android.content.Context;
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
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Objects;

/**
 * Created by haytham on 29/04/18.
 *  = Serves the requests of the List all books only.
 *  = All the data is encoded in UTF-8
 *  = This class doesn't invoke  a loading dialog.
 *  = Same comments as BackgroundTask in Main_Activity folder
 */

public class BackgroundTaskNoDialog extends AsyncTask<String,Void,String> {

    private Context context;
    private FoundFragment foundFragment=null;
    private BorrowedFragment borrowedFragment=null;
    private RequestedFragment requestedFragment = null;
    private String title;
    private String name;
    private String pin;

    BackgroundTaskNoDialog(Activity context, FoundFragment foundFragment){
        this.context=context;
        this.foundFragment =foundFragment;
    }

    BackgroundTaskNoDialog(Activity context, BorrowedFragment borrowedFragment){
        this.context=context;
        this.borrowedFragment =borrowedFragment;
    }
    BackgroundTaskNoDialog(Activity context, RequestedFragment requestedFragment){
        this.context=context;
        this.requestedFragment =requestedFragment;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String fetch_found_url  = "http://ekra2.000webhostapp.com/fetch_found.php";
        String fetch_borrowed_url  = "http://ekra2.000webhostapp.com/fetch_borrowed.php";
        String fetch_requested_url  = "http://ekra2.000webhostapp.com/fetch_requested.php";
        String return_url  = "http://ekra2.000webhostapp.com/return.php";
        String borrow_url  = "http://ekra2.000webhostapp.com/borrow.php";
        String respond_url = "http://ekra2.000webhostapp.com/respond_to_request.php";
        String get_pin_url = "http://ekra2.000webhostapp.com/get_pin.php";

        if(params[0].equals("fetch_found") || params[0].equals("fetch_borrowed") || params[0].equals("fetch_requested"))
        {
            String json_data;
            try {
                URL url = null;
                if(params[0].equals("fetch_found")){
                    url = new URL(fetch_found_url);
                }
                else if(params[0].equals("fetch_borrowed")){
                    url = new URL(fetch_borrowed_url);
                }
                else if(params[0].equals("fetch_requested")){
                    url = new URL(fetch_requested_url);
                }
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

                String result = stringBuilder.toString().trim();
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else if(params[0].equals("return")){
            String title = params[1];
            String name  = params[2];
            String pin   = params[3];
            try {
                URL url = new URL(return_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String encoded_data =
                        URLEncoder.encode("Title","UTF-8") + "="
                                +   URLEncoder.encode(title,"UTF-8") +"&"
                                +   URLEncoder.encode("Name","UTF-8") + "="
                                +   URLEncoder.encode(name,"UTF-8")+"&"
                                +   URLEncoder.encode("Status","UTF-8") + "="
                                +   URLEncoder.encode("2","UTF-8")+"&"
                                +   URLEncoder.encode("Pin","UTF-8") + "="
                                +   URLEncoder.encode(pin,"UTF-8");
                bufferedWriter.write(encoded_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                /* Get response from server */
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String returnValue = "Return "+bufferedReader.readLine();
                inputStream.close();
                return returnValue;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(params[0].equals("borrow")){
            title = params[1];
            name  = params[2];
            pin   = params[3];
            try {
                URL url = new URL(borrow_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String encoded_data =
                        URLEncoder.encode("Title","UTF-8") + "="
                                +   URLEncoder.encode(title,"UTF-8") +"&"
                                +   URLEncoder.encode("Name","UTF-8") + "="
                                +   URLEncoder.encode(name,"UTF-8")+"&"
                                +   URLEncoder.encode("Status","UTF-8") + "="
                                +   URLEncoder.encode("1","UTF-8")+"&"
                                +   URLEncoder.encode("Pin","UTF-8") + "="
                                +   URLEncoder.encode(pin,"UTF-8");
                bufferedWriter.write(encoded_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                /* Get response from server */
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String returnValue = "Borrow "+bufferedReader.readLine();
                inputStream.close();
                return returnValue;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else if(params[0].equals("respond_to_request")){
            title = params[1];
            name  = params[2];
            pin   = params[3];
            try {
                URL url = new URL(respond_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String encoded_data =
                        URLEncoder.encode("Title","UTF-8") + "="
                                +   URLEncoder.encode(title,"UTF-8") +"&"
                                +   URLEncoder.encode("Name","UTF-8") + "="
                                +   URLEncoder.encode(name,"UTF-8")+"&"
                                +   URLEncoder.encode("Status","UTF-8") + "="
                                +   URLEncoder.encode("3","UTF-8")+"&"
                                +   URLEncoder.encode("Pin","UTF-8") + "="
                                +   URLEncoder.encode(pin,"UTF-8");
                bufferedWriter.write(encoded_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                /* Get response from server */
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String returnValue = "Add "+bufferedReader.readLine();
                inputStream.close();
                return returnValue;
            } catch (MalformedURLException e) {
                e.printStackTrace();
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

        if(foundFragment!=null){
            if(Objects.equals(result, "Borrow Success")){
                Toast.makeText(foundFragment.getContext(),"Correct Pin : Book Borrowed",Toast.LENGTH_LONG).show();
            }
            else if(Objects.equals(result, "Borrow Failure")){
                Toast.makeText(foundFragment.getContext(),"Wrong Pin : Book not Borrowed",Toast.LENGTH_LONG).show();
            }
            else{
                foundFragment.fillWithData(result);
            }
        }
        else if (borrowedFragment!=null){
            if(Objects.equals(result, "Return Success")){
                Toast.makeText(borrowedFragment.getContext(),"Correct Pin : Book returned",Toast.LENGTH_LONG).show();
            }
            else if(Objects.equals(result, "Return Failure")){
                Toast.makeText(borrowedFragment.getContext(),"Wrong Pin : Book not returned",Toast.LENGTH_LONG).show();
            }
            else{
                borrowedFragment.fillWithData(result);
            }
        }
        else if(requestedFragment != null){
            if(Objects.equals(result, "Add Success")){
                Toast.makeText(requestedFragment.getContext(),"Correct Pin : Book added",Toast.LENGTH_LONG).show();
            }
            else if(Objects.equals(result, "Add Failure")){
                Toast.makeText(requestedFragment.getContext(),"Wrong Pin : Book not added",Toast.LENGTH_LONG).show();
            }
            else{
                requestedFragment.fillWithData(result);
            }
        }
    }
}
