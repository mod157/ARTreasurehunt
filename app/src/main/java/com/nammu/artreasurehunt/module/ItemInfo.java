package com.nammu.artreasurehunt.module;

/**
 * Created by SunJae on 2017-03-14.
 */

public class ItemInfo {
    private int number;
    private boolean status;

    public void setNumber(int num){number= num;}
    public int getNumber(){return number;}
    public void setStatus(boolean status){
        this.status = status;
    }
    public boolean getStatus(){return status;}

}
