package com.example.myapplication;

import java.io.Serializable;

public class UserInformation implements Serializable {
    private String userName;
    private String password;
    private String phone;

    UserInformation(){}
    UserInformation(String userName,String password,String phone){
        this.userName=userName;
        this.password=password;
        this.phone=phone;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public boolean seekPassword(String phone,String userName){
        return this.userName.equals(userName)&&this.phone.equals(phone);
    }
}
