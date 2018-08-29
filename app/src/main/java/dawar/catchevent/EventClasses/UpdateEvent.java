package dawar.catchevent.EventClasses;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import dawar.catchevent.R;

import static dawar.catchevent.CatchEvent.getCompressed;
import static dawar.catchevent.CatchEvent.mdatabase;
import static dawar.catchevent.CatchEvent.mstorage;
import static dawar.catchevent.EventClasses.AddEvent.PICK;

public class UpdateEvent extends AppCompatActivity {

    //ImageView eventImage;
    EditText editDesc;
    Spinner spinner;
    Bitmap bitmap;
    String key;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_event);

        //eventImage=findViewById(R.id.edit_imageView3);
        ((TextView)findViewById(R.id.event_name)).setText(getIntent().getStringExtra("name"));
        spinner=findViewById(R.id.edit_spinner);
        editDesc=findViewById(R.id.edit_desc);
        key=getIntent().getStringExtra("key");
        List<String> list=new ArrayList<>();
        list.add("date");
        list.add("description");
        list.add("registration fee");
        list.add("time");
        list.add("venue");
        ArrayAdapter<String> aa = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,list);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);
    bitmap=null;
        editDesc.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_SCROLL:
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                        return true;
                    case MotionEvent.ACTION_BUTTON_PRESS:
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        Objects.requireNonNull(imm).showSoftInput(editDesc, InputMethodManager.SHOW_IMPLICIT);
                }
                return false;
            }
        });
    }

    public void updateEvent(View view) {
     switch (view.getId()){

         /*
         case R.id.edit_imageView3:
             Intent i=new Intent();
             i.setType("image/*");
             i.setAction(Intent.ACTION_GET_CONTENT);
             startActivityForResult(Intent.createChooser(i,"Select An Image"),PICK);
             break;
             */
         case R.id.button2:
             /*
                    if(bitmap!=null ) {
                        final ProgressDialog pd=new ProgressDialog(UpdateEvent.this);
                        final DatabaseReference reference= mdatabase.child("Events").child(getIntent().getStringExtra("key")).child("image");
                        pd.setTitle("Updating The Event Details...");
                        pd.show();
                        final Uri[] url = new Uri[1];

                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                mstorage = FirebaseStorage.getInstance().getReferenceFromUrl(dataSnapshot.getValue().toString());

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                                Locale.getDefault()).format(new Date());
                        final StorageReference riversRef = mstorage.child("images").child(getIntent().getStringExtra("name")).child("MAN_"+timeStamp);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        final byte[] data1 = baos.toByteArray();

                        riversRef.putBytes(data1).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()){
                                    url[0] =task.getResult().getDownloadUrl();
                                    reference.setValue(url[0].toString());
                                   mstorage.delete();
                                   mstorage=FirebaseStorage.getInstance().getReference();
                                }
                            }
                        });


                        pd.dismiss();
                    }
                    */
                    if(!TextUtils.isEmpty(editDesc.getText().toString())){

                    String s=(String) spinner.getSelectedItem();
                       if(s.matches("registration fee"))
                           s="regfee";
                       if(s.matches("description"))
                           s="desc";
                    mdatabase.child("Events").child(key).child(s).setValue(editDesc.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"field updated",Toast.LENGTH_LONG).show();
                                editDesc.clearComposingText();
                            }
                        }
                    });
                    }
             break;

         case R.id.button3:
             editDesc=findViewById(R.id.add_org);
             final String[] uid=new String[1];
             if(!TextUtils.isEmpty(editDesc.getText().toString())){
                 mdatabase.child("usernames").addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(DataSnapshot dataSnapshot) {
                         if(!dataSnapshot.hasChild(editDesc.getText().toString()))
                         {
                             Toast.makeText(getApplicationContext(),"username doesn't exists",Toast.LENGTH_LONG).show();

                         }
                         else {

                             mdatabase.child("usernames").child(editDesc.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                 @Override
                                 public void onDataChange(DataSnapshot dataSnapshot) {
                                     uid[0]=dataSnapshot.getValue().toString();
                                 }

                                 @Override
                                 public void onCancelled(DatabaseError databaseError) {

                                 }
                             });
                             mdatabase.child("users").child(uid[0]).addListenerForSingleValueEvent(new ValueEventListener() {
                                 @Override
                                 public void onDataChange(DataSnapshot dataSnapshot) {
                                     if(dataSnapshot.child("userType").getValue().toString().equals("2")){
                                         mdatabase.child("Events").child(key).child("organiser")
                                                 .child(uid[0]).setValue(dataSnapshot.getKey());
                                         mdatabase.child("users").child(uid[0]).child("events").child(key)
                                                 .setValue(getIntent().getStringExtra("name"));
                                         Toast.makeText(getApplicationContext(),dataSnapshot.getKey()+" added",Toast.LENGTH_LONG).show();
                                         finish();

                                     }
                                     else {
                                         Toast.makeText(getApplicationContext(),"user isn't organiser",Toast.LENGTH_LONG).show();
                                     }
                                 }
                                 @Override
                                 public void onCancelled(DatabaseError databaseError) {

                                 }
                             });
                         }

                     }

                     @Override
                     public void onCancelled(DatabaseError databaseError) {

                     }
                 });
             }
     }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK && resultCode==RESULT_OK && data!=null && data.getData()!=null){
          Uri filepath=data.getData();

            try {

                bitmap= MediaStore.Images.Media.getBitmap( getContentResolver(),filepath);
                bitmap=getCompressed(bitmap);
               // eventImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
