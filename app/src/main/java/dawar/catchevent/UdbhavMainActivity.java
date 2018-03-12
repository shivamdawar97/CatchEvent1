package dawar.catchevent;

import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionManager;

import com.google.firebase.database.Transaction;

public class UdbhavMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_udbhav_main);

 //       android.support.v4.app.FragmentTransaction t= getSupportFragmentManager().beginTransaction().addToBackStack(null);
   //     t.replace(R.id.udv_main,new UdbhavFragment()).commit();


    }
}
