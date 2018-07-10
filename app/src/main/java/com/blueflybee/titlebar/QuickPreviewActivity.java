package com.blueflybee.titlebar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.blueflybee.titlebarlib.utils.AppUtils;
import com.blueflybee.titlebarlib.utils.KeyboardConflictCompat;
import com.blueflybee.titlebarlib.widget.CommonTitleBar;


/**
 * Created by liufei on 2017/8/29.
 */

public class QuickPreviewActivity extends SwipeBackActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_quick_preview);
    ((CommonTitleBar) findViewById(R.id.titlebar)).setListener(new CommonTitleBar.OnTitleBarListener() {
      @Override
      public void onClicked(View v, int action, String extra) {
        if (action == CommonTitleBar.ACTION_LEFT_TEXT) {
          onBackPressed();
        }
      }
    });
    ((CommonTitleBar) findViewById(R.id.titlebar_3)).showCenterProgress();
  }

  @Override
  public void onAttachedToWindow() {
    super.onAttachedToWindow();
    AppUtils.StatusBarLightMode(getWindow());
    AppUtils.transparencyBar(getWindow());
    KeyboardConflictCompat.assistWindow(getWindow());
  }
}
