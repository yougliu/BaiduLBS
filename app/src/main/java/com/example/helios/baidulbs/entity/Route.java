package com.example.helios.baidulbs.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Bonus Liu on 1/7/16.
 * email : wumumawl@163.com
 */
@DatabaseTable(tableName = "route_history")
public class Route {
    @DatabaseField(generatedId = true)
    private int _id;
    @DatabaseField(columnName = "startLat")
    private double startLat;
    @DatabaseField(columnName = "startLng")
    private double startLng;
    @DatabaseField(columnName = "endLat")
    private double endLat;
    @DatabaseField(columnName = "endLng")
    private double endLng;
    @DatabaseField(columnName = "startAddr")
    private String startAddr;
    @DatabaseField(columnName = "endAddr")
    private String endAddr;
    @DatabaseField(columnName = "distance")
    private long distance;
    @DatabaseField(columnName = "type")
    private int type;
    @DatabaseField(columnName = "updateTime")
    private long updateTime;

    public Route() {
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public double getStartLat() {
        return startLat;
    }

    public void setStartLat(double startLat) {
        this.startLat = startLat;
    }

    public double getStartLng() {
        return startLng;
    }

    public void setStartLng(double startLng) {
        this.startLng = startLng;
    }

    public double getEndLat() {
        return endLat;
    }

    public void setEndLat(double endLat) {
        this.endLat = endLat;
    }

    public double getEndLng() {
        return endLng;
    }

    public void setEndLng(double endLng) {
        this.endLng = endLng;
    }

    public String getStartAddr() {
        return startAddr;
    }

    public void setStartAddr(String startAddr) {
        this.startAddr = startAddr;
    }

    public String getEndAddr() {
        return endAddr;
    }

    public void setEndAddr(String endAddr) {
        this.endAddr = endAddr;
    }

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
}
