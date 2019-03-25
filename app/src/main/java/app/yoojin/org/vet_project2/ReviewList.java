package app.yoojin.org.vet_project2;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.BottomNavigationView;
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
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ReviewList extends AppCompatActivity {

    private Retrofit retrofit;
    private RecyclerView recyclerView;
    private List<ReviewVO> data;
    private ReviewDataAdapter adapter;
    private TextView hptname, total;
    private Toolbar topToolbar;
    private Button writeBtn;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);

        // Top Navigation
        topToolbar = findViewById(R.id.topToolbarSub);
        setSupportActionBar(topToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // 왼쪽 버튼 사용 여부 true
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);  // 왼쪽 버튼 이미지 설정
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Bottom Navigation
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        initViews();

        hptname = findViewById(R.id.textView2);
        final Bundle intent = getIntent().getExtras();
        hptname.setText("'"+intent.getString("hpt_name")+"'");

        // 후기 작성 버튼 누르면 후기 작성 페이지로 이동
        writeBtn = findViewById(R.id.writeRv);
        writeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent writeIntent = new Intent(v.getContext(), ReviewWrite.class);
                writeIntent.putExtra("hpt_id", intent.getInt("hpt_id"));
                writeIntent.putExtra("hpt_name", intent.getString("hpt_name"));
                startActivity(writeIntent);
            }
        });
    }

    // Bottom Navigation 리스너
    public BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intent = new Intent(ReviewList.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                case R.id.navigation_findvet:
                    return true;
                case R.id.navigation_notice:
                    Intent intent2 = new Intent(ReviewList.this, NoticeActivity.class);
                    startActivity(intent2);
                    return true;
            }
            return false;
        }
    };

    // 리사이클러뷰
    private void initViews(){
        recyclerView = (RecyclerView) findViewById(R.id.recyclerRv);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        loadReview();
    }


    // 후기 리스트 로드
    private void loadReview(){
        Bundle intent = getIntent().getExtras();
        int hpt_id = intent.getInt("hpt_id");

        Call<List<ReviewVO>> call = RetrofitInit.getInstance().getService().getReviewList(hpt_id);

        call.enqueue(new Callback<List<ReviewVO>>() {
            @Override
            public void onResponse(Call<List<ReviewVO>> call, Response<List<ReviewVO>> response) {
                data = response.body();
                adapter = new ReviewDataAdapter(data);
                recyclerView.setAdapter(adapter);

                total = findViewById(R.id.total);
                String totalRes = String.format("%d", adapter.getItemCount()-1);
                total.setText(totalRes+"건");
            }

            @Override
            public void onFailure(Call<List<ReviewVO>> call, Throwable t) {
                Log.d("Error: ",t.getMessage());
            }
        });
    }

    // Top Navigation에 top_navigation.xml을 집어넣는다
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.top_navigation, menu);

        SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);
        // 검색 버튼 클릭 시 searchView 꽉차게
        searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);

        if(searchView != null){
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            // 검색 버튼 클릭 시 searchView에 힌트 추가
            searchView.setQueryHint("결과 내 검색");

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String query) {
                    adapter.filter(query);
                    return false;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    adapter.filter(query);
                    return false;
                }
            });
        }

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
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
