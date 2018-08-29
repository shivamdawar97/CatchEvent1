package dawar.catchevent;

import android.app.Application;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import dawar.catchevent.GalleryAndAlertClasses.LoaderClasses;

/**
 * Created by shivam97 on 28/2/18.
 */

public class CatchEvent extends Application {

    public static DatabaseReference mdatabase;
    public static StorageReference mstorage;
    public static FirebaseAuth mAuth;
    public static SQLiteDatabase sdatabase;
    public static ArrayList<String> imgKeys,altKeys;
    public static String Uid;
    public static int userType=1;

    DatabaseHelper databaseHelper;

    public static ArrayList<String> Titles,keys;
    public   static ArrayList<Bitmap> images;

    public ArrayList<Bitmap> getImages() {
        return images;
    }
    public Bitmap getImageAtPos(int pos){
        return images.get(pos);
    }

    public static void  updateData(ArrayList<String> titles,ArrayList<Bitmap> imgs,ArrayList<String> kys){
        Titles=titles;
        images=imgs;
        keys=kys;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        databaseHelper=new DatabaseHelper(this,"Events",null,1);
        sdatabase=databaseHelper.getReadableDatabase();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mdatabase=FirebaseDatabase.getInstance().getReference();
        mstorage= FirebaseStorage.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();

        Picasso.Builder builder=new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        Picasso built=builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);


        imgKeys=new ArrayList<>();
        altKeys=new ArrayList<>();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                try {
                    Uid= mAuth.getCurrentUser().getUid();

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

    }

    public static void getUserType(){

        mdatabase.child("users").child(Uid).child("userType").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userType =Integer.parseInt(dataSnapshot.getValue().toString()) ;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public  static String getTime(){
        String date= Calendar.getInstance().getTime().toString();

        int index=Integer.parseInt(date.substring(30));
        index++;
        date=date.split(String.valueOf("G"),9)[0];

        date=date.concat(String.valueOf(index));
        return date;
    }


    public static Bitmap getCompressed(Bitmap bitmap){
        try {
            //Decode image size
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data1 = baos.toByteArray();

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
           // Toast.makeText(context,String.valueOf(data1.length),Toast.LENGTH_LONG).show();
            if( data1.length <500000) {

                return bitmap;
            }
            else
                BitmapFactory.decodeByteArray(data1,0,data1.length,o);
            //The new size we want to scale to
            final int REQUIRED_SIZE=200;

            //Find the correct scale value. It should be the power of 2.
            int scale=1;
            while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
                scale*=2;

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeByteArray(data1,0,data1.length, o2);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

}
