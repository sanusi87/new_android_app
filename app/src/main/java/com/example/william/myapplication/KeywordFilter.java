package com.example.william.myapplication;

import java.io.Serializable;

public class KeywordFilter implements Serializable {
    public int id;
    public String name;

    public KeywordFilter(int id, String name){
        this.id = id;
        this.name = name;
    }
}
