package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class Zhaohuimima extends AppCompatActivity {

    private EditText phone_edit;
    private EditText zhanghao_edit;
    private Button zhaohuimima_button;

    private String phone_value;
    private String zhanghao_value;
    private String username_value;
    private String password_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhaohuimima);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setSupportActionBar((Toolbar)findViewById(R.id.zhaohuimima_layout));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        phone_edit=findViewById(R.id.phone_value);
        zhanghao_edit=findViewById(R.id.zhanghao_value);
        zhaohuimima_button=findViewById(R.id.zhaohuimima_button);

        zhaohuimima_button.setOnClickListener(View->{
           if(phone_edit.length()!=0&& TextUtils.isEmpty(zhanghao_edit.getText())==false){
               phone_value=phone_edit.getText().toString();
               zhanghao_value=zhanghao_edit.getText().toString();
               List<UserInformation> list=new ArrayList<>();

               try {
                   FileInputStream fileInputStream=openFileInput("zhanghaomima.bin");
                   ObjectInputStream objectInputStream=new ObjectInputStream(fileInputStream);
                   while (fileInputStream.available()>0) {
                       list.add((UserInformation) objectInputStream.readObject());
                   }
               } catch (ClassNotFoundException e){
                   e.printStackTrace();
               } catch (FileNotFoundException e) {
                   e.printStackTrace();
               } catch (IOException e){
                   e.printStackTrace();
               }

               for (int i=0;i<list.size();i++){
                   if (list.get(i).seekPassword(phone_value,zhanghao_value)){
                       username_value=list.get(i).getUserName();
                       password_value=list.get(i).getPassword();
                       createAlertDialog().show();
                       break;
                   }else{
                       Toast.makeText(Zhaohuimima.this,"请检查信息是否有误",Toast.LENGTH_SHORT).show();
                   }
               }

           }else {
               Toast.makeText(Zhaohuimima.this,"请填写相关信息",Toast.LENGTH_SHORT).show();
           }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent=new Intent(this,DengluActivity.class);
        switch (item.getItemId()){
            case android.R.id.home: intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);break;
            default:
                Toast.makeText(this,"你点了个寂寞",Toast.LENGTH_SHORT).show();break;
        }
        return super.onOptionsItemSelected(item);
    }

    private AlertDialog.Builder createAlertDialog(){
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(this)
                .setTitle("找回密码")
                .setMessage("\n帐号:"+username_value+"\n\n密码:"+password_value);
        alertDialog.setPositiveButton("确定",((dialog, which) -> {
           createAlertDialog().show().dismiss();
        }));
        return alertDialog;
    }

}
