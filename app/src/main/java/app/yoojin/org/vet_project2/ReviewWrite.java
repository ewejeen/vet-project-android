package app.yoojin.org.vet_project2;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ReviewWrite extends AppCompatActivity {

    private Retrofit retrofit;
    private Toolbar topToolbar;
    private TextView hptName, date, ratingRes, textArea;
    private Button dateBtn, pickImg;
    private RatingBar ratingBar;
    private Spinner spinner2;
    private EditText title, content;
    private RadioGroup radioGroup;

    private int mYear, mMonth, mDay;
    private int visit_is_new;
    private String strDate, pet_type, hpt_name;
    private static final int DIALOG_DATE = 1;

    private final int REQ_CODE_SELECT_IMAGE = 1001;
    private Uri uri;

    private final String IMG_FILE_PATH = "imgfilepath";
    private final String IMG_FILE_TITLE = "imgtitle";
    private final String IMG_FILE_ORIENTATION = "imgorientation";

    private String imgPath = null;
    private String mImgTitle = null;
    private String mImgOrient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_write);

        // Top Navigation
        topToolbar = findViewById(R.id.topToolbarSub);
        setSupportActionBar(topToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // 왼쪽 버튼 사용 여부 true
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);  // 왼쪽 버튼 이미지 설정
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Bottom Navigation
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Bundle intent = getIntent().getExtras();
        hptName = findViewById(R.id.hpt);
        hpt_name = intent.getString("hpt_name");
        hptName.setText(hpt_name); // 방문 병원 이름 설정

        // DatePicker
        date = findViewById(R.id.date);
        dateBtn = findViewById(R.id.dateBtn);
        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_DATE);    // DatePicker Dialog
            }
        });

        // 오늘 날짜 가져와서 방문일 텍스트에 설정
        final Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH)+1;
        mDay = calendar.get(Calendar.DATE);
        updateDate();
        // end of DatePicker

        // 별점 설정
        ratingBar = findViewById(R.id.ratingBar3);
        ratingRes = findViewById(R.id.ratingRes);
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

        // 반려동물 스피너 값 받아오기
        spinner2 = findViewById(R.id.spinner2);

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

        // 후기 제목과 내용
        title = findViewById(R.id.editText2);
        content = findViewById(R.id.textArea);

        // 라디오 버튼 값 가져오기
        radioGroup = findViewById(R.id.radioGroup);
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

        // 이미지 선택 버튼
        pickImg = findViewById(R.id.pickImg);
        pickImg .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
                openGalleryIntent.setType("image/*");
                startActivityForResult(openGalleryIntent, REQ_CODE_SELECT_IMAGE);
            }
        });
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

    private void writeReview(){
        // 프로그레시브 바
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(ReviewWrite.this);
        progressDialog.setMessage("업로드 진행 중...");
        progressDialog.show();

        Bundle intent = getIntent().getExtras();
        final int hpt_id = intent.getInt("hpt_id");
        double hpt_rate = Double.parseDouble(ratingRes.getText().toString());
        final String rv_title = title.getText().toString();
        String rv_content = content.getText().toString();

        MultipartBody.Part imageFile = null;
        Log.d("패스",imgPath);
        // 이미지를 선택하지 않을 경우의 예외 처리

        File file = new File(imgPath);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        imageFile = MultipartBody.Part.createFormData("imageFile", file.getName(), requestBody);

        RequestBody dateBody = RequestBody.create(MediaType.parse("text/plain"),strDate);
        RequestBody petBody = RequestBody.create(MediaType.parse("text/plain"),pet_type);
        RequestBody titleBody = RequestBody.create(MediaType.parse("text/plain"),rv_title);
        RequestBody conBody = RequestBody.create(MediaType.parse("text/plain"),rv_content);

        Call<String> call = RetrofitInit.getInstance().getService().insertReview(imageFile, hpt_id, hpt_rate, dateBody, petBody, visit_is_new, titleBody, conBody);
        //Call<List<ReviewVO>> call = request.insertReview(hpt_id, hpt_rate, strDate, pet_type, visit_is_new, rv_title, rv_content, rv_image);
        //Call<String> call = request.insertReview(new ReviewVO(hpt_id, hpt_rate, rv_title, rv_content, rv_image, pet_type, strDate, visit_is_new));

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String data = response.body();

                Intent confirmIntent = new Intent(getApplicationContext(), ReviewDetail.class);
                confirmIntent.putExtra("rv_id",data);
                confirmIntent.putExtra("hpt_name",hptName.getText().toString());
                confirmIntent.putExtra("hpt_id",hpt_id);
                startActivity(confirmIntent);
                finish();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("후기 작성 레트로핏 오류 ", t.getMessage());
            }
        });
    }

    // 이미지 업로드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // 선택된 사진을 받아 서버에 업로드한다
        if(requestCode == REQ_CODE_SELECT_IMAGE && resultCode == RESULT_OK){
            if(data == null){
                Toast.makeText(this, "이미지를 선택해 주세요", Toast.LENGTH_SHORT).show();
                return;
            }
            uri = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri, filePathColumn, null,null,null);
            if(cursor!=null){
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgPath = cursor.getString(columnIndex);
                Log.d("imgPath",imgPath);
                ImageView img = findViewById(R.id.upImageView);
                Glide
                        .with(getApplicationContext())
                        .load(new File(imgPath))
                        .error(R.drawable.no_image)
                        .into(img);

                cursor.close();
            }
        }
    }

    // Bottom Navigation 리스너
    public BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_rvwrite:
                    writeReview();
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
}
