package app.yoojin.org.vet_project2;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
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
    private Button deleteBtn, updateBtn;
    private String hpt_nameT;

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

        deleteBtn = findViewById(R.id.button5);
        updateBtn = findViewById(R.id.button3);

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
                Toast.makeText(getApplicationContext(), "검색 버튼이 클릭됨", Toast.LENGTH_LONG).show();
                return true;
            case R.id.home:
                onBackPressed();
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
                List<ReviewVO> data = response.body();
                rv_title.setText(data.get(0).getRv_title());
                rv_rate.setText(String.format("%.2f",data.get(0).getHpt_rate()));
                rv_reg_date.setText(data.get(0).getRv_reg_date());
                visit_date.setText(data.get(0).getVisit_date());

                // 재방문 or 첫방문
                int vis = data.get(0).getVisit_is_new();
                if(vis==0){
                    visit_is_new.setText("첫 방문");
                } else if(vis==1){
                    visit_is_new.setText("재방문");
                }

                pet_type.setText(data.get(0).getPet_type());
                rv_content.setText(data.get(0).getRv_content());
                ratingBar.setRating(Float.parseFloat(String.format("%.2f",data.get(0).getHpt_rate())));
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
            updateIntent.putExtra("visit_date", visit_date.getText().toString());
            updateIntent.putExtra("rv_rate", rv_rate.getText().toString());
            updateIntent.putExtra("pet_type", pet_type.getText().toString());
            updateIntent.putExtra("visit_is_new", visit_is_new.getText().toString());
            updateIntent.putExtra("rv_title", rv_title.getText().toString());
            updateIntent.putExtra("rv_content", rv_content.getText().toString());
            updateIntent.putExtra("rv_id",rv_id);
            updateIntent.putExtra("hpt_id",hpt_id);

            startActivity(updateIntent);
        }
    };

}
