package www.practice.com.searchcafe;

public class Cafe {
    private String mId;
    private String mImgUrl;
    private String mCafeName;
    private String mAddress;
    private double mLatitude;
    private double mLongitude;
    private int mTotal;
    private int mCurrent;

    public Cafe(String id, String imgUrl, String cafeName, String address, double latitude, double longitude, int total, int current) {
        mId = id;
        mImgUrl = imgUrl;
        mCafeName = cafeName;
        mAddress = address;
        mLatitude = latitude;
        mLongitude = longitude;
        mTotal = total;
        mCurrent = current;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getImgUrl() {
        return mImgUrl;
    }

    public void setImgUrl(String imgUrl) {
        mImgUrl = imgUrl;
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

    public int getTotal() {
        return mTotal;
    }

    public void setTotal(int total) {
        mTotal = total;
    }

    public int getCurrent() {
        return mCurrent;
    }

    public void setCurrent(int current) {
        mCurrent = current;
    }

    public float getColor() {
        float rate = 1f - mCurrent / (float)mTotal;
        return 120f * rate;
    }
}
