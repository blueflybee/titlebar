package com.blueflybee.titlebar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.blueflybee.titlebarlib.utils.TitleBarUtils;
import com.blueflybee.titlebarlib.utils.KeyboardConflictCompat;
import com.blueflybee.titlebarlib.widget.TitleBar;


/**
 * Created by liufei on 2017/8/29.
 */

public class QuickPreviewActivity extends SwipeBackActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_quick_preview);
    ((TitleBar) findViewById(R.id.titlebar)).setListener(new TitleBar.OnTitleBarListener() {
      @Override
      public void onClicked(View v, int action, String extra) {
        if (action == TitleBar.ACTION_LEFT_TEXT) {
          onBackPressed();
        }
      }
    });
    ((TitleBar) findViewById(R.id.titlebar_3)).showCenterProgress();
  }

  @Override
  public void onAttachedToWindow() {
    super.onAttachedToWindow();
    TitleBarUtils.StatusBarLightMode(getWindow());
    TitleBarUtils.transparencyBar(getWindow());
    KeyboardConflictCompat.assistWindow(getWindow());
  }
}
