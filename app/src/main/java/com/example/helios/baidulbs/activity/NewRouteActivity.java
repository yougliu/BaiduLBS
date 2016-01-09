package com.example.helios.baidulbs.activity;

import android.content.Intent;
import android.media.RemoteControlClient;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.search.busline.BusLineResult;
import com.baidu.mapapi.search.busline.BusLineSearch;
import com.baidu.mapapi.search.busline.OnGetBusLineSearchResultListener;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.search.share.OnGetShareUrlResultListener;
import com.baidu.mapapi.search.share.ShareUrlResult;
import com.baidu.mapapi.search.share.ShareUrlSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.example.helios.baidulbs.R;
import com.example.helios.baidulbs.adapter.SearchPoiAdapter;
import com.example.helios.baidulbs.widget.ScrollListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bonus Liu on 1/7/16.
 * email : wumumawl@163.com
 * POI检索功能：POI检索、公交信息查询、线路规划、地理编码、在线建议查询、短串分享
 * POI检索：周边检索、区域检索和城市内检索
 *
 */
public class NewRouteActivity extends AppCompatActivity implements View.OnClickListener,
        OnItemClickListener{

    private static final String TAG = NewRouteActivity.class.getSimpleName();
    private static final int MSG_REFRESH_VIEW = 0x01;
    private ImageView mBack ;
    private EditText mSearch;
    private ImageView mVoice;
    private TextView mEmpty;
    private ScrollListView mScrollListView;
    private TextView mFood;
    private TextView mHotel;
    private TextView mBus;
    private TextView mPark;
    private TextView mMovie;
    private TextView mBeauty;
    private TextView mShop;
    private TextView mTilet;
    private View mTopTips;
    private String mCity;
    private List<PoiInfo> mPoiInfos = new ArrayList<>();
    private SearchPoiAdapter mAdapter;
    private View mFooterView;

    //baidu map
    private PoiSearch mPoiSearch;
    private BusLineSearch mBusLineSearch;
    private GeoCoder mGeoCoder;
    private RoutePlanSearch mRoutePlanSearch;
    private ShareUrlSearch mShareUrlSearch;
    private SuggestionSearch mSuggestionSearch;
    private OnGetPoiSearchResultListener mPoiSearchResultListener;
    private OnGetBusLineSearchResultListener mBusLineSearchResultListener;
    private OnGetGeoCoderResultListener mGeoCodeResultListener;
    private OnGetRoutePlanResultListener mRoutePlanResultListener;
    private OnGetShareUrlResultListener mShareUrlResultListener;
    private OnGetSuggestionResultListener mSuggestionOnResultListener;
    private RemoteControlClient.OnGetPlaybackPositionListener mPlaybackPositionListener;


    //baidu end
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_REFRESH_VIEW:
                    mScrollListView.removeFooterView(mFooterView);
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_route_layout);
        mCity = getIntent().getStringExtra("city");
        Log.d(TAG,"mCity = "+mCity);
        initView();
        initInstatnce();
        initListener();
        mSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString() != null){
                    mTopTips.setVisibility(View.GONE);
                    mPoiSearch.searchInCity(new PoiCitySearchOption().city(mCity).keyword(s.toString()).pageNum(0));
                }
            }
        });
        mFooterView = LayoutInflater.from(NewRouteActivity.this).inflate(R.layout.adapter_clean_history_footer,null);
        mScrollListView.addFooterView(mFooterView);
        mAdapter = new SearchPoiAdapter(NewRouteActivity.this,mPoiInfos);
        mScrollListView.setAdapter(mAdapter);
        mScrollListView.setOnItemClickListener(this);
    }

    private void initInstatnce() {
        mPoiSearch = PoiSearch.newInstance();
        mBusLineSearch = BusLineSearch.newInstance();
        mRoutePlanSearch = RoutePlanSearch.newInstance();
        mShareUrlSearch = ShareUrlSearch.newInstance();
        mSuggestionSearch = SuggestionSearch.newInstance();
        mGeoCoder = GeoCoder.newInstance();
    }

    private void initListener() {
        //地址搜索结果
        mPoiSearchResultListener = new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                if(poiResult == null || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND){
                    Toast.makeText(NewRouteActivity.this, "未搜索到结果！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(poiResult != null && poiResult.error == SearchResult.ERRORNO.NO_ERROR){
                    mPoiInfos.clear();
                    for (PoiInfo info : poiResult.getAllPoi()){
                        Toast.makeText(NewRouteActivity.this, "找到"+poiResult.getAllPoi().size()+"条结果！", Toast.LENGTH_SHORT).show();
                        Log.d(TAG,"search result = "+info.address+", "+info.name+", "+info.phoneNum);
                        if(info.name != null){
                            mPoiInfos.add(info);
                        }
                    }
                    mHandler.sendEmptyMessage(MSG_REFRESH_VIEW);
                }
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
                Toast.makeText(NewRouteActivity.this, "搜索失败！", Toast.LENGTH_SHORT).show();
            }
        };
        mPoiSearch.setOnGetPoiSearchResultListener(mPoiSearchResultListener);
        //公交路线搜索结果
        mBusLineSearchResultListener = new OnGetBusLineSearchResultListener() {
            @Override
            public void onGetBusLineResult(BusLineResult busLineResult) {

            }
        };

        //反编译结果
        mGeoCodeResultListener = new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

            }
        };

        //计划路线结果
        mRoutePlanResultListener = new OnGetRoutePlanResultListener() {
            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

            }

            @Override
            public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

            }

            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {

            }
        };

        //分享结果
        mShareUrlResultListener = new OnGetShareUrlResultListener() {
            @Override
            public void onGetPoiDetailShareUrlResult(ShareUrlResult shareUrlResult) {

            }

            @Override
            public void onGetLocationShareUrlResult(ShareUrlResult shareUrlResult) {

            }
        };
        //建议路线搜索结果
        mSuggestionOnResultListener = new OnGetSuggestionResultListener() {
            @Override
            public void onGetSuggestionResult(SuggestionResult suggestionResult) {

            }
        };

    }

    private void initView() {
        mBack = (ImageView) findViewById(R.id.back);
        mSearch = (EditText) findViewById(R.id.search_content);
        mVoice = (ImageView) findViewById(R.id.search_voice);
        mEmpty = (TextView) findViewById(R.id.empty_route);
        mScrollListView = (ScrollListView) findViewById(R.id.search_route_history);
        mFood = (TextView) findViewById(R.id.food);
        mBus = (TextView) findViewById(R.id.bus);
        mBeauty = (TextView) findViewById(R.id.beauty);
        mTilet = (TextView) findViewById(R.id.tilet);
        mShop = (TextView) findViewById(R.id.shop);
        mPark = (TextView) findViewById(R.id.park);
        mHotel = (TextView) findViewById(R.id.hotel);
        mMovie = (TextView) findViewById(R.id.movie);
        mTopTips = findViewById(R.id.top_tips);
        mFood.setOnClickListener(this);
        mBus.setOnClickListener(this);
        mTilet.setOnClickListener(this);
        mBeauty.setOnClickListener(this);
        mShop.setOnClickListener(this);
        mPark.setOnClickListener(this);
        mHotel.setOnClickListener(this);
        mMovie.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mVoice.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                if(this != null){
                    this.finish();
                }
                break;
            case R.id.search_voice:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPoiSearch.destroy();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(NewRouteActivity.this, "position = "+position, Toast.LENGTH_SHORT).show();
        //跳转到该搜索地址的图层定位
        if(mPoiInfos == null || mPoiInfos.size() == 0){
            return;
        }
        PoiInfo poiInfo = mPoiInfos.get(position);
        if(poiInfo == null){
            return;
        }
        if(poiInfo.type == PoiInfo.POITYPE.SUBWAY_LINE || poiInfo.type == PoiInfo.POITYPE.BUS_LINE){
            //线路展示，不进行线路图层显示
            Intent lineIntent = new Intent(NewRouteActivity.this,LineDisplayActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("poiInfo",poiInfo);
            lineIntent.putExtras(bundle);
            startActivity(lineIntent);
        }else{
            //对应位置的图层界面
            Intent searchIntent = new Intent(NewRouteActivity.this,SearchLocationActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("poiInfo",poiInfo);
            searchIntent.putExtra("position",position+1);
            searchIntent.putExtras(bundle);
            startActivity(searchIntent);
        }
    }
}
