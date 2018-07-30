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

/**
 * Created by shivam97 on 21/1/18.
 */

public  class CommonAdapter  {
    private static int  RC_SIGN_IN = 10;

    FirebaseAuth mAuth;
    String m_Text;
    private GoogleApiClient mgoogleApiClient;
    Context ctx;


    public CommonAdapter(final Context ctx,FirebaseAuth mAuth){
        this.ctx=ctx;
        this.mAuth=mAuth;
        m_Text=new String("");

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

                    AlertDialog.Builder builder1=new AlertDialog.Builder(ctx);
                    builder1.setMessage("Enter Username and Password");

                    LinearLayout layout = new LinearLayout(ctx);
                    layout.setOrientation(LinearLayout.VERTICAL);


                    final EditText username=new EditText(ctx);
                    username.setHint("Username");
                    layout.addView(username);
                    final EditText password=new EditText(ctx);
                    password.setHint("Password");
                    layout.addView(password);
                    builder1.setView(layout);
                    builder1.setPositiveButton("LogIn", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            final String us=username.getText().toString();
                            final String pss=password.getText().toString();


                            mAuth.signInWithEmailAndPassword(us,pss).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(ctx,"Signed In Successfully",Toast.LENGTH_SHORT).show();

                                    }
                                    else {
                                        Toast.makeText(ctx,"Sign In Falied",Toast.LENGTH_SHORT).show();
                                    }
                                }


                            });


                        }
                    });



                    builder1.show();



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



        }


    }

    public void showSettings(int j){
        Intent i=new Intent(ctx,ListActivity.class);
        i.putExtra("sett",j);
        ctx.startActivity(i);
    }

}
