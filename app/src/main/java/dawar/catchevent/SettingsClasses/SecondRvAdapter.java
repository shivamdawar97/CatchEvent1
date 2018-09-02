package dawar.catchevent.SettingsClasses;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import dawar.catchevent.CatchEvent;
import dawar.catchevent.R;
import dawar.catchevent.SettingsClasses.SecondFragment.OnListFragmentInteractionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Handler;

import static dawar.catchevent.CatchEvent.Titles;
import static dawar.catchevent.CatchEvent.Uid;
import static dawar.catchevent.CatchEvent.mdatabase;
import static dawar.catchevent.CatchEvent.userType;


public class SecondRvAdapter extends RecyclerView.Adapter<SecondRvAdapter.ViewHolder> {

    private  List<String> mValues;
    private  List<String> keys;
    private final OnListFragmentInteractionListener mListener;


     SecondRvAdapter(OnListFragmentInteractionListener listener, final TextView tv) {
        mListener = listener;
        mValues=new ArrayList<>();
        keys=new ArrayList<>();


        if(userType==2) {
            mdatabase.child("users").child(Uid).child("events").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        keys.add(snapshot.getKey());
                        mValues.add(Objects.requireNonNull(snapshot.getValue()).toString());
                        notifyDataSetChanged();
                        tv.setVisibility(View.INVISIBLE);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else if(userType==3) {

            mdatabase.child("Events").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        keys.add(snapshot.getKey());
                        mValues.add(snapshot.child("name").getValue().toString());
                        notifyDataSetChanged();
                        tv.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        holder.textView.setText(mValues.get(position));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    holder.mView.setBackgroundColor(holder.mView.getContext().getResources().getColor(R.color.select));
                    mListener.onListFragmentInteraction(mValues.get(holder.getAdapterPosition())
                            ,keys.get(holder.getAdapterPosition()));
                    (new android.os.Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            holder.mView.setBackgroundColor(0);
                        }
                    },700);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        final View mView;
        final TextView textView;
         ViewHolder(View view) {
            super(view);
            mView=view;
            textView=view.findViewById(R.id.item_number);
        }
    }
}
