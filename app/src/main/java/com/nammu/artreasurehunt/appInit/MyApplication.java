//realm 초기화

package com.nammu.artreasurehunt.appInit;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import java.lang.reflect.Field;

public class MyApplication extends Application {
    private static String myUid;
    private static String myName;
    private static String myPhotoUrl;
    private static String issueFriendImageUrl;
    private static String issueFriendName;
    private static String issueFriendUid;
    private static String issueLocationName;
    private static double currentLng;
    private static double currentLat;
    private static int stickerPosition;

    @Override
    public void onCreate() {
        super.onCreate();
        setDefaultFont(this, "DEFAULT", "font_second.ttf");
        setDefaultFont(this, "SANS_SERIF", "font_second.ttf");
        setDefaultFont(this, "SERIF", "font_second.ttf");
    }

    public static void init(){
        issueFriendImageUrl = null;
        issueFriendName = null;
        issueFriendUid = null;
        issueLocationName = null;
        stickerPosition = -1;
    }
    public static void setDefaultFont(Context ctx,
                                      String staticTypefaceFieldName, String fontAssetName) {
        final Typeface regular = Typeface.createFromAsset(ctx.getAssets(),
                fontAssetName);
        replaceFont(staticTypefaceFieldName, regular);
        }

    protected static void replaceFont(String staticTypefaceFieldName,
                                                  final Typeface newTypeface) {
        try {
            final Field StaticField = Typeface.class
            .getDeclaredField(staticTypefaceFieldName);
            StaticField.setAccessible(true);
            StaticField.set(null, newTypeface);
            } catch (NoSuchFieldException e) {
            e.printStackTrace();
            } catch (IllegalAccessException e) {
                Log.d("aa", "aa");
            }
    }

    public static String getMyUid() {
        return myUid;
    }

    public static void setMyUid(String myUid) {
        MyApplication.myUid = myUid;
    }

    public static String getMyName() {
        return myName;
    }

    public static void setMyName(String myName) {
        MyApplication.myName = myName;
    }

    public static String getMyPhotoUrl() {
        return myPhotoUrl;
    }

    public static void setMyPhotoUrl(String myPhotoUrl) {
        MyApplication.myPhotoUrl = myPhotoUrl;
    }

    public static String getIssueFriendUid() {
        return issueFriendUid;
    }

    public static void setIssueFriendUid(String issueFriendUid) {
        MyApplication.issueFriendUid = issueFriendUid;
    }

    public static String getIssueFriendImageUrl() {
        return issueFriendImageUrl;
    }

    public static void setIssueFriendImageUrl(String issueFriendImageUrl) {
        MyApplication.issueFriendImageUrl = issueFriendImageUrl;
    }

    public static String getIssueFriendName() {
        return issueFriendName;
    }

    public static void setIssueFriendName(String issueFriendName) {
        MyApplication.issueFriendName = issueFriendName;
    }

    public static int getStickerPosition() {
        return stickerPosition;
    }

    public static void setStickerPosition(int stickerPosition) {
        MyApplication.stickerPosition = stickerPosition;
    }

    public static double getCurrentLat() {
        return currentLat;
    }

    public static void setCurrentLat(double currentLat) {
        MyApplication.currentLat = currentLat;
    }

    public static double getCurrentLng() {
        return currentLng;
    }

    public static void setCurrentLng(double currentLng) {
        MyApplication.currentLng = currentLng;
    }

    public static String getIssueLocationName() {
        return issueLocationName;
    }

    public static void setIssueLocationName(String issueLocationName) {
        MyApplication.issueLocationName = issueLocationName;
    }

}
