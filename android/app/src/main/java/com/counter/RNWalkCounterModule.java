package com.counter;

import static android.content.Context.SENSOR_SERVICE;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RNWalkCounterModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;
  private static SharedPreferences mPrefs;

  int tSteps = 0;

  public RNWalkCounterModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    mPrefs = PreferenceManager.getDefaultSharedPreferences(this.reactContext);
  }

  @Override
  public String getName() {
    return "RNWalkCounter";
  }

  @ReactMethod
  public void getSteps(Promise promise) {
    try {
//      int spSteps = mPrefs.getInt("Steps",0);
      int spSteps = mPrefs.getInt(getDate(),0);
      promise.resolve(spSteps);
    } catch (Exception e) {
      promise.reject("Error",e);
    }
  }

  @ReactMethod
  public void testSteps(Promise promise) {
    try {
      tSteps = tSteps + 1000;
      promise.resolve(tSteps);
    } catch (Exception e) {
      promise.reject("Error",e);
    }
  }

  public String getDate() {
    Date currentTime = Calendar.getInstance().getTime();
    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
    return format.format(currentTime);
  }

}