package app.yoojin.org.vet_project2;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VetListActivity extends AppCompatActivity{

    private RecyclerView recyclerView;
    private List<VetVO> data;
    private VetDataAdapter adapter;
    private TextView textView, total;
    private Toolbar topToolbar;
    private Spinner sortSpinner;
    private String selectItem;
    private SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;

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
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        initViews();
        // 정렬 스피너
        sortSpinner = findViewById(R.id.sortSpinner);

        textView = findViewById(R.id.textView);
        Bundle intent = getIntent().getExtras();
        Log.v("위도", ""+intent.getDouble("lat"));
        Log.v("경도", ""+intent.getDouble("lng"));
        if(intent.getString("searchKeyword")!=null){
            textView.setText("'"+intent.getString("searchKeyword")+"'");
        } else if(intent.getDouble("lat") != 0 && intent.getDouble("lng") != 0){
            textView.setText("가까운 동물 병원");
            //textView.setText("'"+intent.getString("nowP") +" "+ intent.getString("nowC")+"'");
        }
        else{
            textView.setText("'"+intent.getString("province") +" "+ intent.getString("city")+"'");
        }

        // 리사이클러뷰 밑으로 당겨서 새로고침
        swipeRefreshLayout = findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(swipeListener);

    }   // End of onCreate()

    // Bottom Navigation 리스너
    public BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_findvet:
                    Intent intent = new Intent(VetListActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
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
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        //loadJSON();

        Bundle intent = getIntent().getExtras();
        if(intent.getString("searchKeyword")!=null) {
            searchByName();
        }  else if(intent.getDouble("lat") != 0 && intent.getDouble("lng") != 0){
            searchNearest();
        }
        else{
            searchByRegion();
        }
    }

    // 상호명으로 검색
    private void searchByName(){
        Bundle intent = getIntent().getExtras();
        String searchKeyword = intent.getString("searchKeyword");
        Call<List<VetVO>> call = RetrofitInit.getInstance().getService().vetGetByName(searchKeyword);
        call.enqueue(new Callback<List<VetVO>>() {
            @Override
            public void onResponse(Call<List<VetVO>> call, final Response<List<VetVO>> response) {

                data = response.body();

                sortSpinner.setOnItemSelectedListener(spinnerListener); // 정렬 스피너

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

        final String province = intent.getString("province");
        String city = intent.getString("city");
        HashMap<String, String> region = new HashMap<>();
        region.put("province", province);
        region.put("city", city);

        Call<List<VetVO>> call = RetrofitInit.getInstance().getService().vetGetByRegion(region);
        call.enqueue(new Callback<List<VetVO>>() {
            @Override
            public void onResponse(Call<List<VetVO>> call, Response<List<VetVO>> response) {
                data = response.body();

                sortSpinner.setOnItemSelectedListener(spinnerListener);

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

    // 내 GPS 기준 가까운 병원 30개 검색
    private void searchNearest(){
        Bundle intent = getIntent().getExtras();

        Double latitude = intent.getDouble("lat");
        Double longitude = intent.getDouble("lng");

        HashMap<String, Double> axis = new HashMap<>();
        axis.put("latitude", latitude);
        axis.put("longitude", longitude);

        Call<List<VetVO>> call = RetrofitInit.getInstance().getService().vetGetByDistance(axis);
        call.enqueue(new Callback<List<VetVO>>() {
            @Override
            public void onResponse(Call<List<VetVO>> call, final Response<List<VetVO>> response) {
                data = response.body();
                adapter = new VetDataAdapter(data);
                adapter.notifyDataSetChanged();

                sortSpinner.setOnItemSelectedListener(spinnerListener);

                Log.d("스피너","안호출");
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

    // 정렬 스피너 리스너
    AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener(){
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selectItem = parent.getItemAtPosition(position).toString(); //상호명 or 지역
            Log.d("선택",selectItem);
            switch (selectItem){
                case "상호명 오름차순":
                    Log.d("상호명","오름차순");
                    adapter.notifyDataSetChanged();
                    SortWithSpinner.NameAscCompare nasc = new SortWithSpinner.NameAscCompare();
                    nasc.compare(data.get(0), data.get(1));
                    Collections.sort(data, nasc);
                    break;
                case "상호명 내림차순":
                    Log.d("상호명","내림차순");
                    adapter.notifyDataSetChanged();
                    SortWithSpinner.NameDescCompare ndesc = new SortWithSpinner.NameDescCompare();
                    ndesc.compare(data.get(0), data.get(1));
                    Collections.sort(data, ndesc);
                    break;
                case "지역명 오름차순":
                    Log.d("지역명","오름차순");
                    adapter.notifyDataSetChanged();
                    SortWithSpinner.RegionAscCompare rasc = new SortWithSpinner.RegionAscCompare();
                    rasc.compare(data.get(0), data.get(1));
                    Collections.sort(data, rasc);
                    break;
                case "지역명 내림차순":
                    Log.d("지역명","내림차순");
                    adapter.notifyDataSetChanged();
                    SortWithSpinner.RegionDescCompare rdesc = new SortWithSpinner.RegionDescCompare();
                    rdesc.compare(data.get(0), data.get(1));
                    Collections.sort(data, rdesc);
                    break;
                case "평점 높은 순":
                    Log.d("평점","높은 순");
                    adapter.notifyDataSetChanged();
                    SortWithSpinner.RateCompare rate = new SortWithSpinner.RateCompare();
                    rate.compare(data.get(0), data.get(1));
                    Collections.sort(data, rate);
                    break;
                case "후기 많은 순":
                    Log.d("후기","많은 순");
                    adapter.notifyDataSetChanged();
                    SortWithSpinner.ReviewCompare review = new SortWithSpinner.ReviewCompare();
                    review.compare(data.get(0), data.get(1));
                    Collections.sort(data, review);
                    break;
                case "조회 많은 순":
                    Log.d("조회","많은 순");
                    adapter.notifyDataSetChanged();
                    SortWithSpinner.ViewCompare viewCompare = new SortWithSpinner.ViewCompare();
                    viewCompare.compare(data.get(0), data.get(1));
                    Collections.sort(data, viewCompare);
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            Log.d("낫띵","셀렉티드");
        }
    };

    // SwipeRefreshLayout (당겨서 새로고침) 리스너
    SwipeRefreshLayout.OnRefreshListener swipeListener = new SwipeRefreshLayout.OnRefreshListener(){
        @Override
        public void onRefresh() {
            // 새로고침 코드
            initViews();
            sortSpinner.setSelection(0);
            // 새로고침 완료
            swipeRefreshLayout.setRefreshing(false);
        }
    };

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
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
