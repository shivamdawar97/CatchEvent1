package dawar.catchevent;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import dawar.catchevent.GalleryAndAlertClasses.AddAlerts;

public class ListActivity extends AppCompatActivity {
    ListView newlv;
    ArrayList<String> setlist,keys;
    DatabaseReference mdata;
    ArrayAdapter<String> listadapter;
    ActionBar actionBar;
    int t;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        newlv = findViewById(R.id.list_view);
        actionBar = getSupportActionBar();
        setlist = new ArrayList<>();
        keys = new ArrayList<>();
        mdata = FirebaseDatabase.getInstance().getReference();
         t = getIntent().getIntExtra("sett", -1);
        if (t == -1) {

            setlist.add("Update an Event");
            setlist.add("Add an Alert");
            setlist.add("Delete an Event");
            setlist.add("Delete an Alert");
            setlist.add("Add an Event");


        }

        listadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, setlist);
        newlv.setAdapter(listadapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        newlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                if(t==-1){
                    if(i==4) {
                        startActivity(new Intent(ListActivity.this, AddEvent.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        return;
                    }
                        actionBar.setTitle(setlist.get(i)+">>");
                        t=i;
                    setlist.clear();
                    mdata.child("Events").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            s=dataSnapshot.getKey();
                            keys.add(s);
                            s=dataSnapshot.child("name").getValue().toString();
                            setlist.add(s);
                            listadapter.notifyDataSetChanged();

                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                                setlist.remove(dataSnapshot.child("name").getValue().toString());
                                keys.remove(dataSnapshot.getKey());
                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                }
               else{
                    switch (t){
                        case 0:
                            Toast.makeText(ListActivity.this,"This feature will be added soon",Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            Intent add=new Intent(ListActivity.this,AddAlerts.class);
                            add.putExtra("key",keys.get(i));
                            add.putExtra("name",setlist.get(i));
                            startActivity(add);
                            finish();
                            break;
                        case 2:
                            final ProgressDialog pd=new ProgressDialog(ListActivity.this);
                            pd.setTitle("Deleting Event\n Please Wait...");

                            AlertDialog.Builder ab=new AlertDialog.Builder(ListActivity.this);
                            ab.setTitle("Delete Event: "+setlist.get(i));
                            ab.setMessage(Html.fromHtml("<font color='#ff0000'>Are You Sure You Want to Delete this Event\n" +
                                    "All its information, stuff ,Images etc. Will be deleted "+
                                    "and will not be Back again</font>"));

                            ab.setPositiveButton("Yes delete it", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialogInterface, int i2) {

                                    pd.show();

                                    DatabaseReference mdelete=mdata.child("Events").child(keys.get(i));
                                    mdelete.child("image").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            StorageReference ms=FirebaseStorage.getInstance().getReferenceFromUrl(dataSnapshot.getValue().toString());
                                            ms.delete();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                    mdelete.child("Alerts").addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                            mdata.child("Alerts").child(dataSnapshot.getValue().toString()).getRef().removeValue();

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
                                    mdelete.child("Gallery").addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot,  String s) {
                                                      final String  s2=dataSnapshot.getValue().toString();
                                            mdata.child("Gallery").child(dataSnapshot.getValue().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    String s1=dataSnapshot.child("url").getValue().toString();
                                                    StorageReference sRef= FirebaseStorage.getInstance().getReferenceFromUrl(s1);
                                                    sRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            mdata.child("Gallery").child(s2).getRef().removeValue();
                                                        }
                                                    });
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
                                    mdelete.getRef().removeValue();
                                    pd.dismiss();
                                    Toast.makeText(ListActivity.this,"Event Deleted",Toast.LENGTH_SHORT).show();
                                    finish();
                                }

                            });

                            ab.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                                ab.show();
                                break;
                        case 3:
                            Toast.makeText(ListActivity.this,"This feature will be added soon",Toast.LENGTH_SHORT).show();
                                break;

                    }

                }
            }
        });
    }
}






