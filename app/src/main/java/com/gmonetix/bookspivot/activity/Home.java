
package com.gmonetix.bookspivot.activity;

/**
 * Created by SHUBHAM on 07-01-2018.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import de.hdodenhof.circleimageview.CircleImageView;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;

import android.support.design.widget.NavigationView;
import android.widget.Toast;

import com.gmonetix.bookspivot.App;
import com.gmonetix.bookspivot.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.gmonetix.bookspivot.util.SharedPrefManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


// This class is a simple activity with NavigationDrawer
// we get data stored in sharedPrefference and display on the header view of the NavigationDrawer
public class Home extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener{

    Context mContext = this;

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private TextView mFullNameTextView, mEmailTextView,fullname;
    private ImageView mProfileImageView,pp;
    private String mUsername, mEmail ,uname;
    SharedPrefManager sharedPrefManager;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initNavigationDrawer();

        View header = navigationView.getHeaderView(0);

        FragmentPagerAdapter adapterViewPager;

        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new main_fragment.MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);


        mFullNameTextView = (TextView) header.findViewById(R.id.nav_user_name);
        mProfileImageView = (CircleImageView) header.findViewById(R.id.nav_profile_image);
        fullname = (TextView)findViewById(R.id.my_name);
        pp = (CircleImageView)findViewById(R.id.account_bg);
        // create an object of sharedPreferenceManager and get stored user data
        sharedPrefManager = new SharedPrefManager(mContext);
        mUsername = sharedPrefManager.getName();
        uname = sharedPrefManager.getName();
        String uri = sharedPrefManager.getPhoto();
        Uri mPhotoUri = Uri.parse(uri);

        //Set data gotten from SharedPreference to the Navigation Header view
        mFullNameTextView.setText(mUsername);
        fullname.setText(uname);

        Picasso.with(mContext)
                .load(mPhotoUri)
                .placeholder(android.R.drawable.sym_def_app_icon)
                .error(android.R.drawable.sym_def_app_icon)
                .into(mProfileImageView);
        Picasso.with(mContext)
                .load(mPhotoUri)
                .placeholder(android.R.drawable.sym_def_app_icon)
                .error(android.R.drawable.sym_def_app_icon)
                .into(pp);
        configureSignIn();




    }


///////////////////////////////after extra
public void onBackPressed() {
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
        drawer.closeDrawer(GravityCompat.START);
    } else {
        super.onBackPressed();
    }
}
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")

    // Initialize and add Listener to NavigationDrawer
    public void initNavigationDrawer(){

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                int id = item.getItemId();

                switch (id){
                    case R.id.nav_home:
                        Toast.makeText(getApplicationContext(),"Home",Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_profile:
                        Intent i = new Intent(Home.this, ProfileActivity.class);
                        startActivity(i);
                    break;
                    case R.id.nav_upload:
                        Intent intent = new Intent(Home.this, UploadActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_settings:
                        startActivity(new Intent(Home.this,SettingsActivity.class));
                        break;
                    case R.id.nav_logout:
                        signOut();
                        drawerLayout.closeDrawers();
                        break;
                /*try{
                    Reader reader = new Reader(); // Max string length for the current page.
                    reader.setIsIncludingTextContent(true); // Optional, to return the tags-excluded version.
                    reader.setFullContent("/sdcard/ebook.epub"); // Must call before readSection.

                    BookSection bookSection = reader.readSection(5);
                    String sectionContent = bookSection.getSectionContent(); // Returns content as html.
                    String sectionTextContent = bookSection.getSectionTextContent(); // Excludes html tags.
                    Log.e("TAG",sectionContent);
                    Log.e("TAG",sectionTextContent);
                    WebView webView = (WebView) findViewById(R.id.nav_website);
                    webView.loadData(sectionContent,"text/html","UTF-8");
                } catch (Exception e) {
                    Log.e("TAG","error occurred");
                }*/
                /*
                        AssetManager assetManager = getAssets();
                        try {
                            // find InputStream for book
                            InputStream epubInputStream = assetManager
                                    .open("ebook.epub");

                            // Load Book from inputStream
                            Book book = (new EpubReader()).readEpub(epubInputStream);

                            // Log the book's authors
                            Log.e("epublib", "author(s): " + book.getMetadata().getAuthors());

                            // Log the book's title
                            Log.e("epublib", "title: " + book.getTitle());

                            // Log the book's coverimage property
                            Bitmap coverImage = BitmapFactory.decodeStream(book.getCoverImage()
                                    .getInputStream());
                            Log.e("epublib", "Coverimage is " + coverImage.getWidth() + " by "
                                    + coverImage.getHeight() + " pixels");

                            // Log the tale of contents
                            logTableOfContents(book.getTableOfContents().getTocReferences(), 0);
                        } catch (IOException e) {
                            Log.e("epublib", e.getMessage());
                        }
                        break;*/

                }
                return false;
            }
        });

        //set up navigation drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

//////////////////////extra
    private void logTableOfContents(List<TOCReference> tocReferences, int depth) {
        if (tocReferences == null) {
            return;
        }
        for (TOCReference tocReference : tocReferences) {
            StringBuilder tocString = new StringBuilder();
            for (int i = 0; i < depth; i++) {
                tocString.append("\t");
            }
            tocString.append(tocReference.getTitle());
            Log.e("epublib", tocString.toString());

            logTableOfContents(tocReference.getChildren(), depth + 1);
        }
    }

    public File getFileFromAssets(String fileName) {

        File file = new File(getCacheDir() + "/" + fileName);

        if (!file.exists()) try {

            InputStream is = getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(buffer);
            fos.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return file;
    }

    ////////////








    // This method configures Google SignIn
    public void configureSignIn(){
// Configure sign-in to request the user's basic profile like name and email
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();
        mGoogleApiClient.connect();
    }

    //method to logout
    private void signOut(){
        new SharedPrefManager(mContext).clear();        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        Intent intent = new Intent(Home.this,LoginActivity.class);
                        startActivity(intent);
                    }
                }
        );
    }



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























}
