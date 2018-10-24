package com.sawatruck.driver.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.sawatruck.driver.R;


/**
 * Created by royal on 8/26/2017.
 */
public class ActivityUtil {

    public static void goOtherActivityBottomEnter(Context context, Class<?> cls) {
        context.startActivity(new Intent(context, cls));
        ((Activity) context)
                .overridePendingTransition(R.anim.slide_in_from_bottom, 0);
    }
    public static void goOtherActivity(Context context, Class<?> cls) {
        context.startActivity(new Intent(context, cls));
        ((Activity) context)
                .overridePendingTransition(R.anim.activity_right_enter, R.anim.activity_left_exit);
    }


    public static void goOtherActivityFinish(Context context, Class<?> cls) {
        context.startActivity(new Intent(context, cls));
        ((Activity) context).finish();
        ((Activity) context)
                .overridePendingTransition(R.anim.activity_right_enter, R.anim.activity_left_exit);
    }

    public static void goOtherActivity(Context context, Intent intent) {

        context.startActivity(intent);
        ((Activity) context)
                .overridePendingTransition(R.anim.activity_right_enter, R.anim.activity_left_exit);
    }

    public static void goOtherActivityFinish(Context context, Intent intent) {
        context.startActivity(intent);
        ((Activity) context).finish();
        ((Activity) context)
                .overridePendingTransition(R.anim.activity_right_enter, R.anim.activity_left_exit);
    }

    public static void goOtherActivityFinishNoAnim(Context context, Class<?> cls) {
        context.startActivity(new Intent(context, cls));
        ((Activity) context).finish();
        ((Activity) context).overridePendingTransition(0, 0);
    }

    public static void goOtherActivityFinishNoAnim(Context context, Intent intent) {
        context.startActivity(intent);
        ((Activity) context).finish();
        ((Activity) context).overridePendingTransition(0, 0);
    }

    public static void finishSelf(Context context) {
        ((Activity) context).finish();
        ((Activity) context)
                .overridePendingTransition(R.anim.activity_left_enter, R.anim.activity_right_exit);
    }

    public static void goOtherActivityBottomIn(Context context, Intent intent) {
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(0, R.anim.slide_in_from_bottom);
    }

    public static void goOtherActivityNoAnim(Context context, Intent intent) {
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(0, 0);
    }
    public static void goOtherActivityNoAnim(Context context, Class<?> cls ) {
        context.startActivity(new Intent(context, cls));
        ((Activity) context).overridePendingTransition(0, 0);
    }

    public static void finishSelfNoAnim(Context context) {
        ((Activity) context).finish();
        ((Activity) context).overridePendingTransition(0, 0);
    }

    public static void goOtherActivityBottomEnter(Context context, Intent i) {
        context.startActivity(i);
        ((Activity) context)
                .overridePendingTransition(R.anim.slide_in_from_bottom, 0);
    }


    public static void goOtherActivityFlipTransition(Context context, Intent i) {
        context.startActivity(i);
        ((Activity) context).overridePendingTransition(R.anim.from_middle,R.anim.to_middle);
    }

}
