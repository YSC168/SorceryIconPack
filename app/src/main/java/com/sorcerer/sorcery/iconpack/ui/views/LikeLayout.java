package com.sorcerer.sorcery.iconpack.ui.views;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CloudQueryCallback;
import com.avos.avoscloud.FindCallback;
import com.sorcerer.sorcery.iconpack.BuildConfig;
import com.sorcerer.sorcery.iconpack.R;
import com.sorcerer.sorcery.iconpack.net.leancloud.LikeBean;
import com.sorcerer.sorcery.iconpack.util.PermissionsHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * @description:
 * @author: Sorcerer
 * @date: 2016/2/29 0029
 */
public class LikeLayout extends FrameLayout {
    private static final String TAG = "LikeLayout";
    public static final String PREF_NAME = "ICON_LIKE";
    private SharedPreferences mSharedPreferences;
    private String mName;
    private Context mContext;
    private boolean mBind;

    private final float mTimeToScaleLikeImg = 1.5f;

    private int mFlag = 0;

    @BindView(R.id.textView_label_like)
    TextView mLikeText;
    @BindView(R.id.textView_label_dislike)
    TextView mDislikeText;

    @OnClick({R.id.textView_label_like, R.id.textView_label_dislike})
    void onClick(View v) {
        if (!PermissionsHelper.hasPermission((Activity) getContext(),
                PermissionsHelper.READ_PHONE_STATE_MANIFEST)
                || !PermissionsHelper.hasPermission((Activity) getContext(),
                PermissionsHelper.WRITE_EXTERNAL_STORAGE_MANIFEST)) {
            PermissionsHelper.requestPermissions((Activity) getContext(),
                    new String[]{PermissionsHelper.READ_PHONE_STATE_MANIFEST,
                            PermissionsHelper.WRITE_EXTERNAL_STORAGE_MANIFEST});
            return;
        }

        int id = v.getId();
        if (id == R.id.textView_label_like) {
            mFlag = 1;
            handleFlag(mFlag, true);
            if (mBind) {
                mSharedPreferences.edit().putInt(mName, mFlag).apply();
//                mIconBmobHelper.like(mName, true);
                like(mName, true);
            }
        } else {
            mFlag = -1;
            handleFlag(mFlag, true);
            if (mBind) {
                mSharedPreferences.edit().putInt(mName, mFlag).apply();
//                mIconBmobHelper.like(mName, false);
                like(mName, false);
            }
        }
    }


    @OnLongClick({R.id.textView_label_like, R.id.textView_label_dislike})
    boolean onLongClick(View v) {
        int id = v.getId();
        if (id == R.id.textView_label_like) {
            Toast.makeText(mContext,
                    mContext.getString(R.string.action_like),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else {
            Toast.makeText(mContext,
                    mContext.getString(R.string.action_dislike),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @BindViews({R.id.textView_label_like, R.id.textView_label_dislike})
    TextView[] mTextViews;

    public LikeLayout(Context context) {
        super(context);
        init(context);
    }

    public LikeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LikeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        View view = View.inflate(context,R.layout.layout_like, null);
        this.addView(view);
        ButterKnife.bind(this, view);

        for (TextView t : mTextViews) {
            t.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "like.ttf"));
        }
        mLikeText.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(mContext,
                        mContext.getString(R.string.action_like),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        mDislikeText.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(mContext,
                        mContext.getString(R.string.action_dislike),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    public void bindIcon(String name) {
        mBind = true;
        mName = name;
        mSharedPreferences = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        mFlag = mSharedPreferences.getInt(name, 0);
        handleFlag(mFlag, false);
    }

    private void handleFlag(int flag, boolean scale) {
        if (flag == 0) {
            mLikeText.setTextColor(ContextCompat.getColor(mContext, R.color.grey_500));
            mDislikeText.setTextColor(ContextCompat.getColor(mContext, R.color.grey_500));
        } else if (flag == 1) {
            mLikeText.setTextColor(ContextCompat.getColor(mContext, R.color.pink_500));
            mDislikeText.setTextColor(ContextCompat.getColor(mContext, R.color.grey_500));
            if (scale) {
                scale(mLikeText, true);
                scale(mDislikeText, false);
            }
        } else if (flag == -1) {
            mLikeText.setTextColor(ContextCompat.getColor(mContext, R.color.grey_500));
            mDislikeText.setTextColor(ContextCompat.getColor(mContext, R.color.blue_grey_500));
            if (scale) {
                scale(mDislikeText, true);
                scale(mLikeText, false);
            }
        }
    }

    private void scale(View target, boolean bigger) {
        if (bigger) {
            target.animate().scaleX(mTimeToScaleLikeImg).scaleY(mTimeToScaleLikeImg)
                    .setDuration(100).start();
        } else {
            target.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100)
                    .start();
        }
    }

    private void like(String name, boolean like) {
        String deviceId;
        try {
            TelephonyManager telephonyManager =
                    (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = telephonyManager.getDeviceId();
        } catch (Exception e) {
            return;
        }


        LikeBean likeBean = new LikeBean();
        likeBean.setLike(like);
        likeBean.setBuild(BuildConfig.VERSION_CODE + "");
        likeBean.setDeviceId(deviceId);
        likeBean.setIconName(name);
        likeBean.saveInBackground();
    }


}
