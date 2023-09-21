package com.example.healthfile;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;

public class NewRecord extends AppCompatActivity {

    ActivityResultLauncher<String> mTakePhoto;

    private Button mButtonUpload;
    private Button mButtonChoose;
    private ProgressBar mProgressBar;
    private EditText mReport;
    private EditText mHospital;
    private EditText mDoctor;
    private EditText mDate;
    private EditText mRemarks;


    private ImageView mImageView;

    private Uri mImageUri;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_record);

        mTakePhoto= registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        mImageUri= result;
                        mImageView.setImageURI(mImageUri);
                    }
                }
        );

        mButtonChoose= findViewById(R.id.button_choose_image);
        mButtonUpload= findViewById(R.id.button_upload);
        mReport= findViewById(R.id.reportNameR);
        mHospital= findViewById(R.id.hospitalNameR);
        mDoctor= findViewById(R.id.doctorNameR);
        mProgressBar= findViewById(R.id.progressBar);
        mDate= findViewById(R.id.visitingDateR);
        mRemarks= findViewById(R.id.remarksR);
        mImageView= findViewById(R.id.image_view);

        mStorageRef= FirebaseStorage.getInstance().getReference("records");
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("uploads");

        mButtonChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTakePhoto.launch("image/*");
            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();

            }
        });


    }


    public void SaveData(View view) {
        startActivity(new Intent(NewRecord.this, user_data.class));
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR= getContentResolver();
        MimeTypeMap mime= MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {

        mProgressBar.setVisibility(View.VISIBLE);
        if (mImageUri!=null){
            StorageReference fileReference= mStorageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(System.currentTimeMillis()
            +"."+getFileExtension(mImageUri));

            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot snapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Handler handler= new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            mProgressBar.setProgress(0);
                                        }
                                    }, 500);

                                    mProgressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(NewRecord.this,"Upload successfully",Toast.LENGTH_SHORT).show();

                                    Upload upload= new Upload(mReport.getText().toString().trim(),
                                            mHospital.getText().toString().trim(),
                                            mDoctor.getText().toString().trim(),
                                            mDate.getText().toString().trim(),
                                            mRemarks.getText().toString().trim(),
                                            uri.toString());

                                    String uploadId= mDatabaseRef.push().getKey();
                                    mDatabaseRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(uploadId).setValue(upload);

                                    //move one to another activity
                                    startActivity(new Intent(NewRecord.this,Drawer.class));
                                    finishAffinity();
                                    //move end
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NewRecord.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress= (100.0 * snapshot.getBytesTransferred()/ snapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });

        } else {
            Toast.makeText(this,"No image selected",Toast.LENGTH_SHORT).show();
        }
    }

    //backspace
    @Override
    public void onBackPressed() {
        finish();
    }
}