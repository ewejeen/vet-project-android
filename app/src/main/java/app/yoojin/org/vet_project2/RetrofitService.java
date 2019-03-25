package app.yoojin.org.vet_project2;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface RetrofitService {

    @GET("/vetproject_v2/vetJsonShort.do")
    Call<List<VetVO>> getVetList();

//////////////////////////////////////////////////////////////

    @GET("/vetproject_v2/vetSearchByName.do")
    Call<List<VetVO>> vetGetByName(
            @Query("searchKeyword") String searchKeyword
    );

    @GET("/vetproject_v2/vetSearchByRegion.do")
    Call<List<VetVO>> vetGetByRegion(
            @QueryMap HashMap<String, String> region
    );

    @POST("/verproject_v2/vetHitUp.do")
    @FormUrlEncoded
    Call<VetVO> vetHitUp(
            @Field("hpt_id") int hpt_id
    );

//////////////////////////////////////////////////////////////

    @GET("/vetproject_v2/review/reviewListThree.do")
    Call<List<ReviewVO>> getReviewListThree(
            @Query("hpt_id") int hpt_id
    );

    @GET("/vetproject_v2/review/reviewList.do")
    Call<List<ReviewVO>> getReviewList(
            @Query("hpt_id") int hpt_id
    );

    @GET("/vetproject_v2/review/reviewDetail.do")
    Call<List<ReviewVO>> getReviewDetail(
            @Query("rv_id") int rv_id
    );

    @POST("/vetproject_v2/review/addAppReview.do")
    @FormUrlEncoded
    Call<String> insertReview(
            @Field("hpt_id") int hpt_id,
            @Field("hpt_rate") double hpt_rate,
            @Field("visit_date") String visit_date,
            @Field("pet_type") String pet_type,
            @Field("visit_is_new") int visit_is_new,
            @Field("rv_title") String rv_title,
            @Field("rv_content") String rv_content,
            @Field("rv_image") String rv_image
    );

    @POST("/vetproject_v2/review/deleteReview.do")
    @FormUrlEncoded
    Call<String> deleteReivew(
            @Field("rv_id") int rv_id
    );

    @POST("/vetproject_v2/review/updateReview.do")
    @FormUrlEncoded
    Call<String> updateReview(
            @Field("hpt_rate") double hpt_rate,
            @Field("visit_date") String visit_date,
            @Field("pet_type") String pet_type,
            @Field("visit_is_new") int visit_is_new,
            @Field("rv_title") String rv_title,
            @Field("rv_content") String rv_content,
            @Field("rv_image") String rv_image,
            @Field("rv_id") int rv_id
    );

    @GET("/vetproject_v2/review/commentList.do")
    Call<List<ReviewVO>> commentList(
            @Query("rv_id") int rv_id
    );

    @POST("/vetproject_v2/review/insertComment.do")
    @FormUrlEncoded
    Call<String> insertComment(
            @Field("cmt_content") String cmt_content,
            @Field("rv_id") int rv_id
    );

    @POST("/vetproject_v2/review/deleteComment.do")
    @FormUrlEncoded
    Call<String> deleteComment(
            @Field("cmt_id") int cmt_id
    );

//////////////////////////////////////////////////////////////

    @GET("/vetproject_v2/noticeListApp.do")
    Call<List<NoticeVO>> getNoticeList();
}
