
package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class WelcomeActivity extends AppCompatActivity {

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Intent intent=new Intent(WelcomeActivity.this, DengluActivity.class);
            startActivity(intent);
            WelcomeActivity.this.finish();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 001:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    recursionCheck(1,0);
                }else{
                    Toast.makeText(this,"缺少访问网络权限,部分功能异常",Toast.LENGTH_SHORT).show();
                    recursionCheck(1,11);
                }
                break;
            case 002:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    recursionCheck(2,0);
                }else{
                    Toast.makeText(this,"缺少读取外部存储权限,部分功能异常",Toast.LENGTH_SHORT).show();
                    recursionCheck(2,22);
                }
                break;
            case 003:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    recursionCheck(3,0);
                }else{
                    Toast.makeText(this,"缺少写入外部存储权限,部分功能异常",Toast.LENGTH_SHORT).show();
                    recursionCheck(3,33);
                }
                break;
            case 004:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    recursionCheck(4,0);
                }else{
                    Toast.makeText(this,"缺少获取位置信息权限,部分功能异常",Toast.LENGTH_SHORT).show();
                    recursionCheck(4,44);
                }
                break;
            case 005:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    handler.sendEmptyMessageDelayed(0,500);
                }else{
                    Toast.makeText(this,"缺少获取位置信息权限,部分功能异常",Toast.LENGTH_SHORT).show();
                    handler.sendEmptyMessageDelayed(0,500);
                }
                break;
            default:break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Typeface typeface=Typeface.createFromAsset(getAssets(),"fonts/STHUPO.TTF");

        TextView textView=findViewById(R.id.welcomeText2);
        textView.setTypeface(typeface);

        recursionCheck(0,0);
    }

    private void recursionCheck(int i,int j){
        if (checkSelfPermission(Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED&&(i!=1&&isPermissionsRefused(j))){
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.INTERNET},001);
        }else{
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED&&(i!=2&&isPermissionsRefused(j))){
                ActivityCompat.requestPermissions(this,new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE},002);
            }else {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED&&(i!=3&&isPermissionsRefused(j))) {
                    ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 003);
                }else {
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED&&(i!=4&&isPermissionsRefused(j))) {
                        ActivityCompat.requestPermissions(this, new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION}, 004);
                    }else {
                        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this, new String[]{
                                    Manifest.permission.ACCESS_COARSE_LOCATION}, 005);
                        }else {
                            handler.sendEmptyMessageDelayed(0,500);
                        }
                    }
                }
            }

        }
    }

    private boolean isPermissionsRefused(int i){
        return i==11?false:i==22?false:i==33?false:i==44?false:true;
    }
}
