package dawar.catchevent.GalleryAndAlertClasses;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import dawar.catchevent.R;

import static dawar.catchevent.CatchEvent.mdatabase;

public class AlertsAdapter extends RecyclerView.Adapter<AlertsAdapter.AlertViewHolder> {
    private Context ctx;
    ArrayList<String> altKeys;
    private ArrayList<String> captns,titles;

    AlertsAdapter(Context context){
       ctx= context;
       captns=new ArrayList<>();
       titles=new ArrayList<>();
       altKeys=new ArrayList<>();

    }
    void updateAltKeys(String s){
        //update for a single Event
        altKeys.add(0,s);
        mdatabase.child("Alerts").child(s).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String s=dataSnapshot.child("title").getValue().toString();
                titles.add(0,s);
                s=dataSnapshot.child("captn").getValue().toString();
                captns.add(0,s);
              AlertsAdapter.this.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    void updateAltKeys(){
        //update for all Events
        mdatabase.child("Alerts").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    dataSnapshot.getRef().keepSynced(true);
                    s=dataSnapshot.getKey();
                    altKeys.add(0,s);
                     s=dataSnapshot.child("title").getValue().toString();
                    titles.add(0,s);
                    s=dataSnapshot.child("captn").getValue().toString();
                    captns.add(0,s);
                    AlertsAdapter.this.notifyDataSetChanged();
                    /*
                    if(!adapter.altKeys.contains(s)){
                        adapter.updateAltKeys(s);

                            ContentValues cv=new ContentValues();
                            cv.put("altKey",s);
                            put String array into byte array
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            DataOutputStream out = new DataOutputStream(baos);
                            for(DataSnapshot s1: dataSnapshot.child("Images").getChildren()){
                               out.writeUTF(Objects.requireNonNull(s1.getValue()).toString());
                            }
                            byte[] bytes = baos.toByteArray();
                            cv.put("imgKeys",bytes);
                            s= Objects.requireNonNull(dataSnapshot.child("eventkey").getValue()).toString();
                            cv.put("eventKey",s);
                            sdatabase.insert("Alerts",null,cv);
                    }*/

                }
                catch (NullPointerException p){
                    p.printStackTrace();
                }

            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
               String s=dataSnapshot.getKey();
                altKeys.remove(s);
                s=dataSnapshot.child("title").getValue().toString();
                titles.remove(s);
                s=dataSnapshot.child("captn").getValue().toString();
                captns.remove(s);
                AlertsAdapter.this.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
