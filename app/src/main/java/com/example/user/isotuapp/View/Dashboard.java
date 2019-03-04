package com.example.user.isotuapp.View;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.user.isotuapp.R;
import com.example.user.isotuapp.fragment.ChatFragment;
import com.example.user.isotuapp.fragment.ContactFragment;
import com.example.user.isotuapp.fragment.EventFragment;
import com.example.user.isotuapp.fragment.HomeFragment;
import com.example.user.isotuapp.fragment.ProfileFragment;

import java.util.ArrayList;
import java.util.List;

public class Dashboard extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    FloatingActionButton fab,fabevent;
    private int[] tabIcons = {
            R.mipmap.dashboard,
            R.mipmap.contact,
            R.mipmap.chat,
            R.mipmap.news,
            R.mipmap.profile
    };
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");

        fab = (FloatingActionButton) findViewById(R.id.to_posting);
        fabevent = (FloatingActionButton) findViewById(R.id.to_addevent);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(Dashboard.this,Posting.class);
                startActivity(intent);
            }
        });

        fabevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(Dashboard.this,EventActivity.class);
                startActivity(intent);
            }
        });

        fabevent.setVisibility(View.GONE);
        fab.setVisibility(View.VISIBLE);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        toolbar.setTitle("Home");
                        fab.setVisibility(View.VISIBLE);

                        fabevent.setVisibility(View.GONE);
                        break;
                    case 1:
                        toolbar.setTitle("Contact");
                        fab.setVisibility(View.GONE);
                        fabevent.setVisibility(View.GONE);
                        break;
                    case 2:
                        toolbar.setTitle("Chat");
                        fab.setVisibility(View.GONE);
                        fabevent.setVisibility(View.GONE);
                        break;
                    case 3:
                        toolbar.setTitle("Event");
                        fab.setVisibility(View.GONE);
                        fabevent.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        toolbar.setTitle("Profile");
                        fab.setVisibility(View.GONE);
                        fabevent.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                if (i == 3) {
                    ActionBar actionBar = getSupportActionBar();
                    actionBar.hide();
                }
            }
        });

        setupTabIcons();
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), "Home");
        adapter.addFragment(new ContactFragment(), "Contact");
        adapter.addFragment(new ChatFragment(), "Chatting");
        adapter.addFragment(new EventFragment(), "Event");
        adapter.addFragment(new ProfileFragment(), "Profile");
        viewPager.setAdapter(adapter);
    }
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
        tabLayout.getTabAt(4).setIcon(tabIcons[4]);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }

    }

}
