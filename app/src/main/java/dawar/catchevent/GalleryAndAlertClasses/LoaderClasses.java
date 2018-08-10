package dawar.catchevent.GalleryAndAlertClasses;

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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import static dawar.catchevent.CatchEvent.sdatabase;

public class LoaderClasses {

    private Context ctx;
    private ImagesAdapter adapter;

     LoaderClasses(Context ctx,ImagesAdapter adapter) {
        this.ctx = ctx;
        this.adapter=adapter;
    }

    class FirebaseImageCallbacks implements LoaderManager.LoaderCallbacks<Void> {

        @NonNull
        @Override
        public Loader<Void> onCreateLoader(int i, Bundle bundle) {

            return new FirebaseLoader(ctx, bundle.getString("url"), bundle.getString("captn")
                    , bundle.getString("eventKey"), bundle.getString("imgKey"));
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Void> loader, Void data) {

        }
        @Override
        public void onLoaderReset(@NonNull Loader<Void> loader) {

        }

    }

    private static class FirebaseLoader extends AsyncTaskLoader<Void> {
        String url, captn, ekey, ikey;

        FirebaseLoader(Context context, String s1, String s2, String s3, String s4) {
            super(context);
            url = s1;
            captn = s2;
            ekey = s3;
            ikey = s4;
        }

        @Override
        public Void loadInBackground() {

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

                //put Bitmap in Bundle
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                ContentValues cv = new ContentValues();
                cv.put("imgKey", ikey);
                cv.put("bytes", byteArray);
                cv.put("captn", captn);
                cv.put("eventKey", ekey);

                sdatabase.insert("Gallery", null, cv);


            } catch (Exception e) {
                e.printStackTrace();
            }

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

                    cursorLoader.setProjection(new String[]{"imgKey","bytes", "captn"});
                    cursorLoader.setUri(GalleryProvider.Content_Uri_1);

                    break;

                case GalleryProvider.ALL_ALERTS:

                    cursorLoader.setProjection(new String[]{"imgKeys"});
                    cursorLoader.setUri(GalleryProvider.Content_Uri_3);

                    break;

                case GalleryProvider.EVENT_ALERTS:

                    cursorLoader.setProjection(new String[]{"imgKeys", "eventKey"});
                    cursorLoader.setSelection("eventKey=?");
                    cursorLoader.setSelectionArgs(new String[]{args.getString("eventKey")});
                    cursorLoader.setUri(GalleryProvider.Content_Uri_4);

                    break;

                case GalleryProvider.EVENT_IMAGES:

                    cursorLoader.setProjection(new String[]{"bytes", "captn"});
                    cursorLoader.setSelection("eventKey=?");
                    cursorLoader.setSelectionArgs(new String[]{args.getString("eventKey")});
                    cursorLoader.setUri(GalleryProvider.Content_Uri_2);
                    cursorLoader.setSortOrder(null);

                    break;
            }

            return cursorLoader;
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
            adapter.swapCusor(data);

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


