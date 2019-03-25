package app.yoojin.org.vet_project2;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInit {

    //회사
    private static final String BASE_URL = "http://192.168.1.33:8080";
    //집
    //private static final String BASE_URL = "http://192.168.35.241:8080";
    //private static final String BASE_URL = "http://192.168.35.146:8080";
    //private static final String BASE_URL = "http://192.168.35.197:8080";
    //스벅
    //private static final String BASE_URL = "http://172.30.48.188:8080";
    //private static final String BASE_URL = "http://172.30.120.188:8080";
    //private static final String BASE_URL = "http://172.30.121.45:8080";

    private static RetrofitInit ourInstance = new RetrofitInit();
    public static RetrofitInit getInstance() {
        return ourInstance;
    }
    private RetrofitInit() {
    }


    public RetrofitService getService() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        //interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit.create(RetrofitService.class);
    }
}