package com.example.myapplication.ui.ui.data_analyze;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private List<Fragment> fragmentList;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_data_analyze, container, false);
        LayoutInflater layoutInflater= (LayoutInflater) ((MainActivity)mListener).getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        viewPager=view.findViewById(R.id.data_analyze_viewPager);


        viewPager.setAdapter(new SectionsPagerAdapter(getChildFragmentManager(),initFragmentList(fragmentList)));
        viewPager.addOnPageChangeListener(new MyViewPager());
        viewPager.setCurrentItem(0);


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        List<Fragment> fragmentList=new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm,List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList=fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }
    private class MyViewPager implements ViewPager.OnPageChangeListener{

        /*当滚动当前页面时，将调用此方法，作为以编程方式启动的平滑滚动或用户启动的触摸滚动的一部分。
指定人：
OnPageChangeListener接口中的onPageScrolled
参数：
位置–当前显示的第一页的位置索引。如果positionOffset为非零，则页面位置+1将可见。
positionOffset–从[0，1]开始的值，指示在位置处与页面的偏移量。
positionOffsetPixels–表示位置偏移的像素值。*/
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        //当滚动当前页面时（作为程序启动的平滑滚动的一部分或由用户启动的触摸滚动的一部分），将调用此方法。
        @Override
        public void onPageSelected(int position) {

        }
/*此方法是在状态改变的时候调用，其中state这个参数有三种状态：

SCROLL_STATE_DRAGGING（1）表示用户手指“按在屏幕上并且开始拖动”的状态（手指按下但是还没有拖动的时候还不是这个状态，只有按下并且手指开始拖动后log才打出。）
SCROLL_STATE_IDLE（0）滑动动画做完的状态。
SCROLL_STATE_SETTLING（2）在“手指离开屏幕”的状态*/
        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    private ArrayList<Fragment> initFragmentList(List<Fragment> fragmens){

        List<Fragment> fragmentList=new ArrayList<>();

        fragmentList.add(new CarProportion());
        fragmentList.add(new RepetitionProportion());
        fragmentList.add(new NumberOfTimesProportion());
        fragmentList.add(new AgeProportion());
        fragmentList.add(new SexProportion());
        fragmentList.add(new TimeFragmentProportion());
        fragmentList.add(new UnlawfulActProportion());

        return (ArrayList<Fragment>) fragmentList;
    }

}
