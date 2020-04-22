package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetWorkSettingActivity extends AppCompatActivity {

    private EditText serviceAddress_editText;
    private EditText port_editText;
    private Button quxiao_button;
    private Button save_button;
    private ProgressBar progressBar;

    private String serviceAddress_value;
    private int port_value;

    private InetAddress inetAddress;
    //判断是否已设置服务端网络地址
    private boolean isNetSetting=false;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_work__setting);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        sharedPreferences=getSharedPreferences("netsetting",MODE_PRIVATE);
        editor=sharedPreferences.edit();

        serviceAddress_editText= findViewById(R.id.serviceAddress);
        port_editText=findViewById(R.id.port);

        progressBar=findViewById(R.id.Netsetting_progressBar);

        quxiao_button=findViewById(R.id.Netsettingquxiao);
        save_button=findViewById(R.id.Netsetting);

        Handler handler=new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0x000:Toast.makeText(NetWorkSettingActivity.this,"让我看看有没有异常",Toast.LENGTH_SHORT).show();
                    break;
                    case 0x001: Toast.makeText(NetWorkSettingActivity.this,"请填写服务器地址",Toast.LENGTH_SHORT).show();break;
                    case 0x002:progressBar.setVisibility(View.VISIBLE);break;
                    case 0x003: Toast.makeText(NetWorkSettingActivity.this
                                ,"该地址不可连接"
                                ,Toast.LENGTH_SHORT).show();break;
                    case 0x004: Toast.makeText(NetWorkSettingActivity.this
                            ,"保存成功",Toast.LENGTH_SHORT).show();break;
                    case 0x005:progressBar.setVisibility(View.INVISIBLE);break;
                        default:Toast.makeText(NetWorkSettingActivity.this,"未知消息",Toast.LENGTH_SHORT);
                }
            }
        };


        if(sharedPreferences.getBoolean("isNetSetting",false)){
            serviceAddress_editText.setText(sharedPreferences.getString("serviceAddress",null));
            try {
                port_editText.setText(sharedPreferences.getString("port",null));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        quxiao_button.setOnClickListener(View->{
           finish();
        });

        save_button.setOnClickListener(View->{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(0x002);
                    if(serviceAddress_editText.length()!=0){
                        serviceAddress_value = serviceAddress_editText.getText().toString();

                        if (port_editText.length()!=0) {
                            port_value = Integer.parseInt(port_editText.getText().toString());
                        }else{
                            port_value= Integer.parseInt("8080");
                        }

                        try {
                            inetAddress=InetAddress.getByName(serviceAddress_value);
                            if(inetAddress.isReachable(5000)){
                                isNetSetting=true;
                                handler.sendEmptyMessage(0x004);
                                handler.sendEmptyMessage(0x005);
                            }else{
                                isNetSetting=false;
                                handler.sendEmptyMessage(0x003);
                                handler.sendEmptyMessage(0x005);
                            }
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e){
                            handler.sendEmptyMessage(0x003);
                            handler.sendEmptyMessage(0x005);
                        }

                    }else{
                        handler.sendEmptyMessage(0x001);
                    }
                }
            }).start();

        });

    }


    @Override
    protected void onPause() {
        super.onPause();


        if (isNetSetting){
            editor.putString("serviceAddress",serviceAddress_value);
            editor.commit();
            editor.putString("port",port_editText.getText().toString());
            editor.commit();
            editor.putBoolean("isNetSetting",isNetSetting);
            editor.commit();
        }else{
            if (sharedPreferences.getBoolean("isNetSetting",false)){

            }else {
                editor.remove("serviceAddress");
                editor.commit();
                editor.remove("port");
                editor.commit();
                editor.putBoolean("isNetSetting",isNetSetting);
                editor.commit();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        port_editText.addTextChangedListener(new TextWatcher() {
            String string="";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                string=s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Integer.parseInt(string)<0||Integer.parseInt(string)>65535){
                    port_editText.setError(getString(R.string.port_format));
                }else{
                    if (port_editText.getText()!=null) {
                        port_value = Integer.parseInt(string);
                    }else{
                        port_value=8080;
                    }
                }
            }
        });
    }

    public Socket getSocket(){
        Socket socket = null;
        if (isNetSetting){
            try {
                socket=new Socket(serviceAddress_value,port_value);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            return null;
        }
        return socket;
    }
}
