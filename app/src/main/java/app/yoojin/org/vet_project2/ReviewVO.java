package app.yoojin.org.vet_project2;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class ReviewVO {

    // review 테이블
    @SerializedName("rvId")
    private int rv_id;
    @SerializedName("hptId")
    private int hpt_id;
    @SerializedName("hptRate")
    private double hpt_rate;
    @SerializedName("rvTitle")
    private String rv_title;
    @SerializedName("rvContent")
    private String rv_content;
    @SerializedName("rvRegDate")
    private String rv_reg_date;
    @SerializedName("rvImage")
    private String rv_image;
    @SerializedName("petType")
    private String pet_type;
    @SerializedName("visitDate")
    private String visit_date;
    @SerializedName("visitIsNew")
    private int visit_is_new;

    // review_comment 테이블
    @SerializedName("cmtId")
    private int cmt_id;
    @SerializedName("cmtContent")
    private String cmt_content;
    @SerializedName("cmtRegDate")
    private Timestamp cmt_reg_date;

    // 관리자가 조회하기 위함
    @SerializedName("hptName")
    private String hpt_name;

    public ReviewVO () { }

    public ReviewVO(int hpt_id, double hpt_rate, String rv_title, String rv_content, String rv_image, String pet_type, String visit_date, int visit_is_new) {
        this.hpt_id = hpt_id;
        this.hpt_rate = hpt_rate;
        this.rv_title = rv_title;
        this.rv_content = rv_content;
        this.rv_reg_date = rv_reg_date;
        this.rv_image = rv_image;
        this.pet_type = pet_type;
        this.visit_date = visit_date;
        this.visit_is_new = visit_is_new;
        this.cmt_id = cmt_id;
        this.cmt_content = cmt_content;
        this.cmt_reg_date = cmt_reg_date;
    }


    public int getRv_id() {
        return rv_id;
    }

    public void setRv_id(int rv_id) {
        this.rv_id = rv_id;
    }

    public int getHpt_id() {
        return hpt_id;
    }

    public void setHpt_id(int hpt_id) {
        this.hpt_id = hpt_id;
    }

    public double getHpt_rate() {
        return hpt_rate;
    }

    public void setHpt_rate(double hpt_rate) {
        this.hpt_rate = hpt_rate;
    }

    public String getRv_title() {
        return rv_title;
    }

    public void setRv_title(String rv_title) {
        this.rv_title = rv_title;
    }

    public String getRv_content() {
        return rv_content;
    }

    public void setRv_content(String rv_content) {
        this.rv_content = rv_content;
    }

    public String getRv_reg_date() {
        return rv_reg_date;
    }

    public void setRv_reg_date(String rv_reg_date) {
        this.rv_reg_date = rv_reg_date;
    }

    public String getRv_image() {
        return rv_image;
    }

    public void setRv_image(String rv_image) {
        this.rv_image = rv_image;
    }

    public String getPet_type() {
        return pet_type;
    }

    public void setPet_type(String pet_type) {
        this.pet_type = pet_type;
    }

    public String getVisit_date() {
        return visit_date;
    }

    public void setVisit_date(String visit_date) {
        this.visit_date = visit_date;
    }

    public int getVisit_is_new() {
        return visit_is_new;
    }

    public void setVisit_is_new(int visit_is_new) {
        this.visit_is_new = visit_is_new;
    }

    public int getCmt_id() {
        return cmt_id;
    }

    public void setCmt_id(int cmt_id) {
        this.cmt_id = cmt_id;
    }

    public String getCmt_content() {
        return cmt_content;
    }

    public void setCmt_content(String cmt_content) {
        this.cmt_content = cmt_content;
    }

    public Timestamp getCmt_reg_date() {
        return cmt_reg_date;
    }

    public void setCmt_reg_date(Timestamp cmt_reg_date) {
        this.cmt_reg_date = cmt_reg_date;
    }

    public String getHpt_name() {
        return hpt_name;
    }

    public void setHpt_name(String hpt_name) {
        this.hpt_name = hpt_name;
    }
}
