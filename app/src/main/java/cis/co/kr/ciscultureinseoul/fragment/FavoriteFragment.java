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
import android.widget.ListView;

import com.google.gson.Gson;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
import butterknife.Unbinder;
import cis.co.kr.ciscultureinseoul.DetailActivity;
import cis.co.kr.ciscultureinseoul.R;
import cis.co.kr.ciscultureinseoul.adapter.SearchAdapter;
import cis.co.kr.ciscultureinseoul.bus.BusProvider;
import cis.co.kr.ciscultureinseoul.data.Favorite;
import cis.co.kr.ciscultureinseoul.data.GetData;
import cis.co.kr.ciscultureinseoul.data.Row;
import cis.co.kr.ciscultureinseoul.retrofit.RetrofitService;
import cis.co.kr.ciscultureinseoul.service.LoginService;
import cis.co.kr.ciscultureinseoul.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteFragment extends Fragment {
    private static FavoriteFragment curr = null;

    public static FavoriteFragment getInstance() {
        if (curr == null) {
            curr = new FavoriteFragment();
        }

        return curr;
    }

    Bus bus = BusProvider.getInstance().getBus();
    private Unbinder unbinder;

    private String OpenApiKey = "686957747a646f6e36377763715546";
    private String SearchDetail = "http://openapi.seoul.go.kr:8088/" + OpenApiKey + "/json/SearchConcertDetailService/";


    @BindView(R.id.lv_favorit)
    ListView lv_favorit;
    SearchAdapter searchAdapter;

    ArrayList<Row> rows = new ArrayList<>();
    int isonof = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        bus.register(this);
        unbinder = ButterKnife.bind(this, view);
        LoginService loginService = LoginService.getInstance();

        searchAdapter = new SearchAdapter(rows);
        lv_favorit.setAdapter(searchAdapter);

        //즐겨찾기 버튼을 누르면 서버에 회원정보를 보내 그 회원이 즐겨찾기한 정보를 가져와 리스트뷰에 뿌려준다.
        Call<ArrayList<Favorite>> favoriteCall = RetrofitService.getInstance().getRetrofitRequest().getfavorite(loginService.getLoginMember().getId().toString());
        favoriteCall.enqueue(new Callback<ArrayList<Favorite>>() {
            @Override
            public void onResponse(Call<ArrayList<Favorite>> call, Response<ArrayList<Favorite>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Favorite> favorite = response.body();
                    LoginService loginService = LoginService.getInstance();
                    for (int i = 0; i < favorite.size(); i++) {
                        try {
                            String Url = SearchDetail + 1 + "/" + 1 + "/" + favorite.get(i).getCode() + "/";
                            DetailTask detailTask = new DetailTask();
                            detailTask.execute(Url);
                        } catch (Exception e) {

                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Favorite>> call, Throwable t) {

            }
        });


        return view;
    }

    //리스트 뷰에 뿌려진 데이터를 길게 눌렀을 시 즐겨찾기 삭제 다이얼로그를 띄우며, yes를 누를시 서버에 있는 데이터를 삭제한다.
    @OnItemLongClick(R.id.lv_favorit)
    public boolean onItemLong(AdapterView<?> parent, View view, int position, long id) {
        final Row item = rows.get(position);
        if (isonof == 1) {
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
            isonof = 0;
            builder.show();
            return true;

        } else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("DELETE FAVORITE?");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    LoginService loginService = LoginService.getInstance();
                    Call<Void> favariteCall = RetrofitService.getInstance().getRetrofitRequest().delfa(item.getCULTCODE().toString(), loginService.getLoginMember().getId().toString());
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
            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    dialogInterface.cancel();
                }
            });

            isonof = 1;
            builder.show();
            return true;
        }
    }

    //리스트 뷰에 뿌려진 데이터를 눌렀을 시 DetailActivity를 띄운다.
    @OnItemClick(R.id.lv_favorit)
    public void onItemMainlv(AdapterView<?> parent, View view, int position, long id) {
        Row item = rows.get(position);
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra("Row", item);
        startActivity(intent);
    }

    //서버와 데이터 통신하여 리스트 뷰에 정보를 띄우는 AsyncTask.
    public class DetailTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            String json = Utils.getStringFromServer(strings[0]);
            return json;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                Gson gson = new Gson();
                GetData getData = gson.fromJson(s, GetData.class);
                ArrayList<Row> roro = getData.getSearchConcertDetailService().getRow();
                rows.add(roro.get(0));
                searchAdapter.notifyDataSetChanged();
            } catch (Exception e) {

            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }

}
