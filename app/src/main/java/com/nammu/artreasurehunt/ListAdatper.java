package com.nammu.artreasurehunt;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nammu.artreasurehunt.module.SLog;
import com.nammu.artreasurehunt.module.SuccessInfo;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by SunJae on 2017-02-13.
 */

public class ListAdatper extends RecyclerView.Adapter<ListAdatper.ViewHolder>  {
    ArrayList<SuccessInfo> list;
    public ListAdatper(ArrayList<SuccessInfo> list){
        SLog.d("Create");
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_list, parent, false);
        return new ListAdatper.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SuccessInfo item = list.get(position);
        holder.tv_dialog.setText(item.getNumber() + " 번 보물");

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_dialog)
        TextView tv_dialog;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
