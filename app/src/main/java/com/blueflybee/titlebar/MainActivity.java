package com.blueflybee.titlebar;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

import com.blueflybee.titlebarlib.utils.KeyboardConflictCompat;
import com.blueflybee.titlebarlib.utils.TitleBarUtils;
import com.blueflybee.titlebarlib.widget.TitleBar;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import static com.blueflybee.titlebarlib.utils.ScreenUtils.getDisplayMetrics;

public class MainActivity extends SwipeBackActivity {
  private double maxAlphaEffectHeight;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    final int position = getIntent().getIntExtra("position", 0);
    switch (position) {
      case 0:
        setContentView(R.layout.content_left_text_);
        break;
      case 1:
        setContentView(R.layout.content_left_button);
        break;
      case 2:
        setContentView(R.layout.content_left_custom_layout);
        break;
      case 3:
        setContentView(R.layout.content_right_text);
        break;
      case 4:
        setContentView(R.layout.content_right_button);
        break;
      case 5:
        setContentView(R.layout.content_right_custom_layout);
        break;
      case 6:
        setContentView(R.layout.content_center_text_marquee);
        break;
      case 7:
        setContentView(R.layout.content_center_subtext);
        break;
      case 8:
        setContentView(R.layout.content_center_custom_layout);
        initSmartTab();
        break;
      case 9:
        setContentView(R.layout.content_center_search_view);
        break;
      case 10:
        setContentView(R.layout.content_all_custom);
        break;
    }


    final TitleBar titleBar = (TitleBar) findViewById(R.id.titlebar);
    titleBar.setListener(new TitleBar.OnTitleBarListener() {
      @Override
      public void onClicked(View v, int action, String extra) {
        if (action == TitleBar.ACTION_LEFT_BUTTON
            || action == TitleBar.ACTION_LEFT_TEXT) {
          onBackPressed();
        }
      }
    });

    titleBar.setDoubleClickListener(new TitleBar.OnTitleBarDoubleClickListener() {
      @Override
      public void onClicked(View v) {
        Toast.makeText(MainActivity.this, "Titlebar double clicked!", Toast.LENGTH_SHORT).show();
      }
    });

    if (position == 1) {
      titleBar.showCenterProgress();
    }

    if (position != 8) {
      maxAlphaEffectHeight = getScreenPixelSize(this)[1] / 3;

      final GScrollView sv = (GScrollView) findViewById(R.id.sv);
      sv.post(new Runnable() {
        @Override
        public void run() {
          sv.setPadding(0, titleBar.getHeight(), 0, 0);
          sv.setClipChildren(false);
          sv.setClipToPadding(false);
        }
      });
      sv.setOnScrollChangeListener(new GScrollView.OnScrollChangeListener() {
        @Override
        public void onScrollChanged(int x, int y, int oldX, int oldY) {
          if (y <= maxAlphaEffectHeight) {
            int alpha = 255 - (int) (y / maxAlphaEffectHeight * 255);
            if (alpha > 255 || alpha < 125) return;
            String alphaHex = Integer.toString(alpha, 16).toUpperCase();
            if (alphaHex.length() == 1) {
              alphaHex = "0" + alphaHex;
            }
            String color = "#" + alphaHex + "f9f9f9";
            titleBar.setBackgroundColor(Color.parseColor(color));
          }
        }
      });
    }
  }

  private void initSmartTab() {
    TitleBar titleBar = (TitleBar) findViewById(R.id.titlebar);
    SmartTabLayout layout = (SmartTabLayout) titleBar.getCenterCustomView().findViewById(R.id.tab_list);
    ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
    viewPager.setAdapter(new TabFragmentAdapter(getSupportFragmentManager()));
    layout.setViewPager(viewPager);
  }

  @Override
  public void onAttachedToWindow() {
    super.onAttachedToWindow();
    KeyboardConflictCompat.assistWindow(getWindow());
    TitleBarUtils.StatusBarLightMode(getWindow());
    TitleBarUtils.transparencyBar(getWindow());
  }

  /**
   * 获取屏幕大小
   *
   * @param context
   * @return
   */
  private int[] getScreenPixelSize(Context context) {
    DisplayMetrics metrics = getDisplayMetrics(context);
    return new int[]{metrics.widthPixels, metrics.heightPixels};
  }
}
