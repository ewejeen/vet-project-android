package app.yoojin.org.vet_project2;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;

public class GPSInfo extends Service implements LocationListener {
    private final Context mContext;

    // 현재 GPS 사용유무
    boolean isGPSEnabled = false;
    // 네트워크 사용유무
    boolean isNetworkEnabled = false;
    // GPS 상태
    boolean isGetLocation = false;

    Location location;
    double lat; // 위도
    double lng; // 경도

    // GPS 정보 업데이트 최소 거리 - 10미터
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    // GPS 정보 업데이트 최소 시간 milliseconds - 1분
    private static final long MIN_TIME_BW_UPDATES = 1000*60*1;

    protected LocationManager locationManager;
    public GPSInfo(Context context){
        this.mContext = context;
        getLocation();
    }

    @TargetApi(23)
    public Location getLocation(){
        // 권한 체크 안 돼있으면 null 반환?
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(
                mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                        mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            return null;
        }

        try{
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            // GPS 정보 가져오기
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // 현재 네트워크 상태 값 알아오기
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(!isGPSEnabled && !isNetworkEnabled){
                // GPS와 Network 사용이 가능하지 않을 때의 소스 구현
            } else{
                this.isGetLocation = true;
                // 네트워크 정보로부터 위치값 가져오기
                if(isNetworkEnabled){
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if(locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if(location != null){
                            // 위도 경도 저장
                            lat = location.getLatitude();
                            lng = location.getLongitude();
                        }
                    }
                }
                if(isGPSEnabled){
                    if(location == null){
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if(locationManager != null){
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if(location != null){
                                lat = location.getLatitude();
                                lng = location.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return location;
    }

    // GPS 종료
    public void stopGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GPSInfo.this);
        }
    }

    // 위도값 반환
    public double getLatitude(){
        if(location != null){
            lat = location.getLatitude();
        }
        return lat;
    }

    // 경도값 반환
    public double getLongtitude(){
        if(location != null){
            lng = location.getLongitude();
        }
        return lng;
    }

    // GPS나 wifi 정보가 켜져 있는지 확인
    public boolean isGetLocation(){
        return this.isGetLocation;
    }

    // GPS 정보 가져오지 못했을 때 설정 값으로 갈지 물어보는 alert
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("GPS 사용 설정");
        alertDialog.setMessage("GPS 사용 여부를 확인해 주세요.");
        // OK 누르면 설정 창으로 이동
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivity(intent);
                    }
                });
        // Cancel 누르면 창 닫음
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();;
                    }
                });
        alertDialog.show();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
