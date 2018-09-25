package cis.co.kr.ciscultureinseoul;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cis.co.kr.ciscultureinseoul.adapter.CommentAdapter;
import cis.co.kr.ciscultureinseoul.data.BoardImage;
import cis.co.kr.ciscultureinseoul.data.Comments;
import cis.co.kr.ciscultureinseoul.module.GlideApp;
import cis.co.kr.ciscultureinseoul.retrofit.RetrofitService;
import cis.co.kr.ciscultureinseoul.service.LoginService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForumBodeActivity extends AppCompatActivity {
    @BindView(R.id.bode_title)
    TextView bode_title;
    @BindView(R.id.bode_boder)
    TextView bode_boder;
    @BindView(R.id.bode_nick)
    TextView bode_nick;

    @BindView(R.id.comment_lv)
    ListView comment_lv;
    @BindView(R.id.comment_nick)
    TextView comment_nick;
    @BindView(R.id.comment_bode)
    EditText comment_bode;
    @BindView(R.id.comment_button)
    Button comment_button;
    @BindView(R.id.comment)
    TextView comment;
    @BindView(R.id.bode_ll2)
    LinearLayout bode_ll2;
    @BindView(R.id.iv_board)
    ImageView iv_board;

    @BindView(R.id.bode_layout)
    RelativeLayout bode_layout;

    CommentAdapter commentAdapter;

    String mycommentnick;

    boolean btn = true;

    //현재 시간
    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    Long bode_id;           //게시판 고유번호


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_bode);
        ButterKnife.bind(this);
        final LoginService loginService = LoginService.getInstance();


        mycommentnick = loginService.getLoginMember().getMember_nickname();

        Intent intent = getIntent();
        bode_title.setText(intent.getStringExtra("title"));
        bode_nick.setText(intent.getStringExtra("nickname"));
        bode_boder.setText(intent.getStringExtra("bode"));
        bode_id = intent.getLongExtra("id", 0);

        //사진첨부이미지 서버로 전송
        Call<BoardImage> boardImageCall = RetrofitService.getInstance().getRetrofitRequest().getboardimage(bode_id.toString());
        boardImageCall.enqueue(new Callback<BoardImage>() {
            @Override
            public void onResponse(Call<BoardImage> call, Response<BoardImage> response) {
                if(response.isSuccessful()){
                    BoardImage boardImage = response.body();
                    if(boardImage==null) {

                    } else {
                        imgSet("http://192.168.1.114:8090/cis/resources/upload/" + boardImage.getSave_name());
                    }
                }
            }

            @Override
            public void onFailure(Call<BoardImage> call, Throwable t) {

            }
        });


        getComment(); //댓글 리스트뷰 불러오기

        comment_nick.setText(mycommentnick);
        //댓글 등록 버튼, 댓글정보 전송
        comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTime();
                Call<Void> insertcomment = RetrofitService.getInstance().getRetrofitRequest().cinfo(comment_bode.getText().toString(), comment_nick.getText().toString(), getTime().toString(), bode_id.toString());
                insertcomment.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        getComment(); //댓글 정보 전송에 성공했을때 댓글 갱신
                        hideKeyboard();//댓글등록후 키보드 숨김
                        comment_bode.setText("");
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });
            }
        });

        bode_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
            }
        });


    }

    public void imgSet(String url) {
        GlideApp.with(this)
                .load(url)
                .error(R.drawable.bg_smallimg)
                .fitCenter()
                .into(iv_board);
    }

    //댓글 리스트뷰 열기/닫기
    @OnClick(R.id.comment)
    public void commentbtn(View view) {
        if(btn) {
            bode_ll2.setVisibility(View.VISIBLE);
            comment_lv.setVisibility(View.VISIBLE);
            btn = false;
        } else {
            bode_ll2.setVisibility(View.GONE);
            comment_lv.setVisibility(View.GONE);
            btn = true;
        }
    }

    //Retrofit을 사용해 댓글정보 불러오기
    public void getComment() {
        Call<ArrayList<Comments>> AC = RetrofitService.getInstance().getRetrofitRequest().getComment(bode_id.toString());
        AC.enqueue(new Callback<ArrayList<Comments>>() {
            @Override
            public void onResponse(Call<ArrayList<Comments>> call, Response<ArrayList<Comments>> response) {
                ArrayList<Comments> comments = response.body();
                commentAdapter = new CommentAdapter(comments);
                comment_lv.setAdapter(commentAdapter);

            }

            @Override
            public void onFailure(Call<ArrayList<Comments>> call, Throwable t) {

            }
        });
    }

    //현재 시간
    private String getTime() {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

    //키보드 숨김
    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
