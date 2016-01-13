package com.example.helios.baidulbs.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.baidu.navisdk.adapter.BNRouteGuideManager;
import com.baidu.navisdk.adapter.BNRoutePlanNode;

/**
 * Created by Bonus Liu on 1/13/16.
 * email : wumumawl@163.com
 */
public class RouteGuideActivity extends AppCompatActivity{

    private static final String TAG = RouteGuideActivity.class.getSimpleName();
    private BNRoutePlanNode mBNRoutePlanNode ;




    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RouteActivity.activityList.add(this);
        View view = BNRouteGuideManager.getInstance().onCreate(RouteGuideActivity.this, new BNRouteGuideManager.OnNavigationListener() {
            @Override
            public void onNaviGuideEnd() {
                if(RouteGuideActivity.this != null){
                    finish();
                }
            }

            @Override
            public void notifyOtherAction(int actionType, int i1, int i2, Object o) {
                Log.d(TAG,"actionType = "+actionType +", "+i1+", "+i2+", "+o.toString());
            }
        });
        if(view != null){
            setContentView(view);
        }
        if(getIntent() != null){
            Bundle bundle = getIntent().getExtras();
            mBNRoutePlanNode = (BNRoutePlanNode) bundle.getSerializable(RouteActivity.ROUTE_PLAN_NODE);
        }
    }

    @Override
    protected void onResume() {
        BNRouteGuideManager.getInstance().onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BNRouteGuideManager.getInstance().onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        BNRouteGuideManager.getInstance().onStop();
    }

    @Override
    protected void onDestroy() {
        BNRouteGuideManager.getInstance().onDestroy();
        RouteActivity.activityList.remove(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        BNRouteGuideManager.getInstance().onBackPressed(false);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        BNRouteGuideManager.getInstance().onConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig);
    }
}
