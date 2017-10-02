package com.example.woga1.navigation.SaveData;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.woga1.navigation.R;

import java.util.ArrayList;

/**
 * Created by Jangwon on 2017-07-31.
 */

public class ImageGridViewCustomAdapter extends BaseAdapter{
    //Main의 그리드뷰에 이미지랑 텍스트 넣는  Activity
    private Context context;
    public ArrayList<GridViewVO> gridViewVO;

    public ImageGridViewCustomAdapter(Context context, ArrayList<GridViewVO> gridViewVO){
        this.context = context;
        this.gridViewVO = gridViewVO;
    }
    @Override
    public int getCount() {
        return gridViewVO.size();
    }

    @Override
    public Object getItem(int position) {
        return gridViewVO.get(position);
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

//        ImageView image = (ImageView)item.findViewById(R.id.imageView_gridItem);
//        image.setImageResource(gridViewVO.get(position).getImg());
        TextView txt = (TextView)item.findViewById(R.id.textView_gridItem);
        txt.setText(gridViewVO.get(position).getName());
        return item;
    }
}
