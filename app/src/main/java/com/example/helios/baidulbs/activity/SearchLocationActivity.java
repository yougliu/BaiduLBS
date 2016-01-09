package com.example.helios.baidulbs.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.busline.BusLineResult;
import com.baidu.mapapi.search.busline.BusLineSearch;
import com.baidu.mapapi.search.busline.BusLineSearchOption;
import com.baidu.mapapi.search.busline.OnGetBusLineSearchResultListener;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.example.helios.baidulbs.R;

/**
 * Created by Bonus Liu on 1/9/16.
 * email : wumumawl@163.com
 */
public class SearchLocationActivity extends AppCompatActivity implements View.OnClickListener,
        OnGetPoiSearchResultListener,OnGetBusLineSearchResultListener{

    private static final String TAG = SearchLocationActivity.class.getSimpleName();
    private PoiInfo mPoiInfo;
    private int mPosition;
    private TextView mName;
    private TextView mAddress;
    private TextView mTopSearch;
    private TextView mSearchNearby;
    private TextView mSearchRoute;
    private TextView mSearchNavi;
    private View mSearchDetail;
    private TextView mDetail;

    //baidu map
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private BitmapDescriptor mCurrentMarker;
    private MapStatusUpdate mStatusUpdate;
    private LatLng mLatLng;
    private MyLocationConfiguration.LocationMode mLocationMode = MyLocationConfiguration.LocationMode.NORMAL;
    //for poi detail
    private PoiSearch mPoiSearch;
    private BusLineSearch mBusLineSearch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location_layout);
        mPoiInfo = getIntent().getParcelableExtra("poiInfo");
        mPosition = getIntent().getIntExtra("position",1);
        if(mPoiInfo != null){
            mLatLng = mPoiInfo.location;
        }
        initView();
        initBaiduMap();
    }

    private void initBaiduMap() {
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);
        if (mPoiInfo.type == PoiInfo.POITYPE.BUS_LINE || mPoiInfo.type == PoiInfo.POITYPE.SUBWAY_LINE){
            //公交地铁路线
            mBusLineSearch = BusLineSearch.newInstance();
            mBusLineSearch.setOnGetBusLineSearchResultListener(this);
            mBusLineSearch.searchBusLine(new BusLineSearchOption().city(mPoiInfo.city).uid(mPoiInfo.uid));
        }else if(mPoiInfo.type == PoiInfo.POITYPE.BUS_STATION || mPoiInfo.type == PoiInfo.POITYPE.SUBWAY_STATION){
            //公交，地铁站
        }else{
            MyLocationData myLocationData = new MyLocationData.Builder()
                    .latitude(mLatLng.latitude)
                    .longitude(mLatLng.longitude)
                    .build();
            mBaiduMap.setMyLocationData(myLocationData);
            mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.b_poi_1_hl);
            MyLocationConfiguration configuration = new MyLocationConfiguration(mLocationMode,true,mCurrentMarker);
            mBaiduMap.setMyLocationConfigeration(configuration);
            //设置地图中心点以及缩放级别 zoom - 缩放级别 [3, 20]
            mStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(mLatLng,16);
            mBaiduMap.setMapStatus(mStatusUpdate);
            mAddress.setText(mPoiInfo.address);
            mTopSearch.setText(mPoiInfo.name);
            mPoiSearch = PoiSearch.newInstance();
            mPoiSearch.setOnGetPoiSearchResultListener(this);
            mPoiSearch.searchPoiDetail(new PoiDetailSearchOption().poiUid(mPoiInfo.uid));
        }
    }

    private void initView() {
        mMapView = (MapView) findViewById(R.id.mapView);
        mName = (TextView) findViewById(R.id.search_name);
        mAddress = (TextView) findViewById(R.id.search_address);
        mTopSearch = (TextView) findViewById(R.id.top_search);
        mSearchNavi = (TextView) findViewById(R.id.search_navi);
        mSearchRoute = (TextView) findViewById(R.id.search_route);
        mSearchNearby = (TextView) findViewById(R.id.search_nearby);
        mSearchDetail = findViewById(R.id.search_detail);
        mDetail = (TextView) findViewById(R.id.detail);
        mSearchDetail.setOnClickListener(this);
        mDetail.setOnClickListener(this);
        mSearchNavi.setOnClickListener(this);
        mSearchNearby.setOnClickListener(this);
        mSearchRoute.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.detail:
            case R.id.search_detail:
                //go to poi detail

                break;

        }
    }

    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        //to nothing
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
        //接受 poi detail
        if (poiDetailResult.error != SearchResult.ERRORNO.NO_ERROR){
            //详细检索失败
            Toast.makeText(SearchLocationActivity.this, "详细检索失败", Toast.LENGTH_SHORT).show();
        }else{
            //成功
            if(poiDetailResult != null){
                mName.setText(mPosition+":"+mPoiInfo.name+"("+poiDetailResult.getType()+")");
            }
        }
    }


    @Override
    public void onGetBusLineResult(BusLineResult busLineResult) {
        if(busLineResult.error != BusLineResult.ERRORNO.NO_ERROR || busLineResult == null){
            return;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPoiSearch.destroy();
    }
}
