package com.example.healthfile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

public class About extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        tabLayout= findViewById(R.id.AllTabs);
        viewPager= findViewById(R.id.aboutView);

        tabLayout.setupWithViewPager(viewPager);

        About_Adopter aboutAdopter= new About_Adopter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        aboutAdopter.addFragment(new aboutRanjha(),"RANJHA");
        aboutAdopter.addFragment(new aboutHuraira(),"HURAIRA");
        aboutAdopter.addFragment(new aboutUsman(),"USMAN");

        viewPager.setAdapter(aboutAdopter);
    }
}