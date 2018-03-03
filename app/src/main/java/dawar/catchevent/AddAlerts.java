package dawar.catchevent;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class AddAlerts extends AppCompatActivity {
    DatabaseReference mdata;
    String postkey;
    EditText title,desc;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alerts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        title=findViewById(R.id.edit_title);
        desc=findViewById(R.id.edit_desc);
        pd=new ProgressDialog(this);
        TextView related=findViewById(R.id.related_event);
        postkey=getIntent().getStringExtra("name");
        actionBar.setTitle("Add an Alert>>"+postkey);
        related.append("\n "+postkey);
        postkey=getIntent().getStringExtra("key");
        mdata = FirebaseDatabase.getInstance().getReference();
        // ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_list_item_1,);
        // aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // spinner.setAdapter(aa);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;

    }

    public void updateAlert(View view) {
       if(title.getText().toString().isEmpty() || desc.getText().toString().isEmpty()){
           Toast.makeText(AddAlerts.this,"Update fields",Toast.LENGTH_SHORT).show();
       }
       else
       {pd.setTitle("Please wait...");
           DatabaseReference newPost=mdata.child("Alerts").push();
           String s=title.getText().toString();
           newPost.child("title").setValue(s);
           s=desc.getText().toString();
           newPost.child("desc").setValue(s);
           s= Calendar.getInstance().getTime().toString();
           newPost.child("date").setValue(s);
           s=getIntent().getStringExtra("name");
           newPost.child("ename").setValue(s);
           s=newPost.getKey();
           newPost=mdata.child("Events").child(postkey).child("Alerts").push();
           newPost.setValue(s).addOnCompleteListener(new OnCompleteListener<Void>() {
               @Override
               public void onComplete(@NonNull Task<Void> task) {
                   pd.dismiss();
                   finish();
               }
           });
       }
    }
}