package com.blueflybee.titlebarlib.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * TitleBarUtils
 */
public class TitleBarUtils {

  private TitleBarUtils() {
    throw new AssertionError();
  }

  public static int generateViewId() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      return View.generateViewId();
    } else {
      return UUID.randomUUID().hashCode();
    }
  }


  public static boolean supportStatusBarLightMode(Context context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      Window window = ((Activity) context).getWindow();
      if (TitleBarUtils.MIUISetStatusBarLightMode(window, true)
          || TitleBarUtils.FlymeSetStatusBarLightMode(window, true)
          || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        return true;
      }
    }
    return false;
  }

  public static boolean supportStatusBarLightMode(Window window) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      if (TitleBarUtils.MIUISetStatusBarLightMode(window, true)
          || TitleBarUtils.FlymeSetStatusBarLightMode(window, true)
          || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        return true;
      }
    }
    return false;
  }

  /**
   * 设置状态栏字体图标为深色，需要MIUIV6以上
   *
   * @param window 需要设置的窗口
   * @param dark   是否把状态栏字体及图标颜色设置为深色
   * @return boolean 成功执行返回true
   */
  public static boolean MIUISetStatusBarLightMode(Window window, boolean dark) {
    boolean result = false;
    if (window != null) {
      Class clazz = window.getClass();
      try {
        int darkModeFlag = 0;
        Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
        Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
        darkModeFlag = field.getInt(layoutParams);
        Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
        if (dark) {
          extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
        } else {
          extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
        }
        result = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          //开发版 7.7.13 及以后版本采用了系统API，旧方法无效但不会报错，所以两个方式都要加上
          if (dark) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
          } else {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
          }
        }
      } catch (Exception e) {
      }
    }
    return result;
  }

  /**
   * 设置状态栏图标为深色和魅族特定的文字风格
   * 可以用来判断是否为Flyme用户
   *
   * @param window 需要设置的窗口
   * @param dark   是否把状态栏字体及图标颜色设置为深色
   * @return boolean 成功执行返回true
   */
  public static boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
    boolean result = false;
    if (window != null) {
      try {
        WindowManager.LayoutParams lp = window.getAttributes();
        Field darkFlag = WindowManager.LayoutParams.class
            .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
        Field meizuFlags = WindowManager.LayoutParams.class
            .getDeclaredField("meizuFlags");
        darkFlag.setAccessible(true);
        meizuFlags.setAccessible(true);
        int bit = darkFlag.getInt(null);
        int value = meizuFlags.getInt(lp);
        if (dark) {
          value |= bit;
        } else {
          value &= ~bit;
        }
        meizuFlags.setInt(lp, value);
        window.setAttributes(lp);
        result = true;
      } catch (Exception e) {
      }
    }
    return result;
  }

  /**
   * 设置状态栏黑色字体图标，
   * 适配4.4以上版本MIUIV、Flyme和6.0以上版本其他Android
   *
   * @param window
   * @return 1:MIUUI 2:Flyme 3:android6.0
   */
  public static int StatusBarLightMode(Window window) {
    int result = 0;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      if (MIUISetStatusBarLightMode(window, true)) {
        result = 1;
      } else if (FlymeSetStatusBarLightMode(window, true)) {
        result = 2;
      } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        result = 3;
      }
    }
    return result;
  }

  public static int StatusBarDarkMode(Window window) {
    int result = 0;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      if (MIUISetStatusBarLightMode(window, false)) {
        result = 1;
      } else if (FlymeSetStatusBarLightMode(window, false)) {
        result = 2;
      } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        result = 3;
      }
    }
    return result;
  }

  /**
   * 已知系统类型时，设置状态栏黑色字体图标。
   * 适配4.4以上版本MIUIV、Flyme和6.0以上版本其他Android
   *
   * @param window
   * @param type   1:MIUUI 2:Flyme 3:android6.0
   */
  public static void StatusBarLightMode(Window window, int type) {
    if (type == 1) {
      MIUISetStatusBarLightMode(window, true);
    } else if (type == 2) {
      FlymeSetStatusBarLightMode(window, true);
    } else if (type == 3) {
      window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

  }

  /**
   * 修改状态栏为全透明
   *
   * @param window
   */
  public static void transparencyBar(Window window) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      if (MIUISetStatusBarLightMode(window, true)) {
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      } else if (FlymeSetStatusBarLightMode(window, true)) {
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
      }
    }
  }

  public static int getStatusBarHeight(Context context) {
    int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
    return context.getResources().getDimensionPixelSize(resourceId);
  }


  public static float dp2Px(Context context, float dp) {
    if (context == null) {
      return -1;
    }
    return dp * density(context);
  }

  public static float density(Context context) {
    return context.getResources().getDisplayMetrics().density;
  }

  public static int dp2PxInt(Context context, float dp) {
    return (int) (dp2Px(context, dp) + 0.5f);
  }

  public static DisplayMetrics getDisplayMetrics(Context context) {
    Activity activity;
    if (!(context instanceof Activity) && context instanceof ContextWrapper) {
      activity = (Activity) ((ContextWrapper) context).getBaseContext();
    } else {
      activity = (Activity) context;
    }
    DisplayMetrics metrics = new DisplayMetrics();
    activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
    return metrics;
  }

  /**
   * 获取屏幕大小
   *
   * @param context
   * @return
   */
  public static int[] getScreenPixelSize(Context context) {
    DisplayMetrics metrics = getDisplayMetrics(context);
    return new int[]{metrics.widthPixels, metrics.heightPixels};
  }

  public static void hideSoftInputKeyBoard(Context context, View focusView) {
    if (focusView != null) {
      IBinder binder = focusView.getWindowToken();
      if (binder != null) {
        InputMethodManager imd = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imd.hideSoftInputFromWindow(binder, InputMethodManager.HIDE_IMPLICIT_ONLY);
      }
    }
  }

  public static void showSoftInputKeyBoard(Context context, View focusView) {
    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.showSoftInput(focusView, InputMethodManager.SHOW_FORCED);
  }

}
