package com.example.skylite.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.skylite.MainActivity;
import com.example.skylite.R;

import java.util.ArrayList;


/**
 * Adapted form https://github.com/jaisonfdo/Onboarding
 */
public class ActivityOnBoarding extends AppCompatActivity {
    private LinearLayout pager_indicator;
    private int dotsCount;
    private ImageView[] dots;
    private ViewPager onboard_pager;
    private OnBoardAdapter mAdapter;
    private Button btn_get_started;
    private int previous_pos = 0;
    private ArrayList<OnBoardItem> onBoardItems = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        grabElementsByID();
        loadData();

        mAdapter = new OnBoardAdapter(this, onBoardItems);
        onboard_pager.setAdapter(mAdapter);
        onboard_pager.setCurrentItem(0);

        setActionListeners();
        setUiPageViewController();
    }

    private void grabElementsByID() {
        btn_get_started = findViewById(R.id.btn_get_started);
        onboard_pager = findViewById(R.id.pager_introduction);
        pager_indicator = findViewById(R.id.viewPagerCountDots);
    }


    private void setActionListeners() {
        onboard_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

                // Change the current position intimation
                for (int i = 0; i < dotsCount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(ActivityOnBoarding.this, R.drawable.non_selected_item_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(ActivityOnBoarding.this, R.drawable.selected_item_dot));
                int pos = position + 1;

                if (pos == dotsCount && previous_pos == (dotsCount - 1))
                    show_animation();
                else if (pos == (dotsCount - 1) && previous_pos == dotsCount)
                    hide_animation();

                previous_pos = pos;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        btn_get_started.setOnClickListener(v -> {
            switchToMainActivity();
            finish();
        });
    }

    // Load data into the viewpager
    public void loadData() {
        int[] header = {R.string.ob_header1, R.string.ob_header2, R.string.ob_header3};
        int[] desc = {R.string.ob_desc1, R.string.ob_desc2, R.string.ob_desc3};
        int[] imageId = {R.drawable.onboard_page1, R.drawable.onboard_page2, R.drawable.onboard_page3};

        for (int i = 0; i < imageId.length; i++) {
            OnBoardItem item = new OnBoardItem();
            item.setImageID(imageId[i]);
            item.setTitle(getResources().getString(header[i]));
            item.setDescription(getResources().getString(desc[i]));

            onBoardItems.add(item);
        }
    }

    // Button bottomUp animation
    public void show_animation() {
        Animation show = AnimationUtils.loadAnimation(this, R.anim.slide_up_anim);
        btn_get_started.startAnimation(show);
        show.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                btn_get_started.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                btn_get_started.clearAnimation();
            }
        });


    }

    // Button Topdown animation
    public void hide_animation() {
        Animation hide = AnimationUtils.loadAnimation(this, R.anim.slide_down_anim);
        btn_get_started.startAnimation(hide);
        hide.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                btn_get_started.clearAnimation();
                btn_get_started.setVisibility(View.GONE);
            }
        });
    }

    // sets up page view controller
    private void setUiPageViewController() {

        dotsCount = mAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(ActivityOnBoarding.this, R.drawable.non_selected_item_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(6, 0, 6, 0);
            pager_indicator.addView(dots[i], params);
        }
        dots[0].setImageDrawable(ContextCompat.getDrawable(ActivityOnBoarding.this, R.drawable.selected_item_dot));
    }

    private void switchToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}