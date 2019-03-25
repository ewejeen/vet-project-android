package app.yoojin.org.vet_project2;

import com.google.gson.annotations.SerializedName;

public class VetVO {
    // hospital_geo2 테이블 사용
    @SerializedName("hptId")
    private int hpt_id;
    @SerializedName("adrsOld")
    private String adrs_old;
    @SerializedName("adrsNew")
    private String adrs_new;
    @SerializedName("address")
    private String address; // 신주소 없으면 구주소로 가져오기 위함
    @SerializedName("province")
    private String province;
    @SerializedName("city")
    private String city;
    @SerializedName("xAxis")
    private String x_axis;
    @SerializedName("yAxis")
    private String y_axis;

    @SerializedName("hptName")
    private String hpt_name;
    @SerializedName("hptPhone")
    private String hpt_phone;
    @SerializedName("hptOpen")
    private String hpt_open;
    @SerializedName("hptHit")
    private int hpt_hit;

    @SerializedName("rateAvg")
    private String rateAvg;
    @SerializedName("reviewCnt")
    private String reviewCnt;

    public String getAddress() {
        return address;
    }

    public int getHpt_id() {
        return hpt_id;
    }

    public void setHpt_id(int hpt_id) {
        this.hpt_id = hpt_id;
    }

    public String getAdrs_old() {
        return adrs_old;
    }

    public void setAdrs_old(String adrs_old) {
        this.adrs_old = adrs_old;
    }

    public String getAdrs_new() {
        return adrs_new;
    }

    public void setAdrs_new(String adrs_new) {
        this.adrs_new = adrs_new;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getX_axis() {
        return x_axis;
    }

    public void setX_axis(String x_axis) {
        this.x_axis = x_axis;
    }

    public String getY_axis() {
        return y_axis;
    }

    public void setY_axis(String y_axis) {
        this.y_axis = y_axis;
    }

    public String getHpt_name() {
        return hpt_name;
    }

    public void setHpt_name(String hpt_name) {
        this.hpt_name = hpt_name;
    }

    public String getHpt_phone() {
        return hpt_phone;
    }

    public void setHpt_phone(String hpt_phone) {
        this.hpt_phone = hpt_phone;
    }

    public String getHpt_open() {
        return hpt_open;
    }

    public void setHpt_open(String hpt_open) {
        this.hpt_open = hpt_open;
    }

    public int getHpt_hit() {
        return hpt_hit;
    }

    public void setHpt_hit(int hpt_hit) {
        this.hpt_hit = hpt_hit;
    }

    public String getRateAvg() {
        return rateAvg;
    }

    public void setRateAvg(String rateAvg) {
        this.rateAvg = rateAvg;
    }

    public String getReviewCnt() {
        return reviewCnt;
    }

    public void setReviewCnt(String reviewCnt) {
        this.reviewCnt = reviewCnt;
    }
}
