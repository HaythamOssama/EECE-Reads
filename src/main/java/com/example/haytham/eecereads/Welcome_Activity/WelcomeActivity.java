package com.example.haytham.eecereads.Welcome_Activity;
/**
 * Created by haytham on 26/04/18.
 *  - This class controls the welcome page shown at first launch only
 *  - Each tab consists of:
 *      = Image view + 2 Textview to describe the tab
 *      = Dots indicating the swiping progress
 *      = Next and skip buttons
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.haytham.eecereads.Main_Activity.MainActivity;
import com.example.haytham.EECE_Reads.R;

public class WelcomeActivity extends AppCompatActivity {

    private ViewPager viewPager;                   // holds the swiped tabs
    private MyViewPagerAdapter myViewPagerAdapter; // Adapter for the views located in the swiped tabs
    private LinearLayout dotsLayout;               // Linearlayout holding the content of every swiped tab
    private TextView[] dots;                       // Textview viewing the description of every swiped tab
    private int[] layouts;                         // Holds the layout views of all the swiped tabs
    private Button btnSkip, btnNext;               // Two buttons to go back and forth between the swiped tabs
    private PrefManager prefManager;               // Class controlling the first time launch

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remove the notification bar from the activity
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Checking for first time launch - before calling setContentView()
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }
        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }

        setContentView(R.layout.activity_welcome);

        viewPager  = (ViewPager)    findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip    = (Button)       findViewById(R.id.btn_skip);
        btnNext    = (Button)       findViewById(R.id.btn_next);


        // layouts of all welcome sliders
        layouts = new int[]{
                R.layout.welcome_slide1,
                R.layout.welcome_slide2,
                R.layout.welcome_slide3,
                R.layout.welcome_slide4};

        // adding bottom dots
        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        // Set the adapter to the viewpager to create the custom views
        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchHomeScreen();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page. If last page home screen will be launched.
                int current = getItem(+1);
                if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }
            }
        });
    }

    /**
     * @brief Controls the dots located at the bottom of the activity when the slider is swiped
     * @param currentPage current page / tab in the viewpager
     */
    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.arraydotactive);
        int[] colorsInactive = getResources().getIntArray(R.array.arraydotinactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }
        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    /**
     * @brief gets the next view in the viewpager
     * @param i item position
     * @return the view in the viewpager of index = i+1
     */
    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    /**
     * @brief launches the homescreen when the skip button / last view is pressed
     */
    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        finish();
    }

    //  Viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        /**
         * @brief Change the text of the buttons
         * @param position tab number
         */
        @Override
        public void onPageSelected(int position) {
            // Change the dots according to position
            addBottomDots(position);
            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                btnNext.setText(getString(R.string.start));
                btnSkip.setVisibility(View.GONE);
            } else {
                // still pages are left
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * View pager adapter class
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        /**
         * @brief Inflates the layout associated with the current tab
         * @param container Viewgroup of the swiped tabs
         * @param position current viewed tab
         * @return inflated view
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        /**
         * @brief gets the number of tabs
         * @return number of tabs
         */
        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        /**
         * @brief destroys the current viewed tab
         * @param container container holding all the views
         * @param position position of the current tab
         * @param object
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}