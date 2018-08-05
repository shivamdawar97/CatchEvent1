package dawar.catchevent.GalleryAndAlertClasses;

import android.content.ContentValues;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.support.v4.content.Loader;
import android.graphics.Bitmap;
import android.os.Bundle;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import dawar.catchevent.CatchEvent;
import dawar.catchevent.MainActivity;

import static dawar.catchevent.CatchEvent.sdatabase;

class LoaderClasses {

    private Context ctx;
    LoaderClasses(Context ctx){
        this.ctx=ctx;
    }

     class FirebaseImageCallbacks implements LoaderManager.LoaderCallbacks<Bundle> {

        @NonNull
        @Override
        public Loader<Bundle> onCreateLoader(int i, Bundle bundle) {

            return new FirebaseLoader(ctx,bundle.getString("url"),bundle.getString("date"),
                    bundle.getString("captn"));
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Bundle> loader, Bundle bundle) {

            try {

                byte[] byteArray = bundle.getByteArray("bitmap");
                Bitmap bmp = null;
                if (byteArray != null) {
                    bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                }
                String s1=bundle.getString("date");
                String s2=bundle.getString("captn");

               GalleryActivity.adapter.updateData(bmp,s1,s2);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Bundle> loader) {

        }
    }

    private static class FirebaseLoader extends AsyncTaskLoader<Bundle> {
        String url,date,captn;

         FirebaseLoader(Context context,String s1,String s2,String s3) {
             super(context);
             url=s1;
             date=s2;
             captn=s3;
        }

        @Override
        public Bundle loadInBackground() {
            Bundle b=new Bundle();
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

                b.putByteArray("bitmap",byteArray);
                b.putString("date",date);
                b.putString("captn",captn);
                return b;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


     class GalleryCallbacks implements LoaderManager.LoaderCallbacks<Bitmap> {


         @NonNull
         @Override
         public Loader<Bitmap> onCreateLoader(int id, @Nullable Bundle args) {
             return new GalleryLoader(ctx);

         }

         @Override
         public void onLoadFinished(@NonNull Loader<Bitmap> loader, Bitmap data) {

         }

         @Override
         public void onLoaderReset(@NonNull Loader<Bitmap> loader) {

         }
     }

     private static class GalleryLoader extends AsyncTaskLoader<Bitmap>{
         public GalleryLoader(@NonNull Context context) {
             super(context);
         }

         @Nullable
         @Override
         public Bitmap loadInBackground() {
             return null;
         }
     }
}

