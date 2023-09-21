package com.example.healthfile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.Permission;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private TextView register, forget_pass;
    private EditText editText_email, editText_password;
    private CardView login;
    private AlertDialog.Builder pre_history;
    private ProgressBar progressBar;
    private  boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        register= findViewById(R.id.register2);
        login= findViewById(R.id.login);
        editText_email= findViewById(R.id.editText_email);
        editText_password= findViewById(R.id.editText_Password);
        forget_pass= findViewById(R.id.forget_password);
        progressBar= findViewById(R.id.progressBar);

        mAuth= FirebaseAuth.getInstance();

        pre_history= new AlertDialog.Builder(this);

        //Login to user account
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_login();
            }
        });

        //sign up button to create new account
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this, SignupForm.class);
                startActivity(intent);
                finish();
            }
        });

        //forget password
        forget_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this, forgot_password.class);
                startActivity(intent);
                finish();
            }
        });

    }


    private void user_login() {
        String email= editText_email.getText().toString().trim();
        String password= editText_password.getText().toString().trim();

        if (email.isEmpty()){
            editText_email.setError("please enter email!");
            editText_email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editText_email.setError("Please valid email!");
            editText_email.requestFocus();
            return;
        }
        if (password.isEmpty()){
            editText_password.setError("please enter password!");
            editText_password.requestFocus();
            return;
        }
        if (password.length()<8){
            editText_password.setError("Minimum length should be 8!");
            editText_password.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();

                    progressBar.setVisibility(View.GONE);
                    assert user != null;
                    if (!user.isEmailVerified()) {
                        user.sendEmailVerification();
                        Toast.makeText(MainActivity.this, "Check your email and verify your account!", Toast.LENGTH_LONG).show();
                    } else {
                       pre_history.setTitle("Wait!!")
                               .setMessage("Do you want to add previous medical record?")
                               .setIcon(R.drawable.add_icon)
                               .setCancelable(true)
                               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {
                                       startActivity(new Intent(MainActivity.this, NewRecord.class));
                                       finish();
                                   }
                               })
                               .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {
                                       startActivity(new Intent(MainActivity.this, Drawer.class));
                                       finish();
                                   }
                               })
                               .show();
                   }
                } else if( ! CheckNetwork.isInternetAvailable(MainActivity.this))
                {
                    Toast.makeText(MainActivity.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);

                }
                else {
                    Toast.makeText(MainActivity.this, "email or password is incorrect or weak internet!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    //backspace
    @Override
    public void onBackPressed() {
        finish();
    }
//internet check
static class CheckNetwork {

    private static final String TAG = CheckNetwork.class.getSimpleName();

    public static boolean isInternetAvailable(Context context) {
        NetworkInfo info = (NetworkInfo) ((ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if (info == null) {
            Log.d(TAG, "no internet connection");
            return false;
        } else {
            if (info.isConnected()) {
                Log.d(TAG, " internet connection available...");
                return true;
            } else {
                Log.d(TAG, " internet connection");
                return true;
            }

        }
    }
}
}