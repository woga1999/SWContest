package com.example.woga1.navigation.Fragment;

import android.support.v4.app.ListFragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.woga1.navigation.ListViewAdapter;
import com.example.woga1.navigation.ListViewItem;
import com.example.woga1.navigation.R;

public class MartFragment extends ListFragment {
    View view;
    ListViewAdapter adapter2 ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(savedInstanceState == null) {
            view = inflater.inflate(R.layout.fragment_mart, container, false);
        }
        // Adapter 생성 및 Adapter 지정.
        adapter2 = new ListViewAdapter() ; setListAdapter(adapter2) ;
        // 첫 번째 아이템 추가.
        adapter2.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.martpoi), "S-oil 태양석유주유소", "380m") ;
        // 두 번째 아이템 추가.
        adapter2.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.martpoi), "SK 윈윈주유소 (직영)", "396m") ;
        // 세 번째 아이템 추가.
        adapter2.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.martpoi), "오일뱅크 MS주유소", "517m") ;
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
        adapter2.addItem(icon, title, desc) ;
    }


}
