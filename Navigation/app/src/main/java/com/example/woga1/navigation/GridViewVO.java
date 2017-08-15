package com.example.woga1.navigation;

/**
 * Created by Jangwon on 2017-08-16.
 */

public class GridViewVO {
    //Menu화면의 그리드뷰 이미지 텍스트 넣을 때 사용했던 activity
    private String name;
    private int img;

    public GridViewVO(String name, int img) {
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
