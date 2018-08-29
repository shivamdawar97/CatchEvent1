package dawar.catchevent.LogInClasses;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.ArrayList;

import dawar.catchevent.R;

import static dawar.catchevent.LogInClasses.CommonAdapter.RC_SIGN_IN;
import static dawar.catchevent.LogInClasses.CommonAdapter.RC_SIGN_UP;
import static dawar.catchevent.LogInClasses.CommonAdapter.onActivityOfResult;

public class LogInActivity extends AppCompatActivity {


    static NonSwipeableViewPager viewPager;
    static ViewPagerAdapter adapter;
    static ArrayList<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("LogIn");
        setSupportActionBar(toolbar);
        viewPager=findViewById(R.id.content_view_logIn);
        viewPager.setScrollContainer(false);
        adapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments=new ArrayList<>();
            fragments.add(new SignInFragment());

        }

        @Override
        public Fragment getItem(int position) {
           return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
         void notifyDataAdded(Fragment fragment){
            fragments.add(fragment);
            this.notifyDataSetChanged();
        }
    }

    public static void notifyPagesAdded(Fragment fragment,Fragment fragment1){
      adapter.notifyDataAdded(fragment);
      movePagerForward();
      adapter.notifyDataAdded(fragment1);
    }

    public static void movePagerForward(){
       viewPager.setCurrentItem(viewPager.getCurrentItem()+1,true);
    }
    public static void movePagerBackward(){
        viewPager.setCurrentItem(viewPager.getCurrentItem()-1,true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        GoogleSignInAccount account= onActivityOfResult(requestCode,requestCode,data);
        if(account ==null)
            return;
        if(requestCode==RC_SIGN_UP)
            ((SignUpFragment)fragments.get(2)).firebaseAuthWithGoogle(account);

        if(requestCode==RC_SIGN_IN)
            ((SignInFragment)fragments.get(0)).firebaseAuthWithGoogle(account);

    }
}
