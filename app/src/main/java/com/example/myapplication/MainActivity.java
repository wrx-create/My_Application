package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.myapplication.ui.ui.data_analyze.AgeProportion;
import com.example.myapplication.ui.ui.data_analyze.CarProportion;
import com.example.myapplication.ui.ui.data_analyze.NumberOfTimesProportion;
import com.example.myapplication.ui.ui.data_analyze.RepetitionProportion;
import com.example.myapplication.ui.ui.data_analyze.SexProportion;
import com.example.myapplication.ui.ui.data_analyze.TimeFragmentProportion;
import com.example.myapplication.ui.ui.data_analyze.UnlawfulActProportion;
import com.example.myapplication.ui.ui.live_assistant.MainFragment;
import com.google.android.material.navigation.NavigationView;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
        , MainFragment.OnFragmentInteractionListener
        , com.example.myapplication.ui.ui.data_analyze.MainFragment.OnFragmentInteractionListener
        , CarProportion.OnFragmentInteractionListener
        , RepetitionProportion.OnFragmentInteractionListener
        , AgeProportion.OnFragmentInteractionListener
        , NumberOfTimesProportion.OnFragmentInteractionListener
        , SexProportion.OnFragmentInteractionListener
        , TimeFragmentProportion.OnFragmentInteractionListener
        , UnlawfulActProportion.OnFragmentInteractionListener {

    private TextView titleText;
    private TextView username;
    private TextView userphone;
    private UserInformation userInformation;
    private View headerlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        titleText = findViewById(R.id.title_text);
        //获取抽屉布局并设监听
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        userInformation = (UserInformation) getIntent().getSerializableExtra("user");
        headerlayout = navigationView.inflateHeaderView(R.layout.nav_header_layout);
        username = headerlayout.findViewById(R.id.user_name);
        username.setText(userInformation.getUserName());
        userphone = headerlayout.findViewById(R.id.user_phone);
        userphone.setText(userInformation.getPhone());

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar
                , R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.host_layout, new MainFragment());
        fragmentTransaction.commit();


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = null;
        switch (menuItem.getItemId()) {
            case R.id.nav_shenghuozhusgou:
                fragment = new MainFragment();
                titleText.setText("生活助手");
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_dataAnalyze:
                fragment = new com.example.myapplication.ui.ui.data_analyze.MainFragment();
                titleText.setText("数据分析");
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
        ft.replace(R.id.host_layout, fragment);
        ft.commit();
        return false;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public String getCity() {

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String city=null;


        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        //Geocoder用于处理地理编码和反向地理编码的类
        //getFromLocation(经度,纬度,要获取的最大地址数)返回一个地址数组，该地址已知可描述紧靠给定纬度和经度的区域

        //Address代表地址的类，即描述位置的一组字符串
        List<Address> result = null;

        try {
            if (location != null) {
                Geocoder gc = new Geocoder(this, Locale.getDefault());
                result = gc.getFromLocation(latitude,longitude, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result != null && result.get(0) != null) {
            //这块获取到的是个数组我们取一个就好 下面是具体的方法查查API就能知道自己要什么
            city=result.get(0).getFeatureName();

            Log.i("address", city);
            //Toast.makeText(mContext,result.get(0).toString(),Toast.LENGTH_LONG).show();
        }

        return city;
    }

}
