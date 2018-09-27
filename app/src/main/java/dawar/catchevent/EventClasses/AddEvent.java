package dawar.catchevent.EventClasses;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import dawar.catchevent.R;

import static dawar.catchevent.CatchEvent.Uid;

import static dawar.catchevent.CatchEvent.getCompressed;
import static dawar.catchevent.CatchEvent.mdatabase;
import static dawar.catchevent.CatchEvent.mstorage;

public class AddEvent extends AppCompatActivity {
    ImageButton imageView;
    EditText ed1,ed2,ed3,ed4,ed5,ed6,ed7,ed8,ed9;
    CheckBox cBox;
    private Uri filepath;
    public static final int PICK=234;
    Bitmap bitmap;
    LinearLayout linearLayout;
  //  private String U1,U2;
  //  int cl,ch;//is cl for parent event (Udbhav/Spardha) and ch-->is for whether it is a special event or not
    private ProgressDialog pd;
    private int t=0;

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ScrollView scroll=findViewById(R.id.scrollView);

        linearLayout=findViewById(R.id.prize_linear);
        collapse(linearLayout);

        imageView=findViewById(R.id.add_event_image);
        cBox=findViewById(R.id.check_box);
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

        cBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
//            linearLayout.setVisibility(View.VISIBLE);
                    expand(linearLayout);
                    scroll.fullScroll(View.FOCUS_DOWN);
            t=1;
                }
                else {
                    t=0;
//                    linearLayout.setVisibility(View.GONE);
                    collapse(linearLayout);
                }
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
        final String s1=ed1.getText().toString().trim();
        final String s2=ed2.getText().toString().trim();
        final String s3=ed3.getText().toString().trim();
        final String s4=ed4.getText().toString().trim();
        final String s5=ed5.getText().toString().trim();
        final String s6=ed6.getText().toString().trim();
        final String s7=ed7.getText().toString().trim();
        final String s8=ed8.getText().toString().trim();
        final String s9=ed9.getText().toString().trim();

        if(filepath != null && !TextUtils.isEmpty(s2) && !TextUtils.isEmpty(s3) &&
                !TextUtils.isEmpty(s4) && !TextUtils.isEmpty(s5) && !TextUtils.isEmpty(s9))
        {
            final StorageReference ref = mstorage.child("images").child(s1).child(filepath.getLastPathSegment());
            pd.setTitle("Updating The Event Details...");
            pd.show();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            final byte[] data1 = baos.toByteArray();
            UploadTask uploadTask=ref.putBytes(data1);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {

                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        @SuppressWarnings("VisibleForTests") final Uri dnldurl = task.getResult();
                        final DatabaseReference newPost = mdatabase.child("Events").push();
                        final Map<String, String> data = new HashMap<>();
                        data.put("image", Objects.requireNonNull(dnldurl).toString());
                        data.put("name", s1);
                        data.put("date", s2);
                        data.put("time", s3);
                        data.put("desc", s4);
                        data.put("venue", s9);
                        data.put("regfee", s5);

                        if (t == 1) {
                            if (!s6.equals("")) {
                                data.put("fstpz", s6);
                            }
                            if (!s7.equals("")) {
                                data.put("sndpz", s7);
                            }
                            if (!s8.equals("")) {
                                data.put("thrdpz", s8);
                            }
                        }

                        mdatabase.child("users").child(Uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                newPost.setValue(data);
                                newPost.child("organiser").child(Uid).setValue(dataSnapshot.child("username").getValue());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        mdatabase.child("users").child(Uid).child("events")
                                .child(newPost.getKey()).setValue(s1);

                        Toast.makeText(AddEvent.this, "Event Created", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        // Handle failures
                        // ...
                        pd.dismiss();
                        Toast.makeText(AddEvent.this, "Event creation failed", Toast.LENGTH_LONG).show();

                    }
                }
            });


         /*   if (task.isSuccessful()){
                @SuppressWarnings("VisibleForTests") final Uri dnldurl = riversRef.getDownloadUrl().getResult();
                final DatabaseReference newPost = mdatabase.child("Events").push();
                final Map<String, String> data = new HashMap<>();
                data.put("image", Objects.requireNonNull(dnldurl).toString());
                data.put("name", s1);
                data.put("date", s2);
                data.put("time", s3);
                data.put("desc", s4);
                data.put("venue", s9);
                data.put("regfee", s5);

                if (t == 1) {
                    if (!s6.equals("")) {
                        data.put("fstpz", s6);
                    }
                    if (!s7.equals("")) {
                        data.put("sndpz", s7);
                    }
                    if (!s8.equals("")) {
                        data.put("thrdpz", s8);
                    }
                }

                mdatabase.child("users").child(Uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        newPost.setValue(data);
                        newPost.child("organiser").child(Uid).setValue(dataSnapshot.child("username").getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                mdatabase.child("users").child(Uid).child("events")
                        .child(newPost.getKey()).setValue(s1);

                Toast.makeText(AddEvent.this, "Event Created", Toast.LENGTH_LONG).show();
                finish();
            }
            else {
                pd.dismiss();
                Toast.makeText(AddEvent.this, "Event creation failed", Toast.LENGTH_LONG).show();

            }*/
        }
        else
        {
            Snackbar.make(view, "Some of the fields may Empty", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

        }

    }
    public static void expand(final View v) {
        v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }
}
