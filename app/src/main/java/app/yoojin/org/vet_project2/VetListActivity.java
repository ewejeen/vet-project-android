package app.yoojin.org.vet_project2;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VetListActivity extends AppCompatActivity{

    private RecyclerView recyclerView;
    private List<VetVO> data;
    private VetDataAdapter adapter;
    private TextView textView, total;
    Toolbar topToolbar;
    Spinner sortSpinner;
    String selectItem;
    private SearchView searchView;
    private  VetVO vetVO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vet_list);

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

        textView = findViewById(R.id.textView);
        Bundle intent = getIntent().getExtras();
        if(intent.getString("searchKeyword")!=null){
            textView.setText("'"+intent.getString("searchKeyword")+"'");
        } else{
            textView.setText("'"+intent.getString("province") +" "+ intent.getString("city")+"'");
        }


    }

    // Bottom Navigation 리스너
    public BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intent = new Intent(VetListActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                case R.id.navigation_findvet:
                    return true;
                case R.id.navigation_notice:
                    Intent intent2 = new Intent(VetListActivity.this, NoticeActivity.class);
                    startActivity(intent2);
                    return true;
            }
            return false;
        }
    };

    // 리사이클러뷰
    private void initViews(){
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        //loadJSON();
        Bundle intent = getIntent().getExtras();
        if(intent.getString("searchKeyword")!=null) {
            searchByName();
        }
        else {
            searchByRegion();
        }
    }

    // 정렬 관련
    private final static Comparator myComparator = new Comparator() {
        private final Collator collator = Collator.getInstance();
        @Override
        public int compare(Object o1, Object o2) {
            return collator.compare(o1.toString(), o2.toString());
        }
    };

    // 상호명으로 검색
    private void searchByName(){
        Bundle intent = getIntent().getExtras();
        String searchKeyword = intent.getString("searchKeyword");
        //Call<List<VetVO>> call = request.vetGetByName(searchKeyword);
        Call<List<VetVO>> call = RetrofitInit.getInstance().getService().vetGetByName(searchKeyword);

        // 정렬 스피너
        sortSpinner = findViewById(R.id.sortSpinner);
        call.enqueue(new Callback<List<VetVO>>() {
            @Override
            public void onResponse(Call<List<VetVO>> call, final Response<List<VetVO>> response) {
                /*
                sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectItem = parent.getItemAtPosition(position).toString(); //상호명 or 지역
                        switch (selectItem){
                            case "상호명 오름차순":
                                data = response.body();
                            case "상호명 내림차순":
                                data = response.body();
                                Collections.reverse(data);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        data = response.body();
                    }
                }); */

                data = response.body();
                adapter = new VetDataAdapter(data);
                recyclerView.setAdapter(adapter);

                total = findViewById(R.id.total);
                String totalRes = String.format("%d", adapter.getItemCount()-1);
                total.setText(totalRes+"건");
            }

            @Override
            public void onFailure(Call<List<VetVO>> call, Throwable t) {
                Log.d("Error: ",t.getMessage());
            }
        });


    }

    // 지역으로 검색
    private void searchByRegion(){
        Bundle intent = getIntent().getExtras();

        String province = intent.getString("province");
        String city = intent.getString("city");
        HashMap<String, String> region = new HashMap<>();
        region.put("province", province);
        region.put("city", city);

        Call<List<VetVO>> call = RetrofitInit.getInstance().getService().vetGetByRegion(region);
        call.enqueue(new Callback<List<VetVO>>() {
            @Override
            public void onResponse(Call<List<VetVO>> call, Response<List<VetVO>> response) {
                data = response.body();
                adapter = new VetDataAdapter(data);
                recyclerView.setAdapter(adapter);

                total = findViewById(R.id.total);
                String totalRes = String.format("%d", adapter.getItemCount()-1);
                total.setText(totalRes+"건");
            }

            @Override
            public void onFailure(Call<List<VetVO>> call, Throwable t) {
                Log.d("Error: ",t.getMessage());
            }
        });
    }

    /*RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener(){
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            LinearLayoutManager layoutManager = LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
            int totalItemCount = layoutManager.getItemCount();
            int lastItem = layoutManager.findLastCompletelyVisibleItemPosition();
            if(lastItem >= totalItemCount - 1){
                Toast.makeText(getApplicationContext(), "총 "+totalItemCount+"건", Toast.LENGTH_SHORT).show();
                Log.d("마지막: ","마지막");
            }
        }
    };*/


    // Top Navigation에 top_navigation.xml을 집어넣는다 + 서치뷰 검색
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
                    //adapter.getFilter().filter(query);
                    return false;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    //adapter.getFilter().filter(query);
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