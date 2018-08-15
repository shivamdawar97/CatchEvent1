package dawar.catchevent.GalleryAndAlertClasses;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.graphics.Bitmap;
import android.os.Bundle;

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
import java.util.Objects;

import static dawar.catchevent.CatchEvent.mdatabase;
import static dawar.catchevent.CatchEvent.sdatabase;

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

//                ImagesAdapter.images.add(bitmap);
//                ImagesAdapter.captns.add(captn);

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

   public class GalleryCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

        @NonNull
        @Override

        public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

            CursorLoader cursorLoader = new CursorLoader(ctx);
            int type = Objects.requireNonNull(args).getInt("type");
            switch (type) {

                case GalleryProvider.ALL_IMAGES:

                    cursorLoader.setProjection(new String[]{"bytes", "captn"});
                    cursorLoader.setUri(GalleryProvider.Content_Uri_1);

                    break;

                case GalleryProvider.EVENT_IMAGES:

                    cursorLoader.setProjection(new String[]{"bytes", "captn"});
                    cursorLoader.setSelection("eventKey=?");
                    cursorLoader.setSelectionArgs(new String[]{args.getString("eventKey")});
                    cursorLoader.setUri(GalleryProvider.Content_Uri_2);
                    cursorLoader.setSortOrder(null);

                    break;

                case GalleryProvider.ALL_ALERTS:

                    cursorLoader.setProjection(new String[]{"altKey"});
                    cursorLoader.setUri(GalleryProvider.Content_Uri_3);

                    break;

                case GalleryProvider.EVENT_ALERTS:

                    cursorLoader.setProjection(new String[]{"altKey"});
                    cursorLoader.setSelection("eventKey=?");
                    cursorLoader.setSelectionArgs(new String[]{args.getString("eventKey")});
                    cursorLoader.setUri(GalleryProvider.Content_Uri_4);

                    break;

            }

            return cursorLoader;
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

            if(adapter!=null)
                adapter.swapCusor(data);
            else
                alertsAdapter.swapCursor(data);

        }


        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> cursor) {
            adapter.swapCusor(null);
        }

    }


}
     /*
     TODO:Later Experiment.
     private static class GalleryLoader extends CursorLoader{

         public GalleryLoader(@NonNull Context context, @NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
             super(context, uri, projection, selection, selectionArgs, sortOrder);
         }

         @Override
         public Cursor loadInBackground() {
             return super.loadInBackground();
         }

     }

     */


