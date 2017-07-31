package com.example.woga1.navigation;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Jangwon on 2017-07-31.
 */

public class ImageGridViewCustomAdapter extends BaseAdapter{

    private Context context;
    public ArrayList<Integer> imageArrayList;

    public ImageGridViewCustomAdapter(Context context, ArrayList<Integer> imageArrayList){
        this.context = context;
        this.imageArrayList = imageArrayList;
    }
    @Override
    public int getCount() {
        return imageArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return imageArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        if(item==null){
            LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();
            item = layoutInflater.inflate(R.layout.custom_grid_item,parent,false);
        }

        ImageView image = (ImageView)item.findViewById(R.id.imageView_gridItem);
        image.setImageResource(imageArrayList.get(position));
        return item;
    }
}
