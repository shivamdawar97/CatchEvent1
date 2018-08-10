package dawar.catchevent.LogInClasses;

import android.app.Activity;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import dawar.catchevent.R;

/**
 * Created by shivam97 on 21/1/18.
 */

public  class CommonAdapter  {
    public static int  RC_SIGN_IN = 10;
    public static int RC_SIGN_UP=11;
    public static GoogleApiClient mgoogleApiClient;
    public static Context ctx;
    private static  GoogleSignInOptions gso;


    CommonAdapter(final Context ctx){
        this.ctx=ctx;

        gso= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                 .requestIdToken(ctx.getString(R.string.default_web_client_id))
                 .requestEmail()
                 .build();
         mgoogleApiClient=new GoogleApiClient.Builder(ctx.getApplicationContext())
                 .enableAutoManage( (FragmentActivity)ctx, new GoogleApiClient.OnConnectionFailedListener() {
                     @Override
                     public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                         Toast.makeText(ctx,"LogIn Failed "+connectionResult.getErrorMessage(),Toast.LENGTH_SHORT).show();
                     }
                 }).addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                 .build();

     }
     static GoogleSignInAccount onActivityOfResult(int i, int i1, Intent intent) {
        //onActivityResult(i,i1,intent);

        if (i == RC_SIGN_IN || i==RC_SIGN_UP) {

            GoogleSignInResult task = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
            // Google Sign In was successful, authenticate with Firebase
            if(task.isSuccess()) {

                return task.getSignInAccount();
            }
            else {

                Toast.makeText(ctx,"LogIn Failed",Toast.LENGTH_SHORT).show();

            }
        }
        return null;
    }


}
