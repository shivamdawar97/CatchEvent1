package dawar.catchevent.SettingsClasses;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dawar.catchevent.EventClasses.UpdateEvent;
import dawar.catchevent.GalleryAndAlertClasses.AddAlerts;
import dawar.catchevent.MainActivity;
import dawar.catchevent.R;

import static dawar.catchevent.CatchEvent.Uid;
import static dawar.catchevent.CatchEvent.mdatabase;


public class SecondFragment extends Fragment {

    static int t;
    Context context;
    private OnListFragmentInteractionListener mListener;

    public SecondFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
            Context context = view.getContext();
            RecyclerView recyclerView =  view.findViewById(R.id.list);

                recyclerView.setLayoutManager(new LinearLayoutManager(context));

            recyclerView.setAdapter(new SecondRvAdapter(mListener,(TextView)view.findViewById(R.id.no_tv)));
            LayoutAnimationController controller
                    = AnimationUtils.loadLayoutAnimation(
                    view.getContext(), R.anim.list_layout_controller);
            recyclerView.setLayoutAnimation(controller);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
        mListener=new OnListFragmentInteractionListener();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

     class OnListFragmentInteractionListener {
        void onListFragmentInteraction(String name,String key){
            switch (t){
                case 1:
                    //Add Alert
                    Intent i=new Intent(context, AddAlerts.class);
                    i.putExtra("name",name);
                    i.putExtra("key",key);
                    startActivity(i);
                    break;
                case 2:
                    //update event
                    Intent i1=new Intent(context, UpdateEvent.class);
                    i1.putExtra("name",name);
                    i1.putExtra("key",key);
                    startActivity(i1);
                    break;
                case 3:
                    //Delete Event
                    deleteEvent(key,name);

                    break;
                case 4:
                    //Delete Alert
                    deleteAlert(key);
                    break;
            }
        }


     }
    private void deleteAlert(final String ekey) {
        final List<String> keys=new ArrayList<>();

        android.app.AlertDialog.Builder builderSingle = new android.app.AlertDialog.Builder(getActivity());
        builderSingle.setTitle("Select One :-");
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_singlechoice);
        mdatabase.child("Events").child(ekey).child("Alerts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    keys.add(snapshot.getKey());
                    arrayAdapter.add(snapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, final int which) {

                AlertDialog.Builder dialog=new AlertDialog.Builder(getActivity());
                dialog.setTitle("Delete Alert");
                dialog.setMessage(" Are you sure you want to delete this "+arrayAdapter.getItem(which)+" Alert"
                +" all the images related to it will also be deleted");
                dialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       String  key=keys.get(which);
                       deleteAlertHasKey(key,ekey);
                    }
                });
                dialog.setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                dialog.show();
            }

        });
        builderSingle.show();

    }

    private void deleteAlertHasKey(final String key, final String ekey) {

        //Delete the images of Alerts
        mdatabase.child("Alerts").child(key).child("images").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){

                    final String  s2= snapshot.getKey();
                    String s1= Objects.requireNonNull(snapshot.getValue()).toString();
                    StorageReference sRef= FirebaseStorage.getInstance().getReferenceFromUrl(s1);
                    sRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mdatabase.child("Gallery").child(s2).getRef().removeValue();
                            mdatabase.child("Events").child(ekey).child(s2).getRef().removeValue();
                        }
                    });
                }
                mdatabase.child("Alerts").child(key).getRef().removeValue();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void deleteEvent(final String key,String name){
        final ProgressDialog pd=new ProgressDialog(context);

        pd.setTitle("Deleting Event\n Please Wait...");

        AlertDialog.Builder ab=new AlertDialog.Builder(context);
        ab.setTitle("Delete Event: "+name);
        ab.setMessage(Html.fromHtml("<font color='#ff0000'>Are You Sure You Want to Delete this Event\n" +
                "All its information, stuff ,Images etc. Will be deleted "+
                "and will not be Back again</font>"));

        ab.setPositiveButton("Yes delete it", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i2) {

                pd.show();

                DatabaseReference mdelete=mdatabase.child("Events").child(key);
                mdelete.child("image").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        StorageReference ms= FirebaseStorage.getInstance().getReferenceFromUrl(dataSnapshot.getValue().toString());
                        ms.delete();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                mdelete.child("Alerts").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                            mdatabase.child("Alerts").child(snapshot.getKey()).getRef().removeValue();

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                mdelete.child("Gallery").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(final DataSnapshot snapshot:dataSnapshot.getChildren()){

                            final String  s2= Objects.requireNonNull(snapshot.getValue()).toString();

                            StorageReference sRef= FirebaseStorage.getInstance().getReferenceFromUrl(s2);
                            sRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mdatabase.child("Gallery").child(snapshot.getKey()).getRef().removeValue();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                mdelete.getRef().removeValue();
                mdatabase.child("users").child(Uid).child("events").child(key).getRef().removeValue();
                pd.dismiss();
                Toast.makeText(context,"Event Deleted",Toast.LENGTH_SHORT).show();
                ((SettingsActivity)context).finish();
            }

        });

        ab.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        ab.show();
    }
}
