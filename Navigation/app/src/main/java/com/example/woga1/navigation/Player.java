package com.example.woga1.navigation;

/**
 * Created by Jangwon on 2017-08-05.
 */

public class Player {
    //Menu화면의 그리드뷰 이미지 텍스트 넣을 때 사용했던 activity인데.... 일단 그때 코드 돌리는게 중요해서 인강이랑 똑같이 activity명하느라 이따구로 했다.
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
