package com.example.tiktokcloneproject.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;

import com.example.tiktokcloneproject.R;
import com.example.tiktokcloneproject.adapters.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class FollowListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_list);


        Bundle i = getIntent().getExtras();
        int pageIndex = i.getInt("pageIndex");
        ActionBar actionBar = getSupportActionBar();

        if (actionBar!=null) {
            actionBar.hide();
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        ViewPager2 viewPager2 = (ViewPager2) findViewById(R.id.view_pager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager2.setAdapter(adapter);


        tabLayout.setScrollPosition(pageIndex,0f,true);
        viewPager2.setCurrentItem(pageIndex);

        new TabLayoutMediator(tabLayout, viewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

                        if (position == 0) {
                            tab.setText("Following");
                        }
                        if (position == 1) {
                            tab.setText("Followers");
                        }
                    }
                }).attach();
    }
}