package dawar.catchevent;

import android.app.Application;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

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


    }


}
