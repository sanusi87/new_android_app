package com.example.william.myapplication;

import java.util.ArrayList;

public class StateArray {
    public static State[] populate(){
        ArrayList<State> c = new ArrayList<>();

        c.add(new State(4,"Johor"));
        c.add(new State(10,"Kedah"));
        c.add(new State(22,"Kelantan"));
        c.add(new State(28,"Melaka"));
        c.add(new State(34,"Negeri Sembilan"));
        c.add(new State(39,"Penang"));
        c.add(new State(44,"Pahang"));
        c.add(new State(48,"Perak"));
        c.add(new State(52,"Perlis"));
        c.add(new State(56,"Sabah"));
        c.add(new State(60,"Selangor"));
        c.add(new State(64,"Sarawak"));
        c.add(new State(68,"Terengganu"));
        c.add(new State(103,"Kuala Lumpur"));
        c.add(new State(104,"Labuan"));
        c.add(new State(365,"Putrajaya"));

        return (State[]) c.toArray();
    }
}
