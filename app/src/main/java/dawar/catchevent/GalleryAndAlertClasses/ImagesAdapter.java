package dawar.catchevent.GalleryAndAlertClasses;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import dawar.catchevent.R;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder> {

     static ArrayList<String> captns;
     static ArrayList<Bitmap> images;
    private Context ctx;


    ImagesAdapter(Context context){

        captns=new ArrayList<>();
        images=new ArrayList<>();
        ctx=context;
        setHasStableIds(true);

    }

     void updateDate(String captn,Bitmap image){
        captns.add(captn);
        images.add(image);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(ctx).inflate(R.layout.image_view_card,parent,false);
        return new ImagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImagesViewHolder holder, int position) {
        holder.im.setImageBitmap(images.get(holder.getAdapterPosition()));
        holder.mview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle b = new Bundle();
                b.putInt("pos", holder.getAdapterPosition());

               FragmentTransaction  transaction = ((GalleryActivity)ctx).getSupportFragmentManager().beginTransaction();
                Image_slider image_slider = new Image_slider();
                image_slider.setArguments(b);
                transaction.addToBackStack(null);
                transaction.replace(R.id.gal_const, image_slider).commit();

            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    static class ImagesViewHolder extends RecyclerView.ViewHolder {
        View mview;
        final ImageView im;
         ImagesViewHolder(View itemView) {
            super(itemView);
            mview=itemView;
            im=mview.findViewById(R.id.gallery_img);
        }

        void setMviewforAlert(Bitmap b,Context ctx){
             final ImageView im=mview.findViewById(R.id.gallery_img);
             im.setImageBitmap(b);
             Toast.makeText(ctx,"Image added",Toast.LENGTH_SHORT).show();
         }
    }

}
