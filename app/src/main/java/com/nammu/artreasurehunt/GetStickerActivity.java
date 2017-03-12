
package com.nammu.artreasurehunt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.flaviofaria.kenburnsview.KenBurnsView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GetStickerActivity extends AppCompatActivity {

    private static final String LOGTAG = "GetStickerActivity";

   // private DatabaseReference appDatabase;

    @BindView(R.id.kv_get_sticker_sticker)KenBurnsView getStickerKenBurnView;
    /*@BindView(R.id.iv_get_sticker_get)CircleImageView getGetCircleImageView;
    @OnClick(R.id.iv_get_sticker_get) void getClick() {
        appDatabase.child("users").child(MyApplication.getMyUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);
                        Sticker sticker = new Sticker(questAsset, questResId);
                        user.addStickerList(sticker);
                        user.removeQuest(questLng, questLat);
                        appDatabase.child("users").child(MyApplication.getMyUid()).setValue(user);

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
        finish();
    }*/
    private String questAsset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_sticker);
        ButterKnife.bind(this);

       // appDatabase = FirebaseDatabase.getInstance().getReference();
        Intent intent = getIntent();
        questAsset = intent.getExtras().getString("questasset");
        Glide.with(this).load(questAsset).into(getStickerKenBurnView);

    }
}
