package com.example.myapplication.ui.ui.live_assistant;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CarbonDioxideFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CarbonDioxideFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CarbonDioxideFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View view;
    private LineChart lineChart;
    private String[] strings=new String[]{"54","45","34","56","77","88","99","45","34","56","77","88"
            ,"45","34","56","77","88","67","23","87"};

    public CarbonDioxideFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CarbonDioxideFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CarbonDioxideFragment newInstance(String param1, String param2) {
        CarbonDioxideFragment fragment = new CarbonDioxideFragment();
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
        view=inflater.inflate(R.layout.fragment_carbon_dioxide, container, false);
        lineChart=view.findViewById(R.id.carbon_dioxide_linechart);

        TextView textView=view.findViewById(R.id.maxorminview);
        textView.setText("过去一分钟最大相对浓度:"+traversalMax(strings));

        initializeLineChart();

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

/*    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

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

    private void initializeLineChart(){
        XAxis xAxis=lineChart.getXAxis();
        xAxis.setLabelCount(20);
        xAxis.setGranularity(3f);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(60f);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setTextColor(Color.parseColor("#ffffff"));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftYAxis=lineChart.getAxisLeft();
        leftYAxis.setLabelCount(4);
        leftYAxis.setGranularity(30f);
        leftYAxis.setAxisMinimum(0f);
        leftYAxis.setAxisMaximum(108f);
        leftYAxis.setDrawGridLines(true);
        leftYAxis.setDrawAxisLine(false);
        leftYAxis.setTextColor(Color.parseColor("#ffffff"));
        leftYAxis.setGridColor(Color.parseColor("#ffffff"));

        YAxis rightYAxis=lineChart.getAxisRight();
        rightYAxis.setEnabled(false);

        List<Entry> entryList=new ArrayList<>();
        int j=3;
        for (int i=0;i<strings.length;i++){
            entryList.add(new Entry(j,Integer.parseInt(strings[i])));
            j=j+3;
        }
        //数据集
        LineDataSet lineDataSet=new LineDataSet(entryList,"");
        lineDataSet.setLineWidth(5f);
        lineDataSet.setColor(Color.parseColor("#ffffff"));
        lineDataSet.setDrawCircles(true);
        lineDataSet.setCircleRadius(10f);
        lineDataSet.setCircleColor(Color.parseColor("#ffffff"));
        lineDataSet.setCircleHoleRadius(5f);
        lineDataSet.setCircleHoleColor(Color.parseColor("#ffffff"));
        lineDataSet.setMode(LineDataSet.Mode.LINEAR);

        //数据
        LineData lineData=new LineData(lineDataSet);
        lineData.setDrawValues(false);

        Legend legend=lineChart.getLegend();
        legend.setEnabled(false);

        Description description=new Description();
        description.setTextColor(Color.parseColor("#ffffff"));
        description.setText("(S)");
        description.setTextSize(8f);
        description.setYOffset(-10f);

        lineChart.setVisibleXRangeMinimum(90f);
        lineChart.setDescription(description);
        lineChart.setDrawBorders(false);
        lineChart.setBackgroundColor(Color.parseColor("#FF00BCD4"));
        lineChart.setData(lineData);
        lineChart.setFitsSystemWindows(true);
        lineChart.invalidate();
    }
    private int traversalMax(String[] strings){
        int max=Integer.parseInt(strings[0]);
        for (int i=0;i<strings.length;i++){
            if (Integer.parseInt(strings[i])>max){
                max=Integer.parseInt(strings[i]);
            }
        }
        return max;
    }
}
