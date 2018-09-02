package dawar.catchevent;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import dawar.catchevent.LogInClasses.LogInActivity;
import static dawar.catchevent.CatchEvent.mAuth;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
         final int SPLASH_DISPLAY_LENGTH = 500;
         final Class c;
         if(mAuth.getCurrentUser()==null)
             c= LogInActivity.class;
         else
             c=MainActivity.class;
        /** Called when the activity is first created. */

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {

                /* Create an Intent that will start the Menu-Activity. */
                    Intent mainIntent = new Intent(Splash.this,c);
                    Splash.this.startActivity(mainIntent);
                    Splash.this.finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
    }
}
