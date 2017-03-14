package com.nammu.artreasurehunt.module;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by SunJae on 2017-01-26.
 */

public class RealmDB {

    public static Realm RealmInit(Context context){
        Realm realm = null;
        realm.init(context);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(realmConfiguration);
        realm = Realm.getDefaultInstance();
        return realm;
    }

    public static void InsertOrUpdate(Context context, final SuccessInfo data){
        Realm realm = RealmInit(context);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(data);
            }
        });
        realm.close();
    }
}
