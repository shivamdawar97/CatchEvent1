package dawar.catchevent.EventClasses;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import dawar.catchevent.R;

import static dawar.catchevent.CatchEvent.Uid;

import static dawar.catchevent.CatchEvent.getCompressed;
import static dawar.catchevent.CatchEvent.mdatabase;
import static dawar.catchevent.CatchEvent.mstorage;

public class AddEvent extends AppCompatActivity {
    ImageButton imageView;
    EditText ed1,ed2,ed3,ed4,ed5,ed6,ed7,ed8,ed9;
    private Uri filepath;
    public static final int PICK=234;
    Bitmap bitmap;
  //  private String U1,U2;
  //  int cl,ch;//is cl for parent event (Udbhav/Spardha) and ch-->is for whether it is a special event or not
    private ProgressDialog pd;

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_SCROLL:
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                        return true;
                    case MotionEvent.ACTION_BUTTON_PRESS:
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        Objects.requireNonNull(imm).showSoftInput(ed4, InputMethodManager.SHOW_IMPLICIT);
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

                 bitmap= MediaStore.Images.Media.getBitmap( getContentResolver(),filepath);
                 //URI uri=new URI(filepath.toString());
                 bitmap=getCompressed(bitmap);

                imageView.setImageBitmap(bitmap);
            } catch (IOException /*| URISyntaxException */e) {
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

        if(filepath != null && s2 != null && s3 != null && s4 != null && s9 != null)
        {
            StorageReference riversRef = mstorage.child("images").child(s1).child(filepath.getLastPathSegment());
            pd.setTitle("Updating The Event Details...");
            pd.show();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            final byte[] data1 = baos.toByteArray();

            riversRef.putBytes(data1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests") final Uri dnldurl=taskSnapshot.getDownloadUrl();

                    final DatabaseReference newPost=mdatabase.child("Events").push();
                    newPost.child("image").setValue(Objects.requireNonNull(dnldurl).toString());
                    newPost.child("name").setValue(s1);
                    newPost.child("date").setValue(s2);
                    newPost.child("time").setValue(s3);
                    newPost.child("desc").setValue(s4);
                    newPost.child("venue").setValue(s9);


                    if(!s5.equals("")){
                        newPost.child("regfee").setValue(s5);
                    }if(!s6.equals("")){
                        newPost.child("fstpz").setValue(s6);
                    } if(!s7.equals("")){
                        newPost.child("sndpz").setValue(s7);
                    } if(!s8.equals("")){
                        newPost.child("thrdpz").setValue(s8);
                    }


                    mdatabase.child("users").child(Uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            newPost.child("organiser").child(Uid).setValue(dataSnapshot.child("username").getValue());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    mdatabase.child("users").child(Uid).child("events")
                            .child(newPost.getKey()).setValue(s1);
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
