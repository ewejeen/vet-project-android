package app.yoojin.org.vet_project2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewDetail extends AppCompatActivity {

    private Toolbar topToolbar;
    private TextView rv_title, rv_rate, rv_reg_date, hpt_name, visit_date, visit_is_new, pet_type, rv_content;
    private int rv_id, hpt_id;
    private ImageView rv_image;
    private RatingBar ratingBar;
    private Button deleteBtn, updateBtn, cmtReg;
    private String hpt_nameT, imageUriToSend;
    private EditText cmtCon;
    private RecyclerView cmt_recycler;
    private List<ReviewVO> data;
    private ReviewCommentAdapter adapter;
    private InputMethodManager inputMethodManager;  // 키보드 숨기기

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_detail);

        rv_title = findViewById(R.id.textView6);
        rv_rate = findViewById(R.id.textView9);
        rv_reg_date = findViewById(R.id.textView14);
        hpt_name = findViewById(R.id.textView25);
        visit_date = findViewById(R.id.textView16);
        visit_is_new = findViewById(R.id.textView17);
        pet_type = findViewById(R.id.textView19);
        rv_image = findViewById(R.id.imageView);
        rv_content = findViewById(R.id.textView20);
        ratingBar = findViewById(R.id.ratingBar4);

        rv_image = findViewById(R.id.imageView);

        deleteBtn = findViewById(R.id.button5);
        updateBtn = findViewById(R.id.button3);

        // 댓글 등록 완료 시 키보드 숨기기
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // ReviewWrite, ReviewList에서 받아온 인텐트 정보들
        Bundle intent = getIntent().getExtras();
        rv_id = Integer.parseInt(intent.getString("rv_id"));
        hpt_nameT = intent.getString("hpt_name");
        hpt_id = intent.getInt("hpt_id");
        Log.d("병원 id",""+hpt_id);

        hpt_name.setText(hpt_nameT);

        fetchDetail();  // 레트로핏으로 정보 불러오기

        // 삭제 기능
        deleteBtn.setOnClickListener(deleteFn);
        // 수정 기능
        updateBtn.setOnClickListener(updateFn);

        // 댓글 목록 불러오기
        initRecycler();

        cmtCon = findViewById(R.id.cmtCon);
        cmtReg = findViewById(R.id.cmtReg);
        // 댓글 등록 기능
        cmtReg.setOnClickListener(cmtRegFn);

        // Top Navigation
        topToolbar = findViewById(R.id.topToolbarSub);
        setSupportActionBar(topToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // 왼쪽 버튼 사용 여부 true
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);  // 왼쪽 버튼 이미지 설정
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Bottom Navigation
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    // Bottom Navigation 리스너
    public BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                // 후기 목록 페이지로 이동
                case R.id.navigation_rvdetail:
                    Bundle intent = getIntent().getExtras();   // 받아올 인텐트
                    Intent listIntent = new Intent(ReviewDetail.this, ReviewList.class); // 후기 목록 화면 가는 인텐트
                    listIntent.putExtra("hpt_id", hpt_id);
                    listIntent.putExtra("hpt_name",hpt_nameT);

                    startActivity(listIntent);
                    return true;
            }
            return false;
        }
    };

    // Top Navigation에 top_navigation.xml을 집어넣는다
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.top_navigation, menu);
        return true;
    }

    // Top Navigation에 삽입된 메뉴에 대해서 이벤트 처리
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_bar_search:
                return true;
            case R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 상세 정보 불러오기 retrofit
    private void fetchDetail(){
        Call<List<ReviewVO>> call = RetrofitInit.getInstance().getService().getReviewDetail(rv_id);
        call.enqueue(new Callback<List<ReviewVO>>() {
            @Override
            public void onResponse(Call<List<ReviewVO>> call, Response<List<ReviewVO>> response) {
                ReviewVO vo = response.body().get(0);
                rv_title.setText(vo.getRv_title());
                rv_rate.setText(String.format("%.2f",vo.getHpt_rate()));
                rv_reg_date.setText(vo.getRv_reg_date());

                // 방문일 날짜 형식 바꾸기 위함
                String[] dateArr = vo.getVisit_date().split("-");
                String date = dateArr[0]+"년 "+dateArr[1]+"월 "+dateArr[2]+"일";

                visit_date.setText(date);

                // 이미지 나타내기
                Uri uri = Uri.parse(RetrofitInit.getImageURL()+vo.getRv_image());
                Glide
                        .with(getApplicationContext())
                        .load(uri)
                        .error(R.drawable.no_image)
                        .into(rv_image);
                imageUriToSend = uri.toString();

                // 재방문 or 첫방문
                if(vo.getVisit_is_new()==0){
                    visit_is_new.setText("첫 방문");
                } else if(vo.getVisit_is_new()==1){
                    visit_is_new.setText("재방문");
                }

                pet_type.setText(vo.getPet_type());
                rv_content.setText(vo.getRv_content());
                ratingBar.setRating(Float.parseFloat(String.format("%.2f",vo.getHpt_rate())));
            }

            @Override
            public void onFailure(Call<List<ReviewVO>> call, Throwable t) {
                Log.d("Retrofit 에러",t.getMessage());
            }
        });
    }

    // 삭제 기능 (예/아니오 확인창)
    Button.OnClickListener deleteFn = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            AlertDialog.Builder alert = new AlertDialog.Builder(ReviewDetail.this);
            alert
                    .setMessage("후기를 삭제하시겠습니까?")
                    .setCancelable(false)
                    .setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Call<String> deleteCall = RetrofitInit.getInstance().getService().deleteReivew(rv_id);
                            deleteCall.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    String data = response.body();
                                    Log.d("삭제 결과",data);
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Log.d("삭제 레트로핏 오류", t.getMessage());
                                }
                            });
                            Intent listIntent = new Intent(getApplicationContext(), ReviewList.class);  // 목록으로 돌아감
                            listIntent.putExtra("hpt_id", hpt_id);
                            listIntent.putExtra("hpt_name", hpt_nameT);
                            startActivity(listIntent);
                            finish();
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
    };

    // 수정 기능
    Button.OnClickListener updateFn = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent updateIntent = new Intent(getApplicationContext(), ReviewUpdate.class);
            // 병원명, 방문일, 평점, 반려동물 종류, 방문여부, 제목, 내용 넘겨주기: 다 text임!!
            updateIntent.putExtra("hpt_name", hpt_name.getText().toString());

            String[] str = visit_date.getText().toString().split(" ");
            String date = str[0].substring(0, str[0].length()-1)+"-"+str[1].substring(0, str[1].length()-1)+"-"+str[2].substring(0, str[2].length()-1);
            updateIntent.putExtra("visit_date", date);
            updateIntent.putExtra("rv_rate", rv_rate.getText().toString());
            updateIntent.putExtra("pet_type", pet_type.getText().toString());
            updateIntent.putExtra("visit_is_new", visit_is_new.getText().toString());
            updateIntent.putExtra("rv_title", rv_title.getText().toString());
            updateIntent.putExtra("rv_content", rv_content.getText().toString());
            updateIntent.putExtra("rv_id",rv_id);
            updateIntent.putExtra("hpt_id",hpt_id);
            updateIntent.putExtra("imageUri",imageUriToSend);

            startActivity(updateIntent);
        }
    };

    // 댓글 등록
    Button.OnClickListener cmtRegFn = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            String cmt_content = cmtCon.getText().toString();
            if(!cmt_content.equals(null) && !cmt_content.equals("")){
                Call<List<ReviewVO>> call = RetrofitInit.getInstance().getService().insertComment(cmtCon.getText().toString(), rv_id);
                call.enqueue(new Callback<List<ReviewVO>>() {
                    @Override
                    public void onResponse(Call<List<ReviewVO>> call, Response<List<ReviewVO>> response) {
                        List<ReviewVO> list = response.body();
                        adapter.notifyDataSetChanged();
                        //data.add(list.get(0));
                        data.add(0,list.get(0));
                    }

                    @Override
                    public void onFailure(Call<List<ReviewVO>> call, Throwable t) {
                        Log.d("댓글 등록 레트로핏 에러", t.getMessage());
                    }
                });
                // 등록 누를 시 키보드 내려가게 하고 editText 내용 지우기
                inputMethodManager.hideSoftInputFromWindow(cmtCon.getWindowToken(),0);
                cmtCon.setText("");
            } else{
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ReviewDetail.this);
                alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                    }
                });
                alertDialog.setMessage("내용을 입력해 주세요.");
                alertDialog.show();
            }
        }

    };

    // 댓글 목록 리사이클러
    private void initRecycler(){
        cmt_recycler = (RecyclerView) findViewById(R.id.cmt_recycler);
        cmt_recycler.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        cmt_recycler.setLayoutManager(layoutManager);
        loadComment();
    }

    // 댓글 리스트 로드
    private void loadComment(){
        Call<List<ReviewVO>> call = RetrofitInit.getInstance().getService().commentList(rv_id);

        call.enqueue(new Callback<List<ReviewVO>>() {
            @Override
            public void onResponse(Call<List<ReviewVO>> call, Response<List<ReviewVO>> response) {
                data = response.body();
                adapter = new ReviewCommentAdapter(data);
                if (data.size() > 0) {    //데이타가 추가, 수정되었을때
                    adapter.notifyDataSetChanged();
                    Log.d("데이터셋체인지", "notify");
                }
                cmt_recycler.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<ReviewVO>> call, Throwable t) {
                Log.d("Error: ",t.getMessage());
            }
        });
    }

}
