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
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nammu.artreasurehunt.SampleApplication.utils.LoadingDialogHandler;
import com.nammu.artreasurehunt.appInit.MyApplication;
import com.nammu.artreasurehunt.module.ItemInfo;
import com.nammu.artreasurehunt.module.RealmDB;
import com.nammu.artreasurehunt.module.SLog;
import com.nammu.artreasurehunt.module.SocketIO;
import com.nammu.artreasurehunt.service.GpsInfo;
import com.nammu.artreasurehunt.userdefinedtargets.UserDefinedTargets;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class TreasuerMapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final int GET_STICKER_DISTANCE = 15;
    private final int REQUEST_CODE_GPS = 20000;
    private static final int REQUEST_CODE_LOCATION = 2000;//임의의 정수로 정의
    // private int questResId;
    private GoogleApiClient mGoogleApiClient = null;
    private LocationRequest mLocationRequest;
    private LocationManager locationManager;
    private String provider;
    private GoogleMap arMap;
    private MapFragment mapFragment;
    boolean setGPS = false;
    private LatLng myLocation;
    private double questLng = 127.0078303;
    private double questLat = 37.267509;

    private boolean locationTag = true;
    private Marker mMarker;
    private SocketIO socketManager;
    LatLng currentPosition;
    Marker current_marker = null;
    List<Marker> previous_marker = null;
    ArrayList<ItemInfo> statusMarker = null;

    protected synchronized void buildGoogleApiClient() {
        SLog.d("buildGoogleAPI");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    @OnClick(R.id.btn_treasuerScan)
    public void scanClick(View view) {
        checkingTreasuer(view);
    }

    @BindView(R.id.iv_mylocation)
    ImageView iv_mylocation;

    @OnClick(R.id.iv_mylocation)
    public void mylocationButton(View view) {locationChange(true);iv_mylocation.setVisibility(View.GONE);}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasuer_map);
        ButterKnife.bind(this);
        SLog.d("MapCreate");
        previous_marker = new ArrayList<>();
        statusMarker = new ArrayList<>();
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        socketManager = new SocketIO("220.149.242.46:50300");
        messageSend();
    }
    private void messageSend(){
        socketManager.socketListener("itemlist",markListListener);
        socketManager.socketListener("noEvent", updateListener);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        SLog.d("MapReady");
        /*arMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationService();
        //GpsInfo gps = new GpsInfo(this);
        arMap.setMyLocationEnabled(true);
        arMap.getUiSettings().setMyLocationButtonEnabled(true);
        //markAdd();*/

        arMap = googleMap;
        LatLng latLng = new LatLng(questLat, questLng);
        arMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        arMap.animateCamera(CameraUpdateFactory.zoomTo(15));


        arMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {

            @Override
            public void onMapLoaded() {
                SLog.d("onMapLoaded");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkLocationPermission();
                } else {

                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !setGPS) {
                        SLog.d("onMapLoaded");
                        showGPSDisabledAlertToUser();
                    }

                    if (mGoogleApiClient == null) {

                        buildGoogleApiClient();
                    }
                    arMap.setMyLocationEnabled(true);
                }

            }
        });


        //구글 플레이 서비스 초기화
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();

                arMap.setMyLocationEnabled(true);
            } else {
                arMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                arMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            }
        } else {
            buildGoogleApiClient();
            arMap.setMyLocationEnabled(true);
        }

    }

    public boolean checkLocationPermission() {
        SLog.d("checkLocationPermission");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                //퍼미션 요청을 위해 UI를 보여줘야 하는지 검사
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_LOCATION);
                } else
                    //UI보여줄 필요 없이 요청
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_LOCATION);

                return false;
            } else {

                SLog.d("checkLocationPermission" + "이미 퍼미션 획득한 경우");

                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !setGPS) {
                    SLog.d("checkLocationPermission Version >= M");
                    showGPSDisabledAlertToUser();
                }

                if (mGoogleApiClient == null) {
                    SLog.d("checkLocationPermission " + "mGoogleApiClient==NULL");
                    buildGoogleApiClient();
                } else SLog.d("checkLocationPermission " + "mGoogleApiClient!=NULL");

                if (mGoogleApiClient.isConnected())
                    SLog.d("checkLocationPermission" + "mGoogleApiClient 연결되 있음");
                else SLog.d("checkLocationPermission" + "mGoogleApiClient 끊어져 있음");


                mGoogleApiClient.reconnect();//이미 연결되 있는 경우이므로 다시 연결

                arMap.setMyLocationEnabled(true);
            }
        } else {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !setGPS) {
                SLog.d("checkLocationPermission Version < M");
                showGPSDisabledAlertToUser();
            }

            if (mGoogleApiClient == null) {
                buildGoogleApiClient();
            }
            arMap.setMyLocationEnabled(true);
        }

        //현재 위치 버튼 눌렀을 경우 현재 위치로 돌아오도록 처리
        arMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {

            @Override
            public boolean onMyLocationButtonClick() {
                //locationTag = !locationTag;
                SLog.d("location Tag :" + locationTag);
                if (locationTag) {
                    locationChange(true);
                    mGoogleApiClient.reconnect();
                } else {
                    locationChange(false);
                    iv_mylocation.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });

        return true;
    }

    private void locationChange(boolean isLocation) {
        locationTag = !locationTag;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        arMap.setMyLocationEnabled(isLocation);
    }

    private void markAdd() {
        SLog.d("Mark 추가");
        //Todo map에 여러 개 뿌리기
        LatLng latLng = new LatLng(questLat, questLng);
        Marker marker = arMap.addMarker(new MarkerOptions()
                .title("보물")
                .position(latLng));
        arMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
    }

    /*@Override
    public void onLocationChanged(Location location) {
        SLog.d("location ChangeH");
        if (locationTag) {//한번만 위치를 가져오기 위해서 tag를 주었습니다
            questLat = location.getLatitude();
            questLng = location.getLongitude();
            SLog.d("lat : lng = " + questLat + " : " + questLng);
            locationTag = false;
        }
    }*/
    @Override
    public void onLocationChanged(Location location) {
        if (arMap.isMyLocationEnabled()) {
            SLog.d("LocationChnage");
            currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

            if (current_marker != null)
                current_marker.remove();

            //현재 위치에 마커 생성
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            myLocation = latLng;
       /* MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("현재위치");
        current_marker = arMap.addMarker(markerOptions);*/

            //지도 상에서 보여주는 영역 이동
            arMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            arMap.animateCamera(CameraUpdateFactory.zoomTo(20));
           // arMap.getUiSettings().setCompassEnabled(true);


       /*  //지오코더... GPS를 주소로 변환
        //Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        // Address found using the Geocoder.
       List<Address> addresses = null;

        try {
            // Using getFromLocation() returns an array of Addresses for the area immediately
            // surrounding the given latitude and longitude. The results are a best guess and are
            // not guaranteed to be accurate.
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, we get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = "지오코더 서비스 사용불가";
            Toast.makeText( this, errorMessage, Toast.LENGTH_LONG).show();
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = "잘못된 GPS 좌표";
            Toast.makeText( this, errorMessage, Toast.LENGTH_LONG).show();

        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "주소 미발견";
                Log.e(TAG, errorMessage);
            }
            Toast.makeText( this, errorMessage, Toast.LENGTH_LONG).show();
        } else {
            Address address = addresses.get(0);
            Toast.makeText( this, address.getAddressLine(0).toString(), Toast.LENGTH_LONG).show();
        }*/
        }else{

        }
    }

    public void checkingTreasuer(View v) {
        Location currentLocation = new Location(LocationManager.GPS_PROVIDER);
        currentLocation.setLongitude(myLocation.longitude);
        currentLocation.setLatitude(myLocation.latitude);
        Location treasherLocation = new Location(LocationManager.GPS_PROVIDER);
        for(int i = 0 ; i< previous_marker.size(); i++){
            Marker marker = previous_marker.get(i);
            treasherLocation.setLongitude(marker.getPosition().longitude);
            treasherLocation.setLatitude(marker.getPosition().latitude);
            int distance = (int) currentLocation.distanceTo(treasherLocation);
            SLog.d("distacne : " + distance);
            if (distance < GET_STICKER_DISTANCE) {
                ItemInfo info = statusMarker.get(i);
                MyApplication.setItemNumber(info.getNumber());
                MyApplication.setItemStatus(info.getStatus());
                Intent intent = new Intent(this, UserDefinedTargets.class);
                startActivity(intent);
                return;
            }
        }
        Toast.makeText(this, "주변에 아무것도 없습니다.", Toast.LENGTH_SHORT).show();
        //int distance = (int) currentLocation.distanceTo(qusetLocation);
        //int distance = 40;
        /*SLog.d("distacne : " + distance);
        if (distance < GET_STICKER_DISTANCE) {
            Intent intent = new Intent(this, UserDefinedTargets.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "주변에 아무것도 없습니다.", Toast.LENGTH_SHORT).show();
        }*/

    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS가 비활성화 되어있습니다. 활성화 할까요?")
                .setCancelable(false)
                .setPositiveButton("설정", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(callGPSSettingIntent, REQUEST_CODE_GPS);
                    }
                });

        alertDialogBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
    Emitter.Listener updateListener = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            final JSONObject obj = (JSONObject) args[0];
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SLog.d("updateListener");
                    try {
                        if(obj.getString("status").equals("0")) {
                            statusMarker.get(obj.getInt("number")).setStatus(false);
                        }else {
                            statusMarker.get(obj.getInt("number")).setStatus(true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
    Emitter.Listener markListListener = new Emitter.Listener(){

        @Override
        public void call(Object... args) {
            final JSONObject obj = (JSONObject)args[0];
            //서버에서 보낸 JSON객체를 사용할 수 있습니다.
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //SLog.d("listen OK\n" + obj);
                    try {
                        SLog.d("Item status : " + obj.getString("ItemStatus"));
                        SLog.d("Item Latlog : " + obj.getDouble("Lat") +":"+ obj.getDouble("Log"));
                        ItemInfo info = new ItemInfo();
                        if(obj.getString("ItemStatus").equals("0")) {
                            info.setNumber(obj.getInt("number"));
                            info.setStatus(false);
                            statusMarker.add(info);
                        }else {
                            info.setNumber(obj.getInt("number"));
                            info.setStatus(true);
                            statusMarker.add(info);
                        }
                        LatLng latLng = new LatLng(obj.getDouble("Lat"), obj.getDouble("Log"));
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title("보물");
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.markimg_32));
                        Marker marker = arMap.addMarker(markerOptions);
                        previous_marker.add(marker);
                        SLog.d("list size :" + previous_marker.size());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    //GPS 활성화를 위한 다이얼로그의 결과 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_GPS:
                //사용자가 GPS 활성 시켰는지 검사
                if (locationManager == null)
                    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    // GPS 가 ON으로 변경되었을 때의 처리.
                    setGPS = true;

                    mapFragment.getMapAsync(this);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_LOCATION: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //퍼미션이 허가된 경우
                    if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED))
                    {

                        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !setGPS)
                        {
                            SLog.d("onRequestPermissionsResult");
                            showGPSDisabledAlertToUser();
                        }


                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        arMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "퍼미션 취소", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        SLog.d("onConnected" );

        if ( locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            setGPS = true;

        mLocationRequest = new LocationRequest();
        //mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);


        if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            SLog.d("onConnected " + "getLocationAvailability mGoogleApiClient.isConnected()="+mGoogleApiClient.isConnected() );
            if ( !mGoogleApiClient.isConnected()  ) mGoogleApiClient.connect();


            // LocationAvailability locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient);

            if ( setGPS && mGoogleApiClient.isConnected() )//|| locationAvailability.isLocationAvailable() )
            {
                SLog.d( "onConnected " + "requestLocationUpdates" );
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

                Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if ( location == null ) return;

                //현재 위치에 마커 생성
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                /*MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("현재위치");
                arMap.addMarker(markerOptions);*/

                //지도 상에서 보여주는 영역 이동
                arMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                arMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            }

        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        previous_marker.clear();
        statusMarker.clear();
        socketManager.sendMessage("items");
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }


    @Override
    public void onPause() {
        if ( mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        SLog.d("OnDestroy");

        if (mGoogleApiClient != null) {
            mGoogleApiClient.unregisterConnectionCallbacks(this);
            mGoogleApiClient.unregisterConnectionFailedListener(this);

            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }

            mGoogleApiClient.disconnect();
            mGoogleApiClient = null;
        }

        super.onDestroy();
        socketManager.socketDeconnetion();
    }
}
