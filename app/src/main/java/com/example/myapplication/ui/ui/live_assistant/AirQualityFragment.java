package com.example.myapplication.ui.ui.live_assistant;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AirQualityFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AirQualityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AirQualityFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private View view;
    private TextView maxorminvalue;
    private BarChart barChart;
    private List<String> airList;
    private List<Float> fs;
    private List<BarEntry> entries = new ArrayList<BarEntry>();
    private List<BarEntry> nullentries = new ArrayList<BarEntry>();
    private BarDataSet nulldataSet=null;
    private BarDataSet dataSet=null;
    private BarData data=null;
    private int j=3;
    public AirQualityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AirQualityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AirQualityFragment newInstance(String param1, String param2) {
        AirQualityFragment fragment = new AirQualityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_air_quality, container, false);
        barChart=view.findViewById(R.id.air_quality_barchart);
        maxorminvalue=view.findViewById(R.id.maxorminview);

        airList=new ArrayList<>(20);

        initializeBarChart();

        return view;
    }

    class MyHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what==0x001){
                maxorminvalue.setText("过去一分钟空气质量最差值:"+dataSet.getYMax());
                //获取j点上的entry组成list
                List<BarEntry> entryList=new ArrayList<>();
                if (dataSet.getEntryCount()%20==0&&dataSet.getEntryCount()>=20) {
                    List<BarEntry> list=new ArrayList<>();
                    dataSet=new BarDataSet(list,"");
                    dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                    //设置数据显示颜色：柱子颜色
                    dataSet.setColor(Color.parseColor("#AAAAAA"));
                    dataSet.setDrawValues(false);
                    dataSet.setBarShadowColor(Color.parseColor("#000000"));

                    //柱状图数据集
                    data = new BarData(dataSet);
                    //设置柱子宽度
                    data.setBarWidth(1f);
                    barChart.setData(data);//装载数据
                    barChart.notifyDataSetChanged();
                    barChart.invalidate();
                            /*entryList = dataSet.getEntriesForXValue(j);
                    if (entryList.size()!=0) {
                        dataSet.removeEntry(entryList.get(0));
                        barChart.notifyDataSetChanged();
                        barChart.invalidate();
                    }else{
                        Log.i("?????????????????????------", String.valueOf(entryList));
                    }*/
                }
                if (airList.size()!=0)
                    dataSet.addEntry(new BarEntry(j,Integer.parseInt(airList.get(airList.size()-1))));
                barChart.notifyDataSetChanged();
                barChart.invalidate();

                Log.i("@@@@@@@@@@@@@@@@@@@@@@", String.valueOf(dataSet.getEntryCount()));

                j=j+3;
                if (j>60) {
                    j = 3;
                    airList.removeAll(airList);
                }
            }
        }
    }

    @Override
    public void onResume() {
        MyHandler myHandler=new MyHandler();
        super.onResume();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Thread thread=new Thread(new MyThread());
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                   myHandler.sendEmptyMessage(0x001);

            }
        },0,500);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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

    private void initializeBarChart(){
        XAxis xAxis=barChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(false);
        xAxis.setLabelCount(20);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(62f);
        //如果设置为true，则图表将避免图表中的第一个和最后一个标签条目“截断”图表或屏幕的边缘
        xAxis.setAvoidFirstLastClipping(true);

        YAxis leftYAxis=barChart.getAxisLeft();
        YAxis rightAxis=barChart.getAxisRight();
        rightAxis.setEnabled(false);
        leftYAxis.setLabelCount(7);
        leftYAxis.setAxisMinimum(0f);
        leftYAxis.setAxisMaximum(118f);
        leftYAxis.setGranularity(18f);
        leftYAxis.setDrawAxisLine(false);

        Legend legend=barChart.getLegend();
        legend.setEnabled(false);

        Description description=new Description();
        description.setEnabled(false);
        MyMarkerView my=new MyMarkerView(getContext(),R.layout.markertv);


        //创建数据集
        dataSet=new BarDataSet(entries,"");
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        //设置数据显示颜色：柱子颜色
        dataSet.setColor(Color.parseColor("#AAAAAA"));
        dataSet.setDrawValues(false);
        dataSet.setBarShadowColor(Color.parseColor("#000000"));

        //柱状图数据集
        data = new BarData(dataSet);
        //设置柱子宽度
        data.setBarWidth(1f);
        barChart.setData(data);//装载数据
        barChart.setMarker(my);
        barChart.setPinchZoom(true);
        //将此设置为false可禁用图表上的所有手势和触摸，默认值：true
        //barChart.setTouchEnabled(false);
        barChart.setDescription(description);
        barChart.setDrawBorders(false);
        barChart.setFitBars(true); //X轴自适应所有柱形图
    }

    private class MyMarkerView extends MarkerView {
        private TextView tvContent;
        /**
         * Constructor. Sets up the MarkerView with a custom layout resource.
         *
         * @param context
         * @param layoutResource the layout resource to use for the MarkerView
         */
        public MyMarkerView(Context context, int layoutResource) {
            super(context, layoutResource);
            tvContent=findViewById(R.id.marker_tv);
        }

        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            super.refreshContent(e,highlight);
            tvContent.setText(""+(int)e.getY());
        }

        @Override
        public MPPointF getOffset() {
            return new MPPointF(-(getWidth()/2),-getHeight());
        }
    }

    private int traversalMax(List<String> list){
        ArrayList<String> strings= (ArrayList) list;
        int max=0;
        if (strings.size()!=0){
            max=Integer.parseInt((String) strings.get(0));
            for (int i=0;i<strings.size();i++){
                if (Integer.parseInt( strings.get(i))>max){
                    max=Integer.parseInt(strings.get(i));
                }else
                    continue;
            }
        }
        return max;
    }
    private String getData(){
        /*
        Gson gson= new Gson();
        try {
            URL url=new URL("https://www.tianqiapi.com/api/?appid=55123848&appsecret=7I45nfxz&version=v1&city=湛江");
            HttpURLConnection urlConnection= (HttpURLConnection) url.openConnection();
            InputStream inputStream=urlConnection.getInputStream();
            InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);

            String line=null;
            StringBuilder stringBuilder=new StringBuilder();
            while ((line=bufferedReader.readLine())!=null){
                stringBuilder.append(line);
            }
            bufferedReader.close();

            WeatherInformation weatherInformation=gson.fromJson(stringBuilder.toString(), WeatherInformation.class);

            airQueue.offer(weatherInformation.data[0].air);
            Log.i("############################",weatherInformation.data[0].air);
        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }*/
        return String.valueOf((int)(Math.random()*100)+1);
    }

    class MyThread implements Runnable{
        @Override
        public void run() {
            airList.add(getData());
        }
    }

    private int formatchange(int i){
        switch (i){
            case 3:return 1;
            case 6:return 2;
            case 9:return 3;
            case 12:return 4;
            case 15:return 5;
            case 18:return 6;
            case 21:return 7;
            case 24:return 8;
            case 27:return 9;
            case 30:return 10;
            case 33:return 11;
            case 36:return 12;
            case 39:return 13;
            case 42:return 14;
            case 45:return 15;
            case 48:return 16;
            case 51:return 17;
            case 54:return 18;
            case 57:return 19;
            case 60:return 20;
        }
        return 0;
    }
}
