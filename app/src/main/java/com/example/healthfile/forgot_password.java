package com.example.healthfile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class forgot_password extends AppCompatActivity {

    private EditText email_text;
    private Button reset_pass_btn;
    private ProgressBar progressBar;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email_text= findViewById(R.id.editText_forget_email);
        reset_pass_btn= findViewById(R.id.reset_button);
        progressBar= findViewById(R.id.progressBar);

        auth= FirebaseAuth.getInstance();

        reset_pass_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private void resetPassword() {
        String email= email_text.getText().toString();

        if (email.isEmpty()){
            email_text.setError("email is required!");
            email_text.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(forgot_password.this, "enter valid email!", Toast.LENGTH_LONG).show();
        }

        progressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(forgot_password.this, "Please check your email to reset password!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(forgot_password.this, "try again!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    //backspace
    @Override
    public void onBackPressed() {
        startActivity(new Intent(forgot_password.this, MainActivity.class));
        finish();
    }
}