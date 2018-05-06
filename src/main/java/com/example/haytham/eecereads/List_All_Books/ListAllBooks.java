package com.example.haytham.eecereads.List_All_Books;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import com.example.haytham.EECE_Reads.R;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

public class ListAllBooks extends AppCompatActivity {

    TabLayout tabLayout;                // Holds the 3 tabs
    private Toolbar toolbar;            // Defines the toolbar
    private Boolean firstTime = null;   // To view tutorial for the first time only

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remove the notification bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_list_all_books);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("EECE Reads");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        // Define the 3 tabs
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Existing Books"));
        tabLayout.addTab(tabLayout.newTab().setText("Borrowed Books"));
        tabLayout.addTab(tabLayout.newTab().setText("Requested Books"));
        tabLayout.setTabTextColors(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        tabLayout.setSelectedTabIndicatorColor((getResources().getColor(R.color.top)));

        // Clear all the lists to avoid duplicates
        new FoundFragment().foundList.clear();
        new BorrowedFragment().borrowedList.clear();
        new RequestedFragment().requestList.clear();

        // Bind the defined tabs to view
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        if (isFirstTime()) { // Show the tutorial for the first time only
            LinearLayout linearLayout = (LinearLayout)findViewById(R.id.dummy);
            new TapTargetSequence(this)
                    .targets(
                            TapTarget.forView((((ViewGroup) tabLayout.getChildAt(0)).getChildAt(0)), "Available Books Tab"
                                    , "Here you can find all the currently available books in the library. " +
                                            "Press on any item to borrow."),
                            TapTarget.forView((((ViewGroup) tabLayout.getChildAt(0)).getChildAt(1)), "Borrowed Books Tab",
                                    "Here you can find all the borrowed books. Press on any item to return the book"),
                            TapTarget.forView((((ViewGroup) tabLayout.getChildAt(0)).getChildAt(2)), "Requested Books Tab",
                                    "Here you can find all the requested books. Press on any item to respond to a request and submit the book"),
                            TapTarget.forView(findViewById(R.id.tab_layout), "Any book has a pin written on it's first page. " +
                                    "To interact with any of the aforementioned tabs, you need the correct pin for the selected book.")
                                    .transparentTarget(true)           // Specify whether the target is transparent (displays the content underneath)
                                    .tintTarget(true)    ,            // Whether to tint the target view's color
                            TapTarget.forView(linearLayout,"Swipe down to refresh the list anytime")
                    ).listener(new TapTargetSequence.Listener() {
                @Override
                public void onSequenceFinish() {
                }
                @Override
                public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                }
                @Override
                public void onSequenceCanceled(TapTarget lastTarget) {
                }
            }).start();
        }
    }

    /**
     * @brief to view the tutorial on the first launch only
     * @return true of first time, false otherwise
     */
    private boolean isFirstTime() {
        if (firstTime == null) {
            SharedPreferences mPreferences = this.getSharedPreferences("first_time", Context.MODE_PRIVATE);
            firstTime = mPreferences.getBoolean("firstTime", true);
            if (firstTime) {
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean("firstTime", false);
                editor.commit();
            }
        }
        return firstTime;
    }

}
