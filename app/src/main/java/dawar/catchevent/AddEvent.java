package dawar.catchevent;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.test.suitebuilder.annotation.Suppress;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.ClientCertRequest;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class AddEvent extends AppCompatActivity {
    ImageButton imageView;
    EditText ed1,ed2,ed3,ed4,ed5,ed6,ed7,ed8,ed9;
    private Uri filepath;
    public static final int PICK=234;
    private StorageReference mStorageRef;
    private ProgressDialog pd;
    DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mStorageRef= FirebaseStorage.getInstance().getReference();
        mDatabaseRef= FirebaseDatabase.getInstance().getReference();
        imageView=findViewById(R.id.add_event_image);
       // View view=getLayoutInflater().inflate(R.layout.content_add_event,null);
        pd= new ProgressDialog(this);
        ed1=findViewById(R.id.edit_ename);
        ed2=findViewById(R.id.event_date);
        ed3=findViewById(R.id.event_timings);
        ed4=findViewById(R.id.event_desc);
        ed5=findViewById(R.id.reg_fee);
        ed6=findViewById(R.id.fst_prize);
        ed7=findViewById(R.id.snd_prize);
        ed8=findViewById(R.id.thrd_prize);
        ed9=findViewById(R.id.event_venue);

      ed4.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        return true;
                }
                return false;
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            filepath=data.getData();
            try {

                Bitmap bitmap= MediaStore.Images.Media.getBitmap( getContentResolver(),filepath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void showFileChooser(View view) {
        Intent i=new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i,"Select An Image"),PICK);

    }

    public void createEvent(View view) {
        final String s1=ed1.getText().toString();
        final String s2=ed2.getText().toString();
        final String s3=ed3.getText().toString();
        final String s4=ed4.getText().toString();
        final String s5=ed5.getText().toString().trim();
        final String s6=ed6.getText().toString().trim();
        final String s7=ed7.getText().toString().trim();
        final String s8=ed8.getText().toString().trim();
        final String s9=ed9.getText().toString();
        if(filepath!=null && s1!=null && s2!=null && s3!=null && s4!=null && s9!=null)
        {
            StorageReference riversRef = mStorageRef.child("images").child(s1).child(filepath.getLastPathSegment());
            pd.setTitle("Updating The Event Details...");
            pd.show();

            riversRef.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests") final Uri dnldurl=taskSnapshot.getDownloadUrl();

                    DatabaseReference newPost=mDatabaseRef.child("Events").push();
                    newPost.child("name").setValue(s1);
                    newPost.child("date").setValue(s2);
                    newPost.child("time").setValue(s3);
                    newPost.child("desc").setValue(s4);
                    newPost.child("venue").setValue(s9);
                    newPost.child("image").setValue(dnldurl.toString());
                    if(!s5.equals("")){
                        newPost.child("regfee").setValue(s5);
                    }if(!s6.equals("")){
                        newPost.child("fstpz").setValue(s6);
                    } if(!s7.equals("")){
                        newPost.child("sndpz").setValue(s7);
                    } if(!s8.equals("")){
                        newPost.child("thrdpz").setValue(s8);
                    }

                    pd.dismiss();
                    Toast.makeText(AddEvent.this,"Event Created",Toast.LENGTH_LONG).show();
                    finish();
                }
            });


        }
        else
        {
            Snackbar.make(view, "Some of the fields may Empty", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

        }

    }

}
