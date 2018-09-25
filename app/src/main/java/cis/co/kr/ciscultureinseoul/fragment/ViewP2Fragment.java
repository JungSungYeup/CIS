package cis.co.kr.ciscultureinseoul.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.Unbinder;
import cis.co.kr.ciscultureinseoul.DetailActivity;
import cis.co.kr.ciscultureinseoul.R;
import cis.co.kr.ciscultureinseoul.adapter.SearchAdapter;
import cis.co.kr.ciscultureinseoul.bus.BusProvider;
import cis.co.kr.ciscultureinseoul.data.GetData;
import cis.co.kr.ciscultureinseoul.data.GetDataSearchDate;
import cis.co.kr.ciscultureinseoul.data.GetDataSearchGenre;
import cis.co.kr.ciscultureinseoul.data.GetDataSearchName;
import cis.co.kr.ciscultureinseoul.data.GetDataSearchPlace;
import cis.co.kr.ciscultureinseoul.data.Row;
import cis.co.kr.ciscultureinseoul.event.BlockWindow;
import cis.co.kr.ciscultureinseoul.event.EndEvent;
import cis.co.kr.ciscultureinseoul.event.SearchDateEvent;
import cis.co.kr.ciscultureinseoul.event.SearchEventNum;
import cis.co.kr.ciscultureinseoul.event.SearchGenreEvent;
import cis.co.kr.ciscultureinseoul.event.SearchNameEvent;
import cis.co.kr.ciscultureinseoul.event.SearchPlaceEvent;
import cis.co.kr.ciscultureinseoul.retrofit.RetrofitService;
import cis.co.kr.ciscultureinseoul.service.LoginService;
import cis.co.kr.ciscultureinseoul.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ViewP2Fragment extends Fragment {
    private static ViewP2Fragment curr = null;

    public static ViewP2Fragment getInstance() {
        if (curr == null) {
            curr = new ViewP2Fragment();
        }

        return curr;
    }

    private String OpenApiKey = "686957747a646f6e36377763715546";
    private String SearchDetail = "http://openapi.seoul.go.kr:8088/" + OpenApiKey + "/json/SearchConcertDetailService/";

    private String SearchName = "http://openapi.seoul.go.kr:8088/" + OpenApiKey + "/json/SearchConcertNameService/";
    private String SearchDate = "http://openapi.seoul.go.kr:8088/" + OpenApiKey + "/json/SearchConcertPeriodService/";
    private String SearchGenre = "http://openapi.seoul.go.kr:8088/" + OpenApiKey + "/json/SearchPerformanceBySubjectService/";
    private String SearchPlace = "http://openapi.seoul.go.kr:8088/" + OpenApiKey + "/json/SearchConcertPlaceService/";

    Bus bus = BusProvider.getInstance().getBus();
    private Unbinder unbinder;
    @BindView(R.id.Main_LV)
    ListView Main_LV;
    Button add;
    SearchAdapter searchAdapter;
    String Url = "";
    ArrayList<Row> rows = new ArrayList<>();
    //이름, 장소 검색 변수는 공유.
    String getname = "";
    String startd = "";
    String endd = "";
    String gcode = "";
    Integer eventNum = 0;
    // 한 페이지마다 로드할 데이터 갯수.
    int OFFSET = 20;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view2_layout, container, false);
        bus.register(this);
        unbinder = ButterKnife.bind(this, view);
        Main_LV.addFooterView(inflater.inflate(R.layout.item_footer, null));
        add = view.findViewById(R.id.add);
        LoginService loginService = LoginService.getInstance();
        add.setVisibility(View.GONE);

        searchAdapter = new SearchAdapter(rows);
        Main_LV.setAdapter(searchAdapter);
        searchAdapter.notifyDataSetChanged();

        //더보기 버튼을 눌렀을 시 동작함.
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlockWindow blockWindow = new BlockWindow();
                bus.post(blockWindow);
                if (eventNum == 0) {
                    Url = SearchName + (OFFSET + 1) + "/" + (OFFSET + 19) + "/" + getname;
                    NameTask nameTask = new NameTask();
                    nameTask.execute(Url);
                } else if (eventNum == 1) {
                    Url = SearchDate + (OFFSET + 1) + "/" + (OFFSET + 19) + "/" + startd + "/" + endd;
                    DateTask dateTask = new DateTask();
                    dateTask.execute(Url);
                } else if (eventNum == 2) {
                    Url = SearchGenre + (OFFSET + 1) + "/" + (OFFSET + 19) + "/" + gcode;
                    GenreTask genreTask = new GenreTask();
                    genreTask.execute(Url);
                } else {
                    Url = SearchPlace + (OFFSET + 1) + "/" + (OFFSET + 19) + "/" + getname;
                    PlaceTask placeTask = new PlaceTask();
                    placeTask.execute(Url);
                }
                OFFSET = OFFSET + 20;
            }
        });

        //리스트 뷰에 띄워진 정보를 길게 클릭할 시 즐겨찾기에 추가할지 말지 선택 가능.
        Main_LV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Row item = rows.get(position);

                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("FAVARITE?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        LoginService loginService = LoginService.getInstance();
                        Call<Void> favariteCall = RetrofitService.getInstance().getRetrofitRequest().setfavorite(item.getCULTCODE().toString(), loginService.getLoginMember().getId().toString());
                        favariteCall.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                Void aVoid = response.body();
                                searchAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {

                            }
                        });
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.cancel();
                    }
                });

                builder.show();
                return true;
            }
        });


        return view;
    }

    //리스트 뷰의 정보를 클릭했을시 DetailActivity로 이동한다.
    @OnItemClick(R.id.Main_LV)
    public void onItemMainlv(AdapterView<?> parent, View view, int position, long id) {
        Row item = rows.get(position);
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra("Row", item);
        startActivity(intent);
    }


    //장소검색 받아와서 작동하는 AsyncTask
    public class PlaceTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            String json = Utils.getStringFromServer(strings[0]);
            return json;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Gson gson = new Gson();
            GetDataSearchPlace getDataSearchPlace = gson.fromJson(s, GetDataSearchPlace.class);
            try {
                if (getDataSearchPlace.getSearchConcertPlaceService().getRow().size() < 19) {
                    add.setVisibility(View.GONE);
                } else {
                    add.setVisibility(View.VISIBLE);
                }
                for (int i = 0; i < getDataSearchPlace.getSearchConcertPlaceService().getRow().size(); i++) {
                    Integer num = getDataSearchPlace.getSearchConcertPlaceService().getRow().get(i).getCULTCODE();
                    String url = SearchDetail + 1 + "/" + 1 + "/" + num;
                    DetailTask detailTask = new DetailTask();
                    detailTask.execute(url);
                    add.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {

            }

        }
    }

    //이름검색 받아와서 작동하는 AsyncTask
    public class NameTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            String json = Utils.getStringFromServer(strings[0]);
            return json;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Gson gson = new Gson();
            GetDataSearchName getDataSearchName = gson.fromJson(s, GetDataSearchName.class);
            try {
                if (getDataSearchName.getSearchConcertNameService().getRow().size() < 19) {
                    add.setVisibility(View.GONE);
                } else {
                    add.setVisibility(View.VISIBLE);
                }
                for (int i = 0; i < getDataSearchName.getSearchConcertNameService().getRow().size(); i++) {
                    Integer num = getDataSearchName.getSearchConcertNameService().getRow().get(i).getCULTCODE();
                    String url = SearchDetail + 1 + "/" + 1 + "/" + num;
                    DetailTask detailTask = new DetailTask();
                    detailTask.execute(url);
                }
            } catch (Exception e) {

            }

        }
    }

    //기간검색 받아와서 작동하는 AsyncTask
    public class DateTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            String json = Utils.getStringFromServer(strings[0]);
            return json;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Gson gson = new Gson();
            GetDataSearchDate getDataSearchDate = gson.fromJson(s, GetDataSearchDate.class);
            try {
                if (getDataSearchDate.getSearchConcertPeriodService().getRow().size() < 19) {
                    add.setVisibility(View.GONE);
                } else {
                    add.setVisibility(View.VISIBLE);
                }
                for (int i = 0; i < getDataSearchDate.getSearchConcertPeriodService().getRow().size(); i++) {
                    Integer num = getDataSearchDate.getSearchConcertPeriodService().getRow().get(i).getCULTCODE();
                    String url = SearchDetail + 1 + "/" + 1 + "/" + num;
                    DetailTask detailTask = new DetailTask();
                    detailTask.execute(url);
                    add.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {

            }

        }
    }

    //장르검색 받아와서 작동하는 AsyncTask
    public class GenreTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            String json = Utils.getStringFromServer(strings[0]);
            return json;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Gson gson = new Gson();
            GetDataSearchGenre getDataSearchGenre = gson.fromJson(s, GetDataSearchGenre.class);
            try {
                if (getDataSearchGenre.getSearchPerformanceBySubjectService().getRow().size() < 19) {
                    add.setVisibility(View.GONE);
                } else {
                    add.setVisibility(View.VISIBLE);
                }
                for (int i = 0; i < getDataSearchGenre.getSearchPerformanceBySubjectService().getRow().size(); i++) {
                    Integer num = getDataSearchGenre.getSearchPerformanceBySubjectService().getRow().get(i).getCULTCODE();
                    String url = SearchDetail + 1 + "/" + 1 + "/" + num;
                    DetailTask detailTask = new DetailTask();
                    detailTask.execute(url);
                    add.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {

            }

        }


    }

    //키워드 받아서 정보 넣음
    public class DetailTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            String json = Utils.getStringFromServer(strings[0]);
            return json;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Gson gson = new Gson();
            try {
                GetData getData = gson.fromJson(s, GetData.class);
                final ArrayList<Row> roro = getData.getSearchConcertDetailService().getRow();
                rows.add(roro.get(0));
                searchAdapter.notifyDataSetChanged();
            }catch (Exception e) {

            }
        }
    }

    //장르검색 받아와서 리스트뷰에 표시
    @Subscribe
    public void searchGenreEvent(SearchGenreEvent event) {
        rows.clear();
        OFFSET = 20;
        gcode = event.getGcode();
        Url = SearchGenre + 1 + "/" + OFFSET + "/" + gcode;
        GenreTask genreTask = new GenreTask();
        genreTask.execute(Url);
    }

    //이름검색 받아와서 리스트뷰에 표시
    @Subscribe
    public void searchNameEvent(SearchNameEvent event) {
        rows.clear();
        OFFSET = 20;
        getname = event.getNameevent();
        Url = SearchName + 1 + "/" + OFFSET + "/" + getname;
        NameTask nameTask = new NameTask();
        nameTask.execute(Url);
    }

    //기간을 받아와서 리스트 뷰에 표시
    @Subscribe
    public void searchDateEvent(SearchDateEvent event) {
        rows.clear();
        OFFSET = 20;
        startd = event.getStartday();
        endd = event.getEndday();
        Url = SearchDate + 1 + "/" + OFFSET + "/" + startd + "/" + endd;
        DateTask dateTask = new DateTask();
        dateTask.execute(Url);
    }

    //장소검색 받아와서 리스트뷰에 표시
    @Subscribe
    public void searchPlaceEvent(SearchPlaceEvent event) {
        rows.clear();
        OFFSET = 20;
        getname = event.getPlaceevent();
        Url = SearchPlace + 1 + "/" + OFFSET + "/" + getname;
        PlaceTask placeTask = new PlaceTask();
        placeTask.execute(Url);
    }

    //검색메뉴 번호 받아와서 더하기 이벤트 적용
    @Subscribe
    public void SearchevnetNum(SearchEventNum event) {
        eventNum = event.getEventNum();
    }

    @Subscribe
    public void EndEvent(EndEvent event) {
        rows.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }
}


