package com.example.helios.baidulbs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.helios.baidulbs.activity.NewRouteActivity;

public class MainActivity extends AppCompatActivity implements OnClickListener{


    private static final String TAG = MainActivity.class.getSimpleName();
    private ProgressBar mProgressBar;
    private TextView mSearch;
    private TextView mNearby;
    private TextView mRoute;
    private TextView mNav;
    private TextView more;
    //for baidu
    private MapView mBMapView;
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;//定位核心类
    private BitmapDescriptorFactory mCurrentMarker;
    private String mCity;
    private MapStatusUpdate mStatusUpdate;
    private LatLng mLatLng;
    private MyLocationConfiguration.LocationMode mLocationMode = MyLocationConfiguration.LocationMode.NORMAL;
    private BDLocationListener mLocationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            if(mBMapView == null || location == null){
                return;
            }
            // 构造定位数据
            MyLocationData locData = new MyLocationData.Builder()
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .accuracy(location.getRadius())
                    .direction(location.getDerect()).build();
            mCity = location.getCity();
            mLatLng = new LatLng(location.getLatitude(),location.getLongitude());
            // 设置定位数据
            mBaiduMap.setMyLocationData(locData);
            // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
//            mCurrentMarker = BitmapDescriptorFactory
//                    .fromResource(R.drawable.icon_geo);
            MyLocationConfiguration config = new MyLocationConfiguration(mLocationMode, true, null);
            mBaiduMap.setMyLocationConfigeration(config);
            //设置地图中心点以及缩放级别 zoom - 缩放级别 [3, 20]
            mStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(mLatLng,16);
            if(mStatusUpdate == null){
                return;
            }
            mProgressBar.setVisibility(View.GONE);
            mBaiduMap.setMapStatus(mStatusUpdate);


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBMapView = (MapView) findViewById(R.id.mapView);
        initView();
        initBaiduMap();
        initLocation();
        //开始定位
        mLocationClient.start();
        mLocationClient.requestLocation();
    }

    /**
     * 初始化其他控件
     */
    private void initView() {
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mSearch = (TextView) findViewById(R.id.top_search);
        mNearby = (TextView) findViewById(R.id.tips_nearby);
        mRoute = (TextView) findViewById(R.id.tips_route);
        mNav = (TextView) findViewById(R.id.tips_navi);
        more = (TextView) findViewById(R.id.tips_more);
        mSearch.setOnClickListener(this);
        mNearby.setOnClickListener(this);
        mNav.setOnClickListener(this);
        mRoute.setOnClickListener(this);
        more.setOnClickListener(this);
    }

    /**
     * 初始化百度地图相关
     */
    private void initBaiduMap() {
        mBaiduMap = mBMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //开启图层定位
        mBaiduMap.setMyLocationEnabled(true);
        mLocationClient = new LocationClient(getApplicationContext());
        //注册监听
        mLocationClient.registerLocationListener(mLocationListener);
    }

    /**
     * 配置定位信息
     */
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=0;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBMapView.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.top_search:
                if(mCity == null){
                    mLocationClient.requestLocation();
                }
                Intent intent = new Intent(MainActivity.this, NewRouteActivity.class);
                intent.putExtra("city",mCity);
                startActivity(intent);
                break;
            case R.id.tips_nearby:
                break;
            case R.id.tips_route:
                break;
            case R.id.tips_navi:
                break;
            case R.id.tips_more:
                break;
        }
    }
}
