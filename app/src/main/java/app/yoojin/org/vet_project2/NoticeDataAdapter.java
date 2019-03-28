package app.yoojin.org.vet_project2;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

// public class VetDataAdapter extends RecyclerView.Adapter<VetDataAdapter.ViewHolder> {
public class NoticeDataAdapter extends RecyclerView.Adapter {
    private List<NoticeVO> list;
    private ArrayList<NoticeVO> listFiltered;

    private Context context;

    public NoticeDataAdapter(List<NoticeVO> list) {
        super();
        this.list = list;
        this.listFiltered = new ArrayList<>();
        this.listFiltered.addAll(list);
    }

    // 알맞은 뷰를 넘겨준다
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_notice_row, viewGroup, false);
        return new ItemViewHolder(view);
    }

    // 뷰를 세팅한다
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ItemViewHolder itemHolder = (ItemViewHolder) holder;
        itemHolder.title.setText(list.get(position).getTitle());
        itemHolder.date.setText(list.get(position).getRegDate());

        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();

                Intent intent = new Intent(v.getContext(),NoticeDetail.class);
                intent.putExtra("id",list.get(position).getId());
                intent.putExtra("title",list.get(position).getTitle());
                intent.putExtra("content",list.get(position).getContent());
                intent.putExtra("reg_date",list.get(position).getRegDate());
                intent.putExtra("image",list.get(position).getImage());

                v.getContext().startActivity(intent);
            }
        });
    }

    // 마지막에 푸터 추가 위해 +1
    @Override
    public int getItemCount() {
        return list.size();
    }

    // 아이템 뷰홀더
    public class ItemViewHolder extends  RecyclerView.ViewHolder{
        private TextView title, date;

        public ItemViewHolder(View view){
            super(view);
            title = view.findViewById(R.id.nttitle);
            date = view.findViewById(R.id.ntdate);
        }
    }

    // 결과 내 검색 필터
    public void filter(String query){
        query = query.toLowerCase();
        list.clear();
        if(query.length()==0){
            list.addAll(listFiltered);
        } else{
            for(NoticeVO vo : listFiltered){
                if(vo.getTitle().toLowerCase().contains(query)){
                    list.add(vo);
                }
            }
        }
        notifyDataSetChanged(); // 데이터셋이 변경된 걸 알려줘서 리사이클러뷰의 데이터 갱신함
    }
}
