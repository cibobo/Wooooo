package com.cibobo.wooooo.model;

/**
 * Created by Cibobo on 9/28/14.
 */
public class UserData {
    private String userName;
    private String passWord;

    public UserData(){
        this.userName = "u";
        this.passWord = "p";
    }

    public UserData(String username){
        this.userName = username;
        this.passWord = "";
    }

    public UserData(String name, String pass){
        this.userName = name;
        this.passWord = pass;
    }

    public void setUserName(String userName){
        this.userName = userName;
    }

    public String getUserName(){
        return this.userName;
    }

    public String getPassWord() { return this.passWord; }
}
