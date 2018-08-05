package dawar.catchevent.GalleryAndAlertClasses;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import dawar.catchevent.Image_slider;
import dawar.catchevent.R;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder> {
    private static ArrayList<String> dates;
    private static ArrayList<String> captns;
    private static ArrayList<Bitmap> images;
    private Context ctx;
    ImagesAdapter(Context context){
        dates=new ArrayList<>();
        captns=new ArrayList<>();
        images=new ArrayList<>();
        ctx=context;
    }
     void updateData(Bitmap image,String date,String captn){
        images.add(image);
        dates.add(date);
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

    public class ImagesViewHolder extends RecyclerView.ViewHolder {
        View mview;
        final ImageView im;
        public ImagesViewHolder(View itemView) {
            super(itemView);
            mview=itemView;
            im=mview.findViewById(R.id.gallery_img);
        }
    }
}
