package com.nammu.artreasurehunt.module;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;

import com.nammu.artreasurehunt.ListAdatper;
import com.nammu.artreasurehunt.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *   Created by SunJae on 2017-02-13.
 */

public class ListDialog extends Dialog {
    @BindView(R.id.rv_List)
    RecyclerView rv_list;
    Context context;
    ArrayList<SuccessInfo> list;
    Activity activity;

    public ListDialog(Context context, ArrayList<SuccessInfo> list) {
        super(context);
        this.context = context;
        this.list = list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.9f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.dialog_list);
        ButterKnife.bind(this);
        ListView();
    }

    private void ListView(){
        rv_list.setLayoutManager(new LinearLayoutManager(activity));
        ListAdatper adapter = new ListAdatper(list);
        SLog.d("size : " + adapter.getItemCount());
        rv_list.setAdapter(adapter);
    }
}
