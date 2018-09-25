package cis.co.kr.ciscultureinseoul;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cis.co.kr.ciscultureinseoul.data.Member;
import cis.co.kr.ciscultureinseoul.retrofit.RetrofitService;
import cis.co.kr.ciscultureinseoul.service.LoginService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.btn_member_login)
    Button btn_member_login;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;
    @BindView(R.id.btn_member_join)
    Button btn_member_join;
    @BindView(R.id.et_login_id)
    EditText et_login_id;
    @BindView(R.id.et_login_pw)
    EditText et_login_pw;

    Intent intent;
    Integer loginSus = -1;
    Member loginMember;
    String member_id;
    String member_pw;
    int logininfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //자동 로그인을 가능하게 해주는 간이 db.
        SharedPreferences pref = getSharedPreferences("member_IdPw", MODE_PRIVATE);
        intent = getIntent();
        logininfo = intent.getIntExtra("preference", 0);

        //logininfo가 2가 들어오면 로그아웃 함수를 실행한다.
        if (logininfo == 2) {
            removelogin();
        }

        //한번 로그인이 되면 로그인 아이디가 저장되고 그것을 불러와 자동으로 로그인 하게 해준다.
        if (getlogin() == 1) {
            if (!pref.getString("member_id", "").equals("")) {
                getId();
            }
            outoLogin(member_id);
            windowBlock();
        }
    }

    //로그인 버튼을 눌럿을 시 로그인 함수가 실행된다.
    @OnClick(R.id.btn_member_login)
    public void memberLogin(View view) {
        login();
    }

    //회원가입 버튼을 눌럿을 시 회원가입 페이지로 넘어간다.
    @OnClick(R.id.btn_member_join)
    public void onClickBtnJoin(View view) {
        Intent intent = new Intent(this, JoinMemberActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    //로그인 함수.
    public void login() {
        member_id = et_login_id.getText().toString();
        member_pw = et_login_pw.getText().toString();
        Call<Integer> integerCall = RetrofitService.getInstance().getRetrofitRequest().login(member_id, member_pw);
        integerCall.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful()) {
                    Integer integer = response.body();
                    loginSus = integer;
                    if (loginSus == 0) {
                        Toast.makeText(MainActivity.this, "아이디가 존재하지 않거나 정확하지 않습니다.", Toast.LENGTH_SHORT).show();
                    } else if (loginSus == 1) {
                        savelogin();
                        outoLogin(member_id);
                        windowBlock();
                    } else {
                        Toast.makeText(MainActivity.this, "비밀번호가 정확하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "서버가 닫혀있습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
            }
        });
    }

    //로그아웃 함수.
    public void removelogin() {
        SharedPreferences pref = getSharedPreferences("member_IdPw", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("login");
        editor.remove("member_id");
        editor.commit();
    }

    //어플이 시작할때 로그인이 한번 됫을시 자동으로 로그인정보를 가져오는 함수.
    public int getlogin() {
        SharedPreferences pref = getSharedPreferences("member_IdPw", MODE_PRIVATE);
        return pref.getInt("login", 0);
    }

    //한번 로그인 할 시 동작하는 함수로 로그인 회원정보를 저장하는 함수.
    public void savelogin() {
        SharedPreferences pref = getSharedPreferences("member_IdPw", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("login", 1);
        editor.commit();
    }

    //저장된 로그인 회원정보를 가져오는 함수.
    public void getId() {
        SharedPreferences pref = getSharedPreferences("member_IdPw", MODE_PRIVATE);
        member_id = pref.getString("member_id", "");
    }

    //로그인 회원정보를 저장하는 함수.
    public void saveId() {
        SharedPreferences pref = getSharedPreferences("member_IdPw", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("member_id", member_id);
        editor.commit();

    }

    //자동로그인시 걸리는 시간동안 화면클릭이 안되게 하는 함수.
    public void windowBlock() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressbar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                progressbar.setVisibility(View.GONE);
            }
        }, 2000);
    }

    //자동으로 로그인이 되게 해주는 함수. 서버에서 로그인회원 정보를 받아온다.
    public void outoLogin(final String member_id) {
        intent = new Intent(this, HomeActivity.class);
        Call<Member> memberCall = RetrofitService.getInstance().getRetrofitRequest().memberinfo(member_id);
        memberCall.enqueue(new Callback<Member>() {
            @Override
            public void onResponse(Call<Member> call, Response<Member> response) {
                if (response.isSuccessful()) {
                    loginMember = response.body();
                    LoginService loginService = LoginService.getInstance();
                    loginService.setLoginMember(loginMember);
                    saveId();
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "서버가 닫혀있습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Member> call, Throwable t) {
            }
        });
    }


}
