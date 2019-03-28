package app.yoojin.org.vet_project2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

// public class VetDataAdapter extends RecyclerView.Adapter<VetDataAdapter.ViewHolder> {
public class ReviewDataAdapterThree extends RecyclerView.Adapter {
    private List<ReviewVO> list;

    private Context context;

    public ReviewDataAdapterThree(List<ReviewVO> list) {
        super();
        this.list = list;
    }

    // 뷰를 넘겨준다
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_review_row_three, viewGroup, false);
        return new ItemViewHolder(view);
    }

    // 뷰를 세팅한다
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ItemViewHolder itemHolder = (ItemViewHolder) holder;
        itemHolder.rvtitle.setText(list.get(position).getRv_title());
        itemHolder.rvdate.setText(list.get(position).getRv_reg_date());
        itemHolder.rvcon.setText(list.get(position).getRv_content());

        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Bundle bundle = ((Activity) context).getIntent().getExtras();   // 어댑터에서 intent값 받아오기

                Intent intent = new Intent(v.getContext(),ReviewDetail.class);
                intent.putExtra("hpt_id",list.get(position).getHpt_id());
                intent.putExtra("hpt_name",bundle.getString("hpt_name"));
                intent.putExtra("rv_id",String.format("%d", list.get(position).getRv_id()));

                v.getContext().startActivity(intent);
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
        private TextView rvtitle, rvdate, rvcon;

        public ItemViewHolder(View view){
            super(view);
            rvtitle = view.findViewById(R.id.rvtitle);
            rvdate = view.findViewById(R.id.rvdate);
            rvcon = view.findViewById(R.id.rvcon);
        }
    }

}
