package com.example.myapplication.ui.ui.live_assistant;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.WeatherInformation;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment implements AirQualityFragment.OnFragmentInteractionListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //折线图
    private LineChart lineChart;

    //即时天气数据
    private Gson gson;
    private WeatherInformation weatherInformation;
    private String[] maxTemperature = new String[7];
    private String[] minTemperature = new String[7];
    private String[] dars=new String[7];
    private TextView nowTemperature;
    private String nowTemperatureValue;
    private TextView relativeTemperature;
    private String relativeTemperatureValue;
    private TextView city;
    private ImageButton refresh;
    //生活指数数据
    private int[] ints = new int[]{R.mipmap.taiyang, R.mipmap.yaowang, R.mipmap.yifu, R.mipmap.qiu,
            R.mipmap.air_pollute_spread};
    private String[] liveIndexnames = new String[]{"紫外线指数", "感冒指数", "穿衣指数"
            , "运动指数", "空气污染扩散指数"};
    private String[] indexs = new String[]{"100", "5", "21", "800", "10"};
    //整点天气实况
    List<Fragment> fragmentList = new ArrayList<>();
    Drawable drawable;
    TextView air_quality;
    TextView temperature;
    TextView relative_humidity;
    TextView carbon_dioxide;

    private OnFragmentInteractionListener mListener;

    public MainFragment() {
        // Required empty public constructor必需的空公共构造函数
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
        //创建Bundle 准备向Fragment传入参数
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        //向Fragment传入参数
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
    public void onFragmentInteraction(Uri uri) {
    }

    class MyThread extends Thread{
        @Override
        public void run() {
            super.run();
            sendGet();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tian_qi, container, false);
        MyThread myThread=new MyThread();
        nowTemperature=view.findViewById(R.id.today_temperature);
        relativeTemperature=view.findViewById(R.id.now_relative_temperature);
        lineChart = view.findViewById(R.id.seven_day_weather);
        myThread.start();
        try {
            myThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        nowTemperature.setText(nowTemperatureValue);
        relativeTemperature.setText("今天："+relativeTemperatureValue);
        initializeLineChart();
        city=view.findViewById(R.id.city);
        city.setText(getCity());
        refresh=view.findViewById(R.id.imageButton);
        refresh.setOnClickListener(View->{
            city.setText(getCity());
            initializeLineChart();
        });

        //生活指数
        RecyclerView recyclerView = view.findViewById(R.id.live_index);
        LinearLayoutManager llm = new LinearLayoutManager(this.getContext());
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(new MRecylerAdapter());

        //整点天气实况
        ViewPager viewPager = view.findViewById(R.id.weather_statistics);
        initializeFragmentList();
        //在activity中使用时创建对象需传入getSupportFragmentManager()作为参数，在fragment中使用时需要传入getChildFragmentManager()作为参数
        viewPager.setAdapter(new SectionsPagerAdapter(getChildFragmentManager()));

        viewPager.addOnPageChangeListener(new MyViewPagerListener());
        drawable = getResources().getDrawable(R.drawable.light_gray_border, null);

        air_quality = view.findViewById(R.id.air_quality);
        carbon_dioxide = view.findViewById(R.id.carbon_dioxide);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override//回调接口
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

    private class Day {
        String maxTemperature;
        String minTemperature;

        Day() {
        }

        Day(String maxTemperature, String minTemperature) {
            this.maxTemperature = maxTemperature;
            this.minTemperature = minTemperature;
        }

        public String getMaxTemperature() {
            return maxTemperature;
        }

        public String getMinTemperature() {
            return minTemperature;
        }
    }

    private void initializeLineChart() {
        lineChart.setDrawBorders(false);
        lineChart.animateX(1000);
        lineChart.setViewPortOffsets(100, 100, 100, 100);
        lineChart.setFitsSystemWindows(true);
        List<Day> list = new ArrayList<>();
        List<Entry> maxT = new ArrayList<>();
        List<Entry> minT = new ArrayList<>();
        if (maxTemperature[0]!=null){
            for (int i = 0; i < maxTemperature.length; i++) {
                list.add(new Day(maxTemperature[i], minTemperature[i]));
                maxT.add(new Entry(i, Integer.parseInt(list.get(i).getMaxTemperature().substring(0,2))));
                minT.add(new Entry(i, Float.parseFloat(list.get(i).getMinTemperature().substring(0,2))));
            }
        }else {
            Toast.makeText(getContext(),"无法获取数据,请检查网络",Toast.LENGTH_SHORT).show();
        }
        LineDataSet maxlineDataSet = new LineDataSet(maxT, "");
        LineDataSet minlineDataSet = new LineDataSet(minT, "");

        List<ILineDataSet> lineDataSets = new ArrayList<>();
        lineDataSets.add(maxlineDataSet);
        lineDataSets.add(minlineDataSet);

        //线颜色
        maxlineDataSet.setColor(Color.parseColor("#ff0000"));
        maxlineDataSet.setCircleColor(Color.parseColor("#ff0000"));
        maxlineDataSet.setCircleHoleColor(Color.parseColor("#ff0000"));
        minlineDataSet.setColor(Color.parseColor("#0000ff"));
        minlineDataSet.setCircleColor(Color.parseColor("#0000ff"));
        minlineDataSet.setCircleHoleColor(Color.parseColor("#0000ff"));
        //线宽度
        maxlineDataSet.setLineWidth(1f);
        minlineDataSet.setLineWidth(1f);
        //不显示线上数据点
        maxlineDataSet.setDrawCircles(true);
        minlineDataSet.setDrawCircles(true);

        maxlineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        minlineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        //绘制填充
        maxlineDataSet.setDrawFilled(false);
        minlineDataSet.setDrawFilled(false);

        LineData lineData = new LineData(lineDataSets);
        //当无数据时
        lineChart.setNoDataText("无数据显示");

        //是否启用数据集的绘图(点上显示数据)
        lineData.setDrawValues(true);

        XAxis xAxis = lineChart.getXAxis();

        //x轴的标签
        xAxis.setDrawLabels(true);

        xAxis.setPosition(XAxis.XAxisPosition.TOP);

        //刻度间最小间隔
        xAxis.setGranularity(1f);

        //X轴的刻度数量和最大最小值
        xAxis.setLabelCount(7);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(6f);

        xAxis.setAvoidFirstLastClipping(false);

        //网格线
        xAxis.setDrawGridLines(false);

        //标签旋转角度
        xAxis.setLabelRotationAngle(0);

        //x轴字体
        xAxis.setTextColor(Color.parseColor("#3f51b5"));
        xAxis.setTextSize(16f);

        //设置数值格式
        if (dars[0]!=null){
            try {
                xAxis.setValueFormatter(new ValueFormatter() {

                    @Override
                    public String getFormattedValue(float value) {
                        int position = (int) value;
                        if (position > dars.length)
                            position = 0;
                        return dars[position];
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(getContext(),"无法获取数据,请检查网络",Toast.LENGTH_SHORT).show();
        }
        //Y轴
        YAxis leftyAxis = lineChart.getAxisLeft();
        YAxis rightyAxis = lineChart.getAxisRight();
        //Y轴是否显示
        rightyAxis.setEnabled(false);//右侧Y轴不显示

        leftyAxis.setDrawGridLines(true);

        leftyAxis.setGranularity(1f);

        //轴的颜色
        xAxis.setAxisLineColor(Color.parseColor("#00ffffff"));
        leftyAxis.setAxisLineColor(Color.parseColor("#00ffffff"));
        leftyAxis.setDrawLabels(false);

        leftyAxis.setLabelCount(4);

        leftyAxis.setAxisMinimum(0);
        leftyAxis.setAxisMaximum(36f);

        //获得图例
        Legend legend = lineChart.getLegend();
        //隐藏图例
        legend.setEnabled(false);
        //隐藏描述
        Description description = new Description();
        description.setEnabled(false);
        lineChart.setDescription(description);

        lineChart.setData(lineData);
    }

    private class MRecylerAdapter extends RecyclerView.Adapter<MRecylerAdapter.mViewHolder> {
        @NonNull
        @Override
        public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.live_index_recyler, parent, false);
            return new mViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull mViewHolder holder, int position) {
            holder.imageView.setImageResource(ints[position]);
            holder.live_index_name.setText(liveIndexnames[position]);
            holder.live_index_level.setText(new MLiveindex().getLiveLevel(position, indexs[position]));
            holder.live_index_describe.setText(new MLiveindex().getLiveDescribe(position, indexs[position]));
        }

        @Override
        public int getItemCount() {
            return 5;
        }

        class mViewHolder extends RecyclerView.ViewHolder {

            private ImageView imageView;
            private TextView live_index_name;
            private TextView live_index_level;
            private TextView live_index_describe;

            public mViewHolder(@NonNull View itemView) {
                super(itemView);
                this.imageView = itemView.findViewById(R.id.live_index_icon);
                this.live_index_name = itemView.findViewById(R.id.live_index_name);
                this.live_index_level = itemView.findViewById(R.id.live_index_level);
                this.live_index_describe = itemView.findViewById(R.id.live_index_describe);
            }
        }
    }

    private class MLiveindex {
        MLiveindex() {
        }

        public String getLiveLevel(int position, String index) {
            switch (position) {
                case 0:
                    return getUltraviolet(index);
                case 1:
                    return getCold(index);
                case 2:
                    return getGard(index);
                case 3:
                    return getExercise(index);
                case 4:
                    return getAirPollution(index);
            }
            return "无数据";
        }

        public String getLiveDescribe(int position, String index) {
            switch (position) {
                case 0:
                    return getUltraviolet_describe(index);
                case 1:
                    return getCold_describe(index);
                case 2:
                    return getGard_describe(index);
                case 3:
                    return getExercise_describe(index);
                case 4:
                    return getAirPollution_describe(index);
            }
            return "无数据";
        }

        public String getCold(String cold) {
            if (Integer.parseInt(cold) < 8) {
                return "较易发(" + cold + ")";
            } else if (Integer.parseInt(cold) >= 8) {
                return "少发（" + cold + ")";
            }
            return "无数据";
        }

        public String getCold_describe(String cold) {
            if (Integer.parseInt(cold) < 8) {
                return "温度低，风较大，较易发生感冒，注意防护";
            } else if (Integer.parseInt(cold) >= 8) {
                return "无明显降温，感冒机率较低";
            }
            return "无数据";
        }

        public String getGard(String gard) {
            if (Integer.parseInt(gard) < 12) {
                return "冷(" + gard + ")";
            } else if (Integer.parseInt(gard)
                    >= 12 && Integer.parseInt(gard) <= 21) {
                return "舒适(" + gard + ")";
            } else if (Integer.parseInt(gard) > 21) {
                return "热(" + gard + ")";
            }
            return "无数据";
        }

        public String getExercise(String exercise) {
            if (Integer.parseInt(exercise) < 3000) {
                return "适宜(" + exercise + ")";
            } else if (Integer.parseInt(exercise)
                    >= 3000 && Integer.parseInt(exercise) <= 6000) {
                return "中(" + exercise + ")";
            } else if (Integer.parseInt(exercise) > 6000) {
                return "较不宜(" + exercise + ")";
            }
            return "无数据";
        }

        public String getGard_describe(String gard) {
            if (Integer.parseInt(gard) < 12) {
                return "建议穿长袖衬衫、单裤等服装";
            } else if (Integer.parseInt(gard)
                    >= 12 && Integer.parseInt(gard) <= 21) {
                return "建议穿短袖衬衫、单裤等服装";
            } else if (Integer.parseInt(gard) > 21) {
                return "适合穿T恤、短薄外套等夏季服装";
            }
            return "无数据";
        }

        public String getUltraviolet(String ultraviolet) {
            if (Integer.parseInt(ultraviolet) < 1000) {
                return "弱(" + ultraviolet + ")";
            } else if (Integer.parseInt(ultraviolet)
                    >= 1000 && Integer.parseInt(ultraviolet) <= 3000) {
                return "中等(" + ultraviolet + ")";
            } else if (Integer.parseInt(ultraviolet) > 3000) {
                return "强(" + ultraviolet + ")";
            }
            return "无数据";
        }

        public String getUltraviolet_describe(String ultraviolet) {
            if (Integer.parseInt(ultraviolet) < 1000) {
                return "辐射较弱，涂擦SPF12~15、PA+护肤品";
            } else if (Integer.parseInt(ultraviolet)
                    >= 1000 && Integer.parseInt(ultraviolet) <= 3000) {
                return "涂擦SPF大于15、PA+防晒护肤品";
            } else if (Integer.parseInt(ultraviolet) > 3000) {
                return "尽量减少外出，需要涂抹高倍数防晒霜";
            }
            return "无数据";
        }

        public String getAirPollution(String airPollution) {
            if (Integer.parseInt(airPollution) < 30) {
                return "优(" + airPollution + ")";
            } else if (Integer.parseInt(airPollution)
                    <= 30 && Integer.parseInt(airPollution) >= 100) {
                return "良(" + airPollution + ")";
            } else if (Integer.parseInt(airPollution) > 100) {
                return "污染(" + airPollution + ")";
            }
            return "无数据";
        }

        public String getAirPollution_describe(String airPollution) {
            if (Integer.parseInt(airPollution) < 30) {
                return "空气质量非常好，非常适合户外活动，趁机出去多呼吸新鲜空气";
            } else if (Integer.parseInt(airPollution)
                    >= 30 && Integer.parseInt(airPollution) <= 100) {
                return "易感人群应适当减少室外活动";
            } else if (Integer.parseInt(airPollution) > 100) {
                return "空气质量差，不适合户外活动";
            }
            return "无数据";
        }

        public String getExercise_describe(String exercise) {
            if (Integer.parseInt(exercise) < 3000) {
                return "气候适宜，推荐您进行户外运动";
            } else if (Integer.parseInt(exercise)
                    >= 3000 && Integer.parseInt(exercise) <= 6000) {
                return "易感人群应适当减少室外活动";
            } else if (Integer.parseInt(exercise) > 6000) {
                return "空气氧气含量低，请在室内进行休闲运动";
            }
            return "无数据";
        }
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                outlineChange(air_quality);
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    private void initializeFragmentList() {
        fragmentList.add(new AirQualityFragment());
        fragmentList.add(new CarbonDioxideFragment());
    }

    private void outlineChange(TextView textView) {
        if (textView == air_quality) {
            air_quality.setBackground(drawable);
            carbon_dioxide.setBackground(null);
        } else if (textView == carbon_dioxide) {
            carbon_dioxide.setBackground(drawable);
            air_quality.setBackground(null);
        }

    }

    private class MyViewPagerListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    outlineChange(air_quality);
                    break;
                case 1:
                    outlineChange(carbon_dioxide);
                    break;
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    private String getCity(){
        String city = null;
        LocationManager locationManager = (LocationManager) getContext()
                .getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && getContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(),"抱歉,本服务需要位置信息权限",Toast.LENGTH_SHORT).show();
            }
        }
        //Location代表地理位置的数据类
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        double longitude=location.getLongitude();
        double latitude=location.getLatitude();
        //Criteria指示用于选择位置提供者的应用标准的类
        Criteria criteria=new Criteria();

        List<Address> addresses=new ArrayList<>();

        if (location!=null){
            Geocoder geocoder=new Geocoder(getContext(), Locale.getDefault());
            try {
                addresses=geocoder.getFromLocation(latitude,longitude,1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (addresses!=null&&addresses.get(0)!=null){

            city=addresses.get(0).getLocality();
        }
        return city;
    }

    private void sendGet(){
         gson= new Gson();
        try {
            URL url=new URL("https://www.tianqiapi.com/api/?appid=55123848&appsecret=7I45nfxz&version=v1&city="+getCity().substring(0,getCity().length()-1));
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

            weatherInformation=gson.fromJson(stringBuilder.toString(),WeatherInformation.class);
            for (int i=0;i<weatherInformation.data.length;i++){
                if (i==0){
                    nowTemperatureValue=formatTemValue(weatherInformation.data[0].tem,0);
                    relativeTemperatureValue=weatherInformation.data[0].tem2+"-"
                            +weatherInformation.data[0].tem1;
                }

                dars[i]=formatTemValue(weatherInformation.data[i].day,1);
                maxTemperature[i]=weatherInformation.data[i].tem1;
                minTemperature[i]=weatherInformation.data[i].tem2;
            }
        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private String formatTemValue(String s,int i){
        String s1, s2;
        switch (i) {
            case 0: return s.substring(0, s.length() - 1) + "\u00b0";
            case 1: s1 = s.replace("）", "");
            s2 = s1.replace("（", "");
            return s2.substring(s2.length() - 2);
            }
        return "0";
    }
}
