package dawar.catchevent;

import android.app.Application;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
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
    public static int userType;
    public static ArrayList<String> imgKeys,altKeys;

     DatabaseHelper databaseHelper;
    ArrayList<String> Titles,keys;
    ArrayList<Bitmap> images;

    public ArrayList<Bitmap> getImages() {
        return images;
    }
    public Bitmap getImageAtPos(int pos){
        return images.get(pos);
    }

    public void updateData(ArrayList<String> titles,ArrayList<Bitmap> imgs,ArrayList<String> keys){
        this.Titles=titles;
        this.images=imgs;
        this.keys=keys;
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
        imgKeys=new ArrayList<>();
        altKeys=new ArrayList<>();

    }
    public static Bundle fireImg(String s, DataSnapshot dataSnapshot){
        Bundle bundle = new Bundle();
        bundle.putString("imgKey", s);
        s = Objects.requireNonNull(dataSnapshot.child("url").getValue()).toString();
        bundle.putString("url", s);
        s = Objects.requireNonNull(dataSnapshot.child("eventKey").getValue()).toString();
        bundle.putString("eventKey", s);
        s = Objects.requireNonNull(dataSnapshot.child("captn").getValue()).toString();
        bundle.putString("captn", s);
        return bundle;
    }
}
