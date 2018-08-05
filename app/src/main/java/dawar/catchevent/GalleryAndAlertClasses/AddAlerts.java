package dawar.catchevent.GalleryAndAlertClasses;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import dawar.catchevent.R;

public class AddAlerts extends AppCompatActivity {
    DatabaseReference mdata;
    String postkey;
    EditText title,desc;
    RecyclerView recycler;
    ProgressDialog pd;
    ArrayList<Bitmap> bitmaps;
    ArrayList<String> captions,date;
    private StorageReference riversRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alerts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        title=findViewById(R.id.edit_title);
        desc=findViewById(R.id.edit_desc);
        pd=new ProgressDialog(this);
        TextView related=findViewById(R.id.related_event);
        postkey=getIntent().getStringExtra("name");
        riversRef= FirebaseStorage.getInstance().getReference().child("images")
        .child(postkey);
        actionBar.setTitle("Add an Alert>>"+postkey);
        related.append("\n "+postkey);
        postkey=getIntent().getStringExtra("key");


        bitmaps=new ArrayList<>();
        captions=new ArrayList<>();
        date=new ArrayList<>();
        recycler=findViewById(R.id.alert_recycler);
        mdata = FirebaseDatabase.getInstance().getReference();
        // ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_list_item_1,);
        // aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // spinner.setAdapter(aa);
        recycler.setAdapter(new RecyclerView.Adapter<GalleryActivity.RecyclerViewHolder>() {
            @Override
            public GalleryActivity.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(AddAlerts.this).inflate(R.layout.image_view_card, parent, false);
                return new GalleryActivity.RecyclerViewHolder(view);
            }

            @Override
            public void onBindViewHolder(GalleryActivity.RecyclerViewHolder holder, int position) {
                holder.setMviewforAlert(bitmaps.get(position),AddAlerts.this);

            }



            @Override
            public int getItemCount() {
                return bitmaps.size();
            }
        });
        recycler.setLayoutManager(new GridLayoutManager(AddAlerts.this,2));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;

    }

    public void updateAlert(View view) {
       if(title.getText().toString().isEmpty() || desc.getText().toString().isEmpty()
               || bitmaps==null){
           Toast.makeText(AddAlerts.this,"Update fields",Toast.LENGTH_SHORT).show();
       }
       else

       {
           pd.setTitle("Please wait...");
           pd.show();
           DatabaseReference newPost=mdata.child("Alerts").push();
            String s = title.getText().toString();
           newPost.child("title").setValue(s);
           riversRef = riversRef.child(s);
           s =desc.getText().toString();
           newPost.child("desc").setValue(s);
           s = Calendar.getInstance().getTime().toString();
           newPost.child("date").setValue(s);
           s =getIntent().getStringExtra("name");
           newPost.child("ename").setValue(s);
           s =newPost.getKey();
           newPost=newPost.child("images");

           for(int i=0;i<bitmaps.size();i++){

               ByteArrayOutputStream baos = new ByteArrayOutputStream();
               if(bitmaps.get(i).getByteCount()>1024) {
                   bitmaps.get(i).compress(Bitmap.CompressFormat.JPEG, 40, baos);
               }
               else {
                   bitmaps.get(i).compress(Bitmap.CompressFormat.JPEG, 60, baos);

               }
               final byte[] data1 = baos.toByteArray();
               final int finalI = i;
               final DatabaseReference finalNewPost = newPost;
               riversRef.child( UUID.randomUUID().toString()).putBytes(data1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                   @Override
                   public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                       @SuppressWarnings("VisibleForTests")
                       final Uri dnldurl=taskSnapshot.getDownloadUrl();
                       DatabaseReference newpost1=mdata.child("Gallery").push();
                       newpost1.child("url").setValue(dnldurl.toString());
                       newpost1.child("date").setValue(date.get(finalI));
                       newpost1.child("captn").setValue(captions.get(finalI));
                       String s =newpost1.getKey();
                       newpost1=mdata.child("Events").child(postkey).child("Gallery").push();
                       newpost1.setValue(s);
                       finalNewPost.push().setValue(s);
                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Toast.makeText(AddAlerts.this,"Uploadation Failed",Toast.LENGTH_SHORT).show();
                   }
               });

           }
           newPost=mdata.child("Events").child(postkey).child("Alerts").push();
           newPost.setValue(s).addOnCompleteListener(new OnCompleteListener<Void>() {
               @Override
               public void onComplete(@NonNull Task<Void> task) {
                   pd.dismiss();
                   finish();
               }
           });
       }
    }


    public void addImages(View view) {
        Intent i=new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i,"Select An Image"),214);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==214 && data.getData()!=null){

            final AlertDialog.Builder builder=new AlertDialog.Builder(AddAlerts.this);
            final EditText caption=new EditText(AddAlerts.this);
            final String[] cptn = new String[1];
            builder.setTitle("Add a Caption");
            builder.setView(caption).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                        cptn[0]=caption.getText().toString();
                        if(!cptn[0].isEmpty()){
                            dialogInterface.dismiss();
                            Bitmap bitmap;
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),data.getData());
                                bitmaps.add(0,bitmap);
                                captions.add(0,cptn[0]);
                                date.add(0,Calendar.getInstance().getTime().toString());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            recycler.getAdapter().notifyDataSetChanged();
                            dialogInterface.dismiss();
                        }
                        else {
                            Toast.makeText(AddAlerts.this,"Caption not provided",Toast.LENGTH_SHORT).show();
                        }
                }
            });
            builder.show();
        }
    }
}