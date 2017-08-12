package com.example.woga1.navigation;

import java.util.ArrayList;

/**
 * Created by Jangwon on 2017-08-11.
 */

public class DestinationList {
    //싱글톤 Activity
    private static DestinationList destinationListObject;

    private ArrayList<String> destination = new ArrayList<String>();
    private ArrayList<String> destinationDetail = new ArrayList<String>();

    public static synchronized DestinationList getInstance(){
        if(destinationListObject ==null){
            destinationListObject = new DestinationList();
        }
        return destinationListObject;
    }

    public void addDestination(String name){
        for(int i=0; i<10; i++)
        {
            if(destination.contains(name))
            {
                destination.remove(i);
                destinationDetail.remove(i);
            }
        }
        destination.add(0,name);
    }

    public void addDestinationDetail(String name){
        destinationDetail.add(0,name);
    }

    public String  getDestination(String name) {
        for (int i=0; i < 10; i++) {
            if (destination.contains(name)) {
                return destinationDetail.get(i);
            }
        }

        return null;

    }


}
