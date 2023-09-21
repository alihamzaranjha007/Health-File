package com.example.healthfile;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Drawer extends AppCompatActivity {

    private DrawerLayout drawer;
    private FirebaseUser user;
    private DatabaseReference reference;
    private DatabaseReference mDatabaseRef;
    private String UserID;

    public TextView textViewName;
    public TextView textViewEmail;
    public ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        Toolbar toolbar= findViewById(R.id.toolBar1);
        setSupportActionBar(toolbar);

        drawer= findViewById(R.id.drawerLayout1);
        NavigationView navigationView= findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.Profile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                                new profileFragment()).commit();
                        break;
                    case R.id.Data:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                                new dataFragment()).commit();
                        break;

                    case R.id.About:
                        startActivity(new Intent(Drawer.this, About.class));
                        break;

                    case R.id.Logout:
                     //   mDatabaseRef.removeEventListener(listener);
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(Drawer.this, MainActivity.class));
                        finish();
                        break;
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(this,drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                    new profileFragment()).commit();
            navigationView.setCheckedItem(R.id.Profile);
        }
//
        View header= navigationView.getHeaderView(0);
        textViewName= header.findViewById(R.id.userName);
        textViewEmail= header.findViewById(R.id.userEmail);
        imageView= header.findViewById(R.id.userImg);

        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        UserID= user.getUid();

        reference= FirebaseDatabase.getInstance().getReference().child("users").child(UserID);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if (user!=null){
                    String profile= user.getProfile();
                    String name= user.getName();
                    String email= user.getEmail();

                    textViewName.setText(name);
                    textViewEmail.setText(email);
                    Picasso.get()
                            .load(profile)
                            .into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Drawer.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem menuItemDelete= menu.findItem(R.id.delete_acc);
        MenuItem menuItem= menu.findItem(R.id.search);

//for delete account
        menuItemDelete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                FirebaseUser curr_user= FirebaseAuth.getInstance().getCurrentUser();
                curr_user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Account deleted successfully!", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                        } else{
                            Toast.makeText(getApplicationContext(), "could not delete successfully!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                return false;
            }
        })   ;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(Drawer.this);
            builder1.setTitle("Close...?");
            builder1.setMessage("Do you want to exit?");
            builder1.setIcon(R.drawable.ic_baseline_exit_to_app_24);
            builder1.setCancelable(true);
            builder1.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(Drawer.this, MainActivity.class));
                            finishAffinity();
                        }
                    });
            builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
    }
}