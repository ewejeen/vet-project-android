package app.yoojin.org.vet_project2;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class NoticeDetail extends AppCompatActivity {

    private Toolbar topToolbar;
    private TextView nttitle, ntcon, ntdate;
    private ImageView imageView;
    private String imageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_detail);

        nttitle = findViewById(R.id.nttitle);
        ntcon = findViewById(R.id.ntcon);
        ntdate = findViewById(R.id.ntdate);

        // ReviewWrite, ReviewList에서 받아온 인텐트 정보들
        Bundle intent = getIntent().getExtras();
        nttitle.setText(intent.getString("title"));
        ntcon.setText(intent.getString("content"));
        ntdate.setText(intent.getString("reg_date"));

        intent.getInt("id");

        // 이미지 띄우기
        imageURL = RetrofitInit.getImageURL()+intent.getString("image");

        imageView = findViewById(R.id.imageView);
        Uri uri = Uri.parse(imageURL);
        Glide.with(this)
                .load(uri)
                .into(imageView);
        // 이미지 띄우기 끝

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
                    finish();
                    return true;
            }
            return false;
        }
    };

    // Top Navigation을 집어넣는다
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.top_navigation_plain, menu);
        return true;
    }

    // Top Navigation에 삽입된 메뉴에 대해서 이벤트 처리
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
