package com.example.woga1.navigation.Search;

import android.app.ListFragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.woga1.navigation.R;
import com.example.woga1.navigation.SaveData.ListViewAdapter;
import com.example.woga1.navigation.SaveData.ListViewItem;

public class GasStationFragment extends ListFragment {
    View view;
    ListViewAdapter gasStationAdapter ;

    public GasStationFragment()
    {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(savedInstanceState == null) {
            view = inflater.inflate(R.layout.fragment_gas_station, container, false);
        }

         //Adapter 생성 및 Adapter 지정.
        gasStationAdapter = new ListViewAdapter() ; setListAdapter(gasStationAdapter) ;
        // 첫 번째 아이템 추가.
        gasStationAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.gasstationpoi), "오일뱅크 KLP제2셀프주유소", "380m") ;
        // 두 번째 아이템 추가.
        gasStationAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.gasstationpoi), "SK 장수주유소", "396m") ;
        // 세 번째 아이템 추가.
        gasStationAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.gasstationpoi), "SK 대영셀프주유소 (직영)", "517m") ;
        gasStationAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.gasstationpoi), "오일뱅크 능동셀프주유소", "737m") ;
        gasStationAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.gasstationpoi), "S-oil 태양석유주유소", "820m") ;
        // 두 번째 아이템 추가.
        gasStationAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.gasstationpoi), "SK 윈윈주유소 (직영)", "996m") ;
        // 세 번째 아이템 추가.
        gasStationAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.gasstationpoi), "오일뱅크 MS주유소", "1017m") ;
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
        gasStationAdapter.addItem(icon, title, desc) ;
    }


}
