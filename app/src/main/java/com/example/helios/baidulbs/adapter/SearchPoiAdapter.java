package com.example.helios.baidulbs.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.example.helios.baidulbs.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bonus Liu on 1/8/16.
 * email : wumumawl@163.com
 */
public class SearchPoiAdapter extends BaseAdapter{

    private Context mContext;
    private LayoutInflater mInflater;
    private List<PoiInfo> mPoiInfos = new ArrayList<>();

    public SearchPoiAdapter(Context mContext, List<PoiInfo> mPoiInfos) {
        this.mContext = mContext;
        this.mPoiInfos = mPoiInfos;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mPoiInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return mPoiInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        PoiInfo info = mPoiInfos.get(position);
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.adapter_search_poi_layout,null);
            holder = new ViewHolder();
            holder.poiName = (TextView) convertView.findViewById(R.id.poi_name);
            holder.poiAddress = (TextView) convertView.findViewById(R.id.poi_address);
            holder.goHere = (TextView) convertView.findViewById(R.id.go_here);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        if(info != null){
            holder.poiName.setText(info.name);
            holder.poiAddress.setText(info.address);
            holder.goHere.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: 1/8/16 navi 
                }
            });
        }
        return convertView;
    }

    public class ViewHolder{
        TextView poiName;
        TextView poiAddress;
        TextView goHere;
    }
}
