package com.example.william.myapplication;

public class Subscription {

    public String subscription_name;
    public int subscription_id;
    public int status;

    public Subscription(String name, int id, int status){
        this.subscription_name = name;
        this.subscription_id = id;
        this.status = status;
    }

    public Subscription(){}
}
