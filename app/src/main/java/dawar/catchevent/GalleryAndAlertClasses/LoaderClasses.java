package dawar.catchevent.GalleryAndAlertClasses;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static dawar.catchevent.CatchEvent.mdatabase;
import static dawar.catchevent.CatchEvent.sdatabase;
import static dawar.catchevent.GalleryAndAlertClasses.GalleryProvider.ALL_ALERTS;
import static dawar.catchevent.GalleryAndAlertClasses.GalleryProvider.ALL_IMAGES;
import static dawar.catchevent.GalleryAndAlertClasses.GalleryProvider.EVENT_ALERTS;
import static dawar.catchevent.GalleryAndAlertClasses.GalleryProvider.EVENT_IMAGES;

public class LoaderClasses {

    private Context ctx;
    private ImagesAdapter adapter;
    private AlertsAdapter alertsAdapter;
    private static ContentResolver contentResolver;

     LoaderClasses(Context ctx,ImagesAdapter adapter) {
        this.ctx = ctx;
        this.adapter=adapter;
        alertsAdapter=null;
        contentResolver=ctx.getContentResolver();
    }

    LoaderClasses(Context ctx,AlertsAdapter adapter) {
        this.ctx = ctx;
        alertsAdapter=adapter;
        this.adapter=null;
        contentResolver=ctx.getContentResolver();
    }

    class FirebaseImageCallbacks implements LoaderManager.LoaderCallbacks {

        @NonNull
        @Override
        public Loader onCreateLoader(int i, Bundle bundle) {

            return new FirebaseImageLoader(ctx, bundle.getString("url"), bundle.getString("captn")
                    , bundle.getString("eventKey"), bundle.getString("imgKey"));

        }

        @Override
        public void onLoadFinished(@NonNull Loader loader, Object data) {
            adapter.notifyDataSetChanged();
        }


        @Override
        public void onLoaderReset(@NonNull Loader loader) {

        }

    }

    private static class FirebaseImageLoader extends AsyncTaskLoader {
        String url, captn, ekey, ikey;

        FirebaseImageLoader(Context context, String s1, String s2, String s3, String s4) {
            super(context);
            url = s1;
            captn = s2;
            ekey = s3;
            ikey = s4;
        }

        @Override
        public Object loadInBackground() {

            try {

                Bitmap bitmap;
                //download image from url
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);

                ImagesAdapter.captns.add(captn);
                ImagesAdapter.images.add(bitmap);

                //put Bitmap in Bundle
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();


                ContentValues cv = new ContentValues();
                cv.put("imgKey", ikey);
                cv.put("bytes", byteArray);
                cv.put("captn", captn);
                cv.put("eventKey", ekey);

                contentResolver.insert(GalleryProvider.Content_Uri_1,cv);


            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

    }

    class FirebaseAlertCallbacks implements LoaderManager.LoaderCallbacks{
        @NonNull
        @Override
        public Loader onCreateLoader(int id, @Nullable Bundle args) {

            return new FirebaseAlertLoader(ctx, Objects.requireNonNull(args).getString("altKey"),args.getByteArray("imgKeys"),
                    args.getString("eventKey"));
        }

        @Override
        public void onLoadFinished(@NonNull Loader loader, Object data) {

        }

        @Override
        public void onLoaderReset(@NonNull Loader loader) {

        }
    }

    private static class FirebaseAlertLoader extends AsyncTaskLoader{
        String aKey,eKey;
        byte[] imgKeys;
         FirebaseAlertLoader(@NonNull Context context, String akey, byte[] imgkeys,String ekey) {
            super(context);
            aKey=akey;
            eKey=ekey;
            imgKeys=imgkeys;
        }

        @Nullable
        @Override
        public Object loadInBackground() {

            /*
            Getting elements for arrayList for byte[]
            ByteArrayInputStream bais = new ByteArrayInputStream(imgKeys);
            DataInputStream in = new DataInputStream(bais);
            try {
                while (in.available() > 0) {
                    String element = in.readUTF();
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
            */


            return null;
        }
    }

   public class GalleryCallbacks implements LoaderManager.LoaderCallbacks<HashMap<String,Bitmap>> {

        @NonNull
        @Override

        public Loader<HashMap<String,Bitmap>> onCreateLoader(int id, @Nullable Bundle args) {

            MyCursorLoader cursorLoader = new MyCursorLoader(ctx);
            int type = Objects.requireNonNull(args).getInt("type");


            switch (type) {

                case ALL_IMAGES:

                    cursorLoader.setProjection(new String[]{"bytes", "captn"});
                    cursorLoader.setContentType(type);
                    cursorLoader.setSelection(null);
                    cursorLoader.setSelectionArgs(null);

                    break;

                case EVENT_IMAGES:

                    cursorLoader.setProjection(new String[]{"bytes", "captn"});
                    cursorLoader.setSelection("eventKey=?");
                    cursorLoader.setSelectionArgs(new String[]{args.getString("eventKey")});
                    cursorLoader.setContentType(type);


                    break;

                case ALL_ALERTS:

                    cursorLoader.setProjection(new String[]{"altKey"});
                    cursorLoader.setContentType(type);

                    break;

                case EVENT_ALERTS:

                    cursorLoader.setProjection(new String[]{"altKey"});
                    cursorLoader.setSelection("eventKey=?");
                    cursorLoader.setSelectionArgs(new String[]{args.getString("eventKey")});
                    cursorLoader.setContentType(type);

                    break;

            }

            return cursorLoader;
        }

        @Override
        public void onLoadFinished(@NonNull Loader<HashMap<String,Bitmap>> loader, HashMap<String,Bitmap> data) {
              if(data!=null)
                for(String s: data.keySet()){
                    adapter.updateDate(s,data.get(s));
                }
                else
                    alertsAdapter.notifyDataSetChanged();
        }

        @Override
        public void onLoaderReset(@NonNull Loader<HashMap<String,Bitmap>> cursor) {

        }

    }


    private static class MyCursorLoader extends AsyncTaskLoader<HashMap<String,Bitmap>> {

        int contentType;
        String[] projection, selectionArgs;
        String selection; //sortOrder;
        HashMap<String,Bitmap> hashMap;


        MyCursorLoader(@NonNull Context context) {
            super(context);
        }

        public void setContentType(int contentType) {
            this.contentType = contentType;
        }

        public void setProjection(String[] projection) {
            this.projection = projection;
        }

        public void setSelectionArgs(String[] selectionArgs) {
            this.selectionArgs = selectionArgs;
        }

        public void setSelection(String selection) {
            this.selection = selection;
        }


        @Nullable
        @Override
        public HashMap<String,Bitmap> loadInBackground() {
            Cursor cursor;
            hashMap=new HashMap<>();

            switch (contentType)
            {
                case  ALL_IMAGES:
                case EVENT_IMAGES:


                    Log.i("In Background","Reached here");
                    cursor=sdatabase.query("Gallery",projection,selection,selectionArgs,null,null,null);

                    if(cursor!=null && cursor.getCount()>0) {
                        cursor.moveToFirst();
                        do {

                            try {
                                byte[] byteArray = cursor.getBlob(0);
                                Bitmap bmp;

                                if (byteArray != null && byteArray.length>0)
                                {

                                    bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                                    hashMap.put(cursor.getString(1),bmp);

                                }
                            }
                            catch (IllegalStateException e){
                                e.printStackTrace();
                            }

                        }
                        while (cursor.moveToNext());
                    }
                    Objects.requireNonNull(cursor).close();
                    return hashMap;

                case ALL_ALERTS:
                case EVENT_ALERTS:

                    cursor=sdatabase.query("Alerts",projection,selection,selectionArgs,null,null,null);
                    if (cursor != null && cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        do {

                            AlertsAdapter.altKeys.add(cursor.getString(0));
                            mdatabase.child("Alerts").child(cursor.getString(0)).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    AlertsAdapter.titles.add(Objects.requireNonNull(dataSnapshot.child("title").getValue()).toString());
                                    AlertsAdapter.captns.add(Objects.requireNonNull(dataSnapshot.child("captn").getValue()).toString());

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }while (cursor.moveToNext());
                    }
                    Objects.requireNonNull(cursor).close();
                    return null;
            }

            return null;
        }

    }

}





