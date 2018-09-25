package cis.co.kr.ciscultureinseoul;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.security.auth.Destroyable;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cis.co.kr.ciscultureinseoul.bus.BusProvider;
import cis.co.kr.ciscultureinseoul.data.MemberImage;
import cis.co.kr.ciscultureinseoul.event.BlockWindow;
import cis.co.kr.ciscultureinseoul.event.EndEvent;
import cis.co.kr.ciscultureinseoul.fragment.FavoriteFragment;
import cis.co.kr.ciscultureinseoul.fragment.ForumFragment;
import cis.co.kr.ciscultureinseoul.fragment.HomeFragment;
import cis.co.kr.ciscultureinseoul.module.GlideApp;
import cis.co.kr.ciscultureinseoul.retrofit.RetrofitService;
import cis.co.kr.ciscultureinseoul.service.LoginService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity{

    //main_fragment영역을 switchToFragment로 재정의한다
    public void switchToFragment1() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.main_fragment, new HomeFragment()).commit();
    }

    public void switchToFragment2() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.main_fragment, new FavoriteFragment()).commit();
    }

    public void switchToFragment3() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.main_fragment, new ForumFragment()).commit();
    }

    //BottomNavigationView로 네비게이션에 재정의한 값을 실행.
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    switchToFragment1();
                    return true;
                case R.id.navigation_dashboard:
                    switchToFragment2();
                    return true;
                case R.id.navigation_forum:
                    switchToFragment3();
                    return true;
            }
            return false;
        }
    };

    @BindView(R.id.main_fragment)
    RelativeLayout main_fragment;
    @BindView(R.id.slide_dr)
    DrawerLayout slide_dr;
    @BindView(R.id.slide_lv)
    LinearLayout slide_lv;
    @BindView(R.id.slide_iv)
    ImageView slide_iv;
    @BindView(R.id.slide_nickname)
    TextView slide_nickname;
    @BindView(R.id.slide_edit)
    Button slide_edit;
    @BindView(R.id.slide_logout)
    Button slide_logout;
    @BindView(R.id.slide_out)
    Button slide_out;
    @BindView(R.id.navigation)
    BottomNavigationView navigation;

    //화면 잠금 부분
    @BindView(R.id.progressbar)
    ProgressBar progressbar;

    //타임 인터벌 설정
    private final long FINISH_INTERVAL_TIME = 2000;
    //타이머설정
    private long backPressedTime = 0;

    Bus bus = BusProvider.getInstance().getBus();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        bus.register(this);
        LoginService loginService = LoginService.getInstance();
        slide_nickname.setText(loginService.getLoginMember().getMember_nickname());
        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        switchToFragment1();
        slideset();

    }


    //슬라이더 이미지,닉네임  DB와 연동
    @TargetApi(Build.VERSION_CODES.M)
    public void slideset() {
        LoginService loginService = LoginService.getInstance();
        Call<MemberImage> stringCall = RetrofitService.getInstance().getRetrofitRequest().getmemberimage(loginService.getLoginMember().getId().toString());
        stringCall.enqueue(new Callback<MemberImage>() {
            @Override
            public void onResponse(Call<MemberImage> call, Response<MemberImage> response) {
                if(response.isSuccessful()){
                    MemberImage memberiMage = response.body();
                    if(memberiMage==null) {
                        notimage();
                    } else {
                        imgSet("http://192.168.1.114:8090/cis/resources/upload/" + memberiMage.getSave_name());
                    }

                }
            }

            @Override
            public void onFailure(Call<MemberImage> call, Throwable t) {

            }
        });

        //퍼미션 설정
        if (
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            }
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }

        //개인정보수정
        slide_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivityForResult(intent, 100);
            }
        });

        //로그아웃
        slide_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EndEvent endEvent = new EndEvent();
                bus.post(endEvent);
                LoginService loginService = LoginService.getInstance();
                loginService.logOut();
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.putExtra("preference",2);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        //종료
        slide_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog =
                new AlertDialog.Builder(HomeActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("종료")
                        .setMessage("정말 앱을 종료하시겠습니까?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    //설정한 이미지가 없을경우 기본이미지 출력
    public void notimage() {
        GlideApp.with(this)
                .load(R.drawable.bg_smallimg)
                .centerCrop()
                .into(slide_iv);
    }

    public void imgSet(String url) {
        GlideApp.with(this)
                .load(url)
                .error(R.drawable.bg_smallimg)
                .centerCrop()
                .into(slide_iv);
    }

    //setResult에서 받은데이터를 연결
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 100) {
            try {
                LoginService loginService = LoginService.getInstance();
                String asd = intent.getStringExtra("nickname");
                if(!asd.equals("")) {
                    loginService.getLoginMember().setMember_nickname(intent.getStringExtra("nickname"));
                }
                slide_nickname.setText(loginService.getLoginMember().getMember_nickname());
                Bitmap myBitmap = BitmapFactory.decodeFile(intent.getStringExtra("bitmap").toString());
                slide_iv.setImageBitmap(myBitmap);

            } catch (Exception e) {

            }
        }
    }





    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;
        if (slide_dr.isDrawerOpen(slide_lv)) {      //슬라이더 열기
            slide_dr.closeDrawer(slide_lv);         //슬라이더 닫기
        } //2초안에 2번 뒤로가기를 눌렀을 경우에 종료 다이알로그 띄움
        else if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("종료")
                    .setMessage("정말 앱을 종료하시겠습니까?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        } else {
            backPressedTime = tempTime; //조건이 맞지않을경우 시간을 0으로 초기화
        }
    }

    //Blockwindow 이벤트가 왔을 시 windowBlock 함수를 동작한다.
    @Subscribe
    public void Blockwindow(BlockWindow event) {
        windowBlock();
    }

    // 화면을 3초동안 터치불가 상태로 만든다.
    public void windowBlock() {
        hideKeyboard();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressbar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                progressbar.setVisibility(View.GONE);
            }
        }, 3000);
    }
    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EndEvent endEvent = new EndEvent();
        bus.post(endEvent);
        bus.unregister(this);
    }

}
