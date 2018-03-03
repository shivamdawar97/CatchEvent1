package dawar.catchevent;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.test.mock.MockApplication;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import android.widget.AbsListView;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;


import com.github.demono.AutoScrollViewPager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    RecyclerView rv;
    FirebaseUser muser;
    String m_Text;
    AlertDialog.Builder builder;
    AutoScrollViewPager viewPager;
    DatabaseReference mdatabase;
    ArrayList<String> Events,images;
    CommonAdapter cA;
    FloatingActionButton fab;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        builder = new AlertDialog.Builder(this);
        mAuth = FirebaseAuth.getInstance();
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewPager=findViewById(R.id.vpager);
        cA = new CommonAdapter(MainActivity.this,mAuth);
        Events=new ArrayList<>();
        images=new ArrayList<>();
        m_Text=new String("");
        rv=  findViewById(R.id.recycler_view);
         fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                muser = mAuth.getCurrentUser();
                if(muser==null)
                Snackbar.make(view, "LogIn First to Add Events here", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                else
                {
                    startActivity(new Intent(MainActivity.this,AddEvent.class));
                }
            }
        });
        if(mAuth.getCurrentUser() == null) {
            fab.setVisibility(View.INVISIBLE);
        }
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

       NavigationView navigationView =  findViewById(R.id.nav_view);
       navigationView.setNavigationItemSelectedListener(this);
       mdatabase=FirebaseDatabase.getInstance().getReference();
        mdatabase.keepSynced(true);
        mdatabase.child("Events").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                s = dataSnapshot.getKey();
                Events.add(0,s);
                mdatabase.child("Events").child(s).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                           images.add(dataSnapshot.child("image").getValue().toString());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

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

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            cA.showSettings(-1);
            return true;
        }

        if(id == R.id.action_login){
                cA.signIn();
            return true;
        }
        if(id==R.id.action_logout){
            if(muser!=null) {
                mAuth.signOut();
                synchronized (mAuth) {
                    muser=null;
                    mAuth.notify();
                }
                fab.setVisibility(View.INVISIBLE);
                Snackbar.make(getCurrentFocus(), "Logged Out Successfully", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
            else    {
                Snackbar.make(getCurrentFocus(), "You are not Logged In", Snackbar.LENGTH_LONG).setAction("Action", null).show();

            }

        }
        if(id== R.id.refresh){
            Toast.makeText(MainActivity.this,"Refresh icon ",Toast.LENGTH_SHORT).show();
            finish();
            startActivity(getIntent());
            rv.getAdapter().notifyDataSetChanged();
            viewPager.getAdapter().notifyDataSetChanged();
        }

        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        Intent intent;
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action


        } else if (id == R.id.nav_gallery) {

        intent=new Intent(this,GalleryActivity.class);
        intent.putExtra("view",3);
        intent.putExtra("name","Gallery");
        startActivity(intent);

        } else if (id == R.id.nav_Alerts) {

            intent=new Intent(this,GalleryActivity.class);
            intent.putExtra("view",4);
            intent.putExtra("name","Alerts");
            startActivity(intent);

        }  else if (id == R.id.nav_aboutus) {



        } else if (id == R.id.nav_send_feedback) {


        }

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder{
        View mview;
        public RecyclerViewHolder(View itemView) {
            super(itemView);
            mview=itemView;
        }
        public  void setName(String evntname){
            final TextView EGrpName= mview.findViewById(R.id.event_name);
            EGrpName.setText(evntname);
        }
        public void setImage(final Context ctx, final String image)  {
            final ProgressDialog   pd=new ProgressDialog(ctx);
            pd.setTitle("Please wait...");
            pd.show();
            final ImageView im=mview.findViewById(R.id.event_image);

            Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                    .into(im, new Callback() {
                @Override
                public void onSuccess() {
                    pd.dismiss();
                }

                @Override
                public void onError() {
                    Picasso.with(ctx).load(image).into(im);
                    pd.dismiss();
                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        cA.onActivityOfResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
      //  Toast.makeText(MainActivity.this,"onStart Called",Toast.LENGTH_SHORT).show();

        viewPager.setAdapter(new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return (view ==(ImageView)object);
            }

            @Override
            public Object instantiateItem(final ViewGroup container, final int position) {
                final ImageView IM= new ImageView(MainActivity.this);
                IM.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                IM.setScaleType(ImageView.ScaleType.CENTER);

                Picasso.with(MainActivity.this).load(images.get(position)).networkPolicy(NetworkPolicy.OFFLINE)
                        .into(IM, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(MainActivity.this).load(images.get(position)).into(IM);
                            }
                        });
                container.addView(IM);

                if(IM.getParent()!=null)
                    ((ViewGroup)IM.getParent()).removeView(IM);
                notifyDataSetChanged();
                return IM;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                //  super.destroyItem(container, position, object);
                container.removeView((ImageView)object);
            }


            @Override
            public int getCount() {
                return 0;
            }
        });

        viewPager.startAutoScroll();
        viewPager.setSlideInterval(3000);
        viewPager.setCycle(true);
        viewPager.setSlideDuration(1500);

        rv.setAdapter(new RecyclerView.Adapter<RecyclerViewHolder>() {
            @Override
            public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.event_card, parent, false);
                return new RecyclerViewHolder(view);
            }

            @Override
            public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {
                mdatabase.child("Events").child(Events.get(position)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                       cA.setName(dataSnapshot.getValue(CommonAdapter.class).getName());
                       holder.setName(cA.getName());
                        cA.setImage(dataSnapshot.getValue(CommonAdapter.class).getImage());
                        holder.setImage(MainActivity.this, cA.getImage());
                        holder.mview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = new Intent(MainActivity.this, EventDetail.class);
                                i.putExtra("key", Events.get(position));
                                startActivity(i);
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

                return Events.size();
            }
        });
        rv.setLayoutManager(new LinearLayoutManager(MainActivity.this));

    }

}