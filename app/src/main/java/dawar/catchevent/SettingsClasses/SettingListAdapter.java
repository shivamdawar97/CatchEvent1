package dawar.catchevent.SettingsClasses;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import dawar.catchevent.EventClasses.AddEvent;
import dawar.catchevent.R;

import static dawar.catchevent.CatchEvent.mAuth;

public class SettingListAdapter extends BaseAdapter {
    private ArrayList<String> list;
    private Context ctx;

    SettingListAdapter(Context c, ArrayList<String> list1){
        ctx=c;
        list=list1;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view= LayoutInflater.from(ctx).inflate(R.layout.list_item,viewGroup,false);
        ((TextView)view.findViewById(R.id.final_list_item)).setText(list.get(i));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                view.setBackgroundColor(ctx.getResources().getColor(R.color.select));
                switch (list.get(i)) {

                    case "Add Event":
                        ((SettingsActivity) ctx).finish();
                        ctx.startActivity(new Intent(ctx, AddEvent.class));
                        break;
                    case "LogOut":
                        mAuth.signOut();
                       Toast.makeText(view.getContext(),"Logged Out Successfully",Toast.LENGTH_LONG).show();
                        ((SettingsActivity) view.getContext()).finish();
                        break;

                    default:
                        SettingsActivity.fragments.add(new SecondFragment());
                        SecondFragment.t = i;
                        ((FragmentPagerAdapter) SettingsActivity.adapter).notifyDataSetChanged();

                        break;
                }
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setBackgroundColor(0);
                    }
                },700);
            }
        });
        return view;
    }


}
