package com.example.william.myapplication;

import java.util.ArrayList;

public class KeywordFilterArray {
    public static ArrayList<KeywordFilter> populate(){
        ArrayList<KeywordFilter> c = new ArrayList<>();

        c.add(new KeywordFilter(1,"Position Title"));
        c.add(new KeywordFilter(2,"Company Name"));
        c.add(new KeywordFilter(3,"Skills"));
        c.add(new KeywordFilter(4,"Job Description"));

        return c;
    }
}
