package com.nammu.artreasurehunt;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nammu.artreasurehunt.SampleApplication.utils.LoadingDialogHandler;
import com.nammu.artreasurehunt.appInit.MyApplication;
import com.nammu.artreasurehunt.module.SLog;
import com.nammu.artreasurehunt.service.GpsInfo;
import com.nammu.artreasurehunt.userdefinedtargets.UserDefinedTargets;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TreasuerMapActivity extends AppCompatActivity implements OnMapReadyCallback, android.location.LocationListener {
    private static final int GET_STICKER_DISTANCE = 50;

    // private int questResId;
    private GoogleMap arMap;
    private SupportMapFragment mapFragment;

    private double questLng = 127.0078303;
    private double questLat = 37.267509;

    private boolean locationTag = true;
    private Marker mMarker;

    @OnClick(R.id.btn_treasuerScan)
    public void onClick(View view){
        checkingTreasuer(view);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setContentView(R.layout.activity_treasuer_map);
        mapFragment = (SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        arMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationService();
        //GpsInfo gps = new GpsInfo(this);
        arMap.setMyLocationEnabled(true);
        arMap.getUiSettings().setMyLocationButtonEnabled(false);
        markAdd();

    }

    private void markAdd(){
        //Todo map에 여러 개 뿌리기
        LatLng latLng = new LatLng(questLat, questLng);
        Marker marker = arMap.addMarker(new MarkerOptions()
                .title("보물")
                .position(latLng));
        arMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
    }

    @Override
    public void onLocationChanged(Location location) {
        if(locationTag){//한번만 위치를 가져오기 위해서 tag를 주었습니다
            questLat = location.getLatitude();
            questLng = location.getLongitude();
            SLog.d("lat : lng = " + questLat + " : " + questLng);
            locationTag=false; }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    private void setMyLocation(){
        arMap.setOnMyLocationChangeListener(myLocationChangeListener);
    }

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override public void onMyLocationChange(Location location) {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            mMarker = arMap.addMarker(new MarkerOptions().position(loc));
            if(arMap != null){
                arMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
            }
        }
    };

    public void checkingTreasuer(View v) {
        Location currentLocation = new Location(LocationManager.GPS_PROVIDER);
        currentLocation.setLongitude(MyApplication.getCurrentLng());
        currentLocation.setLatitude(MyApplication.getCurrentLat());
        Location qusetLocation = new Location(LocationManager.GPS_PROVIDER);
        qusetLocation.setLongitude(questLng);
        qusetLocation.setLatitude(questLat);
        //int distance = (int) currentLocation.distanceTo(qusetLocation);
        int distance = 40;
        SLog.d("distacne : " + distance);
        if (distance < GET_STICKER_DISTANCE) {
            Intent intent = new Intent(this, UserDefinedTargets.class);
            startActivity(intent);
        }else{
            Toast.makeText(this, "주변에 아무것도 없습니다.", Toast.LENGTH_SHORT).show();
        }

    }

    private LocationManager locationManager;
    private String provider;

    private void locationService() {
        int googlePlayServiceResult = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (googlePlayServiceResult != ConnectionResult.SUCCESS) { //구글 플레이 서비스를 활용하지 못할경우 <계정이 연결이 안되어 있는 경우
            //실패
            GooglePlayServicesUtil.getErrorDialog(googlePlayServiceResult, this, 0, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            }).show();
        } else { //구글 플레이가 활성화 된 경우
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            provider = locationManager.getBestProvider(criteria, true);

            if (provider == null) {  //위치정보 설정이 안되어 있으면 설정하는 엑티비티로 이동합니다
                new AlertDialog.Builder(this)
                        .setTitle("위치서비스 동의")
                        .setNeutralButton("이동", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                            }
                        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                })
                        .show();
            } else {   //위치 정보 설정이 되어 있으면 현재위치를 받아옵니다
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                //locationManager.requestLocationUpdates(provider, 1, 1, this); //기본 위치 값 설정
            }

            //setMyLocation(); //내위치 정하는 함수
        }
    }
}
