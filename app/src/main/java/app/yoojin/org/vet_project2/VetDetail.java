package app.yoojin.org.vet_project2;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VetDetail extends AppCompatActivity implements OnMapReadyCallback {

    private List<ReviewVO> data;
    private ReviewDataAdapterThree adapter;
    private Toolbar topToolbar;
    private TextView name, newAdd, oldAdd, phone, hit, rvcnt, rateavg;
    private RatingBar ratingBar2;
    private Geocoder geocoder;
    private RecyclerView recyclerView;
    private Button moreBtn;

    private String hpt_name;
    private int hpt_id, hpt_hit;
    private float rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vet_detail);

        // Top Navigation
        topToolbar = findViewById(R.id.topToolbarSub);
        setSupportActionBar(topToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // 왼쪽 버튼 사용 여부 true
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);  // 왼쪽 버튼 이미지 설정
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Bottom Navigation
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        name = findViewById(R.id.name);
        rvcnt = findViewById(R.id.rvcnt);
        rateavg = findViewById(R.id.rateavg);
        ratingBar2 = findViewById(R.id.ratingBar2);
        newAdd = findViewById(R.id.newAdd);
        oldAdd = findViewById(R.id.oldAdd);
        phone = findViewById(R.id.phone);
        hit = findViewById(R.id.hit);

        Bundle intent = getIntent().getExtras();
        hpt_id = intent.getInt("hpt_id");
        hpt_name = intent.getString("hpt_name");

        fetchDetail(hpt_id);  // 레트로핏으로 정보 불러오기
        hitUp(hpt_id);  // 조회수 ++

        // 구글 지도 API
        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this); //implement 확인

        // 후기 더 보기 버튼
        moreBtn = findViewById(R.id.button2);
        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle intent = getIntent().getExtras(); // 병원 이름과 아이디 받아와서 보내주기 위함
                Intent moreIntent = new Intent(v.getContext(), ReviewList.class);
                moreIntent.putExtra("hpt_name",hpt_name);
                moreIntent.putExtra("hpt_id",hpt_id);
                startActivity(moreIntent);
            }
        });

        initViews();
    }

    private void fetchDetail(int hpt_id){
        Call<List<VetVO>> call = RetrofitInit.getInstance().getService().vetDetail(hpt_id);
        call.enqueue(new Callback<List<VetVO>>() {
            @Override
            public void onResponse(Call<List<VetVO>> call, Response<List<VetVO>> response) {
                VetVO info = response.body().get(0);

                hpt_hit = info.getHpt_hit();
                rating = Float.parseFloat(info.getRateAvg());
                String ratingRound = String.format("%.2f", rating);

                name.setText(info.getHpt_name());
                hit.setText(""+hpt_hit);
                rvcnt.setText("("+info.getReviewCnt()+")");
                ratingBar2.setRating(rating);
                rateavg.setText(ratingRound);
                String phoneData = info.getHpt_phone();
                String newAddData = info.getAdrs_new();
                String oldAddData = info.getAdrs_old();
                if(newAddData != null && !newAddData.isEmpty()){
                    newAdd.setText(newAddData);
                }
                if(oldAddData != null && !oldAddData.isEmpty()){
                    oldAdd.setText("(지번) "+oldAddData);
                }
                if(phoneData != null && !phoneData.isEmpty()){
                    phone.setText(phoneData);
                }
            }

            @Override
            public void onFailure(Call<List<VetVO>> call, Throwable t) {
                Log.d("VetDetail 에러",t.getMessage());
            }
        });
    }

    // 구글 지도 API
    @Override
    public void onMapReady(GoogleMap map) {
        geocoder = new Geocoder(this);  // 주소 -> 좌표 변환
        double lati=0, longti=0;    // 위도, 경도 변수 선언
        List<Address> addressList = null;
        Bundle intent = getIntent().getExtras();
        String address = intent.getString("address");

        try{
            addressList = geocoder.getFromLocationName(
                    address,
                    10);
        } catch (IOException e){
            e.printStackTrace();
            Log.e("test","입출력 오류");
        }

        MarkerOptions markerOptions = new MarkerOptions();
        if(addressList!=null){
            lati = addressList.get(0).getLatitude();   // 해당 주소의 위도
            longti = addressList.get(0).getLongitude();  // 해당 주소의 경도
        }
        LatLng marker = new LatLng(lati,longti);

        markerOptions.position(marker);
        markerOptions.title(intent.getString("hpt_name"));  // 마커 클릭하면 뜨는 정보
        markerOptions.snippet(intent.getString("hpt_phone"));
        map.addMarker(markerOptions);

        map.moveCamera(CameraUpdateFactory.newLatLng(marker));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
    }
    /* 구글 지도 끝 */

    // 리사이클러뷰
    private void initViews(){
        recyclerView = (RecyclerView) findViewById(R.id.rv_recycler);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        loadJSON();
    }

    // 리사이클러뷰 데이터 로드
    private void loadJSON(){
        Bundle intent = getIntent().getExtras();
        int hpt_id = intent.getInt("hpt_id");
        //Call<List<ReviewVO>> call = request.getReviewListThree(hpt_id);
        Call<List<ReviewVO>> call = RetrofitInit.getInstance().getService().getReviewListThree(hpt_id);

        call.enqueue(new Callback<List<ReviewVO>>() {
            @Override
            public void onResponse(Call<List<ReviewVO>> call, Response<List<ReviewVO>> response) {
                data = response.body();
                Log.d("호출",data.toString());
                adapter = new ReviewDataAdapterThree(data);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<ReviewVO>> call, Throwable t) {
                Log.d("Error: ",t.getMessage());
            }
        });

    }


    // 병원 조회수 +1
    private void hitUp(int hpt_id){
        Call<String> call = RetrofitInit.getInstance().getService().vetHitUp(hpt_id);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("조회수+1","성공");
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    // Bottom Navigation 리스너
    public BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                // 전화 걸기
                case R.id.navigation_call:
                    Bundle intent = getIntent().getExtras(); // 전화번호 받아올 인텐트
                    String phoneData = intent.getString("hpt_phone"); // 받아온 전화번호 000-0000-0000
                    if(!phoneData.equals(null) && !phoneData.equals("")){
                        Intent callIntent = new Intent(Intent.ACTION_DIAL); // 전화 다이얼 화면 가는 인텐트
    
                        String tel = "tel:" + phoneData.replaceAll("[^0-9]",""); // 정규식으로 하이픈 제거
                        callIntent.setData(Uri.parse(tel));
                        try{
                            startActivity(callIntent);
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    } else{
                        Toast.makeText(VetDetail.this, "전화번호 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                // 후기 작성 페이지로 이동
                case R.id.navigation_rvwrite:
                    Bundle intent2 = getIntent().getExtras();   // 병원 이름, 아이디 받아올 인텐트
                    Intent writeIntent = new Intent(VetDetail.this, ReviewWrite.class); // 후기 작성 화면 가는 인텐트
                    writeIntent.putExtra("hpt_id", intent2.getInt("hpt_id"));
                    writeIntent.putExtra("hpt_name", intent2.getString("hpt_name"));
                    startActivity(writeIntent);
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
}
