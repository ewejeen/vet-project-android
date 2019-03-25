package app.yoojin.org.vet_project2;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
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
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    android.support.v7.widget.Toolbar topToolbar;
    Spinner spinner;
    String selectItem;
    FrameLayout locationL, frame;
    LinearLayout nameL;
    Button searchIcon, searchIcon2;
    EditText searchWord;
    int index = 0;
    Retrofit retrofit;

    public static Context mContext;

    // 스피너 어댑터 설정(시도/시군구)
    ArrayAdapter<CharSequence> ad_province, ad_city;
    // 검색시 선택된 메세지 띄우기 위해 선언
    String choice_province="", choice_city="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getHashKey();

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
        frame = findViewById(R.id.frame);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectItem = parent.getItemAtPosition(position).toString(); //상호명 or 지역
                if (selectItem.equals("상호명")) {
                    index=0;
                    // 수정 필요
                    if(index==0){
                        nameL.setVisibility(View.VISIBLE);
                        locationL.setVisibility(View.INVISIBLE);
                    } else if(index==1){
                        nameL.setVisibility(View.INVISIBLE);
                        locationL.setVisibility(View.VISIBLE);
                    } else{
                        index=0;
                    }
                }else if(selectItem.equals("지역")){
                    index+=1;

                    if(index==0){
                        nameL.setVisibility(View.VISIBLE);
                        locationL.setVisibility(View.INVISIBLE);
                    } else if(index==1){
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
    }
    //onCreate 끝


    // Bottom Navigatoin 리스너
    public BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_findvet:
                    Intent intent = new Intent(MainActivity.this, VetListActivity.class);
                    searchWord = findViewById(R.id.searchWord);
                    intent.putExtra("searchKeyword", searchWord.getText().toString());
                    startActivity(intent);
                    return true;
                case R.id.navigation_notice:
                    Intent intent2 = new Intent(MainActivity.this, NoticeActivity.class);
                    startActivity(intent2);
                    return true;
            }
            return false;
        }
    };

/*
    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }
*/


}