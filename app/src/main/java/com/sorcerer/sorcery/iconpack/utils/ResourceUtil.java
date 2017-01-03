package com.sorcerer.sorcery.iconpack.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;

import com.sorcerer.sorcery.iconpack.BuildConfig;
import com.sorcerer.sorcery.iconpack.R;

import java.util.Random;

import timber.log.Timber;

/**
 * @description:
 * @author: Sorcerer
 * @date: 2016/5/16
 */
public class ResourceUtil {

    public static String getString(Context context, @StringRes int resId) {
        return context.getString(resId);
    }

    public static String getStringFromResString(Context context, String resString) {
        try {
            int id = context.getResources()
                    .getIdentifier(resString, "string", context.getPackageName());
            return context.getResources().getString(id);
        } catch (Exception e) {
            Timber.e(e);
            return "load fail";
        }
    }

    public static int getColor(Context context, @ColorRes int resId) {
        return ContextCompat.getColor(context, resId);
    }

    public static Drawable getDrawable(Context context, @DrawableRes int resId) {
        return ContextCompat.getDrawable(context, resId);
    }

    /**
     * @param context context
     * @param resId   resource id
     * @param alpha   0~255
     * @return final drawable
     */
    public static Drawable getDrawableWithAlpha(Context context, int resId, int alpha) {
        Drawable drawable = getDrawable(context, resId);
        drawable.setAlpha(alpha);
        return drawable;
    }

    public static Drawable getDrawableWithAlpha(Context context, int resId, float alpha) {
        return getDrawableWithAlpha(context, resId, alpha * 255);
    }

    /**
     * @param context to get resource
     * @param resName if need "R.array.example", resName is "example"
     * @return string array
     */
    public static String[] getStringArray(Context context, String resName) {
        try {
            int id = getResourceIdFromString(context, resName, "array");
            return context.getResources().getStringArray(id);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
            return new String[]{"**load fail**"};
        }
    }

    public static String[] getStringArray(Context context, int id) {
        return context.getResources().getStringArray(id);
    }

    public static int getResourceIdFromString(Context context, String resName, String resFold) {
        return context.getResources().getIdentifier(resName, resFold, context.getPackageName());
    }
}
