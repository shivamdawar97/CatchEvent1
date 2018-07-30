package dawar.catchevent;

import android.app.Application;
import android.graphics.Bitmap;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by shivam97 on 28/2/18.
 */

public class CatchEvent extends Application {
    DatabaseReference mdatabase;
    ArrayList<String> Titles,keys;
    ArrayList<Bitmap> images;

    public ArrayList<String> getTitles() {
        return Titles;
    }

    public ArrayList<String> getKeys() {
        return keys;
    }


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

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mdatabase=FirebaseDatabase.getInstance().getReference();

    }

    public DatabaseReference getMdatabase() {
        return mdatabase;
    }

}
