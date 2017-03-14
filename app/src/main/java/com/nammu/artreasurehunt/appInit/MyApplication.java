//realm 초기화

package com.nammu.artreasurehunt.appInit;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import java.lang.reflect.Field;

public class MyApplication extends Application {
    private static int itemNumber;
    private static boolean itemStatus;

    @Override
    public void onCreate() {
        super.onCreate();
        setDefaultFont(this, "DEFAULT", "font_second.ttf");
        setDefaultFont(this, "SANS_SERIF", "font_second.ttf");
        setDefaultFont(this, "SERIF", "font_second.ttf");
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

    public static int getItemNumber() {
        return itemNumber;
    }

    public static void setItemNumber(int number) {
        MyApplication.itemNumber = number;
    }

    public static boolean getItemStatus() {
        return itemStatus;
    }

    public static void setItemStatus(boolean status) {
        MyApplication.itemStatus = status;
    }
}
