package com.example.helios.baidulbs.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.example.helios.baidulbs.R;
import com.example.helios.baidulbs.overlay.TransitRouteOverlay;

import java.util.List;

/**
 * Created by Bonus Liu on 1/12/16.
 * email : wumumawl@163.com
 */
public class RouteActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = RouteActivity.class.getSimpleName();
    private String mAddress;
    private ImageView mBack,mTaxi,mBus,mFoot;
    private Button mSearch;
    private TextView mFromAdd,mToAdd;
    private int mType = 2;
    private static final int TYPE_TAXI = 1;
    private static final int TYPE_BUS = 2;
    private static final int TYPE_FOOT = 3;

    //for baidu
    private RoutePlanSearch mPlanSearch;
    private OnGetRoutePlanResultListener mPlanResultListener;
    private MapView mMapView;
    private BaiduMap mBaiduMap;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_search_layout);
        mAddress = getIntent().getStringExtra("address");
        mPlanSearch = RoutePlanSearch.newInstance();
        initView();
        reFreshType(TYPE_BUS);
    }

    private void reFreshType(int mType) {
        switch (mType){
            case 1:
                mTaxi.setFocusable(true);
                break;
            case 2:
                mBus.setFocusable(true);
                mTaxi.setFocusable(false);
                mFoot.setFocusable(false);
                break;
            case 3:
                break;
        }
    }

    private void initView() {
        mMapView = (MapView) findViewById(R.id.mapView);
        mBaiduMap = mMapView.getMap();
        mBack = (ImageView) findViewById(R.id.back);
        mTaxi  = (ImageView) findViewById(R.id.taxi);
        mBus = (ImageView) findViewById(R.id.bus);
        mFoot = (ImageView) findViewById(R.id.foot);
        mSearch = (Button) findViewById(R.id.search);
        mFromAdd = (TextView) findViewById(R.id.from_address);
        mToAdd = (TextView) findViewById(R.id.to_address);
        mBack.setOnClickListener(this);
        mTaxi.setOnClickListener(this);
        mBus.setOnClickListener(this);
        mFoot.setOnClickListener(this);
        mSearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search:
                if(mType == 2){
                    //bus
                    initRoutePlanListerner();
                }
                break;
        }
    }

    private void initRoutePlanListerner() {
        mPlanResultListener = new OnGetRoutePlanResultListener() {
            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

            }

            @Override
            public void onGetTransitRouteResult(TransitRouteResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(RouteActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                }
                if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                    //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                    //result.getSuggestAddrInfo()
                    return;
                }
                if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                    if(result != null){
                        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                        mBaiduMap.setMyLocationEnabled(true);
                        TransitRouteOverlay overlay = new TransitRouteOverlay(mBaiduMap);
                        mBaiduMap.setOnMarkerClickListener(overlay);
                        overlay.setData(result.getRouteLines().get(0));
                        overlay.addToMap();
                        overlay.zoomToSpan();
                    }
                    TransitRouteLine results = result.getRouteLines().get(0);
                    List<TransitRouteLine.TransitStep> steps = results.getAllStep();
                    Log.d("bonus","steps = "+steps.size());
                    for (TransitRouteLine.TransitStep step : steps){
                        Log.d("bonus","routenode = "+step.getEntrance().getTitle()+", "+step.getExit().getTitle());
                    }
                }
            }

            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {

            }
        };
        mPlanSearch.setOnGetRoutePlanResultListener(mPlanResultListener);
        PlanNode fromNode = PlanNode.withCityNameAndPlaceName("上海", "桂林路");
        PlanNode toNode = PlanNode.withCityNameAndPlaceName("上海", "长江南路");
        mPlanSearch.transitSearch(new TransitRoutePlanOption().from(fromNode).city("上海").to(toNode));
    }
}
