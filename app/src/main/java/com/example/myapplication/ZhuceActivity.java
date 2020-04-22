package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZhuceActivity extends AppCompatActivity {
    //View控件
    private TextView netSetting;
    private Button qu_xioa;
    private Button zhu_ce;
    private EditText phone_input;
    private EditText username_input;
    private EditText password_input;
    private EditText password_reinput;
    //输入的注册信息
    private CharSequence phone;
    private CharSequence user_name;
    private CharSequence password;
    //输入格式规范
    private Pattern string_format=Pattern.compile("[^\\w]");
    private Pattern password_constraint=Pattern.compile("[\\d]");
    private Pattern password_constraint2=Pattern.compile("[\\_]");
    private Pattern password_constraint3=Pattern.compile("[a-z[A-Z]]");
    //各框填完情况
    private boolean phone_statu;
    private boolean username_statu;
    private boolean password_statu;
    private boolean repassword_statu;
    //用于验证帐号是否存在
    private boolean verifyZhanhao=false;
    private boolean verifyPhonehao=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhuce);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        netSetting=findViewById(R.id.net_setting);

        phone_input=findViewById(R.id.phone_input);
        username_input=findViewById(R.id.new_user_name_input);
        password_input=findViewById(R.id.new_progress_input);
        password_reinput=findViewById(R.id.repetition_progress_input);

        zhu_ce=findViewById(R.id.que_ren_zhu_ce);

        qu_xioa=findViewById(R.id.qu_xiao_zhu_ce);
        qu_xioa.setOnClickListener(View->{
            finish();
        });

        netSetting.setOnClickListener(View->{
            Intent intent=new Intent(ZhuceActivity.this,NetWorkSettingActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        phone_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                phone=s;
            }

            @Override
            public void afterTextChanged(Editable s) {

                if(phone.length()<11||phone.length()>11){
                    if (phone_input.getText()!=null){
                        phone_input.setError(getString(R.string.phone_error));
                    }
                }else{
                    phone_statu=true;
                }
            }
        });
        username_input.addTextChangedListener(new TextWatcher() {
            private int username_long;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                username_long=s.toString().getBytes().length;
                user_name=s;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (string_format.matcher(user_name).find()||(username_long<4||username_long>30)){
                    if (username_input.getText()!=null){
                        username_input.setError(getString(R.string.username_format));
                    }
                }else {
                    username_statu=true;
                }
            }
        });
        password_input.addTextChangedListener(new TextWatcher() {
            private int password_long;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password_long=s.toString().getBytes().length;
                password=s;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (string_format.matcher(password).find()||passwordconstraint(password)||(password_long<6)){
                    if(password_input.getText()!=null){
                        password_input.setError(getString(R.string.password_format));
                    }
                }else {
                    password_statu=true;
                }
            }
        });
        password_reinput.addTextChangedListener(new TextWatcher() {
            private CharSequence repassword;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                repassword=s;
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (repassword.toString().matches(password.toString())==false){
                    if (password_reinput.getText()!=null){
                        password_reinput.setError(getString(R.string.password_re));
                    }
                }else{
                    repassword_statu=true;
                }
            }
        });

        zhu_ce.setOnClickListener(View->{
            //判断注册信息是否完整
            if(isZhuce()){
                iszhanghao();
                //判断帐号是否已存在
                if(verifyZhanhao){
                        Toast.makeText(ZhuceActivity.this,"该帐号已存在",Toast.LENGTH_SHORT).show();
                }else if (verifyPhonehao){
                    Toast.makeText(ZhuceActivity.this,"该手机号已注册",Toast.LENGTH_SHORT).show();
                }else {

                    try {
                        FileOutputStream zhanghaoou = openFileOutput("zhanghaomima.bin", Context.MODE_APPEND);
                        ObjectOutputStream oos=new ObjectOutputStream(zhanghaoou);
                        oos.writeObject(new UserInformation(user_name.toString(),password.toString(),phone.toString()));

                        Toast.makeText(ZhuceActivity.this,"注册成功",Toast.LENGTH_SHORT).show();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }else {
                Toast.makeText(ZhuceActivity.this,"请填写注册信息",Toast.LENGTH_SHORT).show();
            }
        });
    }
    //判断密码是否达到一定强度
    private boolean passwordconstraint(CharSequence charSequence){
        boolean b=false;

        if(password_constraint.matcher(charSequence).find()&&password_constraint2.matcher(charSequence).find()
                ==false&&password_constraint3.matcher(charSequence).find()==false){
            b=true;
        }
        if(password_constraint3.matcher(charSequence).find()&&password_constraint.matcher(charSequence).find()
                ==false&&password_constraint2.matcher(charSequence).find()==false){
            b=true;
        }
        if(password_constraint2.matcher(charSequence).find()&&password_constraint3.matcher(charSequence).find()
                ==false&&password_constraint.matcher(charSequence).find()==false){
            b=true;
        }

        return b;
    }
    //判断是否已正确填完注册信息
    private boolean isZhuce(){
        return password_statu?phone_statu?repassword_statu?username_statu?true:false:false:false:false;
    }
    //判断帐号是否已存在
    private void iszhanghao(){
        List<UserInformation> list=new ArrayList<>();
        try {
            FileInputStream zhanghaoin=openFileInput("zhanghaomima.bin");
            ObjectInputStream ois=new ObjectInputStream(zhanghaoin);
            while (zhanghaoin.available()>0){
                list.add((UserInformation) ois.readObject());
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        for (int i=0;i<list.size();i++){

            if (list.get(i).getUserName().equals(user_name.toString())){
                verifyZhanhao=true;
                break;
            }else{
                verifyZhanhao=false;
            }

            if (list.get(i).getPhone().equals(phone.toString())){
                verifyPhonehao=true;
                break;
            }else {
                verifyPhonehao=false;
            }

        }
    }
}
