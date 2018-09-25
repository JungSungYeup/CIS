package cis.co.kr.ciscultureinseoul.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.otto.Bus;


import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cis.co.kr.ciscultureinseoul.ForumBodeActivity;
import cis.co.kr.ciscultureinseoul.ForumWriteActivity;
import cis.co.kr.ciscultureinseoul.R;
import cis.co.kr.ciscultureinseoul.adapter.ForumAdapter;
import cis.co.kr.ciscultureinseoul.bus.BusProvider;
import cis.co.kr.ciscultureinseoul.data.Board;
import cis.co.kr.ciscultureinseoul.retrofit.RetrofitService;
import cis.co.kr.ciscultureinseoul.service.LoginService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForumFragment extends Fragment {
    private static ForumFragment curr = null;
    public static ForumFragment getInstance() {
        if (curr == null) {
            curr = new ForumFragment();
        }

        return curr;
    }

    Bus bus = BusProvider.getInstance().getBus();
    private Unbinder unbinder;

    ForumAdapter forumAdapter;
    @BindView(R.id.forum_lv)
    ListView forum_lv;
    @BindView(R.id.search2_et)
    EditText search2_et;
    @BindView(R.id.search2_button)
    ImageButton search2_button;
    @BindView(R.id.search2_write)
    Button search2_write;
    @BindView(R.id.hide_nickname)
    TextView hide_nickname;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum, container, false);
        unbinder = ButterKnife.bind(this, view);
        bus.register(this);
        LoginService loginService = LoginService.getInstance();

        getlist();

        //필터링 사용한다고 선언
        forum_lv.setTextFilterEnabled(true);
        search2_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            //Edittext에 입력한내용에대한 필터링 내용 받아오기
            @Override
            public void afterTextChanged(Editable s) {
                forumAdapter.getFilter().filter(s.toString());
            }
        });






        //리스트뷰 클릭시 해당하는 아이템에 정보전달 및 ForumBodeActivity으로 전환
        forum_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Board board = (Board)forumAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), ForumBodeActivity.class);
                intent.putExtra("title",board.getBoard_title());
                intent.putExtra("nickname",board.getBoard_nick());
                intent.putExtra("bode",board.getBoard_contents());
                intent.putExtra("id",board.getId());

                startActivity(intent);
            }
        });



        //게시물 글쓰기 화면으로 전환
        search2_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ForumWriteActivity.class);
                startActivity(intent);
            }
        });


        return view;
    }
    //getlist 갱신
    @Override
    public void onResume() {
        super.onResume();
        getlist();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);

    }

    //Retrofit getBoard()호출후 어뎁터,리스트뷰에 데이터 삽입
    public void getlist(){
        final Call<ArrayList<Board>> AB = RetrofitService.getInstance().getRetrofitRequest().getBoard();
        AB.enqueue(new Callback<ArrayList<Board>>() {
            @Override
            public void onResponse(Call<ArrayList<Board>> call, Response<ArrayList<Board>> response) {
                ArrayList<Board> forums = response.body();
                forumAdapter = new ForumAdapter(forums,forums);
                forum_lv.setAdapter(forumAdapter);
            }

            @Override
            public void onFailure(Call<ArrayList<Board>> call, Throwable t) {

            }
        });
    }

}