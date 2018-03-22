package dawar.catchevent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UdbhavMainActivity extends AppCompatActivity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    DatabaseReference mdata;
    HashMap<String, List<String>> listDataChild;
    ArrayList< String> day1,day2,day3,spl;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_udbhav_main);

        // get the listview

        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        mdata= FirebaseDatabase.getInstance().getReference();
        // preparing list data
        prepareListData();
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Udbhav");
        actionBar.setDisplayHomeAsUpEnabled(true);
        day1=new ArrayList<>();
        day2=new ArrayList<>();
        day3=new ArrayList<>();
        spl=new ArrayList<>();
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
         expListView.setAdapter(listAdapter);

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Intent i;
               switch (groupPosition){

                   case 0:
                       i=new Intent(UdbhavMainActivity.this,EventDetail.class);
                       i.putExtra("key",day1.get(childPosition));
                       startActivity(i);
                       break;
                   case 1:
                       i=new Intent(UdbhavMainActivity.this,EventDetail.class);
                       i.putExtra("key",day2.get(childPosition));
                       startActivity(i);
                    break;
                   case 2:
                       i=new Intent(UdbhavMainActivity.this,EventDetail.class);
                       i.putExtra("key",day3.get(childPosition));
                       startActivity(i);
                       break;
                   case 3:
                       i=new Intent(UdbhavMainActivity.this,EventDetail.class);
                       i.putExtra("key",spl.get(childPosition));
                       startActivity(i);
                       break;

               }
                return false;
            }
        });



    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }


    /*
     * Preparing the list data
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Day 1");
        listDataHeader.add("Day 2");
        listDataHeader.add("Day 3");
        listDataHeader.add("Special");

        // Adding child data
        final List<String> Day1 = new ArrayList<String>();
        mdata.child("Udbhav").child("Day1").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
               mdata.child("Events").child(dataSnapshot.getValue().toString()).addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {
                       try {
                           Day1.add(0,dataSnapshot.child("name").getValue().toString());
                           day1.add(0,dataSnapshot.getKey());
                       }
                       catch (NullPointerException e){

                       }

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

        final List<String> Day2 = new ArrayList<String>();
        mdata.child("Udbhav").child("Day2").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mdata.child("Events").child(dataSnapshot.getValue().toString()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            Day2.add(0,dataSnapshot.child("name").getValue().toString());
                            day2.add(0,dataSnapshot.getKey());
                        }
                        catch (NullPointerException e){

                        }

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





        final List<String> Day3 = new ArrayList<String>();
        mdata.child("Udbhav").child("Day3").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mdata.child("Events").child(dataSnapshot.getValue().toString()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            Day3.add(0,dataSnapshot.child("name").getValue().toString());
                            day3.add(0,dataSnapshot.getKey());
                        }
                        catch (NullPointerException e){

                        }

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



        final List<String> Special = new ArrayList<String>();
        mdata.child("Udbhav").child("Special").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mdata.child("Events").child(dataSnapshot.getValue().toString()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            Special.add(0,dataSnapshot.child("name").getValue().toString());
                            spl.add(0,dataSnapshot.getKey());
                        }
                        catch (NullPointerException e){

                        }

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



        listDataChild.put(listDataHeader.get(0), Day1); // Header, Child data
        listDataChild.put(listDataHeader.get(1), Day2);
        listDataChild.put(listDataHeader.get(2), Day3);
        listDataChild.put(listDataHeader.get(3), Special);
    }
}