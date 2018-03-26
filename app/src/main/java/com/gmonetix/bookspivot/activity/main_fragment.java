package com.gmonetix.bookspivot.activity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmonetix.bookspivot.R;
import com.gmonetix.bookspivot.util.SharedPrefManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class main_fragment extends AppCompatActivity  {
    int a;
    int b;
    // ...
    Context mContext = this;

    SharedPrefManager sharedPrefManager;
    private TextView mFullNameTextView, mEmailTextView;
    private ImageView mProfileImageView;
    private String mUsername, mEmail;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;


    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 2;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return firstfragment.newInstance(0, "Page # 1");
                case 1: // Fragment # 0 - This will show SecondFragment
                    return SecondFragment.newInstance(1, "Page # 2");
               // case 2: // Fragment # 0 - This will show ThirdFragment
                 //   return ThirdFragment.newInstance(2, "Page # 3");

                default:
                    return null;
            }
        }


        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {

                return "Books"                 ;
            } else
                if (position == 1) {
                    return "Credit";
                }
                return null;

        }}





    FragmentPagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //   navigationView.setNavigationItemSelectedListener(this);




    }

}
