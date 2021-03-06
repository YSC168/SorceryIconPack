package com.sorcerer.sorcery.iconpack.ui.activities;

import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Display;
import android.view.MenuItem;

import com.sorcerer.sorcery.iconpack.R;
import com.sorcerer.sorcery.iconpack.ui.activities.base.SlideInAndOutAppCompatActivity;
import com.sorcerer.sorcery.iconpack.ui.adapters.recyclerviewAdapter.HelpAdapter;

import butterknife.BindView;

public class HelpActivity extends SlideInAndOutAppCompatActivity {

    @BindView(R.id.recyclerView_help)
    RecyclerView mRecyclerView;

    @Override
    protected int provideLayoutId() {
        return R.layout.activity_help;
    }

    private HelpAdapter mAdapter;
    private StaggeredGridLayoutManager mLayoutManager;

    @Override
    protected void init() {
        super.init();

        setToolbarBackIndicator();

        mLayoutManager = new StaggeredGridLayoutManager(calcNumOfRows(),
                StaggeredGridLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new HelpAdapter(this, calcNumOfRows());

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resize();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            super.onBackPressed();
        }
        return false;
    }

    private void resize() {
        mLayoutManager.setSpanCount(calcNumOfRows());
        mLayoutManager.requestLayout();
        mAdapter.changeSpan(calcNumOfRows());
    }

    private int calcNumOfRows() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float s = getResources().getDimension(R.dimen.help_item_size);
        return Math.max(1, (int) (size.x / s));
    }

}
