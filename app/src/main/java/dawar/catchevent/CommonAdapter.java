package dawar.catchevent;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Created by shivam97 on 21/1/18.
 */

public  class CommonAdapter  {
    private static int  RC_SIGN_IN = 10;
    private String name;
    private String image;
    private String gimage;
    FirebaseAuth mAuth;
    String m_Text;
    private GoogleApiClient mgoogleApiClient;
    Context ctx;
    private boolean b;


    public CommonAdapter(final Context ctx,FirebaseAuth mAuth){
        this.ctx=ctx;
        this.mAuth=mAuth;
        m_Text=new String("");


    }

    CommonAdapter(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void signIn(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Enter Security Key");
        builder.setMessage("If you doesn't have the key contact to officials");
// Set up the input
        final EditText input = new EditText(ctx);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                if (m_Text.equals("54321")) {
                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(ctx.getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build();

                    mgoogleApiClient=new GoogleApiClient.Builder(ctx.getApplicationContext())
                            .enableAutoManage( (FragmentActivity)ctx, new GoogleApiClient.OnConnectionFailedListener() {
                                @Override
                                public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                                    Toast.makeText(ctx,"LogIn Failed",Toast.LENGTH_SHORT).show();
                                }
                            }).addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                            .build();

                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mgoogleApiClient);
                    ((Activity)ctx).startActivityForResult(signInIntent, RC_SIGN_IN);

                } else {

                    builder.setMessage("Wrong key");
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();


    }

    public void onActivityOfResult(int i, int i1, Intent intent) {
        //onActivityResult(i,i1,intent);

        if (i == RC_SIGN_IN) {

            GoogleSignInResult task = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
            // Google Sign In was successful, authenticate with Firebase
            if(task.isSuccess()) {
                GoogleSignInAccount account = task.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
            else {
                Toast.makeText(ctx,"LogIn Failed",Toast.LENGTH_SHORT).show();
            }
        }


    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                     Toast.makeText(ctx,"Logged In successfully",Toast.LENGTH_SHORT).show();
                    b =true;
                }
                else {
                    b=false;
                }

            }
        });

    }

    public void showSettings(int j){
        Intent i=new Intent(ctx,ListActivity.class);
        i.putExtra("sett",j);
        ctx.startActivity(i);
    }

}
