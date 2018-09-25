package cis.co.kr.ciscultureinseoul.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cis.co.kr.ciscultureinseoul.DetailActivity;
import cis.co.kr.ciscultureinseoul.R;
import cis.co.kr.ciscultureinseoul.data.Row;
import cis.co.kr.ciscultureinseoul.module.GlideApp;


public class AutoScrollAdapter extends PagerAdapter {

    Context context;
    ArrayList<Row> items;

    public AutoScrollAdapter (Context context,ArrayList<Row> items) {
        this.context = context;
        this.items = items;
    }

    //instantiateItem(ViewGroup, int) : position 값을 받아 주어진 위치에 페이지를 생성합니다.
    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        //뷰페이지 슬라이딩 할 레이아웃 인플레이션
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.item_banner,container,false);

        ImageView item_img = (ImageView) v.findViewById(R.id.item_img);
        TextView item_title = (TextView) v.findViewById(R.id.item_title);
        TextView item_data = (TextView) v.findViewById(R.id.item_data);
        TextView item_code = (TextView) v.findViewById(R.id.item_code);
        TextView item_place = (TextView) v.findViewById(R.id.item_place);
        RelativeLayout onclick_banner = (RelativeLayout) v.findViewById(R.id.onclick_banner);

        final Row item = (Row)items.get(position);
        item_title.setText(item.getTITLE());
        item_data.setText(item.getSTRTDATE()+ "~"+item.getEND_DATE());
        item_code.setText(item.getGCODE());
        item_place.setText(item.getPLACE());
        String url = "";

        String[] mainImg = item.getMAIN_IMG().split("\\/");

        for(int i = 0; i < mainImg.length; ++i) {
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

            GlideApp.with(context)
                    .load(url)
                    .error(R.drawable.bg_smallimg)
                    .centerCrop()
                    .into(item_img);

        onclick_banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,DetailActivity.class);
                intent.putExtra("Row", item);
                context.startActivity(intent);
            }
        });


        container.addView(v);
        return v;
    }


    //destroyItem(ViewGroup, int, Object) : position 값을 받아 주어진 위치에 있는 페이지를 삭제 합니다.
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((View)object);
    }

    //getCount() : 사용 가능한 뷰의 개수를 return 시킵니다.
    @Override
    public int getCount() {
        return items.size();
    }

    //isViewFromObject(View, Object) : 페이지 뷰가 생성된 페이지의 object key와 같은지 확인합니다.
    //object key는 instantiateItem 메소드에서 리턴 시킨 object 입니다. 즉, 페이지의 뷰가 생성된 뷰인지 아닌지를 확인합니다.
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }




}
