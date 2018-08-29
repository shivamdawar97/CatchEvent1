package dawar.catchevent.UdbhavClasses;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import dawar.catchevent.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class UdbhavFragment extends Fragment {

    RelativeLayout relativeLayout;
    public UdbhavFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        View view=inflater.inflate(R.layout.fragment_udbhav, container, false);
        relativeLayout=view.findViewById(R.id.frag_ubv);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),UdbhavMainActivity.class));
            }
        });


        return view;
    }

}
