package com.example.project1_test3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Window;

import com.example.project1_test3.Adapter.ContentsPagerAdapter;
import com.example.project1_test3.Fragment.AddressFragment;
import com.example.project1_test3.Fragment.ImageFragment;
import com.example.project1_test3.Fragment.MemoFragment;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private NonSwipeViewPager viewPager;
    private ContentsPagerAdapter mContentsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Permission permission = new Permission();
        permission.checkPermissions(MainActivity.this);


        tabLayout = findViewById(R.id.layout_tab);
//        tabLayout.addTab(tabLayout.newTab().setText("연락처"));
//        tabLayout.addTab(tabLayout.newTab().setText("갤러리"));
//        tabLayout.addTab(tabLayout.newTab().setText("메모장"));
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.getTabAt(0).setIcon(R.drawable.stick_man_b);
        tabLayout.getTabAt(1).setIcon(R.drawable.picture);
        tabLayout.getTabAt(2).setIcon(R.drawable.paint_brushes);

        viewPager = findViewById(R.id.pager_content);
        mContentsPagerAdapter = new ContentsPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(mContentsPagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                tabLayout.getTabAt(0).setIcon(R.drawable.stick_man);
                tabLayout.getTabAt(1).setIcon(R.drawable.picture);
                tabLayout.getTabAt(2).setIcon(R.drawable.paint_brushes);
                switch (tab.getPosition()) {
                    case 0:
                        viewPager.setEnable(true);
                        tabLayout.getTabAt(0).setIcon(R.drawable.stick_man_b);
                        break;
                    case 1:
                        viewPager.setEnable(true);
                        tabLayout.getTabAt(1).setIcon(R.drawable.picture_b);
                        break;
                    case 2:
                        viewPager.setEnable(false);
                        tabLayout.getTabAt(2).setIcon(R.drawable.paint_brushes_b);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
