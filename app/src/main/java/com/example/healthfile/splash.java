package com.example.healthfile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        Thread td= new Thread(){
            public void run(){
                try {
                    sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Intent it= new Intent(splash.this, MainActivity.class);
                    startActivity(it);
                    finish();
                }
            }
        }; td.start();
    }
}