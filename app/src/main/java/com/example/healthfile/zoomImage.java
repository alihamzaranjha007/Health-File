package com.example.healthfile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class zoomImage extends AppCompatActivity {
    String Name="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image);

        ImageView imageViewZoom = findViewById(R.id.zoomIMG);

        Bundle b= getIntent().getExtras();
        if(b != null){
            Name= b.getString("zoomImage");
        }
        Picasso.get()
                .load(Name)
                .placeholder(R.drawable.ic_menu_gallery)
                .into(imageViewZoom);
    }
}