package cis.co.kr.ciscultureinseoul.fragment;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.squareup.otto.Bus;

import java.util.ArrayList;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cis.co.kr.ciscultureinseoul.R;
import cis.co.kr.ciscultureinseoul.adapter.AutoScrollAdapter;
import cis.co.kr.ciscultureinseoul.bus.BusProvider;
import cis.co.kr.ciscultureinseoul.data.GetData;
import cis.co.kr.ciscultureinseoul.data.Row;
import cis.co.kr.ciscultureinseoul.util.Utils;
import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;
import kr.go.seoul.airquality.AirQualityTypeMini;


public class ViewP1Fragment extends Fragment {
    private static ViewP1Fragment curr = null;

    public static ViewP1Fragment getInstance() {
        if (curr == null) {
            curr = new ViewP1Fragment();
        }

        return curr;
    }

    private String OpenApiKey = "686957747a646f6e36377763715546";
    private String SearchOpenAPI = "http://openapi.seoul.go.kr:8088/" + OpenApiKey + "/json/SearchConcertDetailService/";
    private Unbinder unbinder;

    GetData getData;
    ArrayList<Row> rows;

    Bus bus = BusProvider.getInstance().getBus();
    @BindView(R.id.autoViewPager) AutoScrollViewPager autoViewPager;
    AutoScrollAdapter viewPagerAdapter;
    @BindView(R.id.mini)
    AirQualityTypeMini mini;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view1_layout, container, false);
        bus.register(this);
        unbinder = ButterKnife.bind(this, view);

        //날씨를 알려주는 api를 화면에 띄운다.
        mini.setOpenAPIKey(OpenApiKey);

        //배너를 5개 띄운다.
        String Url = SearchOpenAPI + 1 + "/" + 5 + "/";
        JsonTask jsonTask = new JsonTask();
        jsonTask.execute(Url);

        return view;
    }

    //배너 부분 api 최신데이터 5개 갱신.
    public class JsonTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            String json = Utils.getStringFromServer(strings[0]);
            return json;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Gson gson = new Gson();
            getData = gson.fromJson(s, GetData.class);
            rows = getData.getSearchConcertDetailService().getRow();
            Activity fragment = getActivity();
            viewPagerAdapter = new AutoScrollAdapter(fragment, rows);
            autoViewPager.setAdapter(viewPagerAdapter);
            autoViewPager.setInterval(5000);
            autoViewPager.startAutoScroll();

        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }
}
