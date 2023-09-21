package com.example.healthfile;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import java.util.ArrayList;
import java.util.List;

public class dataFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private ProgressBar progressBarCircle;
    private ImageView addRec;

    private DatabaseReference mDatabaseRef;
    private FirebaseUser user;
    private String UserID;
    private List<Upload> mUpload;

    private ValueEventListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_data, container, false);


        mRecyclerView= view.findViewById(R.id.recycleView);
        addRec= view.findViewById(R.id.addRecord);
        progressBarCircle= view.findViewById(R.id.progressBar_circle);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        addRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),NewRecord.class));
            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        UserID= user.getUid();
        mUpload= new ArrayList<>();
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("uploads").child(UserID);


        listener= mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUpload.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()){
                    String id= postSnapshot.getKey();
                    Upload upload= postSnapshot.getValue(Upload.class);
                    upload.setId(id);
                    mUpload.add(upload);
                }

                mAdapter= new ImageAdapter(getContext(), mUpload);
                mRecyclerView.setAdapter(mAdapter);

                //remove
                mAdapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
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
                                                Toast.makeText(getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(), "Did not delete", Toast.LENGTH_SHORT).show();
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

                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBarCircle.setVisibility(View.INVISIBLE);
            }
        });
//
        View abc= inflater.inflate(R.layout.activity_drawer, container,false);
        NavigationView navigationView= abc.findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.Logout) {
                     mDatabaseRef.removeEventListener(listener);
                }
                return true;
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu,menu);
        MenuItem menuItem= menu.findItem(R.id.search);
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
        super.onCreateOptionsMenu(menu, inflater);
    }
}