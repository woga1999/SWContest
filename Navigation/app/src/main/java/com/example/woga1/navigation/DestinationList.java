package com.example.woga1.navigation;

import java.util.List;

/**
 * Created by Jangwon on 2017-08-11.
 */

public class DestinationList {

    private static DestinationList destinationListObject;

    private List<String> destination;
    private List<String> destinationDetail;

    public static synchronized DestinationList getInstance(){
        if(destinationListObject ==null){
            destinationListObject = new DestinationList();
        }
        return destinationListObject;
    }

    public void addDestination(String name){
        destination.add(name);
    }

    public void addDestinationDetail(String name){
        destinationDetail.add(name);
    }


}
