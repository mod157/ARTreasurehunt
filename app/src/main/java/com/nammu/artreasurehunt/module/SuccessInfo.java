package com.nammu.artreasurehunt.module;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by SunJae on 2017-03-14.
 */

public class SuccessInfo extends RealmObject {
    @PrimaryKey
    int number;
    String roomID;

    public void setRoomID(String id){
        roomID = id;
    }
    public String getRoomID(){
        return roomID;
    }
    public void setNumber(int number){
        this.number = number;
    }
    public int getNumber(){
        return number;
    }
}
