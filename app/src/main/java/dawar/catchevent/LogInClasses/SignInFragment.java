package dawar.catchevent.LogInClasses;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import dawar.catchevent.MainActivity;
import dawar.catchevent.R;

import static dawar.catchevent.CatchEvent.mdatabase;
import static dawar.catchevent.CatchEvent.mAuth;

import static dawar.catchevent.LogInClasses.CommonAdapter.RC_SIGN_IN;

import static dawar.catchevent.LogInClasses.CommonAdapter.mgoogleApiClient;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {


    public SignInFragment() {
        // Required empty public constructor
    }
    TextView needAcc;
    EditText editEmail,editPassword;
    View v;
    Button signIn;
    ProgressDialog progressDialog;
    SignInButton googlebtn;
    FragmentActivity activity;
    DatabaseReference mUSerData;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v=inflater.inflate(R.layout.fragment_sign_in, container, false);

        needAcc=v.findViewById(R.id.textView8);
        needAcc.setOnClickListener(new
                NeedAccListener());
        activity=getActivity();
        signIn=v.findViewById(R.id.button4);
        editEmail=v.findViewById(R.id.editUsername);
        editPassword=v.findViewById(R.id.editPass);

        googlebtn=v.findViewById(R.id.googlebtn);

        googlebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new CommonAdapter(activity);
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mgoogleApiClient);
                (activity).startActivityForResult(signInIntent, RC_SIGN_IN);
                mgoogleApiClient.stopAutoManage( activity);
                mgoogleApiClient.disconnect();
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInwithEmailandPass();
            }
        });
        return v;
    }

    private static class NeedAccListener implements View.OnClickListener {
        @Override
        public void onClick(final View view) {
            view.setBackgroundColor(view.getContext().getResources().getColor(R.color.select));
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 200ms
                    LogInActivity.notifyPagesAdded(new BlankFragment(),new SignUpFragment());
                    view.setBackgroundColor(0);
                }
            },200);


        }
    }

    private void signInwithEmailandPass( ) {
        String email,pass;
        email=editEmail.getText().toString().trim();
        pass=editPassword.getText().toString().trim();
        email=email.concat("@catchevent.com");
        if( !(TextUtils.isEmpty(email)) && !(TextUtils.isEmpty(pass))){
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setTitle("Signing in...");
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information

                                getActivity().finish();
                                startActivity(new Intent(getActivity(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            } else {
                                // If sign in fails, display a message to the user.

                                Toast.makeText(getActivity(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }

                            // ...
                        }
                    });

        }
    }



    void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        final FirebaseAuth mAuth=FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    FirebaseUser user=mAuth.getCurrentUser();
                    checkUser(Objects.requireNonNull(user));
                }
                else {
                    Toast.makeText(activity,"Login failed",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void checkUser(final FirebaseUser user) {

        final String id = user.getUid();
        mUSerData = mdatabase.child("users");


        mUSerData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(id)) {
                    //Send user to Register Fragment

                    mAuth.signOut();
                    user.delete();
                    if(LogInActivity.fragments.size()<3) {
                        Toast.makeText(activity, "Register first", Toast.LENGTH_LONG).show();
                        needAcc.callOnClick();
                    }

                }
                else
                {
                    Toast.makeText(activity,"log in Successful",Toast.LENGTH_LONG).show();
                    activity.finish();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
