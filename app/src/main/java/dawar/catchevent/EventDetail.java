package dawar.catchevent;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EventDetail extends AppCompatActivity {

    String keyID;
    DatabaseReference mdata;
    ImageView im;
    FirebaseAuth mAuth;
    FirebaseUser muser;
    CommonAdapter ca;
    TextView title,regfee,date,time,desc;
    View layout;
    String s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        s=new String();
        layout=findViewById(R.id.detailcontent);
        im=findViewById(R.id.imageView2);
        mAuth=FirebaseAuth.getInstance();
        muser=mAuth.getCurrentUser();
        title=findViewById(R.id.event_title);
        regfee=findViewById(R.id.RegistrationFee);
        date=findViewById(R.id.Date);
        time=findViewById(R.id.Timings);
        desc=findViewById(R.id.desc);
        mdata= FirebaseDatabase.getInstance().getReference();
        keyID=getIntent().getStringExtra("key");
        populate(keyID,im);

    }

    private void populate(final String keyID, final ImageView imageView) {

        mdata.child("Events").child(keyID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                s=dataSnapshot.child("name").getValue().toString();
                title.setText(s);
                android.support.v7.app.ActionBar actionBar = getSupportActionBar();
                actionBar.setTitle(s);
                actionBar.setDisplayHomeAsUpEnabled(true);
                Picasso.with(EventDetail.this).load(dataSnapshot.child("image").getValue().toString()).into(imageView);
                date.setText(" DATE :"+ dataSnapshot.child("date").getValue());
                date.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_date_range_black_24dp,0,0,0);
                time.setText(" Timings :"+dataSnapshot.child("time").getValue().toString()+
                "\n Venue:"+dataSnapshot.child("venue").getValue().toString());
                time.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_place_black_24dp,0,0,0);
                desc.setText(dataSnapshot.child("desc").getValue().toString());
                String newtext;
                if(dataSnapshot.hasChild("regfee")) {
                    newtext = dataSnapshot.child("regfee").getValue().toString();
                    regfee.setText(" Registration Fee : " + newtext);
                    regfee.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_attach_money_black_24dp,0,0,0);
                }
                if(dataSnapshot.hasChild("fstpz")) {
                    newtext = dataSnapshot.child("fstpz").getValue().toString();
                    regfee.append("\n First Prize : " + newtext);
                }
                if(dataSnapshot.hasChild("sndpz")) {
                    newtext = dataSnapshot.child("sndpz").getValue().toString();
                    regfee.append("\n Second Prize : " + newtext);
                }
                if(dataSnapshot.hasChild("thrdpz")) {
                    newtext = dataSnapshot.child("thrdpz").getValue().toString();
                    regfee.append("\n Third Prize : " + newtext);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        muser=mAuth.getCurrentUser();
        if(muser==null)
        {
            MenuItem item=menu.findItem(R.id.action_logout);
            item.setVisible(false);
            item=menu.findItem(R.id.action_settings);
            item.setVisible(false);
        }
        else {
            MenuItem item =menu.findItem(R.id.action_login);
            item.setVisible(false);
        }
        this.invalidateOptionsMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id==android.R.id.home){
            finish();
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            ca=new CommonAdapter(EventDetail.this,mAuth);
            ca.showSettings(-1);
            return true;
        }

        if(id == R.id.action_login){
            ca=new CommonAdapter(EventDetail.this,mAuth );
            ca.signIn();
            return true;
        }
        if(id==R.id.action_logout){

            if(muser!=null) {
                mAuth.signOut();
                synchronized (mAuth) {
                    muser=null;
                    mAuth.notify();
                }
                Snackbar.make(layout, "Logged Out Successfully", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
            else    {
                Snackbar.make(layout, "You are not Logged In", Snackbar.LENGTH_LONG).setAction("Action", null).show();

            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ca=new CommonAdapter(EventDetail.this,mAuth );
        ca.onActivityOfResult(requestCode, resultCode, data);
    }

    public void showGallery(View view) {
        Intent i=new Intent(EventDetail.this,GalleryActivity.class);
            if(view.getId()==R.id.gal_btn){
                i.putExtra("view",1);
            }
            else
                i.putExtra("view",2);

        i.putExtra("key",keyID);
        i.putExtra("name",s);

        startActivity(i);
    }

}
