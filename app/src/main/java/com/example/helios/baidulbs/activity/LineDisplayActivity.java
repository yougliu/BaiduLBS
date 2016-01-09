package com.example.helios.baidulbs.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.baidu.mapapi.search.busline.BusLineResult;
import com.baidu.mapapi.search.busline.BusLineSearch;
import com.baidu.mapapi.search.busline.BusLineSearchOption;
import com.baidu.mapapi.search.busline.OnGetBusLineSearchResultListener;
import com.baidu.mapapi.search.core.PoiInfo;
import com.example.helios.baidulbs.R;

/**
 * Created by Bonus Liu on 1/9/16.
 * email : wumumawl@163.com
 */
public class LineDisplayActivity extends AppCompatActivity implements OnClickListener,
        OnGetBusLineSearchResultListener{

    private static final String TAG = LineDisplayActivity.class.getSimpleName();
    private ListView mListView;
    private ImageView mBack;

    //for baidu
    private BusLineSearch mBuslineSearch;
    private PoiInfo mPoiInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_display_layout);
        mPoiInfo = getIntent().getParcelableExtra("poiInfo");
        initView();
        initBaiduMap();
    }

    private void initBaiduMap() {
        mBuslineSearch = BusLineSearch.newInstance();
        mBuslineSearch.setOnGetBusLineSearchResultListener(this);
        mBuslineSearch.searchBusLine(new BusLineSearchOption().city(mPoiInfo.city).uid(mPoiInfo.uid));
    }

    private void initView() {
        
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onGetBusLineResult(BusLineResult busLineResult) {
        if(busLineResult.error != BusLineResult.ERRORNO.NO_ERROR){
            return;
        }

        Log.d(TAG,busLineResult.getBusLineName()+", "+busLineResult.getStartTime()+", "+busLineResult.getEndTime());

    }
}
