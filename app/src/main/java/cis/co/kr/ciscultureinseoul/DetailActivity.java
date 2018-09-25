package cis.co.kr.ciscultureinseoul;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Bus;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cis.co.kr.ciscultureinseoul.bus.BusProvider;
import cis.co.kr.ciscultureinseoul.data.Row;
import cis.co.kr.ciscultureinseoul.module.GlideApp;
import cis.co.kr.ciscultureinseoul.retrofit.RetrofitService;
import cis.co.kr.ciscultureinseoul.service.LoginService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DetailActivity extends AppCompatActivity {
    @BindView(R.id.item_img)
    ImageView item_img;
    @BindView(R.id.txt_title)
    TextView txt_title;
    @BindView(R.id.txt_codename)
    TextView txt_codename;
    @BindView(R.id.txt_date)
    TextView txt_date;
    @BindView(R.id.txt_time)
    TextView txt_time;
    @BindView(R.id.txt_place)
    TextView txt_place;
    @BindView(R.id.txt_use_trgt)
    TextView txt_use_trgt;
    @BindView(R.id.txt_use_fee)
    TextView txt_use_fee;
    @BindView(R.id.txt_sponsor)
    TextView txt_sponsor;
    @BindView(R.id.txt_inquiry)
    TextView txt_inquiry;
    @BindView(R.id.btn_org_link)
    Button btn_org_link;
    @BindView(R.id.favarit_icon)
    ImageView favarit_icon;
    @BindView(R.id.btn_back)
    ImageView btn_back;

    Bus bus = BusProvider.getInstance().getBus();
    Row row = new Row();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        bus.register(this);
        LoginService loginService = LoginService.getInstance();

        Intent intent = getIntent();
        row = (Row) intent.getSerializableExtra("Row");
        detailset();

    }

    @OnClick(R.id.btn_back)
    public void onclickback(View view) {
       finish();
    }

    @OnClick(R.id.btn_org_link)
    public void onclickLink(View view) {
        try{
            Intent i = new Intent(Intent.ACTION_VIEW);
            Uri u = Uri.parse(row.getORG_LINK());
            i.setData(u);
            startActivity(i);
        }catch (Exception e) {
            Toast.makeText(this, "등록된 행사정보가 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    //리스트 뷰에 저장된 정보를 가져와 화면에 띄운다.
    public void detailset() {
        txt_title.setText(row.getTITLE());
        txt_codename.setText(row.getCODENAME());
        txt_date.setText(row.getSTRTDATE() + " ~ " + row.getEND_DATE());
        txt_time.setText(row.getTIME());
        txt_place.setText(row.getPLACE());
        txt_use_trgt.setText(row.getUSE_TRGT());
        txt_use_fee.setText(row.getUSE_FEE());
        txt_sponsor.setText(row.getSPONSOR());
        txt_inquiry.setText(row.getINQUIRY());
        String url = "";

        String[] mainImg = row.getMAIN_IMG().split("\\/");

        for (int i = 0; i < mainImg.length; ++i) {
            if (i != 0 && i != 1 && i != 2) {
                if (i == mainImg.length - 1) {
                    url = url + mainImg[i];
                } else {
                    url = url + mainImg[i] + "/";
                }
            } else {
                url = url + mainImg[i].toLowerCase() + "/";
            }
        }

        GlideApp.with(this)
                .load(url)
                .error(R.drawable.bg_smallimg)
                .fitCenter()
                .into(item_img);

        LoginService loginService = LoginService.getInstance();
        Call<Integer> integerCall = RetrofitService.getInstance().getRetrofitRequest().fafa(row.getCULTCODE().toString(), loginService.getLoginMember().getId().toString());
        integerCall.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful()) {
                    Integer integer = response.body();
                    if (integer == 0) {
                        favarit_icon.setVisibility(View.GONE);
                    } else {
                        favarit_icon.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }

}
