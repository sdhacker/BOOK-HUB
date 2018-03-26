package com.gmonetix.bookspivot.activity;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmonetix.bookspivot.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
private TextView profilename,names,emailid,emailverified;
private ImageView profilephoto;
    Context mContext = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        profilename = (TextView) findViewById(R.id.profile_name);
        names = (TextView) findViewById(R.id.name);
        emailid = (TextView) findViewById(R.id.email_id);
      //  emailverified = (TextView) findViewById(R.id.verified);
        profilephoto = (CircleImageView)findViewById(R.id.profile_photo);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();


            profilename.setText(name);
            emailid.setText(email);
            names.setText(name);
            Picasso.with(mContext)
                    .load(photoUrl)
                    .placeholder(android.R.drawable.sym_def_app_icon)
                    .error(android.R.drawable.sym_def_app_icon)
                    .into(profilephoto);
            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();
        }
    }
}
