package cis.co.kr.ciscultureinseoul;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;

import cis.co.kr.ciscultureinseoul.retrofit.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinMemberActivity extends AppCompatActivity {
    @BindView(R.id.et_join_member_id)
    EditText et_join_member_id;
    @BindView(R.id.et_join_member_pw)
    EditText et_join_member_pw;
    @BindView(R.id.et_join_member_pw2)
    EditText et_join_member_pw2;
    @BindView(R.id.et_join_member_nickname)
    EditText et_join_member_nickname;
    @BindView(R.id.txt_id)
    TextView txt_id;
    @BindView(R.id.txt_pw)
    TextView txt_pw;
    @BindView(R.id.txt_pw2)
    TextView txt_pw2;
    @BindView(R.id.txt_nickname)
    TextView txt_nickname;
    @BindView(R.id.btn_join_member)
    Button btn_join_member;
    @BindView(R.id.btn_back)
    ImageView btn_back;
    Integer integer = -1;
    String id;
    String pw;
    String pw2;
    String nickname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_member);
        ButterKnife.bind(this);

    }

    //뒤로가기 버튼.
    @OnClick(R.id.btn_back)
    public void back(View view) {
        finish();
    }

    //회원가입 버튼을 눌럿을시 각 함수가 검토하여 모두 통과되면 회원정보가 서버 db에 저장된다.
    @OnClick(R.id.btn_join_member)
    public void JoinMember(View view) {
        id = et_join_member_id.getText().toString();
        pw = et_join_member_pw.getText().toString();
        pw2 = et_join_member_pw2.getText().toString();
        nickname = et_join_member_nickname.getText().toString();

        checkid();
        checkpw();
        checkpw2();
        checknick();
        joincheck();
    }

   //글 입력칸을 눌럿을 시 동작.
    @OnFocusChange(R.id.et_join_member_id)
    public void checkid(View view, boolean hasFocus) {
        id = et_join_member_id.getText().toString();
        if (!hasFocus) {
            checkid();
        }
    }
    //글 입력칸을 눌럿을 시 동작.
    @OnFocusChange(R.id.et_join_member_pw)
    public void checkpw(View view, boolean hasFocus) {
        pw = et_join_member_pw.getText().toString();
        if (!hasFocus) {
            checkpw();
        }
    }
    //글 입력칸을 눌럿을 시 동작.
    @OnFocusChange(R.id.et_join_member_pw2)
    public void checkpw2(View view, boolean hasFocus) {
        pw2 = et_join_member_pw2.getText().toString();
        if (!hasFocus) {
            checkpw2();
        }
    }
    //글 입력칸을 눌럿을 시 동작.
    @OnFocusChange(R.id.et_join_member_nickname)
    public void checknick(View view, boolean hasFocus) {
        nickname = et_join_member_nickname.getText().toString();
        if (!hasFocus) {
            checknick();
        }
    }

    //아이디 정보를 서버에 보내 조건에 맞는지 확인 한다.
    public void checkid() {
        if (id.equals("")) {
            txt_id.setText("필수 정보입니다.");
            txt_id.setTextColor(Color.parseColor("#ff0000"));
            txt_id.setVisibility(View.VISIBLE);
        } else {
            if (id.length() >= 8 && id.length() <= 16) {
                Call<Integer> integerCall = RetrofitService.getInstance().getRetrofitRequest().what(id);
                integerCall.enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        if (response.isSuccessful()) {
                            integer = response.body();
                            if (integer == 0) {
                                txt_id.setText("중복된 아이디입니다.");
                                txt_id.setTextColor(Color.parseColor("#ff0000"));
                                txt_id.setVisibility(View.VISIBLE);
                            } else if (integer == 1) {
                                txt_id.setVisibility(View.GONE);
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {

                    }
                });
            } else {
                txt_id.setVisibility(View.VISIBLE);
                txt_id.setTextColor(Color.parseColor("#ff0000"));
                txt_id.setText("아이디는 8~16자를 사용하세요.");
            }
        }
    }

    //패스워드 정보를 서버에 보내 조건에 맞는지 확인 한다.
    public void checkpw() {
        if (pw.equals("")) {
            txt_pw.setText("필수 정보입니다.");
            txt_pw.setTextColor(Color.parseColor("#ff0000"));
            txt_pw.setVisibility(View.VISIBLE);
        } else {
            if (pw.length() >= 8 && pw.length() <= 16) {
                txt_pw.setVisibility(View.GONE);
            } else {
                txt_pw.setVisibility(View.VISIBLE);
                txt_pw.setTextColor(Color.parseColor("#ff0000"));
                txt_pw.setText("비밀번호는 8~16자를 사용하세요.");
            }
        }
    }
    //패스워드 정보를 서버에 보내 조건에 맞는지 확인 한다.
    public void checkpw2() {
        if (pw2.equals("")) {
            txt_pw2.setText("필수 정보입니다.");
            txt_pw2.setTextColor(Color.parseColor("#ff0000"));
            txt_pw2.setVisibility(View.VISIBLE);
        } else {
            if (pw.equals(pw2)) {
                txt_pw2.setVisibility(View.GONE);
            } else {
                txt_pw2.setText("비밀번호가 다릅니다.");
                txt_pw2.setTextColor(Color.parseColor("#ff0000"));
                txt_pw2.setVisibility(View.VISIBLE);
            }
        }
    }
    //별명 정보를 서버에 보내 조건에 맞는지 확인 한다.
    public void checknick() {
        if (nickname.equals("")) {
            txt_nickname.setText("필수 정보입니다.");
            txt_nickname.setTextColor(Color.parseColor("#ff0000"));
            txt_nickname.setVisibility(View.VISIBLE);
        } else {
            Call<Integer> integerCall = RetrofitService.getInstance().getRetrofitRequest().what2(nickname);
            integerCall.enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {
                    if (response.isSuccessful()) {
                        integer = response.body();
                        if (integer == 0) {
                            txt_nickname.setText("중복된 닉네임입니다.");
                            txt_nickname.setTextColor(Color.parseColor("#ff0000"));
                            txt_nickname.setVisibility(View.VISIBLE);
                        } else if (integer == 1) {
                            txt_nickname.setVisibility(View.GONE);

                        }
                    }

                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {

                }
            });

        }
    }

    //모든 조건을 만족햇을시 정보를 서버 db에 보내는 함수.
    public void joincheck() {
        if (integer == 1 && pw.length() >= 8 && pw.length() <= 16 && pw.equals(pw2)) {
            Toast.makeText(this, "회원가입 완료", Toast.LENGTH_SHORT).show();
            System.out.println(id + " " + pw + " " + nickname);
            Call<Void> voidCall = RetrofitService.getInstance().getRetrofitRequest().info(id, pw, nickname);
            voidCall.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {

                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                }
            });
            finish();
        } else {
            Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show();
        }
    }
}

