package dawar.catchevent.GalleryAndAlertClasses;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import dawar.catchevent.R;

import static dawar.catchevent.CatchEvent.mdatabase;

public class AlertsAdapter extends RecyclerView.Adapter<AlertsAdapter.AlertViewHolder> {
    private Context ctx;
    private ArrayList<String> titles,captns,altKeys;

    AlertsAdapter(Context context){
       ctx= context;
       captns=new ArrayList<>();
       titles=new ArrayList<>();
       altKeys=new ArrayList<>();
    }

    void swapCursor(Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                altKeys.add(cursor.getString(0));
                mdatabase.child("Alerts").child(cursor.getString(0)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        titles.add(Objects.requireNonNull(dataSnapshot.child("title").getValue()).toString());
                        captns.add(Objects.requireNonNull(dataSnapshot.child("captn").getValue()).toString());
                        AlertsAdapter.this.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }while (cursor.moveToNext());
        }

    }

    @NonNull
    @Override
    public AlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

       View view= LayoutInflater.from(ctx).inflate(R.layout.alert_view,parent,false);
        return new AlertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AlertViewHolder holder, int position) {
        holder.setAlert(titles.get(position),captns.get(position));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(ctx,AlertDetails.class);
                i.putExtra("altKey",altKeys.get(holder.getAdapterPosition()));
                i.putExtra("title",titles.get(holder.getAdapterPosition()));
                i.putExtra("captn",captns.get(holder.getAdapterPosition()));
                ctx.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

     class AlertViewHolder extends RecyclerView.ViewHolder {
        View mView;
         AlertViewHolder(View itemView) {
            super(itemView);
             mView=itemView;
        }

         void setAlert(String s1, String s2){
            TextView textView=mView.findViewById(R.id.alert_title2);
            textView.setText(s1);
            //title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googleg_standard_color_18,0,0,0);
            textView=mView.findViewById(R.id.alert_date);
            textView.setText(s2);
        }
    }
}
