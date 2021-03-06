package dawar.catchevent;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.ArrayAdapter;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;
import dawar.catchevent.GalleryAndAlertClasses.GalleryActivity;
import dawar.catchevent.LogInClasses.LogInActivity;
import dawar.catchevent.SettingsClasses.SettingsActivity;


import static dawar.catchevent.CatchEvent.Uid;
import static dawar.catchevent.CatchEvent.getUserType;
import static dawar.catchevent.CatchEvent.mdatabase;
import static dawar.catchevent.CatchEvent.sdatabase;
import static dawar.catchevent.CatchEvent.userType;
import  static dawar.catchevent.CatchEvent.height;
import  static dawar.catchevent.CatchEvent.width;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    RecyclerView rv;
    FirebaseUser muser;
    AlertDialog.Builder builder;
    Cursor cursor;
    android.support.v4.app.FragmentTransaction ft;
    ArrayList<String> titles,Events;
    ArrayList<Bitmap> images;
    private FirebaseAuth mAuth;
    int i=0;
    RecyclerAdapter recyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        builder = new AlertDialog.Builder(this);
        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        titles=new ArrayList<>();
        images=new ArrayList<>();
        Events=new ArrayList<>();

        rv=  findViewById(R.id.recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(this));
        recyclerAdapter=new RecyclerAdapter(this,titles,images,Events);
        rv.setAdapter(recyclerAdapter);

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

       NavigationView navigationView =  findViewById(R.id.nav_view);
       navigationView.setNavigationItemSelectedListener(this);

       getLoaderManager().initLoader(i,null,new LoaderCallbacks()).forceLoad();
       i++;

       startService(new Intent(this,FirebaseService.class));

        Display display = getWindowManager(). getDefaultDisplay();
        Point size = new Point();
        display. getSize(size);
        width= size. x;
        height = size. y;
     }

     private void initiateFirebaseLoaders(){

         mdatabase.child("Events").addChildEventListener(new ChildEventListener() {
             @Override
             public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
              try {

                  s=dataSnapshot.getKey();
                  if(Events==null || !Events.contains(s))
                  {
                      Bundle bundle=new Bundle();
                      bundle.putString("key",s);

                      s = Objects.requireNonNull(dataSnapshot.child("image").getValue()).toString();
                      bundle.putString("url",s);

                      s= Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                      bundle.putString("title",s);

                      getLoaderManager().initLoader(i,bundle,new FirebaseCallbacks()).forceLoad();
                      i++;
                  }
              }catch (NullPointerException e){
                 e.printStackTrace();
                 // finish();
                //  startActivity(getIntent());
              }

             }

             @Override
             public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {

             }

             @Override
             public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                 String s=dataSnapshot.getKey();
                 if(Events.contains(s)){
                     int i=Events.indexOf(s);
                     Events.remove(i);
                     titles.remove(i);
                     images.remove(i);
                     recyclerAdapter.updateData(titles,images,Events);
                     recyclerAdapter.notifyDataSetChanged();
                     sdatabase.delete("Events","EventKey=?",new String[]{s});
                 }
             }

             @Override
             public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });

     }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.


    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(ft == null){
            finish();
        }
        else {
            finish();
            startActivity(getIntent());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent i=new Intent(MainActivity.this,SettingsActivity.class);
            i.putExtra("sett",-1);
            startActivity(i);
            return true;
        }

        if(id == R.id.action_login){
            startActivity(new Intent(MainActivity.this, LogInActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        Intent intent;
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            muser = mAuth.getCurrentUser();
            getUserType();
            if (muser == null || userType==1 ) {
                Toast.makeText(MainActivity.this, "You are not Registered as Organiser", Toast.LENGTH_SHORT).show();

            } else {
                getUserType();
                AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
                ab.setTitle("Choose Image Source:");
                ab.setPositiveButton("CAMERA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            //Permission Denied
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 2);
                        } else {
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, 9);

                        }
                    }
                });
                ab.setNegativeButton("GALLERY", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                //Permission Denied
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                            } else {
                                Intent i2 = new Intent();
                                i2.setType("image/*");
                                i2.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(i2, "Select An Image"), 8);

                            }
                        }
                    }
                });
                ab.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                ab.show();
                //here
            }
        } else if (id == R.id.nav_gallery) {

            intent = new Intent(this, GalleryActivity.class);
            intent.putExtra("view", 1);
            startActivity(intent);

        } else if (id == R.id.nav_Alerts) {

            intent = new Intent(this, GalleryActivity.class);
            intent.putExtra("view", 4);
            intent.putExtra("name", "Alerts");
            startActivity(intent);

        } else if (id == R.id.nav_aboutus) {
            intent = new Intent(MainActivity.this, AboutUs.class);
            intent.putExtra("pg", 1);
            startActivity(intent);


        } else if (id == R.id.nav_send_feedback) {
            intent = new Intent(MainActivity.this, AboutUs.class);
            intent.putExtra("pg", 2);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
            @Override
            public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                if(requestCode==1 && permissions== new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}){
                    Intent i=new Intent();
                    i.setType("image/*");
                    i.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(i,"Select An Image"),8);

                }
                if(requestCode==2 ){
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 9);
                }
            }


    @Override
    protected void onActivityResult(final int requestCode, int resultCode, final Intent data) {

        if((requestCode==9 || requestCode==8) && data!=null ){

            final ArrayList<String> keys;
            keys=new ArrayList<>();
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
            builderSingle.setTitle("Select One Name:-");
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_singlechoice);

            //TODO: FOR SINGLE ORGANISER

            if(userType==2) {
                mdatabase.child("users").child(Uid).child("events").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            keys.add(snapshot.getKey());
                            arrayAdapter.add(Objects.requireNonNull(snapshot.getValue()).toString());

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            else if(userType==3) {

                mdatabase.child("Events").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                            keys.add(snapshot.getKey());
                            arrayAdapter.add(Objects.requireNonNull(snapshot.child("name").getValue()).toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }




            builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    final String strName = arrayAdapter.getItem(which);
                   final Uri filepathuri = data.getData();
                    final StorageReference[] mstore = {FirebaseStorage.getInstance().getReference()};
                    final Bitmap[] compress = {null};
                   final ProgressDialog mDialog=new ProgressDialog(MainActivity.this);
                   final AlertDialog.Builder abuilder=new AlertDialog.Builder(MainActivity.this);
                    final EditText caption=new EditText(MainActivity.this);
                    abuilder.setView(caption).setTitle("Enter a Caption:");
                    abuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface2, int i) {
                            final String s3=caption.getText().toString();
                            if(s3.isEmpty()) {
                                dialogInterface2.dismiss();
                                dialog.dismiss();
                            }
                            else {
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                byte[] data1 = new byte[0];
                                if (requestCode == 9 && data.getExtras()!=null) {
                                    compress[0] = (Bitmap) data.getExtras().get("data");
                                    compress[0].compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    data1=baos.toByteArray();
                                } else {
                                    try {
                                        compress[0] = MediaStore.Images.Media.getBitmap(getContentResolver(), filepathuri);
                                        compress[0].compress(Bitmap.CompressFormat.JPEG, 50, baos);
                                        data1= baos.toByteArray();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                mstore[0] = mstore[0].child("images").child(strName);
                                mstore[0] = mstore[0].child(push());
                                mstore[0].putBytes(data1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        @SuppressWarnings("VisibleForTests") final Uri dnldurl = taskSnapshot.getUploadSessionUri();
                                        String s = Calendar.getInstance().getTime().toString();
                                        DatabaseReference newpost = mdatabase.child("Gallery").push();
                                        newpost.child("url").setValue(dnldurl.toString());
                                        newpost.child("date").setValue(s);
                                        newpost.child("captn").setValue(s3);
                                        s = newpost.getKey();
                                        newpost = mdatabase.child("Events").child(keys.get(which)).child("Gallery").push();
                                        newpost.setValue(s);
                                        Toast.makeText(MainActivity.this, "Image Uploaded Succesfully",
                                                Toast.LENGTH_SHORT).show();
                                        mDialog.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "Uploadation Failed", Toast.LENGTH_SHORT).show();
                                        mDialog.dismiss();
                                    }
                                });
                            }
                    //here
                        }
                    });abuilder.show();
                }
                private String push() {
                   return  UUID.randomUUID().toString();
                }
            });
            builderSingle.show();
        }
    }


    public class FirebaseCallbacks implements LoaderManager.LoaderCallbacks<Bundle>{
        @Override
        public Loader<Bundle> onCreateLoader(int i, Bundle bundle) {
            String title=bundle.getString("title");
            String url=bundle.getString("url");

            return new  FirebaseLoder(MainActivity.this,title,url,bundle.getString("key"));
        }

        @Override
        public void onLoadFinished(Loader<Bundle> loader, Bundle bundle) {
            try {

                titles.add(bundle.getString("title"));
                byte[] byteArray = bundle.getByteArray("image");
                Bitmap bmp = null;
                if (byteArray != null) {
                    bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                }

                images.add(bmp);
                Events.add(bundle.getString("key"));
                recyclerAdapter.updateData(titles,images,Events);
                recyclerAdapter.notifyDataSetChanged();
                CatchEvent.updateData(titles,images,Events);

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onLoaderReset(Loader<Bundle> loader) {

        }
    }

    private static class FirebaseLoder extends AsyncTaskLoader<Bundle> {
        String s1,s2,s3;

        FirebaseLoder(Context context, String t, String u,String k) {
            super(context);
            s1=t;
            s2=u;
            s3=k;
        }

        @Override
        public Bundle loadInBackground() {
            Bundle b=new Bundle();

            try {
                Bitmap bitmap;
                //download image from url
                URL urlConnection = new URL(s2);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);


                //put Bitmap in byteArray
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                byte[] byteArray = stream.toByteArray();

                ContentValues cv=new ContentValues();
                cv.put("Name",s1);
                cv.put("ImageRes",byteArray);
                cv.put("EventKey",s3);
                sdatabase.insert("Events",null,cv);

                b.putByteArray("image",byteArray);
                b.putString("title",s1);
                b.putString("key",s3);
                return b;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class LoaderCallbacks implements LoaderManager.LoaderCallbacks<EventsHolder> {
        @Override
        public Loader<EventsHolder> onCreateLoader(int i, Bundle bundle) {

            return new CusrorLoader(MainActivity.this);
        }

        @Override
        public void onLoadFinished(Loader<EventsHolder> loader, EventsHolder holder) {
            try {

                titles.addAll(holder.Titles);
                images.addAll(holder.imgs);
                Events.addAll(holder.keys);
                recyclerAdapter.updateData(titles,images,Events);
                recyclerAdapter.notifyDataSetChanged();
                CatchEvent.updateData(titles,images,Events);
                initiateFirebaseLoaders();

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onLoaderReset(Loader<EventsHolder> loader) {

        }
    }


    private static class CusrorLoader extends AsyncTaskLoader<EventsHolder>{
        Cursor c;
        final EventsHolder holder;
        CusrorLoader(Context context) {
            super(context);
            holder = new EventsHolder();
        }

        @Override
        public EventsHolder loadInBackground() {

            c=sdatabase.query("Events",
                    new String[]{"Name","ImageRes","EventKey"},
                    null,null,
                    null,null,
                    null);

            if (c != null ) {

                if (c.moveToFirst()) {
                    do {

                        try{

                            holder.Titles.add(c.getString(0));
                            byte[] image=c.getBlob(1);
                            Bitmap bitmap=BitmapFactory.decodeByteArray(image,0,image.length);
                            holder.imgs.add(bitmap);
                            holder.keys.add(c.getString(2));

                        }catch (IllegalStateException e){
                            e.printStackTrace();
                        }
                        /*If file cursor have filepath
                         File image = new File( c.getString(1));
                          BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                           */


                    } while (c.moveToNext());
                }

                return holder;
            }
            return null;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(cursor!=null)
        cursor.close();
    }
}
