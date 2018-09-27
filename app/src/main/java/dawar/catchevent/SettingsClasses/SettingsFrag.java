package dawar.catchevent.SettingsClasses;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import dawar.catchevent.R;

import static dawar.catchevent.CatchEvent.Uid;
import static dawar.catchevent.CatchEvent.mAuth;
import static dawar.catchevent.CatchEvent.mdatabase;
import static dawar.catchevent.CatchEvent.userType;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFrag extends Fragment {
    ListView newlv;
    ArrayList<String> setlist,keys;
    FragmentActivity activity;
    TextView name_view,type_view;
    ImageView user_dp;

    public SettingsFrag() {

        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view=inflater.inflate(R.layout.fragment_settings, container, false);


        newlv = view.findViewById(R.id.list_view);

        setlist = new ArrayList<>();
        keys = new ArrayList<>();

        if(userType!=1) {
            setlist.add("Add Event");
            setlist.add("Add Alert");
            setlist.add("Update Event");
            setlist.add("Delete Event");
            setlist.add("Delete Alert");
        }
        setlist.add("LogOut");
        activity=getActivity();
        setListViewHeightBasedOnChildren(newlv);
        newlv.setAdapter(new SettingListAdapter(activity,setlist));
        LayoutAnimationController controller
                = AnimationUtils.loadLayoutAnimation(
                activity, R.anim.list_layout_controller);
        newlv.setLayoutAnimation(controller);
        name_view=view.findViewById(R.id.user_name_text);
        type_view=view.findViewById(R.id.user_type_text);
        user_dp=view.findViewById(R.id.imageId);


        mdatabase.child("users").child(Uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

               if(!dataSnapshot.hasChild("name")){
                    activity.finish();
                    mAuth.signOut();
                    return;
                }
                    String s = (dataSnapshot.child("name").getValue()).toString();

                name_view.setText(s);
                s="@";
                s=s.concat(dataSnapshot.child("username").getValue().toString());
                ((TextView)view.findViewById(R.id.username_text)).setText(s);
                switch (dataSnapshot.child("userType").getValue().toString()){
                    case "1":
                        s="Normal User";
                        break;
                    case "2":
                        s="Organiser";
                        break;
                    case "3":
                        s="Administrator";
                        break;

                }
                type_view.setText(s);
                s= Objects.requireNonNull(dataSnapshot.child("image").getValue()).toString();

                final String finalS = s;
                Picasso.get().load(s).networkPolicy(NetworkPolicy.OFFLINE).into(user_dp, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(finalS).into(user_dp);

                    }

                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return view;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LinearLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}
