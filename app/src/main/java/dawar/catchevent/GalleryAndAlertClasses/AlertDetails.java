package dawar.catchevent.GalleryAndAlertClasses;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import dawar.catchevent.MainActivity;
import dawar.catchevent.R;

import static dawar.catchevent.CatchEvent.mdatabase;

public class AlertDetails extends AppCompatActivity {
    TextView t1,t4;
    TextView t2;
    RecyclerView al_rv;
    String key;
    ArrayList<String> images;
    String title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_details);
        t1=findViewById(R.id.al_ttl_view);
        t2=findViewById(R.id.al_dsc_view);
        t4=findViewById(R.id.al_date_);
        al_rv=findViewById(R.id.al_rec_view);
        images=new ArrayList<>();
        key=getIntent().getStringExtra("altKey");

        title=getIntent().getStringExtra("title");
        t1.setText(title);
        t4.setText(getIntent().getStringExtra("captn"));
        mdatabase.child("Alerts").child(key).keepSynced(true);
        mdatabase.child("Alerts").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {

                    t2.setText(dataSnapshot.child("desc").getValue().toString());

                }
                catch (NullPointerException e){

                    Intent i=new Intent(AlertDetails.this,MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mdatabase.child("Alerts").child(key).child("images").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                s=dataSnapshot.getKey();
                images.add(0,s);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        al_rv.setAdapter(new RecyclerViewHolder());
        al_rv.setLayoutManager(new GridLayoutManager(AlertDetails.this,2));
    }

    public class RecyclerViewHolder extends RecyclerView.Adapter<RecyclerViewHolder.MyviewHolder> {


        @NonNull
        @Override
        public MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(AlertDetails.this).inflate(R.layout.image_view_card,parent,false);
            return new MyviewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyviewHolder holder, int position) {

            mdatabase.child("Gallery").child(images.get(position)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    String s=null;
                   try {
                       s=dataSnapshot.child("url").getValue().toString();
                   }
                   catch (NullPointerException e){
                       finish();
                       startActivity(new Intent(AlertDetails.this,MainActivity.class));
                   }

                    holder.setImage(s);
                    final String finalS = s;
                    holder.im.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bundle bundle=new Bundle();

                            bundle.putString("img", finalS);
                                try {
                                    bundle.putString("cts",dataSnapshot.child("captn").getValue().toString());

                                    bundle.putString("dts",dataSnapshot.child("date").getValue().toString());

                                }
                                catch (NullPointerException e){

                                }


                            bundle.putInt("pos",-1);

                            android.support.v4.app.FragmentTransaction t=getSupportFragmentManager().beginTransaction().addToBackStack(null);
                            Image_slider imageSlider=new Image_slider();
                            imageSlider.setArguments(bundle);
                            t.replace(R.id.al_det_cont, imageSlider).commit();
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        @Override
        public int getItemCount() {
            return images.size();
        }

        public class MyviewHolder extends RecyclerView.ViewHolder {
            ImageView im;
             MyviewHolder(View itemView) {
                super(itemView);
                 im=itemView.findViewById(R.id.gallery_img);
            }
            protected void setImage(final String image){
                Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).into(im, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(image).into(im);

                    }

                });
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }


}
