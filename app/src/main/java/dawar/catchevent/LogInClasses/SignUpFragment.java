package dawar.catchevent.LogInClasses;



import android.app.ProgressDialog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import dawar.catchevent.MainActivity;
import dawar.catchevent.R;

import static android.app.Activity.RESULT_OK;
import static dawar.catchevent.EventClasses.AddEvent.PICK;
import static dawar.catchevent.CatchEvent.mdatabase;
import static dawar.catchevent.CatchEvent.mstorage;
import static dawar.catchevent.CatchEvent.mAuth;
import static dawar.catchevent.LogInClasses.CommonAdapter.RC_SIGN_UP;
import static dawar.catchevent.LogInClasses.CommonAdapter.mgoogleApiClient;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {

    static int card=0;
    ImageView imageCard;
    ProgressDialog mProgressDialog;
    TextView back;
    View view;
    private Uri filepath;
    String name,email,password,cpassword;
    Button signUpBtn;
    SignInButton googlebtn;
    DatabaseReference mUserdata;
    StorageReference mUserStorage;
    EditText editName,editEmail,editPass,editcPass;

    Bitmap bitmap;
    FragmentActivity activity;


    public SignUpFragment() {
        // Required empty public constructor

    }

    private Runnable runnable=new Runnable() {
        @Override
        public void run() {
            editName=view.findViewById(R.id.editText2);
            editEmail=view.findViewById(R.id.editText3);
            editPass=view.findViewById(R.id.editText4);
            editcPass=view.findViewById(R.id.editText5);
            signUpBtn=view.findViewById(R.id.button5);
            imageCard=view.findViewById(R.id.imageId);
            googlebtn=view.findViewById(R.id.googlebtn);
            activity=getActivity();
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_sign_up, container, false);
        back=view.findViewById(R.id.textView7);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogInActivity.movePagerBackward();
            }
        });
        runnable.run();
        imageCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });
        ClickListener clickListener=new ClickListener();
        signUpBtn.setOnClickListener(clickListener);
        googlebtn.setOnClickListener(clickListener);

        return view;
    }

    public void showFileChooser() {
        Intent i=new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i,"Select An Image"),PICK);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            filepath=data.getData();
            try {

                bitmap= MediaStore.Images.Media.getBitmap( activity.getContentResolver(),filepath);
                imageCard.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(final View view) {
            email=editEmail.getText().toString().trim();

            if(TextUtils.isEmpty(email))
            {   Toast.makeText(activity,"provide username",Toast.LENGTH_LONG).show();
                return;}
            mdatabase.child("usernames").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.hasChild(email))
                        {
                            if(view.getId()==R.id.button5) {
                                createUser(card);
                            }
                            if(view.getId()==R.id.googlebtn){

                                if(TextUtils.isEmpty(editName.getText().toString().trim()) || filepath==null){
                                    Toast.makeText(activity,"provide name and image",Toast.LENGTH_LONG).show();
                                    return;
                                }

                                new CommonAdapter(activity);
                                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mgoogleApiClient);
                                (activity).startActivityForResult(signInIntent, RC_SIGN_UP);
                                mgoogleApiClient.stopAutoManage( activity);
                                mgoogleApiClient.disconnect();

                            }
                        }

                        else
                            Toast.makeText(activity,"username already exists",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });




        }

    }

    private void createUser(final int userType){

        name=editName.getText().toString().trim();
        password=editPass.getText().toString().trim();
        cpassword=editcPass.getText().toString().trim();

        if(!(TextUtils.isEmpty(name) || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(password) || TextUtils.isEmpty(cpassword)
                || filepath==null)){

            if(password.matches(cpassword)){


                mProgressDialog=new ProgressDialog(getContext());
                mProgressDialog.setTitle("Creating Account...");
                mProgressDialog.show();
                email= email.concat("@catchevent.com");
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Objects.requireNonNull(activity), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateDatabase(Objects.requireNonNull(user),userType);
                                    mProgressDialog.dismiss();
                                    activity.finish();
                                    startActivity(new Intent(getActivity(), MainActivity.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                                }
                                else {
                                    Toast.makeText(activity, "Authentication failed."+task,
                                            Toast.LENGTH_SHORT).show();
                                    mProgressDialog.dismiss();
                                }

                            }
                        });
            }
        }
    }

    private void updateDatabase(final FirebaseUser user,int userType) {

        String uid = user.getUid();
        String username;
        name=editName.getText().toString().trim();
        username=editEmail.getText().toString();
        mdatabase.child("usernames").child(username).setValue(uid);
        mUserdata=  mdatabase.child("users").child(uid);
        mUserdata.child("name").setValue(name);
        mUserdata.child("userType").setValue(userType);
        mUserdata.child("username").setValue(username);
        mUserStorage=mstorage.child("users").child(uid).child(filepath.getLastPathSegment());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if(bitmap.getByteCount()>1024) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        }
        else {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
        }

        final byte[] data1 = baos.toByteArray();
        mUserStorage.putBytes(data1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
               final Uri dnldurl=taskSnapshot.getDownloadUrl();
                mUserdata.child("image").setValue(Objects.requireNonNull(dnldurl).toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                     user.delete();
                     mUserdata.removeValue();
                     Toast.makeText(activity,"Authentication failed Image",Toast.LENGTH_SHORT).show();
            }
        });

    }

     void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    final FirebaseUser user=mAuth.getCurrentUser();
                    mdatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(Objects.requireNonNull(user).getUid())){
                                mAuth.signOut();
                                Toast.makeText(activity,"user already exists",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                 Toast.makeText(activity,"Login successfully",Toast.LENGTH_SHORT).show();
                                updateDatabase(Objects.requireNonNull(user),card);
                                activity.finish();
                                startActivity(new Intent(getActivity(), MainActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                }
                else {
                    Toast.makeText(activity,"Login failed",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

}
