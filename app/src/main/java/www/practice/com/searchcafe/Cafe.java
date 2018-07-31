package www.practice.com.searchcafe;

import android.graphics.Color;

public class Cafe {
    private String mCafeName;
    private String mAddress;
    private double mLatitude;
    private double mLongitude;
    private float mTotal;
    private float mCurrent;

    public Cafe(String cafeName, String address, double latitude, double longitude, float total, float current) {
        mCafeName = cafeName;
        mAddress = address;
        mLatitude = latitude;
        mLongitude = longitude;
        mTotal = total;
        mCurrent = current;
    }

    public String getCafeName() {
        return mCafeName;
    }

    public void setCafeName(String cafeName) {
        mCafeName = cafeName;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public float getTotal() {
        return mTotal;
    }

    public void setTotal(float total) {
        mTotal = total;
    }

    public float getCurrent() {
        return mCurrent;
    }

    public void setCurrent(float current) {
        mCurrent = current;
    }

    public float getColor() {
        float rate = 1f - mCurrent / mTotal;
        return 120f * rate;
    }
}
