package dawar.catchevent;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.*;
import com.bumptech.glide.module.GlideModule;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.Calendar;

import javax.net.ssl.HttpsURLConnection;

import id.zelory.compressor.Compressor;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class GalleryActivity extends AppCompatActivity {


    private FloatingTextButton ftb;
    private Uri filepathuri;
    StorageReference mstore;
    String keyID;
    CommonAdapter ca;
    StorageReference riversRef;
    DatabaseReference mdata;
    RecyclerView imagerecycler;
    ProgressDialog mDialog;
    private int t;
    private Bitmap compress;
    private FirebaseUser muser;
    private ArrayList<String> images;
    private ArrayList<String> dates;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ftb=findViewById(R.id.ftb);
        images=new ArrayList<>();
        dates=new ArrayList<>();
        mstore= FirebaseStorage.getInstance().getReference();
        mdata= FirebaseDatabase.getInstance().getReference();
        imagerecycler=findViewById(R.id.gallery_recycler);
        mDialog=new ProgressDialog(this);
        keyID=getIntent().getStringExtra("name");
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(keyID);

        t=getIntent().getIntExtra("view",3);
        ftb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i,"Select An Image"),234);

            }
        });

        if(t==1){
            //For Gallery of an particular a particular Event
            ftb.setVisibility(View.VISIBLE);
            keyID=getIntent().getStringExtra("key");
        mdata.child("Events").child(keyID).child("Gallery").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                s=dataSnapshot.getValue().toString();
                mdata.child("Gallery").child(s).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String s = dataSnapshot.child("url").getValue().toString();
                        images.add(0,s);
                        s = dataSnapshot.child("date").getValue().toString();
                        dates.add(0,s);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        }
        else  if(t==2 || t==4){
            ftb.setVisibility(View.GONE);
            if(t==2) {
                //Get Alerts for a particular Event
                //Else: Alerts of all Events
                keyID = getIntent().getStringExtra("key");
                mdata=mdata.child("Events").child(keyID);
            }
            mdata.child("Alerts").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                 if(t==2) {
                     s = dataSnapshot.getValue().toString();
                     images.add(0, s);
                 }
                 else
                 {
                    s=dataSnapshot.getKey();
                    images.add(0,s);
                 }

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else {
            //For all Gallery Images
            ftb.setVisibility(View.INVISIBLE);
            mdata.child("Gallery").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    s=dataSnapshot.child("url").getValue().toString();
                    images.add(0,s);
                    s = dataSnapshot.child("date").getValue().toString();
                    dates.add(0,s);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();


        if(t==1 || t==3) {
            //Update rescycler View with images for a particular Event or for all
            imagerecycler.setAdapter(new RecyclerView.Adapter<RecyclerViewHolder>() {
                @Override
                public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(GalleryActivity.this).inflate(R.layout.image_view_card, parent, false);
                    return new RecyclerViewHolder(view);
                }

                @Override
                public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {

                    holder.setImg(images.get(position), GalleryActivity.this);
                    holder.mview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bundle b = new Bundle();
                            b.putInt("pos", position);
                            b.putStringArrayList("img", images);
                            b.putStringArrayList("dts", dates);
                            android.support.v4.app.FragmentTransaction t = getSupportFragmentManager().beginTransaction().addToBackStack(null);
                            Image_slider image_slider = new Image_slider();
                            image_slider.setArguments(b);
                            t.replace(R.id.gal_const, image_slider).commit();
                        }
                    });
                }

                @Override
                public int getItemCount() {

                    return images.size();
                }
            });

            imagerecycler.setLayoutManager(new GridLayoutManager(GalleryActivity.this, 2));
        }
        else  {

            imagerecycler.setAdapter(new RecyclerView.Adapter<RecyclerViewHolder>() {
                @Override
                public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                   View view=LayoutInflater.from(GalleryActivity.this).inflate(R.layout.alert_view,parent,false);
                    return new RecyclerViewHolder(view);
                }

                @Override
                public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {
                    mdata=FirebaseDatabase.getInstance().getReference().child("Alerts");
                        mdata.child(images.get(position)).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                String s1 = "<font color='#bf4080'> " + dataSnapshot.child("title").getValue().toString() +
                                        "</font><br/> Event:" + dataSnapshot.child("ename").getValue().toString();
                                String s2 = dataSnapshot.child("date").getValue().toString();
                                holder.setAlert(s1, s2);
                                holder.mview.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                }

                @Override
                public int getItemCount() {
                    return images.size();
                }
            });
            imagerecycler.setLayoutManager(new LinearLayoutManager(GalleryActivity.this));
        }
    }
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {

            if(requestCode==234 && resultCode==RESULT_OK && data!=null && data.getData()!=null) {
                mDialog.setTitle("Uploading");
                mDialog.setMessage("Please Wait!");
                mDialog.show();
                filepathuri = data.getData();


/*
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(filepathuri, filePath, null, null, null);
                cursor.moveToFirst();
                String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                compress = BitmapFactory.decodeFile(imagePath,options);
                cursor.close();
*/

                try {
                    compress=MediaStore.Images.Media.getBitmap( getContentResolver(),filepathuri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compress.compress(Bitmap.CompressFormat.JPEG, 0, baos);

                byte[] data1 = baos.toByteArray();

                riversRef=mstore.child("images").child(getIntent().getStringExtra("name"));
                riversRef = riversRef.child(filepathuri.getLastPathSegment());
                riversRef.putBytes(data1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressWarnings("VisibleForTests") final Uri dnldurl=taskSnapshot.getDownloadUrl();
                        String s= Calendar.getInstance().getTime().toString();
                       DatabaseReference newpost=mdata.child("Gallery").push();
                        newpost.child("url").setValue(dnldurl.toString());
                        newpost.child("date").setValue(s);
                        s=newpost.getKey();
                        newpost=mdata.child("Events").child(keyID).child("Gallery").push();
                        newpost.setValue(s);
                        Toast.makeText(GalleryActivity.this,"Image Uploaded Succesfully",
                                Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(GalleryActivity.this,"Uploadation Failed",Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                    }
                });


            }
            else {
                mAuth=FirebaseAuth.getInstance();
                ca=new CommonAdapter(GalleryActivity.this,mAuth );
                ca.onActivityOfResult(requestCode, resultCode, data);
            }
        }
    public static class RecyclerViewHolder extends RecyclerView.ViewHolder{
        View mview;
        public RecyclerViewHolder(View itemView) {
            super(itemView);
            mview=itemView;
        }
        public void setImg(final String img, final Context ctx){
            final ImageView im=mview.findViewById(R.id.gallery_img);
           // Toast.makeText(ctx,img,Toast.LENGTH_SHORT).show();
            final ProgressDialog pd=new ProgressDialog(ctx);
            pd.setTitle("Please wait...");
            pd.show();
            Picasso.with(ctx).load(img).networkPolicy(NetworkPolicy.OFFLINE).into(im, new Callback() {
                @Override
                public void onSuccess() {
                    pd.dismiss();
                }

                @Override
                public void onError() {
                    Picasso.with(ctx).load(img).into(im);
                    pd.dismiss();
                }
            });
           // im.setImageBitmap(img);

        }

        public void setAlert(String s1, String s2){
            TextView title=mview.findViewById(R.id.alert_title2);
            title.setText(Html.fromHtml(s1));
            //title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googleg_standard_color_18,0,0,0);
            title=mview.findViewById(R.id.alert_date);
            title.setText(s2);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        mAuth=FirebaseAuth.getInstance();
        muser=mAuth.getCurrentUser();
        if(muser==null)
        {
            MenuItem item=menu.findItem(R.id.action_logout);
            item.setVisible(false);
            item=menu.findItem(R.id.action_settings);
            item.setVisible(false);
        }
        else {
            MenuItem item =menu.findItem(R.id.action_login);
            item.setVisible(false);
        }
        this.invalidateOptionsMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id==android.R.id.home){
            finish();
        }
        if (id == R.id.action_settings) {
            ca=new CommonAdapter(GalleryActivity.this,mAuth);
            ca.showSettings(-1);
            return true;
        }

        if(id == R.id.action_login){
            CommonAdapter ca=new CommonAdapter(GalleryActivity.this,mAuth);
            ca.signIn();
            return true;
        }
        if(id==R.id.action_logout){
            if(muser!=null) {
                mAuth.signOut();
                synchronized (mAuth) {
                    muser=null;
                    mAuth.notify();
                }
                Snackbar.make(getCurrentFocus(), "Logged Out Successfully", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
            else    {
                Snackbar.make(getCurrentFocus(), "You are not Logged In", Snackbar.LENGTH_LONG).setAction("Action", null).show();

            }

        }

        return super.onOptionsItemSelected(item);
    }
    }

