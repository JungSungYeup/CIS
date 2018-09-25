package cis.co.kr.ciscultureinseoul.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cis.co.kr.ciscultureinseoul.R;
import cis.co.kr.ciscultureinseoul.data.Row;
import cis.co.kr.ciscultureinseoul.module.GlideApp;
import cis.co.kr.ciscultureinseoul.retrofit.RetrofitService;
import cis.co.kr.ciscultureinseoul.service.LoginService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchAdapter extends BaseAdapter {
    ArrayList<Row> items;

    public SearchAdapter(ArrayList<Row> items) {
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list,parent,false);
            holder.item_img = convertView.findViewById(R.id.item_img);
            holder.item_title = convertView.findViewById(R.id.item_title);
            holder.item_data = convertView.findViewById(R.id.item_data);
            holder.item_place = convertView.findViewById(R.id.item_place);
            holder.book_marker = convertView.findViewById(R.id.book_marker);
            holder.item_code = convertView.findViewById(R.id.item_code);
            convertView.setTag(holder);
        }else {
            holder = (Holder)convertView.getTag();
        }

        final Row item = (Row)items.get(position);
        holder.item_title.setText(item.getTITLE());
        holder.item_data.setText(item.getSTRTDATE()+ "~"+item.getEND_DATE());
        holder.item_place.setText(item.getPLACE());
        holder.item_code.setText(item.getGCODE());
        String url = "";

            String[] mainImg = item.getMAIN_IMG().split("\\/");

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

            GlideApp.with(parent.getContext())
                    .load(url)
                    .error(R.drawable.bg_smallimg)
                    .centerCrop()
                    .into(holder.item_img);

        LoginService loginService = LoginService.getInstance();
        //리스트 뷰에 즐겨찾기 정보가 있을 시 아이콘 표시.
        Call<Integer> integerCall = RetrofitService.getInstance().getRetrofitRequest().fafa(items.get(position).getCULTCODE().toString(),loginService.getLoginMember().getId().toString());
        final Holder finalHolder = holder;
        integerCall.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.isSuccessful()) {
                    Integer integer = response.body();
                    if(integer == 0) {
                        finalHolder.book_marker.setVisibility(View.GONE);
                    } else {
                        finalHolder.book_marker.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });


        return convertView;
    }
    public class Holder{
        ImageView item_img;
        TextView item_title;
        TextView item_data;
        TextView item_place;
        TextView item_code;
        ImageView book_marker;
    }
}