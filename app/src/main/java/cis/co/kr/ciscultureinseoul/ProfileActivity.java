package cis.co.kr.ciscultureinseoul;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cis.co.kr.ciscultureinseoul.data.MemberImage;
import cis.co.kr.ciscultureinseoul.module.GlideApp;
import cis.co.kr.ciscultureinseoul.retrofit.RetrofitService;
import cis.co.kr.ciscultureinseoul.service.LoginService;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.iv_small)
    ImageView iv_small;
    @BindView(R.id.btn_enter)
    Button btn_enter;
    @BindView(R.id.btn_return)
    Button btn_return;
    @BindView(R.id.txt_nickname)
    TextView txt_nickname;
    @BindView(R.id.et_nicknameinfo)
    EditText et_nicknameinfo;
    @BindView(R.id.rl_profile)
    RelativeLayout rl_profile;


    private final int GALLERY_CODE = 1112;
    InputMethodManager imm;
    String imagepath = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        //InputMethodManager 정의
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

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
                        imgSeturl("http://192.168.1.114:8090/cis/resources/upload/" + memberiMage.getSave_name());
                    }

                }

            }
            @Override
            public void onFailure(Call<MemberImage> call, Throwable t) {

            }
        });
        et_nicknameinfo.setText(loginService.getLoginMember().getMember_nickname());

        //엔터시 줄넘김 방지
        et_nicknameinfo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER) {
                    return true;
                }
                return false;
            }
        });
    }

    @OnClick(R.id.iv_small)
    public void onclickImg(View view) {
        selectGallery();
    }

    @OnClick(R.id.btn_return)
    public void onclickbtn1(View view) {
        finish();
    }
    //작성한 Profile 정보를 서버로 전송
    @OnClick(R.id.btn_enter)
    public void onclickbtn2(View view) {
        LoginService loginService = LoginService.getInstance();
        String asd = et_nicknameinfo.getText().toString();
        Intent intent = new Intent();
        if (!asd.equals("")) {
            intent.putExtra("nickname", asd);
            Call<Void> voidCall = RetrofitService.getInstance().getRetrofitRequest().renick(loginService.getLoginMember().getId().toString(), et_nicknameinfo.getText().toString());
            voidCall.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                }
            });
        }
        if(imagepath!=null) {
            File file = new File(imagepath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
            MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("asdfff", file.getName(), requestFile);
            Call<String> call = RetrofitService.getInstance().getRetrofitRequest().insertPromoteMsg(uploadFile, loginService.getLoginMember().getId().toString());
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        String url = response.body();
                    } else {
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.i("test", "onFailure: " + t.getMessage());
                }
            });
            intent.putExtra("bitmap", imagepath.toString());
        }
        setResult(101, intent);
        finish();
    }
    //다른 화면 클릭시 키보드 숨기기
    @OnClick(R.id.rl_profile)
    public void onclickpro(View view) {
        imm.hideSoftInputFromWindow(et_nicknameinfo.getWindowToken(), 0);
    }
    //이미지없을경우 보여주는 이미지
    public void notimage() {
        GlideApp.with(this)
                .load(R.drawable.bg_smallimg)
                .centerCrop()
                .into(iv_small);
    }

    public void imgSeturl(String url) {
        GlideApp.with(this)
                .load(url)
                .error(R.drawable.bg_smallimg)
                .centerCrop()
                .into(iv_small);
    }

    public void imgSet() {
        Glide.with(this)
                .load(R.drawable.bg_smallimg)
                .into(iv_small);
    }

    //갤러리 출력
    private void selectGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_CODE);
    }

    //갤러리에서 이미지 가져오기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GALLERY_CODE:
                    sendPicture(data.getData());
                    break;
                default:
                    break;
            }
        }
    }

    // path 경로, 경로를 통해 비트맵으로 전환
    private void sendPicture(Uri imgUri) {
        imagepath = getRealPathFromURI(imgUri);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagepath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeFile(imagepath);
        iv_small.setImageBitmap(bitmap);
    }


    //앨범 경로찾기
    private String getRealPathFromURI(Uri contentUri) {
        int column_index = 0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }
        return cursor.getString(column_index);
    }
}
