package dawar.catchevent;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.callback.Callback;

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
        mdata = FirebaseDatabase.getInstance().getReference().child("Events");
         t = getIntent().getIntExtra("sett", -1);
        if (t == -1) {

            setlist.add("Update an Event");
            setlist.add("Add an Alert");
            setlist.add("Delete an Event");
            setlist.add("Delete an Alert");
            setlist.add("Update Tumbnail Events");

        }

        listadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, setlist);
        newlv.setAdapter(listadapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        newlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(t==-1){
                    actionBar.setTitle(setlist.get(i)+">>");
                        t=i;
                    setlist.clear();
                    mdata.addChildEventListener(new ChildEventListener() {
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
                            break;
                        case 1:
                            Intent add=new Intent(ListActivity.this,AddAlerts.class);
                            add.putExtra("key",keys.get(i));
                            add.putExtra("name",setlist.get(i));
                            startActivity(add);
                            finish();
                            break;
                        case 2:
                                break;
                        case 3:
                                break;
                        case 4:

                    }

                }
            }
        });
    }
}






