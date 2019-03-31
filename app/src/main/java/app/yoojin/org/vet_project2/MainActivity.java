package app.yoojin.org.vet_project2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;

import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    android.support.v7.widget.Toolbar topToolbar;
    Spinner spinner;
    String selectItem, nowAddress, nowProvince, nowCity;
    FrameLayout locationL, frame;
    LinearLayout nameL, myLocL;
    Button searchIcon, searchIcon2, getGpsBtn;
    TextView myGps;
    EditText searchWord;
    int index = 0;
    double latitude, longtitude;

    public static Context mContext;

    /* GPS 관련 */
    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;
    private boolean isAccessFineLocation = false;
    private boolean isAccessCoarseLocation = false;
    private boolean isPermission = false;
    private GPSInfo gps;

    // 스피너 어댑터 설정(시도/시군구)
    ArrayAdapter<CharSequence> ad_province, ad_city;
    // 검색시 선택된 메세지 띄우기 위해 선언
    String choice_province="", choice_city="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        // Top Navigation
        topToolbar = findViewById(R.id.topToolbarSub);
        setSupportActionBar(topToolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Bottom Navigation
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // 검색기준(상호명or지역) 선택 스피너
        spinner = findViewById(R.id.spinner);
        nameL = findViewById(R.id.nameL);
        locationL = findViewById(R.id.locationL);
        myLocL = findViewById(R.id.myLocL);
        frame = findViewById(R.id.frame);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectItem = parent.getItemAtPosition(position).toString(); //상호명 or 지역
                if (selectItem.equals("내 위치로 검색")) {
                    index=0;
                    // 수정 필요
                    if(index==0){
                        myLocL.setVisibility(View.VISIBLE);
                        nameL.setVisibility(View.INVISIBLE);
                        locationL.setVisibility(View.INVISIBLE);
                    } else if(index==1){
                        myLocL.setVisibility(View.INVISIBLE);
                        nameL.setVisibility(View.VISIBLE);
                        locationL.setVisibility(View.INVISIBLE);
                    } else if (index==2) {
                        myLocL.setVisibility(View.INVISIBLE);
                        nameL.setVisibility(View.INVISIBLE);
                        locationL.setVisibility(View.VISIBLE);
                    } else{
                        index=0;
                    }
                }else if(selectItem.equals("상호명으로 검색")){
                    index=1;

                    if(index==0){
                        myLocL.setVisibility(View.VISIBLE);
                        nameL.setVisibility(View.INVISIBLE);
                        locationL.setVisibility(View.INVISIBLE);


                    } else if(index==1){
                        myLocL.setVisibility(View.INVISIBLE);
                        nameL.setVisibility(View.VISIBLE);
                        locationL.setVisibility(View.INVISIBLE);
                    } else if (index==2) {
                        myLocL.setVisibility(View.INVISIBLE);
                        nameL.setVisibility(View.INVISIBLE);
                        locationL.setVisibility(View.VISIBLE);
                    } else{
                        index=0;
                    }
                }else if(selectItem.equals("지역으로 검색")){
                    index=2;

                    if(index==0){
                        myLocL.setVisibility(View.VISIBLE);
                        nameL.setVisibility(View.INVISIBLE);
                        locationL.setVisibility(View.INVISIBLE);
                    } else if(index==1){
                        myLocL.setVisibility(View.INVISIBLE);
                        nameL.setVisibility(View.VISIBLE);
                        locationL.setVisibility(View.INVISIBLE);
                    } else if (index==2) {
                        myLocL.setVisibility(View.INVISIBLE);
                        nameL.setVisibility(View.INVISIBLE);
                        locationL.setVisibility(View.VISIBLE);
                    } else{
                        index=0;
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 지역 선택 스피너
        final Spinner proSpinner = findViewById(R.id.proSpinner);
        final Spinner citySpinner = findViewById(R.id.citySpinner);
        searchIcon2 = findViewById(R.id.searchIcon2);

        ad_province = ArrayAdapter.createFromResource(this,R.array.province, android.R.layout.simple_spinner_dropdown_item);
        ad_province.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        proSpinner.setAdapter(ad_province);
        proSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(ad_province.getItem(position).equals("서울특별시")){
                    choice_province="서울특별시";
                    ad_city = ArrayAdapter.createFromResource(MainActivity.this, R.array.city_seoul, android.R.layout.simple_spinner_dropdown_item);
                    ad_city.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(ad_city);
                } else if(ad_province.getItem(position).equals("경기도")){
                    choice_province="경기도";
                    ad_city = ArrayAdapter.createFromResource(MainActivity.this, R.array.city_gyeonggi, android.R.layout.simple_spinner_dropdown_item);
                    ad_city.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(ad_city);
                } else if(ad_province.getItem(position).equals("강원도")){
                    choice_province="강원도";
                    ad_city = ArrayAdapter.createFromResource(MainActivity.this, R.array.city_gangwon, android.R.layout.simple_spinner_dropdown_item);
                    ad_city.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(ad_city);
                } else if(ad_province.getItem(position).equals("경상남도")){
                    choice_province="경상남도";
                    ad_city = ArrayAdapter.createFromResource(MainActivity.this, R.array.city_gyeongnam, android.R.layout.simple_spinner_dropdown_item);
                    ad_city.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(ad_city);
                } else if(ad_province.getItem(position).equals("경상북도")){
                    choice_province="경상북도";
                    ad_city = ArrayAdapter.createFromResource(MainActivity.this, R.array.city_gyeongbuk, android.R.layout.simple_spinner_dropdown_item);
                    ad_city.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(ad_city);
                } else if(ad_province.getItem(position).equals("전라남도")){
                    choice_province="전라남도";
                    ad_city = ArrayAdapter.createFromResource(MainActivity.this, R.array.city_jeonnam, android.R.layout.simple_spinner_dropdown_item);
                    ad_city.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(ad_city);
                } else if(ad_province.getItem(position).equals("전라북도")){
                    choice_province="전라북도";
                    ad_city = ArrayAdapter.createFromResource(MainActivity.this, R.array.city_jeonbuk, android.R.layout.simple_spinner_dropdown_item);
                    ad_city.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(ad_city);
                } else if(ad_province.getItem(position).equals("충청남도")){
                    choice_province="충청남도";
                    ad_city = ArrayAdapter.createFromResource(MainActivity.this, R.array.city_chungnam, android.R.layout.simple_spinner_dropdown_item);
                    ad_city.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(ad_city);
                } else if(ad_province.getItem(position).equals("충청북도")){
                    choice_province="충청북도";
                    ad_city = ArrayAdapter.createFromResource(MainActivity.this, R.array.city_chungbuk, android.R.layout.simple_spinner_dropdown_item);
                    ad_city.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(ad_city);
                } else if(ad_province.getItem(position).equals("부산광역시")){
                    choice_province="부산광역시";
                    ad_city = ArrayAdapter.createFromResource(MainActivity.this, R.array.city_busan, android.R.layout.simple_spinner_dropdown_item);
                    ad_city.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(ad_city);
                } else if(ad_province.getItem(position).equals("대전광역시")){
                    choice_province="대전광역시";
                    ad_city = ArrayAdapter.createFromResource(MainActivity.this, R.array.city_daejeon, android.R.layout.simple_spinner_dropdown_item);
                    ad_city.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(ad_city);
                } else if(ad_province.getItem(position).equals("대구광역시")){
                    choice_province="대구광역시";
                    ad_city = ArrayAdapter.createFromResource(MainActivity.this, R.array.city_daegu, android.R.layout.simple_spinner_dropdown_item);
                    ad_city.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(ad_city);
                } else if(ad_province.getItem(position).equals("인천광역시")){
                    choice_province="인천광역시";
                    ad_city = ArrayAdapter.createFromResource(MainActivity.this, R.array.city_incheon, android.R.layout.simple_spinner_dropdown_item);
                    ad_city.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(ad_city);
                } else if(ad_province.getItem(position).equals("울산광역시")){
                    choice_province="울산광역시";
                    ad_city = ArrayAdapter.createFromResource(MainActivity.this, R.array.city_ulsan, android.R.layout.simple_spinner_dropdown_item);
                    ad_city.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(ad_city);
                } else if(ad_province.getItem(position).equals("제주특별자치도")){
                    choice_province="제주특별자치도";
                    ad_city = ArrayAdapter.createFromResource(MainActivity.this, R.array.city_jeju, android.R.layout.simple_spinner_dropdown_item);
                    ad_city.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(ad_city);
                } else if(ad_province.getItem(position).equals("세종특별자치시")){
                    choice_province="세종특별자치시";
                    ad_city = ArrayAdapter.createFromResource(MainActivity.this, R.array.city_sejong, android.R.layout.simple_spinner_dropdown_item);
                    ad_city.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(ad_city);
                } else if(ad_province.getItem(position).equals("광주광역시")){
                    choice_province="광주광역시";
                    ad_city = ArrayAdapter.createFromResource(MainActivity.this, R.array.city_gwangju, android.R.layout.simple_spinner_dropdown_item);
                    ad_city.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(ad_city);
                } else{
                    ad_city = ArrayAdapter.createFromResource(MainActivity.this,R.array.city, android.R.layout.simple_spinner_dropdown_item);
                    ad_city.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(ad_city);
                }

                citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        choice_city = ad_city.getItem(position).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 지역 선택 완료하고 검색 버튼 눌렀을 시 리스너
        searchIcon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context,VetListActivity.class);
                intent.putExtra("province", choice_province);
                intent.putExtra("city", choice_city);
                startActivity(intent);
            }
        });

        // 상호명으로 검색
        searchIcon = findViewById(R.id.searchIcon);
        searchWord = findViewById(R.id.searchWord);

        // 상호명 입력 완료하고 검색 버튼 눌렀을 시 리스너
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),VetListActivity.class);
                intent.putExtra("searchKeyword", searchWord.getText().toString());
                startActivity(intent);
            }
        });

        // GPS 정보 보여주기 위함
        getGpsBtn = findViewById(R.id.getGpsBtn);
        myGps = findViewById(R.id.myGps);

        gps = new GPSInfo(MainActivity.this);

        getGpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent listIntent = new Intent(v.getContext(), VetListActivity.class);
                if(gps.isGetLocation()) {
                    listIntent.putExtra("lat",gps.getLatitude());
                    listIntent.putExtra("lng",gps.getLongtitude());
                    nowProvince = getCurrentLocation()[1];
                    nowCity = getCurrentLocation()[2];
                    // 현재 시도, 시군구로 검색
                    Log.v("인텐트도",nowProvince);
                    Log.v("인텐트시",nowCity);
                    listIntent.putExtra("nowP",nowProvince);
                    listIntent.putExtra("nowC",nowCity);

                    startActivity(listIntent);
                } else{
                    Toast.makeText(MainActivity.this, "위치 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        callPermission(); // 권한 요청
        getCurrentLocation();
        // GPS 끝

    }
    //onCreate 끝


    // Bottom Navigation 리스너
    public BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                /*case R.id.navigation_home:
                    return true;*/
                case R.id.navigation_findvet:
                    //Toast.makeText(MainActivity.this, "병원을 검색해 주세요.", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.navigation_notice:
                    Intent intent2 = new Intent(MainActivity.this, NoticeActivity.class);
                    startActivity(intent2);
                    //startActivityForResult(intent2,101);
                    return true;
            }
            return false;
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSIONS_ACCESS_FINE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            isAccessFineLocation = true;
        } else if(requestCode == PERMISSIONS_ACCESS_COARSE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            isAccessCoarseLocation = true;
        }

        if(isAccessFineLocation && isAccessCoarseLocation){
            isPermission = true;
        }
    }

    // 권한 요청 메소드
    private void callPermission(){
        // SDK 버전과 권한이 주어졌는지 여부를 확인
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_ACCESS_FINE_LOCATION);
        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_ACCESS_COARSE_LOCATION);
        } else{
            isPermission = true;
        }
    }

    // 주소 가져오기
    private String[] getCurrentLocation(){
        // 권한 요청
        String[] result = {};
        if(!isPermission){
            callPermission();
            return result;
        }

        gps = new GPSInfo(MainActivity.this);
        // GPS 사용 유무 가져오기
        if(gps.isGetLocation()){
            double latitude = gps.getLatitude();
            double longtitude = gps.getLongtitude();

            //Geocoder (위도경도 -> 주소)
            Geocoder geocoder = new Geocoder(this);
            List<Address> addressList = null;

            try{
                addressList = geocoder.getFromLocation(latitude, longtitude, 1);
            } catch (IOException e){
                e.printStackTrace();
                Log.e("test","입출력 오류");
            }

            if(addressList!=null){
                // 변환된 주소 확인 + 주소 파싱 + 텍스트뷰에 적용
                String[] adrs = addressList.get(0).getAddressLine(0).split(" ");
                nowAddress = addressList.get(0).getAddressLine(0).substring(4);
                nowProvince = adrs[1];
                nowCity = adrs[2];
                Log.v("시도",nowProvince);
                Log.v("시군구",nowCity);

                myGps.setText("내 위치: "+nowAddress);
                result = new String[]{nowAddress, nowProvince, nowCity};
            }

        } else{
            // GPS 사용할 수 없을 시의 alert
            gps.showSettingsAlert();
        }
        return result;
    }
}