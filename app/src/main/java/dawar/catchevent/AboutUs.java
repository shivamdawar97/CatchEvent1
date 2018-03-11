package dawar.catchevent;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AboutUs extends AppCompatActivity {
    EditText tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tv = findViewById(R.id._aboutus);
        Button b=findViewById(R.id.send_feedback);
        int i = getIntent().getIntExtra("pg", 1);
        if (i == 1) {
            tv.clearFocus();
            tv.setFocusable(false);
            b.setVisibility(View.INVISIBLE);
            InputStream is = getResources().openRawResource(R.raw.aboutus);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            String entireFile = "";
            try {
                while ((line = br.readLine()) != null) { // <--------- place readLine() inside loop
                    entireFile += (line + "\n"); // <---------- add each line to entireFile
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            tv.setText(entireFile); // <------- assign entireFile to TextView
        }
        else {
            ActionBar actionBar=getSupportActionBar();
            actionBar.setTitle("Send Feddback");
            tv.setTextSize(20);
        }

    }


    public void sendEmail(View view) {
        if(!tv.getText().toString().isEmpty()) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{"shivam97.dawar@gmail.com"});
            i.putExtra(Intent.EXTRA_SUBJECT, "CatchEvent Feedback");
            i.putExtra(Intent.EXTRA_TEXT, tv.getText().toString());
            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(AboutUs.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
