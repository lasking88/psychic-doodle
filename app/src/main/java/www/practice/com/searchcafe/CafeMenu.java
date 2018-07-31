package www.practice.com.searchcafe;

public class CafeMenu {
    private String mImgUrl;
    private String mName;
    private int mPrice;
    private String mInformation;
    private int mAmount;

    public CafeMenu(String imgUrl, String name, int price, String information) {
        mImgUrl = imgUrl;
        mName = name;
        mPrice = price;
        mInformation = information;
        mAmount = 0;
    }

    public String getImgUrl() {
        return mImgUrl;
    }

    public void setImgUrl(String imgUrl) {
        mImgUrl = imgUrl;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getPrice() {
        return mPrice;
    }

    public void setPrice(int price) {
        mPrice = price;
    }

    public String getInformation() {
        return mInformation;
    }

    public void setInformation(String information) {
        mInformation = information;
    }

    public int getAmount() {
        return mAmount;
    }

    public void setAmount(int amount) {
        mAmount = amount;
    }
}
