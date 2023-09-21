package com.example.healthfile;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class SignupForm extends AppCompatActivity implements View.OnClickListener{

    ActivityResultLauncher<String> profileTakePhoto;
    private FirebaseAuth mAuth;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private EditText editText_name, editText_CNIC, editText_password, editText_phone,
            editText_dob, edit_address, edit_Email;
    private TextView login;
    private ImageView profile_img;
    private ProgressBar progressBar;
    private Uri imgUri;

    private StorageTask uploadTask;
//    RadioGroup gender;
    private CardView sign_up;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_form);

        profileTakePhoto= registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        imgUri= result;
                        profile_img.setImageURI(imgUri);
                    }
                }
        );


        mAuth = FirebaseAuth.getInstance();

        editText_name= findViewById(R.id.editText_name);
        editText_CNIC= findViewById(R.id.editTextCNIC);
        editText_password= findViewById(R.id.editTextTextPassword);
        editText_phone= findViewById(R.id.editTextPhone);
        editText_dob= findViewById(R.id.editText_dob);
        edit_address= findViewById(R.id.editTextAddress);
        edit_Email= findViewById(R.id.editText_email);
        progressBar= findViewById(R.id.progressBar);
        profile_img= findViewById(R.id.imageViewProfile);
        profile_img.setOnClickListener(this);
//        gender= findViewById(R.id.radioGroupGender);
        login= findViewById(R.id.login2);
        login.setOnClickListener(this);
        sign_up= findViewById(R.id.cardView_signup);
        sign_up.setOnClickListener(this);

        mStorageRef= FirebaseStorage.getInstance().getReference("profiles");
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("users");

    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cardView_signup:
                sign_up();
                break;

            case  R.id.login2:
                Intent intent= new Intent(SignupForm.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.imageViewProfile:
                profileTakePhoto.launch("image/*");

        }

    }

    private String getFileExtension(Uri uri){
        ContentResolver cR= getContentResolver();
        MimeTypeMap mime= MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


    private void sign_up() {
        String name= editText_name.getText().toString().trim();
        String dob= editText_dob.getText().toString().trim();
        String email= edit_Email.getText().toString().trim();
        String cnic= editText_CNIC.getText().toString().trim();
        String password= editText_password.getText().toString();
        String phone= editText_phone.getText().toString().trim();
        String address= edit_address.getText().toString().trim();


        if (name.isEmpty()){
            editText_name.setError("Enter name!");
            editText_name.requestFocus();
            return;
        }
        if (dob.isEmpty()){
            editText_dob.setError("Enter date of birth!");
            editText_dob.requestFocus();
            return;
        }
        if (email.isEmpty()){
            edit_Email.setError("Enter email!");
            edit_Email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edit_Email.setError("Please valid email!");
            edit_Email.requestFocus();
            return;
        }
        if (cnic.isEmpty()){
            editText_CNIC.setError("Enter CNIC!");
            editText_CNIC.requestFocus();
            return;
        }
        if (password.isEmpty()){
            editText_password.setError("Enter password!");
            editText_password.requestFocus();
            return;
        }
        if (password.length()<8){
            editText_password.setError("Minimum length should be 8!");
            editText_password.requestFocus();
            return;
        }
        if (phone.isEmpty()){
            editText_phone.setError("Enter phone!");
            editText_phone.requestFocus();
            return;
        }
        if (address.isEmpty()){
            edit_address.setError("Enter address!");
            edit_address.requestFocus();
            return;
        }

        if (imgUri != null) {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                StorageReference fileReference = mStorageRef.child(FirebaseAuth.getInstance().getCurrentUser().
                                        getUid()).child(System.currentTimeMillis()
                                        + "." + getFileExtension(imgUri));

                                fileReference.putFile(imgUri)
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot snapshot) {
                                                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {


                                                        User user = new User(uri.toString(), name, dob, email, cnic, address, phone);

                                                        FirebaseDatabase.getInstance().getReference("users")
                                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {

                                                                            FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
                                                                            Toast.makeText(SignupForm.this, "User has been registered successfully", Toast.LENGTH_LONG).show();
                                                                            progressBar.setVisibility(View.GONE);
                                                                            //move to login
                                                                            startActivity(new Intent(SignupForm.this, MainActivity.class));
                                                                            finish();
                                                                        } else {
                                                                            Toast.makeText(SignupForm.this, "Failed to register try again!", Toast.LENGTH_LONG).show();
                                                                            progressBar.setVisibility(View.INVISIBLE);
                                                                        }
                                                                    }
                                                                });

                                                    }
                                                });

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(SignupForm.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            } else {
                                Toast.makeText(SignupForm.this, "Failed try again!" + task.getException(), Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }

                    }
                });
        } else {
            Toast.makeText(SignupForm.this,"No image selected",Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    //backspace
    @Override
    public void onBackPressed() {
        startActivity(new Intent(SignupForm.this, MainActivity.class));
        finish();
    }
}