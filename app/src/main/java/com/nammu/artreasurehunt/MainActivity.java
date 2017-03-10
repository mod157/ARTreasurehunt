package com.nammu.artreasurehunt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    private void startTreasuerHuntPage(){
        Toast.makeText(this,"입장 합니다.",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, TreasuerActivity.class);
        startActivity(intent);
    }
}
