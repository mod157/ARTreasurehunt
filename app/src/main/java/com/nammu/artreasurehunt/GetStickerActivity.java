
package com.nammu.artreasurehunt;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import com.nammu.artreasurehunt.appInit.MyApplication;
import com.nammu.artreasurehunt.module.RealmDB;
import com.nammu.artreasurehunt.module.SLog;
import com.nammu.artreasurehunt.module.SocketIO;
import com.nammu.artreasurehunt.module.SuccessInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GetStickerActivity extends AppCompatActivity {


   // private DatabaseReference appDatabase;

    @BindView(R.id.iv_fail)
    ImageView iv_fail;
    @BindView(R.id.iv_Success)
    ImageView iv_Success;

    @OnClick({R.id.iv_Success, R.id.iv_fail})
    public void onClick(View view){
        if(view.getId() == R.id.iv_Success){
            SuccessInfo successInfo = new SuccessInfo();
            successInfo.setNumber(MyApplication.getItemNumber());
            successInfo.setRoomID("AEGIS");
            RealmDB.InsertOrUpdate(this,successInfo);
        }
        finish();
    }
    @Override
    public void onBackPressed() {}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_sticker);
        SLog.d("Sticker Start");
        ButterKnife.bind(this);
        SocketIO socketIO = new SocketIO("220.149.242.46:50300");
        String[] titles = {"number", "ItemStatus"};
        String bol;
        if(MyApplication.getItemStatus()){
            bol = "1";
        }else{
            bol = "0";
        }
        String[] message = {MyApplication.getItemNumber()+"", bol};
        socketIO.sendMessage("noEvent", titles, message);
        if(MyApplication.getItemStatus()){
            SLog.d("T Status : " + MyApplication.getItemStatus());
            iv_Success.setVisibility(View.VISIBLE);
            iv_fail.setVisibility(View.GONE);
        }else{
            SLog.d("F Status : " + MyApplication.getItemStatus());
            iv_fail.setVisibility(View.VISIBLE);
            iv_Success.setVisibility(View.GONE);
        }
    }
}
