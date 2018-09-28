package dawar.catchevent.EventClasses;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import dawar.catchevent.BlurBuilder;
import dawar.catchevent.CatchEvent;
import dawar.catchevent.GalleryAndAlertClasses.GalleryActivity;
import dawar.catchevent.GalleryAndAlertClasses.Image_slider;
import dawar.catchevent.R;
import dawar.catchevent.SettingsClasses.SettingsActivity;
import  static dawar.catchevent.CatchEvent.height;
import  static dawar.catchevent.CatchEvent.width;


public class EventDetail extends AppCompatActivity {

    String keyID;
    DatabaseReference mdata;

    FirebaseAuth mAuth;
    FirebaseUser muser;
    EditText fst,snd,thd;
    LinearLayout resultsLayout;
    int  position;
    TextView title,regfee,date,time,
             desc,venue,prizeView;
    String s;
    Button b;
    AppBarLayout layout;
    Toolbar toolbar;
    TextView titleTv ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        layout=findViewById(R.id.app_bar);

        ViewGroup.LayoutParams params = layout.getLayoutParams();
        // Changes the height and width to the specified *pixels*
        params.height = height/2;
        params.width = width;
        layout.setLayoutParams(params);

        resultsLayout=findViewById(R.id.results_layout);
        resultsLayout.setVisibility(View.GONE);

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
        venue=findViewById(R.id.event_venue);
        mdata= FirebaseDatabase.getInstance().getReference();
        prizeView=findViewById(R.id.prize_view);
        prizeView.setVisibility(View.GONE);

        keyID=getIntent().getStringExtra("key");
        position=getIntent().getIntExtra("pos",0);
        final CollapsingToolbarLayout layout1=findViewById(R.id.toolbar_layout);


        populate(keyID,layout1,position);

        if(muser==null ){
            fst.setFocusable(false);
            snd.setFocusable(false);
            thd.setFocusable(false);
            b.setVisibility(View.INVISIBLE);
        }

    }

    private void populate(final String keyID, final CollapsingToolbarLayout layout1, final int position) {

        mdata.child("Events").child(keyID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {

                    s = dataSnapshot.child("name").getValue().toString();
                    title.setText(s);
                    android.support.v7.app.ActionBar actionBar = getSupportActionBar();
                    if (actionBar != null) {

                        actionBar.setDisplayHomeAsUpEnabled(true);
                    }

                    //imageView.setImageBitmap(((CatchEvent)EventDetail.this.getApplication()).getImageAtPos(position));
                    Bitmap bitmap=((CatchEvent)EventDetail.this.getApplication()).getImageAtPos(position);
                    BitmapDrawable drawable=new BitmapDrawable(getResources(),bitmap);
                    layout1.setBackground(drawable);

                    //Blur Drawable
                    bitmap= BlurBuilder.blur(EventDetail.this,bitmap);
                    drawable=new BitmapDrawable(getResources(),bitmap);

                    titleTv=(TextView) getToolbarTitle();

                    final BitmapDrawable finalDrawable = drawable;
                    layout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                        boolean isShow = true;
                        int scrollRange = -1;

                        Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                        @Override
                        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                            if (scrollRange == -1) {
                                scrollRange = appBarLayout.getTotalScrollRange();
                            }
                            if (scrollRange + verticalOffset == 0) {

                                toolbar.setBackground(finalDrawable);
                                titleTv.setText(s);
                               // layout1.setTitle(s);
                                isShow = true;
                            } else if(isShow) {
                                toolbar.setBackground(null);
                                titleTv.setText(" ");
                                //carefull there should a space between double quote otherwise it wont work
                                layout1.setTitle(" ");
                                isShow = false;
                            }
                        }
                    });

                    layout1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            enlargeIt(layout1);
                        }
                    });

                    date.setText( "\t"+dataSnapshot.child("date").getValue().toString());
                    time.setText( dataSnapshot.child("time").getValue().toString());
                    venue.setText( dataSnapshot.child("venue").getValue().toString());
                    desc.setText(Objects.requireNonNull(dataSnapshot.child("desc").getValue()).toString());
                    String newtext;

                        newtext = Objects.requireNonNull(dataSnapshot.child("regfee").getValue()).toString();
                        regfee.setText(newtext);

                    if (dataSnapshot.hasChild("fstpz")) {
                        prizeView.setVisibility(View.VISIBLE);
                        newtext = Objects.requireNonNull(dataSnapshot.child("fstpz").getValue()).toString();
                        prizeView.setText(" First:" + newtext);
                    }
                    if (dataSnapshot.hasChild("sndpz")) {
                        newtext = Objects.requireNonNull(dataSnapshot.child("sndpz").getValue()).toString();
                        prizeView.append("\n Second: " + newtext);
                    }
                    if (dataSnapshot.hasChild("thrdpz")) {
                        newtext = Objects.requireNonNull(dataSnapshot.child("thrdpz").getValue()).toString();
                        prizeView.append("\n Third: " + newtext);
                    }

                    if(dataSnapshot.hasChild("rs_fst")){

                        resultsLayout.setVisibility(View.VISIBLE);
                        newtext = Objects.requireNonNull(dataSnapshot.child("rs_fst").getValue()).toString();
                        fst.setText(newtext);
                        newtext = Objects.requireNonNull(dataSnapshot.child("rs_snd").getValue()).toString();
                        snd.setText(newtext);
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
            if(view.getId()==R.id.gal_btn) {
                i.putExtra("view", 2);
                i.putExtra("name",s);
            }

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

    private View getToolbarTitle() {
        int childCount = toolbar.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = toolbar.getChildAt(i);
            if (child instanceof TextView) {
                return child;
            }
        }

        return new View(this);
    }
}
