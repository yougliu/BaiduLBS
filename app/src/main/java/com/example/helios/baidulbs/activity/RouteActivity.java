package com.example.helios.baidulbs.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.example.helios.baidulbs.R;
import com.example.helios.baidulbs.overlay.TransitRouteOverlay;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Bonus Liu on 1/12/16.
 * email : wumumawl@163.com
 */
public class RouteActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = RouteActivity.class.getSimpleName();
    private String mAddress;
    private ImageView mBack,mTaxi,mBus,mFoot;
    private Button mSearch,mNavi;
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

    //for baidu navi
    public static List<Activity> activityList = new LinkedList<>();
    private static final String APP_FOLDER_NAME = "BaiduLBS";
    public static final String ROUTE_PLAN_NODE = "routePlanNode";
    public static final String SHOW_CUSTOM_ITEM = "showCustomItem";
    public static final String RESET_END_NODE = "resetEndNode";
    public static final String VOID_MODE = "voidMode";
    private String mSDCardPath = null;
    private String authinfo = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityList.add(this);
        setContentView(R.layout.activity_route_search_layout);
        mAddress = getIntent().getStringExtra("address");
        mPlanSearch = RoutePlanSearch.newInstance();
        initView();
        reFreshType(TYPE_BUS);
        mNavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BaiduNaviManager.isNaviInited()){
                    routeplanToNavi(BNRoutePlanNode.CoordinateType.BD09LL);
                }
            }
        });

        if(initDirs()){
            initNavi();
        }
    }

    private void initNavi() {
//        BNOuterTTSPlayerCallback ttsCallback = new BNOuterTTSPlayerCallback() {
//            @Override
//            public int getTTSState() {
//                Log.d(TAG,"getTTSState = ");
//                return 1;
//            }
//
//            @Override
//            public int playTTSText(String s, int i) {
//                Log.d(TAG,"playTTSText = "+s+", "+i);
//                return 1;
//            }
//
//            @Override
//            public void phoneCalling() {
//                Log.d(TAG,"phoneCalling ");
//            }
//
//            @Override
//            public void phoneHangUp() {
//                Log.d(TAG,"phoneHangUp ");
//            }
//
//            @Override
//            public void initTTSPlayer() {
//                Log.d(TAG,"initTTSPlayer ");
//            }
//
//            @Override
//            public void releaseTTSPlayer() {
//                Log.d(TAG,"releaseTTSPlayer ");
//            }
//
//            @Override
//            public void stopTTS() {
//                Log.d(TAG,"stopTTS ");
//            }
//
//            @Override
//            public void resumeTTS() {
//                Log.d(TAG,"resumeTTS ");
//            }
//
//            @Override
//            public void pauseTTS() {
//                Log.d(TAG,"pauseTTS ");
//            }
//        };
        BaiduNaviManager.getInstance().init(RouteActivity.this, mSDCardPath, APP_FOLDER_NAME, new BaiduNaviManager.NaviInitListener() {
            @Override
            public void onAuthResult(int i, String msg) {
                if(i == 0){
                    authinfo = "key校验成功!";
                }else {
                    authinfo = "key校验失败, " + msg;
                }
                RouteActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(RouteActivity.this, authinfo, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void initStart() {
                Toast.makeText(RouteActivity.this, "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void initSuccess() {
                Toast.makeText(RouteActivity.this, "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void initFailed() {
                Toast.makeText(RouteActivity.this, "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
            }
        },null);

    }


    private void routeplanToNavi(BNRoutePlanNode.CoordinateType coType) {
        BNRoutePlanNode sNode = null;
        BNRoutePlanNode eNode = null;
        sNode = new BNRoutePlanNode(116.30784537597782, 40.057009624099436, "百度大厦", null, coType);
        eNode = new BNRoutePlanNode(116.40386525193937, 39.915160800132085, "北京天安门", null, coType);
        if(sNode != null && eNode != null){
            List<BNRoutePlanNode> nodes = new ArrayList<>();
            nodes.add(sNode);
            nodes.add(eNode);
            BaiduNaviManager.getInstance().launchNavigator(RouteActivity.this,nodes,1,true,new MyRoutePlanListener(sNode));
        }

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
        mNavi = (Button) findViewById(R.id.navi);
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

    private boolean initDirs() {
        mSDCardPath = getSdcardDir();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }


    public class MyRoutePlanListener implements BaiduNaviManager.RoutePlanListener{

        private BNRoutePlanNode mBNRoutePlanNode = null;

        public MyRoutePlanListener(BNRoutePlanNode mBNRoutePlanNode) {
            this.mBNRoutePlanNode = mBNRoutePlanNode;
        }

        @Override
        public void onJumpToNavigator() {
            /*
			 * 设置途径点以及resetEndNode会回调该接口
			 */
            for(Activity ac : activityList){
                if(ac.getClass().getName().endsWith("RouteGuideActivity")){
                    return;
                }
            }

            Intent intent = new Intent(RouteActivity.this,RouteGuideActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(ROUTE_PLAN_NODE,mBNRoutePlanNode);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        @Override
        public void onRoutePlanFailed() {
            Toast.makeText(RouteActivity.this, "route plan 失败", Toast.LENGTH_SHORT).show();
        }
    }
}
