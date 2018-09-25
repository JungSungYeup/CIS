package cis.co.kr.ciscultureinseoul;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.otto.Bus;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cis.co.kr.ciscultureinseoul.bus.BusProvider;
import cis.co.kr.ciscultureinseoul.retrofit.RetrofitService;
import cis.co.kr.ciscultureinseoul.service.LoginService;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Callback;
import retrofit2.Response;

public class ForumWriteActivity extends AppCompatActivity {
    @BindView(R.id.write_title)
    EditText write_title;
    @BindView(R.id.write_boder)
    EditText write_boder;
    @BindView(R.id.write_return)
    Button write_return;
    @BindView(R.id.write_enter)
    Button write_enter;
    @BindView(R.id.write_upload)
    Button write_upload;
    @BindView(R.id.write_nick)
    TextView write_nick;
    @BindView(R.id.write_image)
    ImageView write_image;


    Bus bus = BusProvider.getInstance().getBus();

    private final int GALLERY_CODE = 1112;
    String imagepath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_write);
        ButterKnife.bind(this);
        bus.register(this);

        //작성자 닉네임 불러오기
        LoginService loginService = LoginService.getInstance();
        write_nick.setText(loginService.getLoginMember().getMember_nickname());


        //작성한 제목,내용과 닉네임을 서버로전송해서 게시물 작성후 화면 나가기
        write_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginService loginService = LoginService.getInstance();

                retrofit2.Call<Void> insertb = RetrofitService.getInstance().getRetrofitRequest().binfo(write_title.getText().toString(), write_boder.getText().toString(), write_nick.getText().toString());
                insertb.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(retrofit2.Call<Void> call, Response<Void> response) {

                    }

                    @Override
                    public void onFailure(retrofit2.Call<Void> call, Throwable t) {

                    }
                });
                if(imagepath!=null) {
                    File file = new File(imagepath);
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
                    MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("asdfff", file.getName(), requestFile);
                    retrofit2.Call<String> call = RetrofitService.getInstance().getRetrofitRequest().insertboard(uploadFile);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(retrofit2.Call<String> call, Response<String> response) {
                            if (response.isSuccessful()) {
                                String url = response.body();
                            } else {
                            }
                        }

                        @Override
                        public void onFailure(retrofit2.Call<String> call, Throwable t) {
                            Log.i("test", "onFailure: " + t.getMessage());
                        }
                    });
                }
                finish();
            }
        });
    }

    @OnClick(R.id.write_return)
    public void write_return(View view) {
      finish();
    }

    @OnClick(R.id.write_upload)
    public void setpicture(View view) {
        selectGallery();
    }

    //갤러리 출력
    private void selectGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_CODE);
    }

    //갤러리에서 가져오기
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
        write_image.setImageBitmap(bitmap);
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
