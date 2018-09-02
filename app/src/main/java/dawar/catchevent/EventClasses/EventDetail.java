package dawar.catchevent.EventClasses;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import dawar.catchevent.CatchEvent;
import dawar.catchevent.GalleryAndAlertClasses.GalleryActivity;
import dawar.catchevent.GalleryAndAlertClasses.Image_slider;
import dawar.catchevent.LogInClasses.LogInActivity;
import dawar.catchevent.R;
import dawar.catchevent.SettingsClasses.SettingsActivity;

public class EventDetail extends AppCompatActivity {

    String keyID;
    DatabaseReference mdata;
    ImageView im;
    FirebaseAuth mAuth;
    FirebaseUser muser;
    EditText fst,snd,thd;

    int  position;
    TextView title,regfee,date,time,desc;
    View layout;
    String s;
    Button b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        layout=findViewById(R.id.detailcontent);
        im=findViewById(R.id.imageView2);
        mAuth=FirebaseAuth.getInstance();
        muser=mAuth.getCurrentUser();
        title=findViewById(R.id.event_title);
        regfee=findViewById(R.id.RegistrationFee);
        date=findViewById(R.id.Date);
        fst=findViewById(R.id.rs_fst);
        snd=findViewById(R.id.rs_snd);
        thd=findViewById(R.id.rs_thd);
        b=findViewById(R.id.rs_bttn);
        time=findViewById(R.id.Timings);
        desc=findViewById(R.id.desc);
        mdata= FirebaseDatabase.getInstance().getReference();

        keyID=getIntent().getStringExtra("key");
        position=getIntent().getIntExtra("pos",0);
        populate(keyID,im,position);

        if(muser==null ){
            fst.setFocusable(false);
            snd.setFocusable(false);
            thd.setFocusable(false);
            b.setVisibility(View.INVISIBLE);
        }

    }

    private void populate(final String keyID, final ImageView imageView, final int position) {

        mdata.child("Events").child(keyID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {

                    s = dataSnapshot.child("name").getValue().toString();
                    title.setText(s);
                    android.support.v7.app.ActionBar actionBar = getSupportActionBar();
                    actionBar.setTitle(s);
                    actionBar.setDisplayHomeAsUpEnabled(true);

                    imageView.setImageBitmap(((CatchEvent)EventDetail.this.getApplication()).getImageAtPos(position));

                    date.setText(" DATE :" + dataSnapshot.child("date").getValue());
                    date.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_date_range_black_24dp, 0, 0, 0);
                    time.setText(" Timings :" + dataSnapshot.child("time").getValue().toString() +
                            "\n Venue:" + dataSnapshot.child("venue").getValue().toString());
                    time.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_place_black_24dp, 0, 0, 0);
                    desc.setText(Objects.requireNonNull(dataSnapshot.child("desc").getValue()).toString());
                    String newtext;
                    if (dataSnapshot.hasChild("regfee")) {
                        newtext = Objects.requireNonNull(dataSnapshot.child("regfee").getValue()).toString();
                        regfee.setText("₹"+newtext);
                    }
                    if (dataSnapshot.hasChild("fstpz")) {
                        newtext = Objects.requireNonNull(dataSnapshot.child("fstpz").getValue()).toString();
                        regfee.append("\n First:" + newtext);
                    }
                    if (dataSnapshot.hasChild("sndpz")) {
                        newtext = Objects.requireNonNull(dataSnapshot.child("sndpz").getValue()).toString();
                        regfee.append("\n Second: " + newtext);
                    }
                    if (dataSnapshot.hasChild("thrdpz")) {
                        newtext = Objects.requireNonNull(dataSnapshot.child("thrdpz").getValue()).toString();
                        regfee.append("\n Third: " + newtext);
                    }
                    if(dataSnapshot.hasChild("rs_fst")){
                        newtext = Objects.requireNonNull(dataSnapshot.child("rs_fst").getValue()).toString();
                        fst.setText(newtext);
                    }
                    if(dataSnapshot.hasChild("rs_snd")){
                        newtext = Objects.requireNonNull(dataSnapshot.child("rs_snd").getValue()).toString();
                        snd.setText(newtext);
                    }
                    if(dataSnapshot.hasChild("rs_thd")){
                        newtext = Objects.requireNonNull(dataSnapshot.child("rs_thd").getValue()).toString();
                        thd.setText(newtext);
                    }
                }
                catch (NullPointerException e){
                    finish();
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
            MenuItem item;
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
            Intent i=new Intent(EventDetail.this,SettingsActivity.class);
            i.putExtra("sett",-1);
            startActivity(i);
            return true;
        }

        if(id == R.id.action_login){

            //startActivity(new Intent(EventDetail.this, LogInActivity.class));
            startActivity(new Intent(EventDetail.this,EventDetail2.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void showGallery(View view) {
        Intent i=new Intent(EventDetail.this,GalleryActivity.class);
            if(view.getId()==R.id.gal_btn)
                i.putExtra("view",2);

            else
                i.putExtra("view", 5);

        i.putExtra("key",keyID);
        startActivity(i);
    }

    public void enlargeIt(View view) {
        android.support.v4.app.FragmentTransaction transaction=getSupportFragmentManager().beginTransaction().addToBackStack(null);
        Bundle b=new Bundle();
        b.putInt("pos",-3);
        b.putInt("img",position);
        Image_slider im=new Image_slider();
        im.setArguments(b);
        transaction.replace(R.id.detailcontent,im).commit();
    }

    public void updateResults(View view) {
        String s1,s2,s3;
        s1=fst.getText().toString();
        s2=snd.getText().toString();
        s3=thd.getText().toString();

        if(!s1.isEmpty() && !s2.isEmpty() && !s3.isEmpty()) {
            mdata.child("Events").child(keyID).child("rs_fst").setValue(s1);
            mdata.child("Events").child(keyID).child("rs_snd").setValue(s2);
            mdata.child("Events").child(keyID).child("rs_thd").setValue(s3);
        }
    }
}
