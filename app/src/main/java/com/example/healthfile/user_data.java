package com.example.healthfile;

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

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class user_data extends AppCompatActivity {

    private DrawerLayout drawer;
    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private ProgressBar progressBarCircle;

    private DatabaseReference mDatabaseRef;
    private List<Upload> mUpload;

    private FirebaseUser user;
    private DatabaseReference reference;
    private String UserID;


    public TextView textViewName;
    public TextView textViewEmail;
    public ImageView imageView2;

    private ValueEventListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);

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
                        startActivity(new Intent(user_data.this, About.class));
                        break;

                    case R.id.Logout:
                        //   mDatabaseRef.removeEventListener(listener);
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(user_data.this, MainActivity.class));
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
        imageView2= header.findViewById(R.id.userImg);

        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        UserID= user.getUid();

        reference= FirebaseDatabase.getInstance().getReference("users").child(UserID);
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
                            .into(imageView2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(user_data.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });





















        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        UserID= user.getUid();




        mRecyclerView= findViewById(R.id.recycleView);
        progressBarCircle= findViewById(R.id.progressBar_circle);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mUpload= new ArrayList<>();
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("uploads").child(UserID);


        listener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUpload.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()){
                    String id= postSnapshot.getKey();
                    Upload upload= postSnapshot.getValue(Upload.class);
                    upload.setId(id);
                    mUpload.add(upload);
                }

                mAdapter= new ImageAdapter(user_data.this, mUpload);
                mRecyclerView.setAdapter(mAdapter);

                //remove
               mAdapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(user_data.this);
                        builder1.setTitle("Delete...?");
                        builder1.setMessage("Do you want to delete?");
                        builder1.setIcon(R.drawable.delete_dialog);
                        builder1.setCancelable(true);
                        builder1.setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {

                                        StorageReference photoRef= FirebaseStorage.getInstance().getReferenceFromUrl(mUpload.get(position).getmImgUrl());
                                        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                String id= mUpload.get(position).getId();
                                                mUpload.remove(position);
                                                mDatabaseRef.child(id).removeValue();
                                                mAdapter.notifyItemRemoved(position);
                                                Toast.makeText(user_data.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(user_data.this, "Did not delete", Toast.LENGTH_SHORT).show();
                                            }
                                        });
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
                });

                progressBarCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(user_data.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBarCircle.setVisibility(View.INVISIBLE);
            }
        });

        final TextView textView_greeting= findViewById(R.id.Heading);
        final TextView textView_name= findViewById(R.id.Name_empty);
        final TextView textView_dob= findViewById(R.id.DOB_empty);
        final TextView textView_email= findViewById(R.id.Email_empty);
        final TextView textView_address= findViewById(R.id.Address_empty);
        final TextView textView_phone= findViewById(R.id.Phone_empty);
        final TextView textView_cnic= findViewById(R.id.cnic_empty);
        final CircleImageView imageView= findViewById(R.id.imageView2);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile= snapshot.getValue(User.class);

                if (userProfile != null){
                    String profile= userProfile.profile;
                    String name= userProfile.name;
                    String dob= userProfile.dob;
                    String email= userProfile.email;
                    String phone= userProfile.phone;
                    String address= userProfile.address;
                    String cnic= userProfile.cnic;


                    textView_greeting.setText("Welcome "+name+"!");
                    textView_name.setText(name);
                    textView_email.setText(email);
                    textView_dob.setText(dob);
                    textView_address.setText(address);
                    textView_phone.setText(phone);
                    textView_cnic.setText(cnic);

                    Picasso.get()
                            .load(profile)
                            .placeholder(R.drawable.ic_baseline_account_profile)
                            .fit()
                            .into(imageView);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(user_data.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void addNewFun(View view) {
        startActivity(new Intent(user_data.this, NewRecord.class));
        finish();
    }



    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);


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
                            Toast.makeText(user_data.this, "Account deleted successfully!", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(user_data.this,MainActivity.class));
                            finish();
                        } else{
                            Toast.makeText(user_data.this, "could not delete successfully!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                return false;
            }
        })   ;
//for search
        SearchView searchView= (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

//backspace
@Override
public void onBackPressed() {
    if (drawer.isDrawerOpen(GravityCompat.START)){
        drawer.closeDrawer(GravityCompat.START);
    }
    else {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(user_data.this);
        builder1.setTitle("Close...?");
        builder1.setMessage("Do you want to exit?");
        builder1.setIcon(R.drawable.ic_baseline_exit_to_app_24);
        builder1.setCancelable(true);
        builder1.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(user_data.this, MainActivity.class));
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