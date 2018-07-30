package dawar.catchevent;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder>
{
    private ArrayList<String> titles,keys;
    private  ArrayList<Bitmap> imgs;
    private Context ctx;

    RecyclerAdapter(Context c,ArrayList<String> titles,ArrayList<Bitmap> imgs,ArrayList<String> keys)
    {
     ctx=c;
     this.titles=titles;
     this.imgs=imgs;
     this.keys=keys;
    }

    public void updateData(ArrayList<String> titles,ArrayList<Bitmap> imgs,ArrayList<String> keys){
        this.titles=titles;
        this.imgs=imgs;
        this.keys=keys;
    }



    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(ctx).inflate(R.layout.event_card,parent,false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        holder.populate(titles.get(position),imgs.get(position));
        holder.mview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ctx, EventDetail.class);
                i.putExtra("key", keys.get(position));
                i.putExtra("pos",position);
                ctx.startActivity(i);
            }
        });
    }



    @Override
    public int getItemCount() {
        if(titles!=null)
            return titles.size();
        else
            return 0;
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        View mview;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            mview = itemView;
        }
        void populate(String s1,Bitmap s2){
            TextView title=mview.findViewById(R.id.event_name);
            ImageView imageView=mview.findViewById(R.id.event_image);
            title.setText(s1);
            imageView.setImageBitmap(s2);
        }
    }
}
