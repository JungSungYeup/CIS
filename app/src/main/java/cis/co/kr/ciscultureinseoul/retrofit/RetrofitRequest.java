package cis.co.kr.ciscultureinseoul.retrofit;


import java.util.ArrayList;

import cis.co.kr.ciscultureinseoul.data.Board;
import cis.co.kr.ciscultureinseoul.data.BoardImage;
import cis.co.kr.ciscultureinseoul.data.Comments;
import cis.co.kr.ciscultureinseoul.data.Favorite;
import cis.co.kr.ciscultureinseoul.data.Member;
import cis.co.kr.ciscultureinseoul.data.MemberImage;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2018-01-29.
 */

public interface RetrofitRequest {

    //회원가입 정보를 전송.
    @FormUrlEncoded
    @POST("info.do")
    Call<Void> info(@Field("member_id") String id, @Field("member_pw") String pw, @Field("member_nickname") String nickname);

    //아이디 중복 확인.
    @FormUrlEncoded
    @POST("id_ok.do")
    Call<Integer> what(@Field("member_id") String id);

    //닉네임 중복 확인
    @FormUrlEncoded
    @POST("nick_ok.do")
    Call<Integer> what2(@Field("member_nickname") String nick);

    //로그인시 정보를 회원목록에 정보가 있나 확인.
    @FormUrlEncoded
    @POST("login_ok.do")
    Call<Integer> login(@Field("member_id") String id, @Field("member_pw") String pw);

    //로그인 회원 정보를 가져온다.
    @FormUrlEncoded
    @POST("getloginmember.do")
    Call<Member> memberinfo(@Field("member_id") String id);

    //회원의 즐겨찾기 정보를 전송.
    @FormUrlEncoded
    @POST("setfavorite.do")
    Call<Void> setfavorite(@Field("code") String code, @Field("member_id") String id);

    //회원의 즐겨찾기 정보를 가져온다.
    @FormUrlEncoded
    @POST("getfavorite.do")
    Call<ArrayList<Favorite>> getfavorite(@Field("member_id") String id);

    //즐겨찾기된 정보일 시 이모티콘 표시.
    @FormUrlEncoded
    @POST("isfavorite.do")
    Call<Integer> fafa(@Field("code") String code, @Field("member_id") String id);

    //즐겨찾기 삭제 정보 전송.
    @FormUrlEncoded
    @POST("deletefavorite.do")
    Call<Void> delfa(@Field("code") String code, @Field("member_id") String id);

    //회원 이미지 정보를 전송
    @Multipart
    @POST("addmemberimage.do")
    Call<String> insertPromoteMsg(@Part MultipartBody.Part uploadFile, @Query("member_id") String member_id);

    //회원 이미지 정보를 가져온다.
    @FormUrlEncoded
    @POST("getmemberimage.do")
    Call<MemberImage> getmemberimage(@Field("member_id") String id);

    //바뀐 닉네임 정보를 전송.
    @FormUrlEncoded
    @POST("renickname.do")
    Call<Void> renick( @Field("id") String id, @Field("member_nickname") String nickname);


    //Board 정보 전송
    @FormUrlEncoded
    @POST("insertBoard.do")
    Call<Void> binfo(@Field("S_title")String title,@Field("S_boder")String boder,@Field("S_nickname")String nick);
    //Board 정보 받아오기
    @GET("getBoard.do")
    Call<ArrayList<Board>> getBoard();
    //Comment 정보 전송
    @FormUrlEncoded
    @POST("insertComments.do")
    Call<Void> cinfo(@Field("C_boder")String cboder,@Field("C_nick")String cnick,@Field("C_clock")String cclock,@Field("C_id")String cid);
    //Comment 정보 받아오기
    @GET("getComment.do")
    Call<ArrayList<Comments>> getComment(@Query("S_id") String sid);
    //Board에 올릴 이미지를 전송
    @Multipart
    @POST("insertboardimage.do")
    Call<String> insertboard(@Part MultipartBody.Part uploadFile);

    //Board에 올릴 이미지 받아오기
    @FormUrlEncoded
    @POST("getboardimage.do")
    Call<BoardImage> getboardimage(@Field("board_id") String id);

    //Board 리스트뷰에 이미지를 포함한 정보일 때 아이콘 표시.
    @FormUrlEncoded
    @POST("getimagenum.do")
    Call<Integer> getimagenum(@Field("board_id") String id);


    //댓글작성자와 나의 닉네임을 비교
    @FormUrlEncoded
    @POST("deleteVisible.do")
    Call<Integer> deleteVisible(@Field("my_nick") String mynick, @Field("comments_nick") String commentnick);
    //Comment 삭제
    @FormUrlEncoded
    @POST("deleteComments.do")
    Call<Void> deleteComments(@Field("comments_id")String comments_id, @Field("board_id")String board_id);


}