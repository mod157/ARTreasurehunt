package com.nammu.artreasurehunt;

import android.*;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.nammu.artreasurehunt.module.ListDialog;
import com.nammu.artreasurehunt.module.RealmDB;
import com.nammu.artreasurehunt.module.SLog;
import com.nammu.artreasurehunt.module.SuccessInfo;
import com.nammu.artreasurehunt.userdefinedtargets.UserDefinedTargets;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.et_startName)
    EditText et_startName;

    @OnClick(R.id.tv_startBtn)
    public void onClick(View view){
        try {
            if (et_startName.getText().toString().equals("이지스")){
                startTreasuerHuntPage();
            }
            if (et_startName.getText().toString().toUpperCase().equals("AEGIS")){
                startTreasuerHuntPage();
            }else
                Toast.makeText(this,"AEGIS(이지스)를 입력해주세요!", Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    @OnClick(R.id.iv_successList)
    public void listView(View view){
        Realm realm = RealmDB.RealmInit(this);
        RealmResults<SuccessInfo> itemResult = realm.where(SuccessInfo.class).findAll();
        if(itemResult.size() != 0){
            ArrayList<SuccessInfo> list = new ArrayList<>(itemResult);
            ListDialog listDialog = new ListDialog(this, list);
            listDialog.show();
        }else{
            Toast.makeText(this,"발견한 보물이 없습니다.",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        permissionCheck();
    }
    private void permissionCheck(){
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
               /* startCamera();
                Intent intent = new Intent(MainActivity.this, UserDefinedTargets.class);
                intent.putExtra("questlng", questLng);
                intent.putExtra("questlat", questLat);
                intent.putExtra("questasset", questAsset);
                intent.putExtra("questresid", questResId);
                startActivity(intent);*/
            }
            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {
                SLog.d("카메라 권한이 없습니다.");finish();
            }
        };

        new TedPermission(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("보물을 찾기 위해 카메라와 위치 기능을 사용합니다.")
                .setDeniedMessage("권한 설정을 하지 않으면 이용하기 어렵습니다.")
                .setPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .setGotoSettingButton(true)
                .check();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    private void startTreasuerHuntPage(){
        Toast.makeText(this,"입장 합니다.",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, TreasuerMapActivity.class);
        startActivity(intent);
    }
    long backKeyTime=0;
    @Override
    public void onBackPressed() {
        Toast toast;

        if (System.currentTimeMillis() > backKeyTime + 2000) {
            backKeyTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "\'뒤로\' 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyTime + 2000) {
            moveTaskToBack(true);
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
}
