package dawar.catchevent;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Image_slider extends Fragment {

        private int pos;
        private ViewPager viewPager;
        private ArrayList<String> dates;
        private ArrayList<String> images,captns;
        TextView dateview,counts,captn;
    public Image_slider() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_image_slider, container, false);
        viewPager=view.findViewById(R.id.gal_pager);
        dateview=view.findViewById(R.id.idate);
        counts=view.findViewById(R.id.lbl_count);
        captn=view.findViewById(R.id.caption);

        pos=getArguments().getInt("pos");
        if(pos!=-1) {
            dates = getArguments().getStringArrayList("dts");
            images = getArguments().getStringArrayList("img");
            captns = getArguments().getStringArrayList("cts");

        }
        else {
            dates=new ArrayList<>();
            images=new ArrayList<>();
            captns=new ArrayList<>();

            dates.add(getArguments().getString("dts"));
            images.add(getArguments().getString("img"));
            captns.add(getArguments().getString("cts"));
            counts.setVisibility(View.INVISIBLE);

        }


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

            viewPager.setAdapter(new PagerAdapter() {
                @Override
                public int getCount() {
                    return images.size();
                }

                @Override
                public boolean isViewFromObject(View view, Object object) {
                    return (view == (ImageView) object);
                }

                @Override
                public Object instantiateItem(ViewGroup container, final int position) {
                    final ImageView IM = new ImageView(getContext());
                    IM.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
                    Picasso.with(getContext()).load(images.get(position)).networkPolicy(NetworkPolicy.OFFLINE).into(IM);
                    container.addView(IM);
//                notifyDataSetChanged();
                    return IM;
                }

                @Override
                public void destroyItem(ViewGroup container, int position, Object object) {

//                super.destroyItem(container, position, object);
                    container.removeView((ImageView) object);
                }
            });
            viewPager.setCurrentItem(pos);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    counts.setText("" + (position + 1) + "/" + dates.size());
                    dateview.setText(dates.get(position));
                    captn.setText(captns.get(position));
                }

                @Override
                public void onPageSelected(int position) {


                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }

}
