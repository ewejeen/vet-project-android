package app.yoojin.org.vet_project2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

// public class VetDataAdapter extends RecyclerView.Adapter<VetDataAdapter.ViewHolder> {
public class ReviewDataAdapter extends RecyclerView.Adapter {
    private List<ReviewVO> list;
    private ArrayList<ReviewVO> listFiltered;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    private Context context;

    public ReviewDataAdapter(List<ReviewVO> list) {
        super();
        this.list = list;
        this.listFiltered = new ArrayList<>();
        this.listFiltered.addAll(list);
    }

    // 뷰타입 정하기
    @Override
    public int getItemViewType(int position) {
        if(isPositionItem(position)) {
            // 푸터 추가
            return TYPE_ITEM;
        }
        return TYPE_FOOTER;
    }

    private boolean isPositionItem(int position){
        return position != getItemCount()-1; // 마지막이 아니면 true
    }

    // 알맞은 뷰를 넘겨준다
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        if(viewType == TYPE_FOOTER){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_vet_row_footer, viewGroup, false);
            return new FooterViewHolder(view);
        } else if(viewType == TYPE_ITEM){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_review_row, viewGroup, false);
            return new ItemViewHolder(view);
        }
        return null;
    }

    // 뷰를 세팅한다
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof ItemViewHolder){ // viewType == item
            final ItemViewHolder itemHolder = (ItemViewHolder) holder;

            itemHolder.rvtitle.setText(list.get(position).getRv_title());
            itemHolder.rvdate.setText(list.get(position).getRv_reg_date());

            float rating = (float) list.get(position).getHpt_rate();
            itemHolder.ratingBar.setRating(rating);
            String ratingRound = String.format("%.2f", rating);
            itemHolder.rating.setText(ratingRound);

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

        } else if(holder instanceof FooterViewHolder){
            FooterViewHolder footerHolder = (FooterViewHolder) holder;
            String totalStr = String.format("%d",list.size());
            footerHolder.res_total.setText("총 "+totalStr+"건");
        }

    }

    // 마지막에 푸터 추가 위해 +1
    @Override
    public int getItemCount() {
        if(list==null){
            return 0;
        }
        return list.size()+1;
    }

    // 기본 아이템 뷰홀더
    public class ItemViewHolder extends  RecyclerView.ViewHolder{
        private TextView rvtitle, rating, rvdate;
        private RatingBar ratingBar;

        public ItemViewHolder(View view){
            super(view);
            rvtitle = view.findViewById(R.id.rvtitle);
            rating = view.findViewById(R.id.rating);
            rvdate = view.findViewById(R.id.rvdate);
            ratingBar = view.findViewById(R.id.ratingBar);
            rating = view.findViewById(R.id.rating);
        }
    }

    // 푸터 뷰홀더
    public class FooterViewHolder extends RecyclerView.ViewHolder {
        private TextView res_total;

        public FooterViewHolder(View view){
            super(view);
            res_total = view.findViewById(R.id.res_total);
        }
    }

    // 결과 내 검색 필터
    public void filter(String query){
        query = query.toLowerCase();
        list.clear();
        if(query.length()==0){
            list.addAll(listFiltered);
        } else{
            for(ReviewVO vo : listFiltered){
                if(vo.getRv_title().toLowerCase().contains(query)){
                    list.add(vo);
                }
            }
        }
        notifyDataSetChanged(); // 데이터셋이 변경된 걸 알려줘서 리사이클러뷰의 데이터 갱신함
    }
}
