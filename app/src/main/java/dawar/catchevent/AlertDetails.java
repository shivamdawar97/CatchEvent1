package dawar.catchevent;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

public class AlertDetails extends AppCompatActivity {
    TextView t1,t2,t3,t4;
    RecyclerView al_rv;
    String key;
    DatabaseReference mdata;
    ArrayList<String> images;
    String title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_details);
        t1=findViewById(R.id.al_ttl_view);
        t2=findViewById(R.id.al_dsc_view);
        t3=findViewById(R.id.al_rel_view);
        t4=findViewById(R.id.al_date_);
        al_rv=findViewById(R.id.al_rec_view);
        images=new ArrayList<>();
        key=getIntent().getStringExtra("key");
        mdata= FirebaseDatabase.getInstance().getReference();
        title=getIntent().getStringExtra("title");
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle(title);
        actionBar.setDisplayHomeAsUpEnabled(true);
        mdata.child("Alerts").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                t1.setText(dataSnapshot.child("title").getValue().toString());
                t2.setText(dataSnapshot.child("desc").getValue().toString());
                t3.append(dataSnapshot.child("ename").getValue().toString());
                t4.setText(dataSnapshot.child("date").getValue().toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mdata.child("Alerts").child(key).child("images").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                s=dataSnapshot.getValue().toString();
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


        @Override
        public MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(AlertDetails.this).inflate(R.layout.image_view_card,parent,false);
            return new MyviewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyviewHolder holder, int position) {
            mdata.child("Gallery").child(images.get(position)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                   final String s;
                   s=dataSnapshot.child("url").getValue().toString();
                    holder.setImage(s);
                    holder.im.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bundle bundle=new Bundle();

                            bundle.putString("img",s);
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
            public MyviewHolder(View itemView) {
                super(itemView);
                 im=itemView.findViewById(R.id.gallery_img);
            }
            protected void setImage(final String image){
                Picasso.with(AlertDetails.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(im, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(AlertDetails.this).load(image).into(im);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.removeItem(R.id.action_logout);
        menu.removeItem(R.id.action_login);
        menu.removeItem(R.id.action_settings);
        MenuItem item=menu.findItem(R.id.refresh);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                al_rv.getAdapter().notifyDataSetChanged();
                return AlertDetails.super.onOptionsItemSelected(menuItem);
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


}
