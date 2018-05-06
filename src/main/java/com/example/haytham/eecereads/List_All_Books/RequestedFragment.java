package com.example.haytham.eecereads.List_All_Books;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.haytham.EECE_Reads.R;
import com.yalantis.phoenix.PullToRefreshView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Created by haytham on 22/04/18.
 */

public class RequestedFragment extends Fragment implements SearchView.OnQueryTextListener{

    public static RecyclerView recyclerView;                    // Recycler view holding the borrowed books
    static BooksAdapter mAdapter;                               // Adapter of the previous recycler view
    public static List<Books> requestList = new ArrayList<>();  // List of requested books
    static private LinearLayoutManager mLayoutManager;          // Manager for the recyclerview
    private ImageView nobooks_image;                            // Image that is set to visible when no books are found
    private String maxPin="0";                                  // Max pin fetched from the server

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_requested, container, false);
        recyclerView  = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        nobooks_image = (ImageView) rootView.findViewById(R.id.nobooks3);

        // Pull to refresh view
        final PullToRefreshView mPullToRefreshView = (PullToRefreshView) rootView.findViewById(R.id.pull_to_refresh3);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefreshView.setRefreshing(false);
                        if(isNetworkAvailable()) {
                            // Clear the requestList to avoid any duplicates
                            requestList.clear();
                            try {
                                // Execute a background task that fetches the requested books from the server
                                //  OnPostExecute calls fillWithData function
                                BackgroundTask backgroundTask = new BackgroundTask(getActivity(), RequestedFragment.this);
                                backgroundTask.execute("fetch_requested");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, 1000);

            }
        });

        // For first time the fragment is viewed
        if(requestList.isEmpty()){
            if(isNetworkAvailable()) {
                try {
                    // Execute a background task that fetches the requested books from the server
                    //  OnPostExecute calls fillWithData function
                    BackgroundTask backgroundTask = new BackgroundTask(getActivity(), this);
                    backgroundTask.execute("fetch_requested");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else{
            mAdapter = new BooksAdapter(requestList,RequestedFragment.this);
            recyclerView.setAdapter(mAdapter);
        }

        // Set the manager and the adapter to the recyclerview
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        setHasOptionsMenu(true); // Bind the actionbar to the fragment
        return rootView;
    }

    /**
     * @brief parses the passed string and sets the recyclerview items
     * @param result JSON String that will be parsed
     */
    public void fillWithData(String result) {
        /* Parse the received JSON data */
        JSONArray jsonArray;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(result);
            jsonArray = jsonObject.getJSONArray("server_response");
            String title="", name="", type="", status="", pin="";
            int count = 0;
            while (count < jsonArray.length()) {
                JSONObject jsonObjectTemp = jsonArray.getJSONObject(count);
                title  = jsonObjectTemp.getString("Title");
                name   = jsonObjectTemp.getString("Name");
                type   = jsonObjectTemp.getString("Type");
                status = jsonObjectTemp.getString("Status");
                pin    = jsonObjectTemp.getString("Pin");
                Books Books = new Books(title, name, type,status,pin);
                if(Objects.equals(title, "null") && Objects.equals(name, "null")
                        && Objects.equals(type, "null") && Objects.equals(status, "null")
                        && Objects.equals(pin,"null")){
                }
                else{
                    requestList.add(Books);
                }
                count++;
            }

            // If no books are found, display 404 error
            if(count == 0 ||
                    (count == 1 && Objects.equals(title, "null") && Objects.equals(name, "null")
                    && Objects.equals(type, "null") && Objects.equals(status, "null")
                    && Objects.equals(pin,"null"))){
                nobooks_image.setVisibility(View.VISIBLE);
            }
            else{
                nobooks_image.setVisibility(View.GONE);
                maxPin = requestList.get(requestList.size()-1).getPin();
                requestList.remove(requestList.size()-1);
            }
            if(requestList.isEmpty()){
                nobooks_image.setVisibility(View.VISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // set the recyclerview adapter
        mAdapter = new BooksAdapter(requestList,RequestedFragment.this);
        recyclerView.setAdapter(mAdapter);

    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

        // Define the search view for searching in the recyclerview
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        // Do something when collapsed
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        // Do something when expanded
                        return true; // Return true to expand action view
                    }
                });

        // Arrange buttons, each implements a Comparator and re-organizes the views
        final MenuItem arrange_alpha = menu.findItem(R.id.menu_item_arrange_alpha);
        arrange_alpha.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Comparator<Books> rankOrder =  new Comparator<Books>() {
                    public int compare(Books s1, Books s2) {
                        return s1.getTitle().compareTo(s2.getTitle());
                    }
                };
                Collections.sort(requestList, rankOrder);
                mAdapter = new BooksAdapter(requestList,RequestedFragment.this);
                recyclerView.setAdapter(mAdapter);
                return true;
            }
        });

        final MenuItem arrange_author = menu.findItem(R.id.menu_item_arrange_author);
        arrange_author.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Comparator<Books> rankOrder =  new Comparator<Books>() {
                    public int compare(Books s1, Books s2) {
                        return s1.getName().compareTo(s2.getName());
                    }
                };
                Collections.sort(requestList, rankOrder);
                mAdapter = new BooksAdapter(requestList,RequestedFragment.this);
                recyclerView.setAdapter(mAdapter);
                return true;
            }
        });

        final MenuItem arrange_type = menu.findItem(R.id.menu_item_arrange_type);
        arrange_type.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Comparator<Books> rankOrder =  new Comparator<Books>() {
                    public int compare(Books s1, Books s2) {
                        return s1.getType().compareTo(s2.getType());
                    }
                };
                Collections.sort(requestList, rankOrder);
                mAdapter = new BooksAdapter(requestList,RequestedFragment.this);
                recyclerView.setAdapter(mAdapter);
                return true;
            }
        });

    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // filter the recycler views out when searching occurs
        final List<Books> filteredModelList = filter(requestList, newText);
        mAdapter.setFilter(filteredModelList);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private List<Books> filter(List<Books> models, String query) {
        query = query.toLowerCase();
        final List<Books> filteredModelList = new ArrayList<>();
        for (Books model : models) {
            final String text = model.getTitle().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    /**
     * @brief Called from the BooksAdapter when an item is selected
     * @param title title of the book
     * @param name author of the book
     */
    public void OpenDialog(String title, String name){
        FragmentManager fm = getFragmentManager();
        DialogAlert dialogFragment = new DialogAlert (getContext(),RequestedFragment.this,
                title,name,maxPin);
        dialogFragment.show(fm, "Sample Fragment");
        fm.executePendingTransactions();
        dialogFragment.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (isNetworkAvailable()) {
                    requestList.clear();
                    try {
                        BackgroundTask backgroundTask = new BackgroundTask(getActivity(), RequestedFragment.this);
                        backgroundTask.execute("fetch_requested");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * @brief checks the network connectivity
     * @return true if network is available, false otherwise
     */
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