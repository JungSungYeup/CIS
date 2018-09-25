package cis.co.kr.ciscultureinseoul.fragment;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.otto.Bus;

import java.util.Calendar;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.Unbinder;
import cis.co.kr.ciscultureinseoul.R;
import cis.co.kr.ciscultureinseoul.adapter.ViewPagerAdapter;
import cis.co.kr.ciscultureinseoul.bus.BusProvider;
import cis.co.kr.ciscultureinseoul.data.GetData;
import cis.co.kr.ciscultureinseoul.data.Row;
import cis.co.kr.ciscultureinseoul.event.BlockWindow;
import cis.co.kr.ciscultureinseoul.event.SearchDateEvent;
import cis.co.kr.ciscultureinseoul.event.SearchEventNum;
import cis.co.kr.ciscultureinseoul.event.SearchGenreEvent;
import cis.co.kr.ciscultureinseoul.event.SearchNameEvent;
import cis.co.kr.ciscultureinseoul.event.SearchPlaceEvent;

public class HomeFragment extends Fragment {
    private static HomeFragment curr = null;

    public static HomeFragment getInstance() {
        if (curr == null) {
            curr = new HomeFragment();
        }

        return curr;
    }


    Bus bus = BusProvider.getInstance().getBus();
    private Unbinder unbinder;

    private String OpenApiKey = "686957747a646f6e36377763715546";
    private String SearchOpenAPI = "http://openapi.seoul.go.kr:8088/" + OpenApiKey + "/json/SearchConcertDetailService/";

    //검색메뉴 부분
    @BindView(R.id.category_TV)
    TextView category_TV;
    @BindView(R.id.category_selector)
    ImageView category_selector;
    @BindView(R.id.layout_category)
    LinearLayout layout_category;

    //검색, 장소 부분
    @BindView(R.id.search_concert_name)
    EditText search_concert_name;
    @BindView(R.id.IB_searchName)
    ImageView IB_searchName;
    @BindView(R.id.layout_category_0)
    LinearLayout layout_category_0;
    // 트루 일땐 검색, 폴스 일땐 장소로 구분.
    Boolean Select;

    //기간 부분
    @BindView(R.id.start_date)
    TextView start_date;
    @BindView(R.id.end_date)
    TextView end_date;
    @BindView(R.id.category1_IB)
    ImageView category1_IB;
    @BindView(R.id.layout_category_1)
    LinearLayout layout_category_1;

    //장르 부분
    @BindView(R.id.category_subcode_name)
    TextView category_subcode_name;
    @BindView(R.id.category_subcode_selector)
    ImageView category_subcode_selector;
    @BindView(R.id.layout_category_2)
    LinearLayout layout_category_2;

    //뷰페이저 부분
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    ViewPagerAdapter viewPagerAdapter;

    //검색메뉴 아이템에 붙이는 번호
    int selectedPos = 0;
    //장르선택 아이템에 붙이는 번호
    int selectedPos2 = 0;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        bus.register(this);
        unbinder = ButterKnife.bind(this, view);

        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewpager.setAdapter(viewPagerAdapter);
        viewPagerAdapter.notifyDataSetChanged();

        //검색 메뉴 선택 버튼을 눌렀을 시 각자에 맞는 이벤트를 포스트로 보낸다.
        if (selectedPos == 0) {
            SearchEventNum searchEventNum = new SearchEventNum(0);
            bus.post(searchEventNum);
        } else if (selectedPos == 1) {
            SearchEventNum searchEventNum = new SearchEventNum(1);
            bus.post(searchEventNum);
        } else if (selectedPos == 2) {
            SearchEventNum searchEventNum = new SearchEventNum(2);
            bus.post(searchEventNum);
        } else {
            SearchEventNum searchEventNum = new SearchEventNum(3);
            bus.post(searchEventNum);
        }

        return view;
    }


    //이름, 장소검색 뷰페이저에 보내는 이벤트
    @OnClick(R.id.IB_searchName)
    public void onSearchPlaceDate(View view) {
        if (search_concert_name.getText().toString().equals("")) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();     //닫기
                }
            });
            alert.setMessage("공백을 포함하지 않는\n검색어를 입력해 주십시오.");
            alert.show();
        } else {
            if (selectedPos == 0) {
                Log.d("hsm", search_concert_name.getText().toString());
                viewpager.setCurrentItem(1);
                String text = search_concert_name.getText().toString();
                SearchNameEvent searchNameEvent = new SearchNameEvent(text);
                bus.post(searchNameEvent);
                BlockWindow blockWindow = new BlockWindow();
                bus.post(blockWindow);

            } else {
                Log.d("hsm", search_concert_name.getText().toString());
                String text = search_concert_name.getText().toString();
                SearchPlaceEvent searchPlaceEvent = new SearchPlaceEvent(text);
                bus.post(searchPlaceEvent);
                BlockWindow blockWindow = new BlockWindow();
                bus.post(blockWindow);
            }
        }

    }



    //기간검색 뷰페이저에 보내는 이벤트
    @OnClick(R.id.category1_IB)
    public void onSearchDate(View view) {
        if (start_date.getText().toString().equals("") || end_date.getText().toString().equals("")) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();     //닫기
                }
            });
            alert.setMessage("기간을 입력하십시오.");
            alert.show();
        } else {
            SearchDateEvent searchDateEvent = new SearchDateEvent(start_date.getText().toString(), end_date.getText().toString());
            bus.post(searchDateEvent);
            BlockWindow blockWindow = new BlockWindow();
            bus.post(blockWindow);
        }
    }

    @OnFocusChange(R.id.search_concert_name)
    public void checkid(View view, boolean hasFocus) {
        viewpager.setCurrentItem(1);
        if (!hasFocus) {

        }
    }

    @OnClick(R.id.search_concert_name)
    public void onClickSearch(View view) {
        viewpager.setCurrentItem(1);
    }

    @OnClick(R.id.layout_category)
    public void onClick(View v) {
        DialogSelectOption();
    }

    @OnClick(R.id.start_date)
    public void onDateClick(View v) {
        DialogDatePicker1();
    }

    @OnClick(R.id.end_date)
    public void onDateClick2(View v) {
        DialogDatePicker2();
    }

    @OnClick(R.id.layout_category_2)
    public void onGenreClick(View v) {
        DialogSelectOption2();
    }

    //검색메뉴 선택 부분
    private void DialogSelectOption() {
        final String items[] = {"검색", "기간", "장르", "장소"};
        try {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setTitle("검색메뉴 선택");
            alertDialog.setSingleChoiceItems(items, selectedPos, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    selectedPos = i;
                }
            });
            alertDialog.setPositiveButton("선택", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (selectedPos == 0) {
                        category_TV.setText("검색");
                        layout_category_0.setVisibility(View.VISIBLE);
                        layout_category_1.setVisibility(View.GONE);
                        layout_category_2.setVisibility(View.GONE);
                        search_concert_name.setText("");
                        viewpager.setCurrentItem(1);
                    } else if (selectedPos == 1) {
                        category_TV.setText("기간");
                        layout_category_0.setVisibility(View.GONE);
                        layout_category_1.setVisibility(View.VISIBLE);
                        layout_category_2.setVisibility(View.GONE);
                        start_date.setText("");
                        end_date.setText("");
                        viewpager.setCurrentItem(1);
                    } else if (selectedPos == 2) {
                        category_TV.setText("장르");
                        layout_category_0.setVisibility(View.GONE);
                        layout_category_1.setVisibility(View.GONE);
                        layout_category_2.setVisibility(View.VISIBLE);
                        category_subcode_name.setText("장르를 선택해 주세요.");
                        viewpager.setCurrentItem(1);
                    } else {
                        category_TV.setText("장소");
                        layout_category_0.setVisibility(View.VISIBLE);
                        layout_category_1.setVisibility(View.GONE);
                        layout_category_2.setVisibility(View.GONE);
                        search_concert_name.setText("");
                        viewpager.setCurrentItem(1);
                    }
                }
            });
            alertDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            alertDialog.show();
        } catch (Exception e) {

        }

    }

    //기간 검색 앞부분
    private void DialogDatePicker1() {
        Calendar c = Calendar.getInstance();
        int cyear = c.get(Calendar.YEAR);
        int cmonth = c.get(Calendar.MONTH);
        int cday = c.get(Calendar.DAY_OF_MONTH);
        try {
            DatePickerDialog.OnDateSetListener mDateSetListener =
                    new DatePickerDialog.OnDateSetListener() {
                        // onDateSet method
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            if (monthOfYear + 1 < 10) {
                                if (dayOfMonth < 10) {
                                    String date_selected = String.valueOf(year) + "0" + String.valueOf(monthOfYear + 1) + "0" + String.valueOf(dayOfMonth);
                                    start_date.setText(date_selected);
                                } else {
                                    String date_selected = String.valueOf(year) + "0" + String.valueOf(monthOfYear + 1) + String.valueOf(dayOfMonth);
                                    start_date.setText(date_selected);
                                }
                            } else {
                                if (dayOfMonth < 10) {
                                    String date_selected = String.valueOf(year) + String.valueOf(monthOfYear + 1) + "0" + String.valueOf(dayOfMonth);
                                    start_date.setText(date_selected);
                                } else {
                                    String date_selected = String.valueOf(year) + String.valueOf(monthOfYear + 1) + String.valueOf(dayOfMonth);
                                    start_date.setText(date_selected);
                                }

                            }
                        }
                    };
            DatePickerDialog alert = new DatePickerDialog(getActivity(), mDateSetListener,
                    cyear, cmonth, cday);
            alert.show();
        } catch (Exception e) {

        }

    }

    //기간 검색 뒷부분
    private void DialogDatePicker2() {
        Calendar c = Calendar.getInstance();
        int cyear = c.get(Calendar.YEAR);
        int cmonth = c.get(Calendar.MONTH);
        int cday = c.get(Calendar.DAY_OF_MONTH);
        try {
            DatePickerDialog.OnDateSetListener mDateSetListener =
                    new DatePickerDialog.OnDateSetListener() {
                        // onDateSet method
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            if (monthOfYear + 1 < 10) {
                                if (dayOfMonth < 10) {
                                    String date_selected = String.valueOf(year) + "0" + String.valueOf(monthOfYear + 1) + "0" + String.valueOf(dayOfMonth);
                                    end_date.setText(date_selected);
                                } else {
                                    String date_selected = String.valueOf(year) + "0" + String.valueOf(monthOfYear + 1) + String.valueOf(dayOfMonth);
                                    end_date.setText(date_selected);
                                }
                            } else {
                                if (dayOfMonth < 10) {
                                    String date_selected = String.valueOf(year) + String.valueOf(monthOfYear + 1) + "0" + String.valueOf(dayOfMonth);
                                    end_date.setText(date_selected);
                                } else {
                                    String date_selected = String.valueOf(year) + String.valueOf(monthOfYear + 1) + String.valueOf(dayOfMonth);
                                    end_date.setText(date_selected);
                                }

                            }
                        }
                    };
            DatePickerDialog alert = new DatePickerDialog(getActivity(), mDateSetListener,
                    cyear, cmonth, cday);
            alert.show();
        } catch (Exception e) {

        }

    }

    //장르 선택 부분
    private void DialogSelectOption2() {

        final String items[] = {"영화", "콘서트", "클래식", "뮤지컬/오페라", "연극",
                "무용", "전시/미술", "기타", "국악", "축제-기타", "독주/독창회", "문화교양/강좌"
                , "축제-문화·예술", "축제-자연·경관", "축제-전통·역사", "축제-시민화합"};

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("장르선택");
        alertDialog.setSingleChoiceItems(items, selectedPos2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selectedPos2 = i;
            }
        });
        alertDialog.setPositiveButton("선택", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SearchGenreEvent searchGenreEvent;
                switch (selectedPos2) {
                    case 0:
                        category_subcode_name.setText(items[0]);
                        searchGenreEvent = new SearchGenreEvent("18");
                        bus.post(searchGenreEvent);
                        break;
                    case 1:
                        category_subcode_name.setText(items[1]);
                        searchGenreEvent = new SearchGenreEvent("1");
                        bus.post(searchGenreEvent);
                        break;
                    case 2:
                        category_subcode_name.setText(items[2]);
                        searchGenreEvent = new SearchGenreEvent("2");
                        bus.post(searchGenreEvent);
                        break;
                    case 3:
                        category_subcode_name.setText(items[3]);
                        searchGenreEvent = new SearchGenreEvent("3");
                        bus.post(searchGenreEvent);
                        break;
                    case 4:
                        category_subcode_name.setText(items[4]);
                        searchGenreEvent = new SearchGenreEvent("5");
                        bus.post(searchGenreEvent);
                        break;
                    case 5:
                        category_subcode_name.setText(items[5]);
                        searchGenreEvent = new SearchGenreEvent("6");
                        bus.post(searchGenreEvent);
                        break;
                    case 6:
                        category_subcode_name.setText(items[6]);
                        searchGenreEvent = new SearchGenreEvent("7");
                        bus.post(searchGenreEvent);
                        break;
                    case 7:
                        category_subcode_name.setText(items[7]);
                        searchGenreEvent = new SearchGenreEvent("10");
                        bus.post(searchGenreEvent);
                        break;
                    case 8:
                        category_subcode_name.setText(items[8]);
                        searchGenreEvent = new SearchGenreEvent("11");
                        bus.post(searchGenreEvent);
                        break;
                    case 9:
                        category_subcode_name.setText(items[9]);
                        searchGenreEvent = new SearchGenreEvent("12");
                        bus.post(searchGenreEvent);
                        break;
                    case 10:
                        category_subcode_name.setText(items[10]);
                        searchGenreEvent = new SearchGenreEvent("17");
                        bus.post(searchGenreEvent);
                        break;
                    case 11:
                        category_subcode_name.setText(items[11]);
                        searchGenreEvent = new SearchGenreEvent("19");
                        bus.post(searchGenreEvent);
                        break;
                    case 12:
                        category_subcode_name.setText(items[12]);
                        searchGenreEvent = new SearchGenreEvent("20");
                        bus.post(searchGenreEvent);
                        break;
                    case 13:
                        category_subcode_name.setText(items[13]);
                        searchGenreEvent = new SearchGenreEvent("21");
                        bus.post(searchGenreEvent);
                        break;
                    case 14:
                        category_subcode_name.setText(items[14]);
                        searchGenreEvent = new SearchGenreEvent("22");
                        bus.post(searchGenreEvent);
                        break;
                    case 15:
                        category_subcode_name.setText(items[15]);
                        searchGenreEvent = new SearchGenreEvent("23");
                        bus.post(searchGenreEvent);
                        break;

                }
                BlockWindow blockWindow = new BlockWindow();
                bus.post(blockWindow);

            }
        });
        alertDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.show();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
