package com.neucore.neulink.util;

import android.util.Log;

import com.neucore.neulink.app.NeulinkConst;

public class LogUtils implements NeulinkConst{
  private static final String TAG = TAG_PREFIX+"LogUtils";
  public static boolean isDebug =true;

  public static void d(Class<?> paramClass, String paramString)
  {
    if (isDebug)
      Log.i(paramClass.getName(), paramString);
  }

  public static void d(String paramString)
  {
    if (isDebug)
      Log.d(TAG, paramString);
  }

  public static void d(String paramString1, String paramString2)
  {
    if (isDebug)
      Log.i(paramString1, paramString2);
  }

  public static void e(Class<?> paramClass, String paramString)
  {
    if (isDebug)
      Log.i(paramClass.getName(), paramString);
  }

  public static void e(String paramString)
  {
    if (isDebug)
      Log.e(TAG, paramString);
  }

  public static void e(String paramString1, String paramString2)
  {
    if (isDebug)
      Log.i(paramString1, paramString2);
  }

  public static void i(Class<?> paramClass, String paramString)
  {
    if (isDebug)
      Log.i(paramClass.getName(), paramString);
  }

  public static void i(String paramString)
  {
    if (isDebug)
      Log.i(TAG, paramString);
  }

  public static void i(String paramString1, String paramString2)
  {
    if (isDebug)
      Log.i(paramString1, paramString2);
  }

  public static void showlog(String paramString)
  {
    if (isDebug)
    {
      if (paramString == null)
        Log.i(TAG, "null");
    }
    else
      return;
  }

  public static void v(Class<?> paramClass, String paramString)
  {
    if (isDebug)
      Log.i(paramClass.getName(), paramString);
  }

  public static void v(String paramString)
  {
    if (isDebug)
      Log.v(TAG, paramString);
  }

  public static void v(String paramString1, String paramString2)
  {
    if (isDebug)
      Log.i(paramString1, paramString2);
  }
}