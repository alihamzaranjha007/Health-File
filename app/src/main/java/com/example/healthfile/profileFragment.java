package com.example.healthfile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class profileFragment extends Fragment {

    private FirebaseUser user;
    private DatabaseReference reference;
    private String UserID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_profile, container, false);

        final TextView textView_greeting= view.findViewById(R.id.Heading);
        final TextView textView_name= view.findViewById(R.id.Name_empty);
        final TextView textView_dob= view.findViewById(R.id.DOB_empty);
        final TextView textView_email= view.findViewById(R.id.Email_empty);
        final TextView textView_address= view.findViewById(R.id.Address_empty);
        final TextView textView_phone= view.findViewById(R.id.Phone_empty);
        final TextView textView_cnic= view.findViewById(R.id.cnic_empty);
        final CircleImageView imageView= view.findViewById(R.id.imageView2);

        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        UserID= user.getUid();

        reference= FirebaseDatabase.getInstance().getReference("users").child(UserID);
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
                Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });


        return view;
    }
}