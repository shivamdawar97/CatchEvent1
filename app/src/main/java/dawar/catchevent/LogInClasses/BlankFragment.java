package dawar.catchevent.LogInClasses;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import dawar.catchevent.R;

import static dawar.catchevent.LogInClasses.SignUpFragment.card;

/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment extends Fragment {

    CardView card1,card2,card3;
    TextView back;
    final Handler handler = new Handler();

    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            card1.setCardBackgroundColor(null);
            card2.setCardBackgroundColor(null);
            card3.setCardBackgroundColor(null);
        }
    };
    ClickListener listener;

    public BlankFragment() {

        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_blank, container, false);
         card1=view.findViewById(R.id.card1);
        card2=view.findViewById(R.id.card2);
        card3=view.findViewById(R.id.card3);

        runnable.run();
        listener=new ClickListener();
        card1.setOnClickListener(listener);
        card2.setOnClickListener(listener);
        card3.setOnClickListener(listener);

        back=view.findViewById(R.id.textView7);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogInActivity.movePagerBackward();
            }
        });
        return view;
    }

    private class ClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            int id=view.getId();
            switch (id){
                case R.id.card1:
                    card1.setCardBackgroundColor(getResources().getColor(R.color.select));
                    card2.setCardBackgroundColor(null);
                    card3.setCardBackgroundColor(null);
                    card = 1;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms
                            LogInActivity.movePagerForward();
                        }
                    }, 200);

                    break;
                case R.id.card2:
                    card2.setCardBackgroundColor(getResources().getColor(R.color.select));
                    card1.setCardBackgroundColor(null);
                    card3.setCardBackgroundColor(null);
                    card = 2;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms
                            keyAlert();
                        }
                    }, 200);

                    break;
                case R.id.card3:
                    card3.setCardBackgroundColor(getResources().getColor(R.color.select));
                    card2.setCardBackgroundColor(null);
                    card1.setCardBackgroundColor(null);
                    card = 3;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms
                            keyAlert();
                        }
                    }, 200);
                    break;

            }
        }
    }

    private void keyAlert(){


        final String key,type;
        final FragmentActivity activity=getActivity();
        final AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        final EditText editKey=new EditText(activity);

        if(card==2)
        {
            key="110022";
            type="Organiser key";
        }
        else {
            key="113300";
            type="Administrator key";
        }

        builder.setView(editKey).setTitle("Enter :"+type);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String s=editKey.getText().toString();

                if(!TextUtils.isEmpty(s)) {
                    if ((card == 2 && s.matches(key)) || (card == 3 && s.matches(key))) {
                        LogInActivity.movePagerForward();
                    }
                    else {
                        Toast.makeText(activity,"Don't try to be smart",Toast.LENGTH_LONG).show();
                    }
                }

            }

        });
        builder.show();

    }


}
