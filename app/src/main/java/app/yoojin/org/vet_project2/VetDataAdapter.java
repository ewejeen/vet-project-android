package app.yoojin.org.vet_project2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Rating;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

// public class VetDataAdapter extends RecyclerView.Adapter<VetDataAdapter.ViewHolder> {
public class VetDataAdapter extends RecyclerView.Adapter {
    private List<VetVO> list;
    private ArrayList<VetVO> listFiltered;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    public VetDataAdapter(List<VetVO> list) {
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
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_vet_row, viewGroup, false);
            return new ItemViewHolder(view);
        }
        return null;
    }

    // 뷰를 세팅한다
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof ItemViewHolder){ // viewType == item
            final ItemViewHolder itemHolder = (ItemViewHolder) holder;
            itemHolder.hpt_name.setText(list.get(position).getHpt_name());
            itemHolder.hpt_name.setSelected(true);  // 상호명이 길어도 한 줄에 표시하고 자동으로 흐르게 하기 위함
            itemHolder.adrs.setText(list.get(position).getAddress());
            itemHolder.review_cnt.setText(list.get(position).getReviewCnt());
            float rating = Float.valueOf(list.get(position).getRateAvg());
            itemHolder.ratingBar.setRating(rating);
            String ratingRound = String.format("%.2f", rating);
            itemHolder.rating.setText(ratingRound);
            itemHolder.hitCnt.setText(String.format("%d",list.get(position).getHpt_hit()));

            // 거리 정보 distance 텍스트뷰에 표시
            final Double distanceVal = Math.round(list.get(position).getDistance() * 100) / 100.0;
            final Double distanceValM = (Math.round(list.get(position).getDistance() * 1000) / 1000.0) * 1000;
            if(distanceVal < 1){    // 1km 미만이면 m로 표시
                itemHolder.distance.setText(String.format("%d", distanceValM.intValue()) + "m");
            } else{
                itemHolder.distance.setText(String.format("%.2f", distanceVal)+"km");
            }

            final int hpt_id = list.get(position).getHpt_id();
            final String hpt_name = list.get(position).getHpt_name();

            itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(v.getContext(),VetDetail.class);
                    intent.putExtra("hpt_id",list.get(position).getHpt_id());
                    intent.putExtra("hpt_name",list.get(position).getHpt_name());
                    intent.putExtra("address", list.get(position).getAddress());
                    intent.putExtra("hpt_phone", list.get(position).getHpt_phone());
                    if(distanceVal < 1){    // 1km 미만이면 m로 표시
                        intent.putExtra("distance", String.format("%d", distanceValM.intValue()) + "m");
                    } else{
                        intent.putExtra("distance", String.format("%.2f", distanceVal)+"km");
                    }
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
        private TextView hpt_name, adrs, review_cnt, rating, hitCnt, distance;
        private RatingBar ratingBar;

        public ItemViewHolder(View view){
            super(view);
            hpt_name = view.findViewById(R.id.hpt_name);
            adrs = view.findViewById(R.id.adrs);
            review_cnt = view.findViewById(R.id.review_cnt);
            ratingBar = view.findViewById(R.id.ratingBar);
            rating = view.findViewById(R.id.rating);
            hitCnt = view.findViewById(R.id.hitCnt);
            distance = view.findViewById(R.id.distance);
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

    // 검색 내 검색 필터링


    /*@Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if(charString.isEmpty()){
                    listFiltered = list;
                } else{
                    List<VetVO> filteredList = new ArrayList<>();
                    for(VetVO vo : list){
                        // 검색 조건
                        if(vo.getHpt_name().contains(charString) || vo.getAddress().contains(charString)){
                            filteredList.add(vo);
                            Log.d("이름",vo.getHpt_name());
                            Log.d("주소",vo.getAddress());
                        }
                    }

                    listFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = listFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                listFiltered = (List<VetVO>) results.values;
                notifyDataSetChanged();
            }
        };
    }*/

    // 결과 내 검색 필터
    public void filter(String query){
        query = query.toLowerCase();
        Log.d("q",query);
        list.clear();
        if(query.length()==0){
            list.addAll(listFiltered);
        } else{
            for(VetVO vo : listFiltered){
                if(vo.getHpt_name().toLowerCase().contains(query) || vo.getAdrs_new().toLowerCase().contains(query) || vo.getAdrs_old().toLowerCase().contains(query)){
                    list.add(vo);
                }
            }
        }
        notifyDataSetChanged(); // 데이터셋이 변경된 걸 알려줘서 리사이클러뷰의 데이터 갱신함
    }
}
