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

public class MartFragment extends ListFragment {
    View view;
    ListViewAdapter martAdapter ;

    public MartFragment()
    {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(savedInstanceState == null) {
            view = inflater.inflate(R.layout.fragment_mart, container, false);
        }
        // Adapter 생성 및 Adapter 지정.
        martAdapter = new ListViewAdapter() ; setListAdapter(martAdapter) ;
        // 첫 번째 아이템 추가.
        martAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.martpoi), "GS25 세종대광개토관점", "90m") ;
        martAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.martpoi), "세븐일레븐 세종대기숙사점", "126m") ;
        martAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.martpoi), "GS25 세종대율곡관점", "157m") ;
        martAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.martpoi), "GS25 세종대우정당점", "220m") ;
        martAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.martpoi), "GS25 군자원룸점", "246m") ;
        martAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.martpoi), "GS25 광진군자로점", "337m") ;
        martAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.martpoi), "세븐일레븐 화양삼거리점", "550m") ;
        martAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.martpoi), "CU 세종대후문점", "706m") ;
        martAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.martpoi), "세븐일레븐 세종대역점", "837m") ;
        martAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.martpoi), "세븐일레븐 군자파라곤점", "950m") ;
        martAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.martpoi), "세븐일레븐 광진군자원룸점", "962m") ;
        martAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.martpoi), "GS25 화양중앙점", "1012m") ;
        return super.onCreateView(inflater, container, savedInstanceState);

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
        martAdapter.addItem(icon, title, desc) ;
    }


}
