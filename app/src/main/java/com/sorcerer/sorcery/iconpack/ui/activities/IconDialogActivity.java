package com.sorcerer.sorcery.iconpack.ui.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.socks.library.KLog;
import com.sorcerer.sorcery.iconpack.BuildConfig;
import com.sorcerer.sorcery.iconpack.R;
import com.sorcerer.sorcery.iconpack.SorceryIcons;
import com.sorcerer.sorcery.iconpack.ui.activities.base.ToolbarActivity;
import com.sorcerer.sorcery.iconpack.ui.views.LikeLayout;
import com.sorcerer.sorcery.iconpack.util.AppInfoUtil;
import com.sorcerer.sorcery.iconpack.util.DisplayUtil;
import com.sorcerer.sorcery.iconpack.util.StringUtil;
import com.sorcerer.sorcery.iconpack.util.TimeWatch;
import com.sorcerer.sorcery.iconpack.util.ViewUtil;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @description:
 * @author: Sorcerer
 * @date: 2016/3/22 0022
 */

public class IconDialogActivity extends ToolbarActivity {
    @BindView(R.id.toolbar_icon_dialog)
    Toolbar mToolbar;

    @BindView(R.id.likeLayout)
    LikeLayout mLikeLayout;

    @BindView(R.id.textView_dialog_title)
    TextView mTitleTextView;

    @BindView(R.id.imageView_dialog_icon)
    ImageView mIconImageView;

    @BindView(R.id.relativeLayout_icon_dialog_background)
    View mBackground;

    @BindView(R.id.linearLayout_dialog_icon_show)
    ViewGroup mRoot;

    public static final String EXTRA_RES = "EXTRA_RES";
    public static final String EXTRA_NAME = "EXTRA_NAME";
    public static final String EXTRA_LABEL = "EXTRA_LABEL";

    private String mLabel;
    private String mName;
    private int mRes;
    private String mComponent;
    private String mPackageName;

    private ImageView mOriginImage;

    @Override
    protected int provideLayoutId() {
        return R.layout.dialog_icon_show;
    }

    @Override
    protected void hookBeforeSetContentView() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Fade());
            getWindow().setExitTransition(new Fade());
        }
    }

    TimeWatch mTimeWatch = new TimeWatch();


    @Override
    protected Toolbar provideToolbar() {
        return mToolbar;
    }

    @Override
    protected void init() {

        mLabel = getIntent().getStringExtra(EXTRA_LABEL);
        mName = getIntent().getStringExtra(EXTRA_NAME);
        mRes = getIntent().getIntExtra(EXTRA_RES, 0);

        mTimeWatch.resetTime();

//        mLabel = SorceryIcons.sTmpIconBean.getLabel();
//        mName = SorceryIcons.sTmpIconBean.getName();
//        mRes = SorceryIcons.sTmpIconBean.getRes();

        KLog.d(mTimeWatch.consumeTime(true));

        if (mRes == 0) {
            this.finish();
        }

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

        mTitleTextView.setText(mLabel);

        mIconImageView.setImageResource(mRes);

        KLog.d(mTimeWatch.consumeTime(true));

        mLikeLayout.bindIcon(mName);

        KLog.d(mTimeWatch.consumeTime(true));


        mBackground.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!ViewUtil.isPointInsideView(event.getX(),
                        event.getY(),
                        findViewById(R.id.cardView_icon_dialog_card))) {
                    onBackPressed();
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mTimeWatch.resetTime();

        Observable.just(mName)
                .subscribeOn(Schedulers.newThread())
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        return AppInfoUtil.getComponentByName(IconDialogActivity.this, mName);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String component) {
                        mComponent = component;
                        mPackageName = StringUtil.componentInfoToPackageName(mComponent);
                        if (mMenu != null) {
                            onCreateOptionsMenu(mMenu);
                        }
                    }
                });

        KLog.d(mTimeWatch.consumeTime(true));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT < 21) {
            overridePendingTransition(0, R.anim.fast_fade_out);
        }
    }

    private Menu mMenu;

    private boolean hasInitMenu = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        if (mComponent != null && !hasInitMenu) {
            hasInitMenu = true;
            getMenuInflater().inflate(R.menu.menu_icon_dialog, menu);

            MenuItem showOrigin = menu.findItem(R.id.action_show_origin_icon);
            if (AppInfoUtil.isPackageInstalled(mContext,
                    StringUtil.componentInfoToPackageName(mComponent))) {
                showOrigin.setVisible(true);
                Drawable icon = showOrigin.getIcon();
                icon.setAlpha((int) (255 * 0.5));
                showOrigin.setIcon(icon);
            } else {
                showOrigin.setVisible(false);
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_show_in_store) {
            final String appPackageName = StringUtil.componentInfoToPackageName(mComponent);
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + appPackageName)));
                onBackPressed();
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id="
                                + appPackageName)));
            }
        } else if (id == R.id.action_show_origin_icon) {
            try {
                if (mOriginImage == null) {
                    mOriginImage = new ImageView(mContext);
                    mOriginImage.setImageDrawable(
                            getPackageManager().getApplicationIcon(
                                    StringUtil.componentInfoToPackageName(mComponent)
                            )
                    );

                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                            (int) mContext.getResources()
                                    .getDimension(R.dimen.dialog_icon_size),
                            (int) mContext.getResources().getDimension(R.dimen.dialog_icon_size)
                    );
                    mOriginImage.setLayoutParams(params);
                    mOriginImage.setPadding(DisplayUtil.dip2px(mContext, 8), 0, 0, 0);
                }
                if (mRoot.getChildCount() > 1) {
                    mRoot.removeView(mOriginImage);
                } else {
                    mRoot.addView(mOriginImage);
                }
            } catch (Exception e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
