package dawar.catchevent.SettingsClasses;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import dawar.catchevent.LogInClasses.NonSwipeableViewPager;
import dawar.catchevent.R;


import static dawar.catchevent.CatchEvent.Uid;
import static dawar.catchevent.CatchEvent.userType;
import static dawar.catchevent.CatchEvent.mdatabase;


public class SettingsActivity extends AppCompatActivity {

    NonSwipeableViewPager viewPager;
    static ViewPagerAdapter adapter;
    static ArrayList<Fragment> fragments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);

        mdatabase.child("users").child(Uid).child("userType").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userType =Integer.parseInt(dataSnapshot.getValue().toString()) ;
                viewPager=findViewById(R.id.content_view_setting);
                viewPager.setScrollContainer(true);
                adapter=new ViewPagerAdapter(getSupportFragmentManager());
                viewPager.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

    private class ViewPagerAdapter  extends FragmentPagerAdapter{
         ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments=new ArrayList<>();
            fragments.add(new SettingsFrag());
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewPager.setCurrentItem(viewPager.getCurrentItem()+1,true);
                }
            },200);

        }
    }

    @Override
    public void onBackPressed() {

        if(viewPager.getCurrentItem()!=0)
            viewPager.setCurrentItem(viewPager.getCurrentItem()-1,true);
        else finish();
    }
}






