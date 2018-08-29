package dawar.catchevent.GalleryAndAlertClasses;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.support.v4.app.LoaderManager;
import android.app.ProgressDialog;
import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import static dawar.catchevent.CatchEvent.getCompressed;
import static dawar.catchevent.CatchEvent.getTime;
import static dawar.catchevent.CatchEvent.mdatabase;
import static dawar.catchevent.CatchEvent.mstorage;
import static dawar.catchevent.CatchEvent.sdatabase;

import dawar.catchevent.CatchEvent;
import dawar.catchevent.SettingsClasses.SettingsActivity;
import dawar.catchevent.LogInClasses.LogInActivity;
import dawar.catchevent.R;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class GalleryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {


    private Uri filepathuri;
    String keyID;
    StorageReference riversRef;
    RecyclerView imagerecycler;
    ProgressDialog mDialog;
    LoaderClasses loaders;
    private Bitmap compress;
    private FirebaseUser muser;
    private FirebaseAuth mAuth;
    private FloatingTextButton ftb;
    int i=0;
    int viewType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gallery);
        Toolbar toolbar =  findViewById(R.id.gallery_toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setTitle(getIntent().getStringExtra("name"));
        ftb= findViewById(R.id.ftb);

        viewType= getIntent().getIntExtra("view",1);

        imagerecycler=findViewById(R.id.gallery_recycler);
        mDialog=new ProgressDialog(this);

        keyID=getIntent().getStringExtra("key");

        ftb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i,"Select An Image"),234);

            }
        });

        Bundle b=new Bundle();

        if(viewType < 3) {

            getSupportActionBar().setTitle("Gallery");
            ImagesAdapter adapter=new ImagesAdapter(this);
            imagerecycler.setAdapter(adapter);
            ImagesAdapter.images.clear();
            ImagesAdapter.captns.clear();
            imagerecycler.setLayoutManager(new GridLayoutManager(GalleryActivity.this, 2));
            loaders=new LoaderClasses(this,adapter);


            if (viewType == 2) {
                b.putInt("type", GalleryProvider.EVENT_IMAGES);
                b.putString("eventKey", getIntent().getStringExtra("key"));

            } else {
                ftb.setVisibility(View.INVISIBLE);
                b.putInt("type", GalleryProvider.ALL_IMAGES);
            }

            getSupportLoaderManager().initLoader(i, b, loaders.new GalleryCallbacks()).forceLoad();
            i++;
        }
        else
            {
                ftb.setVisibility(View.INVISIBLE);
                getSupportActionBar().setTitle("Alerts");
                AlertsAdapter adapter=new AlertsAdapter(this);
                imagerecycler.setAdapter(adapter);
                imagerecycler.setLayoutManager(new LinearLayoutManager(this));
                loaders=new LoaderClasses(this,adapter);

            if (viewType == 5) {

                Log.i("LogMessage","selected event alerts");
                b.putInt("type", GalleryProvider.EVENT_ALERTS);
                b.putString("eventKey", keyID);

            } else {

                Log.i("LogMessage","selected all alerts");
                ftb.setVisibility(View.INVISIBLE);
                b.putInt("type", GalleryProvider.ALL_ALERTS);
            }

            getSupportLoaderManager().initLoader(i, b, loaders.new GalleryCallbacks()).forceLoad();
            i++;
        }

        getSupportLoaderManager().initLoader(i,null,this).forceLoad();
        i++;

    }


    @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {

            if(requestCode==234 && resultCode==RESULT_OK && data!=null && data.getData()!=null) {

                final AlertDialog.Builder builder=new AlertDialog.Builder(GalleryActivity.this);
                final EditText caption=new EditText(GalleryActivity.this);
                final String[] cptn = new String[1];
                mDialog.setTitle("Uploading");
                mDialog.setMessage("Please Wait!");

                filepathuri = data.getData();
                try {
                    compress= MediaStore.Images.Media.getBitmap( getContentResolver(),filepathuri);
                    compress=getCompressed(compress);
                      }
                      catch (IOException e) {
                    e.printStackTrace();
                }


                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compress.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] data1 = baos.toByteArray();
                builder.setView(caption).setTitle("Enter a Caption:");

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cptn[0] =caption.getText().toString();

                            dialogInterface.dismiss();
                            mDialog.show();
                            riversRef=mstorage.child("images").child(getIntent().getStringExtra("name"));
                            riversRef = riversRef.child(filepathuri.getLastPathSegment());
                            riversRef.putBytes(data1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    @SuppressWarnings("VisibleForTests") final Uri dnldurl=taskSnapshot.getDownloadUrl();
                                    DatabaseReference newpost=mdatabase.child("Gallery").push();
                                    cptn[0]=cptn[0]+"\n"+getTime()+"\n Event:"+getIntent().getStringExtra("name");
                                    newpost.child("url").setValue(Objects.requireNonNull(dnldurl).toString());
                                    newpost.child("eventKey").setValue(keyID);
                                    newpost.child("captn").setValue(cptn[0]);
                                    mdatabase.child("Events").child(keyID).child("Gallery").child(newpost.getKey()).setValue(cptn[0]);
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
                });


                builder.show();
            }
        }

    @NonNull
    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {

        return new ImageOrAlertKeysLoader(this,viewType);
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object o) {

        if(viewType<3)
            initiateFirebaseForGallery();
        else
            initiateFirebaseForAlerts();

    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    private static class ImageOrAlertKeysLoader extends AsyncTaskLoader {
        Cursor cursor;
        int vType;
       ImageOrAlertKeysLoader(Context context,int vType) {
            super(context);
            this.vType=vType;

        }

        @Override
        public Object loadInBackground() {

           if(vType<3)
            cursor = sdatabase.query("Gallery", new String[]{"imgKey"}, null,
                    null, null, null, null);
           else
               cursor = sdatabase.query("Alerts", new String[]{"altKey"}, null,
                       null, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                        if(vType<3)
                    CatchEvent.imgKeys.add(cursor.getString(0));
                        else
                    CatchEvent.altKeys.add(cursor.getString(0));

                } while (cursor.moveToNext());

            }
            return null;
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
            MenuItem item;
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id==android.R.id.home){
            finish();
        }
        if (id == R.id.action_settings) {
            Intent i=new Intent(GalleryActivity.this,SettingsActivity.class);
            i.putExtra("sett",-1);
            startActivity(i);
            return true;
        }

        if(id == R.id.action_login){
            startActivity(new Intent(GalleryActivity.this,LogInActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




     void initiateFirebaseForGallery()

    {
        mdatabase.child("Gallery").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                try {
                    s=dataSnapshot.getKey();
                    if(!CatchEvent.imgKeys.contains(s))

                    {
                        Bundle bundle = new Bundle();
                        bundle.putString("imgKey", s);
                        s = Objects.requireNonNull(dataSnapshot.child("url").getValue()).toString();
                        bundle.putString("url", s);
                        s = Objects.requireNonNull(dataSnapshot.child("eventKey").getValue()).toString();
                        bundle.putString("eventKey", s);
                        s = Objects.requireNonNull(dataSnapshot.child("captn").getValue()).toString();
                        bundle.putString("captn", s);
                        getSupportLoaderManager().initLoader(i, bundle, loaders.new FirebaseImageCallbacks()).forceLoad();
                        i++;
                    }
                }
                catch (NullPointerException e){
                    e.printStackTrace();
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

    private void initiateFirebaseForAlerts()

    {
        mdatabase.child("Alerts").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    try {
                        s=dataSnapshot.getKey();
                        if(!CatchEvent.altKeys.contains(s)){

                            ContentValues cv=new ContentValues();
                            cv.put("altKey",s);
                            AlertsAdapter.altKeys.add(s);

                            /*
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            DataOutputStream out = new DataOutputStream(baos);

                            for(DataSnapshot s1: dataSnapshot.child("Images").getChildren()){

                                out.writeUTF(Objects.requireNonNull(s1.getValue()).toString());
                            }

                            byte[] bytes = baos.toByteArray();
                            cv.put("imgKeys",bytes);
                            */

                            s= Objects.requireNonNull(dataSnapshot.child("eventkey").getValue()).toString();
                            cv.put("eventKey",s);

                            sdatabase.insert("Alerts",null,cv);
                        }

                    }
                    catch (NullPointerException p){
                        p.printStackTrace();
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

}

