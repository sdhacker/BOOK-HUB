package com.gmonetix.bookspivot.activity;



import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gmonetix.bookspivot.R;
import com.gmonetix.bookspivot.model.Upload;
import com.gmonetix.bookspivot.util.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener {

    //this is the pic pdf code used in file chooser
    final static int PICK_PDF_CODE = 2342;

    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 123;
    private Uri filePath;
    //these are the views
    TextView textViewStatus;
    EditText  bookname,author,edition;
    ProgressBar progressBar;

    //the firebase objects for storage and database
    StorageReference mStorageReference;
    DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);


        //getting firebase objects
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);

        //getting the views
       textViewStatus = (TextView) findViewById(R.id.textViewStatus);
        bookname = (EditText) findViewById(R.id.input_bookname);
       // author = (EditText) findViewById(R.id.input_author);
       // edition = (EditText) findViewById(R.id.input_version);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        //attaching listeners to views
        findViewById(R.id.buttonupload).setOnClickListener(this);
       // findViewById(R.id.buttonchoose).setOnClickListener(this);
       // findViewById(R.id.textViewUploads).setOnClickListener(this);
    }

    //this function will get the pdf from the storage
    private void getPDF() {
        //for greater than lolipop versions we need the permissions asked on runtime
        //so if the permission is not available user will go to the screen to allow storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermission();
        }
            //creating an intent for file chooser
            Intent intent = new Intent();
            intent.setType("application/pdf");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Choose file"), PICK_PDF_CODE);
        }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //when the user choses the file
        if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //if a file is selected
            if (data.getData() != null) {
                //uploading the file
                uploadFile(data.getData());

                Uri uri = data.getData();
                Log.e("TAG", uri.toString());
//            File myFile = new File(uri.toString());
//            Log.e("TAG", String.valueOf(myFile.length()));
                generateImageFromPdfUsingFD(uri,"bookfirstpage.png");

                //  Intent formIntent = new Intent(this, BookDetails.class);
                //startActivity(formIntent);
                uploadFile(data.getData());
            }else{
                Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //Requesting permission
    private void requestStoragePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission

            Toast.makeText(UploadActivity.this, "Read External Storage permission allows us to do upload file. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            //And finally ask for the permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if(requestCode == 1)
                //If permission is granted
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Displaying a toast
                    Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
                } else {
                    //Displaying another toast if permission is not granted
                    Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
                }

        }
    }
    private void generateImageFromPdfUsingFD(Uri pdfUri, String name) {
        int pageNumber = 0;
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, pdfUri));
        PdfiumCore pdfiumCore = new PdfiumCore(this);
        try {
            ParcelFileDescriptor fd = getContentResolver().openFileDescriptor(pdfUri, "r");
            PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
            pdfiumCore.openPage(pdfDocument, pageNumber);
            int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber);
            int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber);
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, width, height);
            FileOutputStream out = null;
            try {

                String path = Environment.getExternalStorageDirectory() + "/" + "Book Hub/";
// Create the parent path
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String fullName = path + name;
                File file = new File(fullName);
//                File file = new File(Environment.getExternalStorageDirectory() + "/" + "KhoborKagoj", name);
                out = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG, 15, out); // bmp is your Bitmap instance
            } catch (Exception e) {
                Log.e("TAG catch1", "" + e.getMessage());
            } finally {
                try {
                    if (out != null)
                        out.close();
                } catch (Exception e) {
//todo with exception
                    Log.e("TAG Catch2", "" + e.getMessage());
                }
            }
            pdfiumCore.closeDocument(pdfDocument); // important!
        } catch (Exception e) {
//todo with exception
            Log.e("TAG catch 3", "" + e.getMessage());
        }
    }

    //this method is uploading the file
    //the code is same as the previous tutorial
    //so we are not explaining it

        private void uploadFile (Uri data){
        progressBar.setVisibility(View.VISIBLE);
        StorageReference sRef = mStorageReference.child(Constants.STORAGE_PATH_UPLOADS + System.currentTimeMillis() + ".pdf");
        sRef.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressBar.setVisibility(View.GONE);
                        textViewStatus.setText("File Uploaded Successfully");

                        Upload upload = new Upload(bookname.getText().toString(), taskSnapshot.getDownloadUrl().toString());
                    //    Upload uploadauthor = new Upload(author.getText().toString(), taskSnapshot.getDownloadUrl().toString());
                      //  Upload uploadedition = new Upload(edition.getText().toString(), taskSnapshot.getDownloadUrl().toString());

                        mDatabaseReference.child(mDatabaseReference.push().getKey()).setValue(upload);
                        //mDatabaseReference.child(mDatabaseReference.push().getKey()).setValue(uploadauthor);
                        //mDatabaseReference.child(mDatabaseReference.push().getKey()).setValue(uploadedition);


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        textViewStatus.setText((int) progress + "% Uploading...");
                    }
                });

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonupload:
                getPDF();
                break;

        }
    }
}



/*

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gmonetix.bookspivot.R;
import com.gmonetix.bookspivot.util.FilePath;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.util.UUID;
import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;


public class UploadActivity extends AppCompatActivity implements View.OnClickListener{
     final String uploadID = UUID.randomUUID().toString();
    public static final String UPLOAD_URL = "gs://book-hub-007.appspot.com/";//Upload server !!
    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 123;
    //Pdf request code
    private int PICK_PDF_REQUEST = 1;
    private Uri filePath;
    Button buttonChoose;
    Button buttonUpload;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        //Initializing views

       buttonChoose=(Button)findViewById(R.id.buttonchoose);
       buttonUpload=(Button)findViewById(R.id.buttonupload);



        //Setting clicklistener
        buttonChoose.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);

    }
    public void uploadMultipart() {


        //getting the actual path of the image
        String path = FilePath.getPath(this, filePath);

        //creating a multipart request
        try {
            new MultipartUploadRequest(this, uploadID, UPLOAD_URL)
                    .addFileToUpload(path, "pdf") //Adding file
                   // .addParameter("name", name) //Adding text parameter to the request
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //Uploading code
        if (path == null) {

            Toast.makeText(this, "Please move your .pdf file to internal storage and retry", Toast.LENGTH_LONG).show();
        } else {
            try {
                String uploadId = UUID.randomUUID().toString();

                //Starting the upload

            } catch (Exception exc) {
                Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void showFileChooser() {

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "choose a file"), PICK_PDF_REQUEST);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();



                Uri uri = data.getData();
                Log.e("TAG", uri.toString());
//            File myFile = new File(uri.toString());
//            Log.e("TAG", String.valueOf(myFile.length()));
                generateImageFromPdfUsingFD(uri, "bookfirstpage.png");
              //  Intent formIntent = new Intent(this, BookDetails.class);
                //startActivity(formIntent);

        }
    }


    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }


    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if(requestCode == 1)
            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }

        }
    }

    private void generateImageFromPdfUsingFD(Uri pdfUri, String name) {
        int pageNumber = 0;
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, pdfUri));
        PdfiumCore pdfiumCore = new PdfiumCore(this);
        try {
            ParcelFileDescriptor fd = getContentResolver().openFileDescriptor(pdfUri, "r");
            PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
            pdfiumCore.openPage(pdfDocument, pageNumber);
            int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber);
            int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber);
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, width, height);
            FileOutputStream out = null;
            try {

                String path = Environment.getExternalStorageDirectory() + "/" + "KhoborKagoj/";
// Create the parent path
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String fullName = path + name;
                File file = new File(fullName);
//                File file = new File(Environment.getExternalStorageDirectory() + "/" + "KhoborKagoj", name);
                out = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG, 15, out); // bmp is your Bitmap instance
            } catch (Exception e) {
                Log.e("TAG catch1", "" + e.getMessage());
            } finally {
                try {
                    if (out != null)
                        out.close();
                } catch (Exception e) {
//todo with exception
                    Log.e("TAG Catch2", "" + e.getMessage());
                }
            }
            pdfiumCore.closeDocument(pdfDocument); // important!
        } catch (Exception e) {
//todo with exception
            Log.e("TAG catch 3", "" + e.getMessage());
        }
    }


    @Override
    public void onClick(View v) {
        if (v == buttonChoose) {
            showFileChooser();
        }
        if (v == buttonUpload) {
            uploadMultipart();
        }
    }
}
*/
