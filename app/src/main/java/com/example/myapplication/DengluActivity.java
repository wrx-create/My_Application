package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class DengluActivity extends AppCompatActivity {

    private TextView netSetting;
    private TextView zhaohuimima;
    private Button denglu;
    private Button zhuce;
    private EditText username_input;
    private EditText password_input;

    private CheckBox jizhumima;
    private CheckBox zidongdenglu;

    private SharedPreferences dengluPreferences;
    private SharedPreferences.Editor savePreferences;

    private String username;
    private String password;

    private Socket socket;

    private Intent intent;
    //判断是否登陆成功
    private boolean verifyZhanghao=false;
    private boolean verifyPassword=false;

    private UserInformation userInformation=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denglu);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        netSetting=findViewById(R.id.net_setting);
        zhaohuimima=findViewById(R.id.zhqohuimima);

        denglu=findViewById(R.id.deng_lu);
        zhuce=findViewById(R.id.zhu_ce);

        jizhumima=findViewById(R.id.jizhumima);
        zidongdenglu=findViewById(R.id.autodenglu);

        username_input=findViewById(R.id.user_name_input);
        password_input=findViewById(R.id.password_input);
        //进入主界面的意图
        intent=new Intent(DengluActivity.this,MainActivity.class);
        //保存记住密码和自动登录的操作
        dengluPreferences=getSharedPreferences("denglusetting", Context.MODE_PRIVATE);
        savePreferences=dengluPreferences.edit();
        //使CheckBox呈已勾选状态
        if (dengluPreferences.getBoolean("jizhumima",false)){
            jizhumima.setChecked(true);
        }else {
            jizhumima.setChecked(false);
        }
        if (dengluPreferences.getBoolean("zidongdenglu",false)){
            zidongdenglu.setChecked(true);
        }else{
            zidongdenglu.setChecked(false);
        }

        if (dengluPreferences.getBoolean("jizhumima",false)
                &&dengluPreferences.contains("username")
                &&dengluPreferences.contains("password")){

            username_input.setText(dengluPreferences.getString("username",null));
            password_input.setText(dengluPreferences.getString("password",null));

        }
        //如果之前登录勾选了自动登录和记住密码
        if (dengluPreferences.getBoolean("jizhumima",false)
                &&dengluPreferences.getBoolean("zidongdenglu",false)
                &&dengluPreferences.contains("username")
                &&dengluPreferences.contains("password")){
            username=username_input.getText().toString();
            password=password_input.getText().toString();
            iszhanghao();
            if (verifyPassword&&verifyZhanghao){
                intent.putExtra("user",userInformation);
                startActivity(intent);
            }else{
                Toast.makeText(this,"未知错误",Toast.LENGTH_SHORT).show();
            }

        }

        netSetting.setOnClickListener(View->{
           Intent intent=new Intent(DengluActivity.this,NetWorkSettingActivity.class);
           startActivity(intent);
        });
        zhaohuimima.setOnClickListener(View->{
           Intent intent=new Intent(DengluActivity.this,Zhaohuimima.class);
           startActivity(intent);
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //手动登录
        denglu.setOnClickListener(View->{
            if(username_input.length()==0||password_input.length()==0){
                Toast.makeText(DengluActivity.this,"请填写登录信息",Toast.LENGTH_SHORT).show();
            }else{
                username=String.valueOf(username_input.getText());
                password=String.valueOf(password_input.getText());
                //判断帐号密码是否存在
                iszhanghao();
                if (verifyZhanghao){
                    if (verifyPassword){
                        intent.putExtra("user",userInformation);
                        startActivity(intent);
                    }else {
                        Toast.makeText(DengluActivity.this,"密码不正确",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(DengluActivity.this,"帐号不存在",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //进入注册界面
        zhuce.setOnClickListener(View->{
            Intent intent=new Intent(DengluActivity.this, ZhuceActivity.class);
            startActivity(intent);
        });
    }

    private void iszhanghao(){
        List<UserInformation> list=new ArrayList<>();
        try {
            FileInputStream fileInputStream=openFileInput("zhanghaomima.bin");
            ObjectInputStream ois=new ObjectInputStream(fileInputStream);
            while (fileInputStream.available()>0){
                list.add((UserInformation)ois.readObject());
            }
            for (int i=0;i<list.size();i++){
                if(list.get(i).getUserName().equals(username)){
                    verifyZhanghao=true;
                }else {
                    verifyZhanghao=false;
                }

                if(list.get(i).getPassword().equals(password)){
                    verifyPassword=true;
                }else {
                    verifyPassword=false;
                }

                if (verifyZhanghao&&verifyPassword)
                    userInformation=list.get(i);
            }
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //如果记住密码
        if (jizhumima.isChecked()){
            //如果密码为真
            if(verifyZhanghao&&verifyPassword){
                savePreferences.putString("username",username);
                savePreferences.commit();
                savePreferences.putString("password",password);
                savePreferences.commit();
            }
            savePreferences.putBoolean("jizhumima",jizhumima.isChecked());
            savePreferences.commit();
        }else if (dengluPreferences.contains("jizhumima")){
            savePreferences.putBoolean("jizhumima",jizhumima.isChecked());
            savePreferences.commit();
        }

        if (zidongdenglu.isChecked()){
            savePreferences.putBoolean("zidongdenglu",zidongdenglu.isChecked());
            savePreferences.commit();
        }else if (dengluPreferences.contains("zidongdenglu")){
            savePreferences.putBoolean("zidongdenglu",zidongdenglu.isChecked());
            savePreferences.commit();
        }


    }
}
