package com.example.woga1.navigation;

/**
 * Created by Jangwon on 2017-08-05.
 */

public class Player {
    private String name;
    private int img;

    public Player(String name, int img) {
        this.img=img;
        this.name=name;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;

    }
    public int getImg(){
        return img;
    }
    public void setImg(int img){
        this.img=img;
    }
}
