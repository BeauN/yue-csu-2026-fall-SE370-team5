package com.example.splitdish;
public class Diner{
    private String name;
    
    public Diner(String name){
        this.name = name;

    }
    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String toString() {
        return name;
    }

}