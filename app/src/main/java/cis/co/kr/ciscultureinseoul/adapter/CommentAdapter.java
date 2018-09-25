package cis.co.kr.ciscultureinseoul.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import cis.co.kr.ciscultureinseoul.R;
import cis.co.kr.ciscultureinseoul.data.Comments;
import cis.co.kr.ciscultureinseoul.retrofit.RetrofitService;
import cis.co.kr.ciscultureinseoul.service.LoginService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentAdapter extends BaseAdapter{
    ArrayList<Comments>com;   //댓글 정보
    String Board_id;        //댓글의 보드 아이디



    public CommentAdapter(ArrayList<Comments> com) {
        this.com = com;
    }

    @Override
    public int getCount() {
        return com.size();
    }

    @Override
    public Object getItem(int position) {
        return com.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    //item_comment의 포지션에 해당하는 값에 닉네임, 댓글, 작성시간 데이터 삽입
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        Holder holder = new Holder();
        if(convertView==null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment,parent,false);
            holder.ic_bode = convertView.findViewById(R.id.ic_bode);
            holder.ic_nick = convertView.findViewById(R.id.ic_nick);
            holder.ic_clock = convertView.findViewById(R.id.ic_clock);
            holder.ic_delete = convertView.findViewById(R.id.ic_delete);



            convertView.setTag(holder);
        }else {
            holder= (Holder)convertView.getTag();
        }
        final LoginService loginService = LoginService.getInstance();
        final Comments item = (Comments)com.get(position);

        Board_id = item.getBoard_id().toString();
        holder.ic_bode.setText(item.getComments_contents());
        holder.ic_nick.setText(item.getComments_nick());
        holder.ic_clock.setText(item.getComments_time());

        //실행하는 사람의 닉네임과 댓글작성자의 닉네임을 비교해서 동일하면 삭제버튼을 출력하고, 다르다면 삭제버튼이 보이지않는다.
        Call<Integer> integerCall = RetrofitService.getInstance().getRetrofitRequest().deleteVisible(loginService.getLoginMember().getMember_nickname(),com.get(position).getComments_nick());
        final Holder Nholder = holder;
        integerCall.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(final Call<Integer> call, final Response<Integer> response) {
                Integer integer = response.body();

                if(integer == 0){
                    Nholder.ic_delete.setVisibility(View.GONE);
                }else if(integer==1){
                    Nholder.ic_delete.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });
        //삭제 버튼을 눌렇을 경우 다이얼로그로 경고창을 보여주고 확인했을경우 댓글을 삭제
        holder.ic_delete.setFocusable(false);
        holder.ic_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(parent.getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("삭제")
                        .setMessage("정말 댓글을 삭제하시겠습니까?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Call<Void>voidCall = RetrofitService.getInstance().getRetrofitRequest().deleteComments(com.get(position).getId().toString(),item.getBoard_id().toString());
                                voidCall.enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        if(response.isSuccessful()){
                                            Void aVoid = response.body();
                                            com.remove(position);
                                            notifyDataSetChanged();
                                        } else {
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        t.printStackTrace();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        return convertView;
    }

    public class Holder{
        TextView ic_bode;
        TextView ic_nick;
        TextView ic_clock;
        ImageButton ic_delete;
    }
}
