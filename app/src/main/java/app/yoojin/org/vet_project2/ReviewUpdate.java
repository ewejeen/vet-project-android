package app.yoojin.org.vet_project2;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReviewUpdate extends AppCompatActivity {

    private Toolbar topToolbar;
    private TextView hptName, date, ratingRes, textArea;
    private Button dateBtn;
    private RatingBar ratingBar;
    private Spinner spinner2;
    private EditText title, content;
    private RadioGroup radioGroup;

    private int mYear, mMonth, mDay;
    private int visit_is_new, rv_id;
    private String strDate, pet_type, hpt_name;
    private static final int DIALOG_DATE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_update);

        // Top Navigation
        topToolbar = findViewById(R.id.topToolbarSub);
        setSupportActionBar(topToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // 왼쪽 버튼 사용 여부 true
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);  // 왼쪽 버튼 이미지 설정
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Bottom Navigation
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Bundle bundle = getIntent().getExtras();
        hptName = findViewById(R.id.hpt);
        hpt_name = bundle.getString("hpt_name");
        hptName.setText(hpt_name); // 방문 병원 이름 설정
        rv_id = bundle.getInt("rv_id");

        // DatePicker
        date = findViewById(R.id.date);
        String[] visit_date = bundle.getString("visit_date").split("-");

        dateBtn = findViewById(R.id.dateBtn);
        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_DATE);    // DatePicker Dialog
            }
        });

        // 방문일 받아와서 설정
        mYear = Integer.parseInt(visit_date[0]);
        mMonth = Integer.parseInt(visit_date[1]);
        mDay = Integer.parseInt(visit_date[2]);
        updateDate();
        // end of DatePicker

        // 별점 설정 및 변경
        ratingBar = findViewById(R.id.ratingBar3);
        ratingRes = findViewById(R.id.ratingRes);
        ratingRes.setText(bundle.getString("rv_rate"));
        ratingBar.setRating(Float.parseFloat(bundle.getString("rv_rate")));

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingRes.setText(String.valueOf(rating));
            }
        });
        // end of RatingBar Listener


        /*
        // 내용 입력 editText의 스크롤 문제 방지
        textArea = findViewById(R.id.textArea);
        textArea.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });
        // textArea 스크롤 방지 끝
        */

        // 반려동물 스피너 값 기본 설정 + 받아오기
        spinner2 = findViewById(R.id.spinner2);

        String selectedPet = bundle.getString("pet_type");
        switch (selectedPet){
            case "고양이":
                spinner2.setSelection(1);
                break;
            case "조류":
                spinner2.setSelection(2);
                break;
            case "소":
                spinner2.setSelection(3);
                break;
            case "돼지":
                spinner2.setSelection(4);
                break;
            case "말":
                spinner2.setSelection(5);
                break;
            case "기타":
                spinner2.setSelection(6);
                break;
        }

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pet_type = parent.getItemAtPosition(position).toString(); // 반려동물 선택 값 반환
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // 스피너 값 받아오기 끝

        // 후기 제목과 내용 가져와서 힌트로 설정하기
        title = findViewById(R.id.editText2);
        content = findViewById(R.id.textArea);
        title.setText(bundle.getString("rv_title"));
        content.setText(bundle.getString("rv_content"));

        // 라디오 버튼 값 가져오기
        radioGroup = findViewById(R.id.radioGroup);
        RadioButton radioButton1 = findViewById(R.id.radioButton1);
        RadioButton radioButton2 = findViewById(R.id.radioButton2);
        if(bundle.getString("visit_is_new").equals("첫 방문")){
            radioButton1.setChecked(true);
        } else if(bundle.getString("visit_is_new").equals("재방문")){
            radioButton2.setChecked(true);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radioButton1:
                        visit_is_new = 0;
                        Log.d("라디오","0");
                        break;
                    case R.id.radioButton2:
                        visit_is_new = 1;
                        Log.d("라디오","1");
                        break;
                }
            }
        });
        // 라디오 버튼 값 가져오기 끝

    }   // end of onCreate()



    // 선택한 날짜 텍스트로 보여 주기
    private void updateDate(){
        strDate = mYear+"-"+mMonth+"-"+mDay;
        String str = mYear+"년 "+mMonth+"월 "+mDay+"일";
        date.setText(str);
    }

    // 방문일 달력으로 선택하기
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    mYear = year;
                    mMonth = month+1;
                    mDay = dayOfMonth;
                    updateDate();
                }
            };

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id){
            case DIALOG_DATE:
                return new DatePickerDialog(this, mDateSetListener, mYear, mMonth-1, mDay);
        }
        return null;
    } // 방문일 달력 끝

    private void updateReview(){
        Bundle intent = getIntent().getExtras();
        final int hpt_id = intent.getInt("hpt_id");
        double hpt_rate = Double.parseDouble(ratingRes.getText().toString());
        final String rv_title = title.getText().toString();
        String rv_content = content.getText().toString();
        String rv_image = "image01.jpg";

        //Call<String> call = RetrofitInit.getInstance().getService().updateReview(hpt_rate, strDate, pet_type, visit_is_new, rv_title, rv_content, rv_image);
        Call<String> call = RetrofitInit.getInstance().getService().updateReview(hpt_rate, strDate, pet_type, visit_is_new, rv_title, rv_content, rv_image, rv_id);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String data = response.body();
                Log.d("vo",data);

                Intent confirmIntent = new Intent(getApplicationContext(), ReviewDetail.class);
                confirmIntent.putExtra("rv_id",data);
                confirmIntent.putExtra("hpt_name",hpt_name);
                confirmIntent.putExtra("hpt_id",hpt_id);
                startActivity(confirmIntent);
                finish();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("후기 수정 레트로핏 오류 ", t.getMessage());
            }
        });

    }



    // Bottom Navigation 리스너
    public BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_rvwrite:
                    updateReview();
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