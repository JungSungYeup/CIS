package cis.co.kr.ciscultureinseoul.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cis.co.kr.ciscultureinseoul.R;
import cis.co.kr.ciscultureinseoul.data.Board;
import cis.co.kr.ciscultureinseoul.retrofit.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForumAdapter extends BaseAdapter implements Filterable{
    private ArrayList<Board> forums = new ArrayList<Board>();   //전체 정보를 담은 ArrayList
    private ArrayList<Board> filteredItemList = forums;       //필터링된 정보를 입력한 ArrayList


    public ForumAdapter(ArrayList<Board> forums, ArrayList<Board> filteredItemList) {
        this.forums = forums;
        this.filteredItemList = filteredItemList;
    }
    //필터링된 갯수 리턴
    @Override
    public int getCount() {
        return filteredItemList.size();
    }
    //필터링된 아이템정보 리턴
    @Override
    public Object getItem(int position) {
        return filteredItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //어뎁터의 정보를 아이템의 포지션에 맞게 삽입
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        if (convertView==null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forum,parent,false);
            holder.if_title = convertView.findViewById(R.id.if_title);
            holder.if_nick = convertView.findViewById(R.id.if_nick);
            holder.if_num = convertView.findViewById(R.id.if_num);
            holder.if_iv = convertView.findViewById(R.id.if_iv);

            convertView.setTag(holder);
        }else {
            holder = (Holder)convertView.getTag();
        }

        Board item = (Board)filteredItemList.get(position);
        holder.if_title.setText(item.getBoard_title());
        holder.if_nick.setText(item.getBoard_nick());
        holder.if_num.setText(item.getComments_count().toString());


        //리스트 뷰에 이미지 정보가 있을 시 아이콘 표시.
        Call<Integer> integerCall = RetrofitService.getInstance().getRetrofitRequest().getimagenum(item.getId().toString());
        final Holder finalHolder = holder;
        integerCall.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.isSuccessful()) {
                    Integer asd = response.body();
                    if(asd == 0) {
                        finalHolder.if_iv.setVisibility(View.INVISIBLE);
                    } else {
                        finalHolder.if_iv.setVisibility(View.VISIBLE);
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
        TextView if_title;
        TextView if_nick;
        TextView if_num;
        ImageView if_iv;
    }

    //필터링 내용
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            //필터링된 정보 ArrayList에 삽입하고 갱신
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredItemList = (ArrayList<Board>) results.values;
                notifyDataSetChanged();
            }
            //필터링 조건
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<Board> FilteredArrList = new ArrayList<>();
                //작성 내용이없을경우
                if ((constraint == null || constraint.length() == 0)) {
                    results.values = forums;
                    results.count = forums.size();
                } else {
                    //작성내용이있을경우
                    for (int i = 0; i < forums.size(); i++) {
                        Board data = forums.get(i);
                        if (data.getBoard_title().toUpperCase().contains(constraint.toString().toUpperCase())) {
                            FilteredArrList.add(data);
                        }
                    }
                    results.values = FilteredArrList;
                    results.count = FilteredArrList.size();
                }
                return results;
            }
        };
        return filter;
    }
}
