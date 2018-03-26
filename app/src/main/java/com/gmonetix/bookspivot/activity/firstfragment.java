
package com.gmonetix.bookspivot.activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.SupportActivity;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gmonetix.bookspivot.R;
import com.gmonetix.bookspivot.model.Upload;
import com.gmonetix.bookspivot.util.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;


public class firstfragment extends Fragment {
    // Store instance variables
    private String title;
    private int page;

    DatabaseReference mDatabaseReference;
    ListView list;
    List<Upload> uploadList;
    String[] book_name = {
            "Book 1",
            "Book 2",
            "Book 3",
            // "Book 4",
            // "Book 5",
            // "Book 6",
            // "Book 7"
    } ;
    Integer[] imageId = {
            R.drawable.bglogin2,
            R.drawable.bglogin2,
            R.drawable.bglogin2,
            //  R.drawable.bglogin2,
            //  R.drawable.bglogin2,
            // R.drawable.bglogin2,
            // R.drawable.bglogin2

    };

    String[] book_upload = {
            "Uploaded on jan 10, 2017",
            "Uploaded on jan 10, 2017",
            "Uploaded on Aug 26, 2017",
            // "Uploaded on Aug 26, 2017",
            //"Uploaded on Aug 26, 2017",
            //"Uploaded on Aug 26, 2017",
            // "Uploaded on Aug 26, 2017"
    } ;

    String[] book_download = {
            "123 Downloads",
            "123 Downloads",
            "123 Downloads",
            //  "123 Downloads",
            //  "123 Downloads",
            //  "123 Downloads",
            //     "123 Downloads"
    } ;

    // newInstance constructor for creating fragment with arguments
    public static firstfragment newInstance(int page, String title) {
        firstfragment fragmentFirst = new firstfragment();
        Bundle args = new Bundle();
        //   args.putInt("someInt", page);
        args.putString("Title", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }


    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("Title");
        uploadList = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_firstfragment, container, false);
        FragmentList adapter = new
                FragmentList(getActivity(), book_name, imageId , book_upload, book_download);
        list=(ListView) view.findViewById(R.id.list1);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Upload upload = uploadList.get(position);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(upload.getUrl()));
                startActivity(intent);


                Toast.makeText(getActivity(), "You Clicked at " +book_name[+ position], Toast.LENGTH_SHORT).show();

            }
        });
//getting the database reference
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);

        //retrieving upload data from firebase database
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    uploadList.add(upload);
                }
                String[] uploads = new String[uploadList.size()];


                for (int i = 0; i < uploads.length; i++) {
                    uploads[i] = uploadList.get(i).getName();
                    //  uploads[i]=uploadList.get(i).getAuthor();
                    // uploads[i]=uploadList.get(i).getEdition();

                }
                //displaying it to list
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, uploads);
                list.setAdapter(adapter);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;



    }

// the following are default settings

}
