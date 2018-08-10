package dawar.catchevent.GalleryAndAlertClasses;

import android.app.Activity;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import dawar.catchevent.Image_slider;
import dawar.catchevent.R;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder> {
    static ArrayList<String> imgKeys;
    private static ArrayList<String> captns;
    private static ArrayList<Bitmap> images;
    private Context ctx;

    ImagesAdapter(Context context){
        imgKeys=new ArrayList<>();
        captns=new ArrayList<>();
        images=new ArrayList<>();
        ctx=context;

    }

    void swapCusor(Cursor cursor){

        if(cursor!=null && cursor.getCount()>0) {
            (new CursorBackground()).execute(cursor);

        }

    }
     void updateData(Bitmap image,String captn){
        images.add(image);
        captns.add(captn);
        this.notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ImagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(ctx).inflate(R.layout.image_view_card,parent,false);
        return new ImagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagesViewHolder holder, final int position) {
        holder.im.setImageBitmap(images.get(position));
        holder.mview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Bundle b = new Bundle();
                b.putInt("pos", position);
                b.putStringArrayList("img", images);
                b.putStringArrayList("dts", dates);
                b.putStringArrayList("cts",captns);
                FragmentTransaction t;
                //TODO: getSupportFragmentManager
                Image_slider image_slider = new Image_slider();
                image_slider.setArguments(b);
                t.replace(R.id.gal_const, image_slider).commit();
                */
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

     class ImagesViewHolder extends RecyclerView.ViewHolder {
        View mview;
        final ImageView im;
         ImagesViewHolder(View itemView) {
            super(itemView);
            mview=itemView;
            im=mview.findViewById(R.id.gallery_img);
        }
    }
     class CursorBackground extends AsyncTask<Cursor,Void,Void> {

        @Override
        protected Void doInBackground(Cursor... cursors) {
            Cursor cursor=cursors[0];
            cursor.moveToFirst();
            do {
                byte[] byteArray = cursor.getBlob(1);
                Bitmap bmp;
                if (byteArray != null && byteArray.length>0)
                {

                    bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    captns.add(cursor.getString(2));
                    images.add(bmp);
                    imgKeys.add(cursor.getString(0));

                }
            }
            while (cursor.moveToNext());

            return null;
        }
    }
}
