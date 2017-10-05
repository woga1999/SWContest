package com.example.woga1.navigation.Search;

import android.app.ListFragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.woga1.navigation.SaveData.ListViewAdapter;
import com.example.woga1.navigation.SaveData.ListViewItem;
import com.example.woga1.navigation.R;

public class RestaurantFragment extends ListFragment {
    View view;
    ListViewAdapter restaurantAdapter ;

    public RestaurantFragment()
    {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(savedInstanceState == null) {
            view = inflater.inflate(R.layout.fragment_restaurant, container, false);
        }
        // Adapter 생성 및 Adapter 지정.
        restaurantAdapter = new ListViewAdapter() ; setListAdapter(restaurantAdapter) ;
        restaurantAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.restaurantpoi), "지어미유황오리쭈꾸미", "80m") ;
        restaurantAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.restaurantpoi), "큰맘할매순대국 세종대점", "106m") ;
        restaurantAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.restaurantpoi), "춘천영양족발", "117m") ;
        restaurantAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.restaurantpoi), "피자스쿨 세종대점", "280m") ;
        restaurantAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.restaurantpoi), "불타는신곱창 군자점", "296m") ;
        restaurantAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.restaurantpoi), "족발보쌈의달인THE족 군자점", "317m") ;
        restaurantAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.restaurantpoi), "본가할매안동찜닭 군자점", "380m") ;
        restaurantAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.restaurantpoi), "푸드스토리 군자점", "396m") ;
        restaurantAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.restaurantpoi), "보쌈집", "517m") ;
        restaurantAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.restaurantpoi), "무한정수제돈까스 군자점", "650m") ;
        restaurantAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.restaurantpoi), "솔로몬피자", "666m") ;
        restaurantAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.restaurantpoi), "스위트앤카츠", "712m") ;
        return super.onCreateView(inflater, container, savedInstanceState);

//        return view;
    }

    //리스트뷰 클릭 이벤트 처리
    @Override
    public void onListItemClick (ListView l, View v, int position, long id) {
        // get TextView's Text.
        ListViewItem item = (ListViewItem) l.getItemAtPosition(position) ;

        String titleStr = item.getTitle() ;
        String descStr = item.getDesc() ;
        Drawable iconDrawable = item.getIcon() ;

        // TODO : use item data.
    }

    //외부에서 아이템 추가를 위한 함수
    public void addItem(Drawable icon, String title, String desc) {
        restaurantAdapter.addItem(icon, title, desc) ;
    }


}
