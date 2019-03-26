package app.yoojin.org.vet_project2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// public class VetDataAdapter extends RecyclerView.Adapter<VetDataAdapter.ViewHolder> {
public class ReviewCommentAdapter extends RecyclerView.Adapter {
    private List<ReviewVO> list;

    private Context context;

    public ReviewCommentAdapter(List<ReviewVO> list) {
        super();
        this.list = list;
    }

    // 뷰를 넘겨준다
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_comment_row, viewGroup, false);
        return new ItemViewHolder(view);
    }

    // 뷰를 세팅한다
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ItemViewHolder itemHolder = (ItemViewHolder) holder;
        itemHolder.cmtcontent.setText(list.get(position).getCmt_content());
        itemHolder.cmtdate.setText(list.get(position).getCmt_reg_date());
        // 댓글 삭제 기능 (예/아니오 확인창)
        itemHolder.cmtdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert
                        .setMessage("댓글을 삭제하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("예", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Call<String> call = RetrofitInit.getInstance().getService().deleteComment(list.get(position).getCmt_id());
                                call.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        Log.d("응답", response.body());
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        Log.d("댓글 삭제 오류", t.getMessage());
                                    }
                                });
                                list.remove(position);
                                notifyDataSetChanged();
                            }

                        }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return ;
                    }
                });
                AlertDialog alertDialog = alert.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        if(list==null){
            return 0;
        }
        return list.size();
    }

    // 아이템 뷰홀더
    public class ItemViewHolder extends  RecyclerView.ViewHolder{
        private TextView cmtcontent, cmtdate;
        private Button cmtdelete;

        public ItemViewHolder(View view){
            super(view);
            cmtcontent = view.findViewById(R.id.cmtcontent);
            cmtdate = view.findViewById(R.id.cmtdate);
            cmtdelete = view.findViewById(R.id.cmtdelete);
        }
    }

}
