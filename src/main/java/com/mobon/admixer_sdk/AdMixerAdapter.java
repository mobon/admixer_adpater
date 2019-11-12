package com.mobon.admixer_sdk;

import android.app.Activity;
import android.app.Application;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;


import com.admixer.ads.AdInfo;
import com.admixer.ads.AdMixer;
import com.admixer.ads.AdView;
import com.admixer.ads.AdViewListener;
import com.admixer.ads.InterstitialAd;
import com.admixer.ads.InterstitialAdListener;
import com.admixer.ads.PopupInterstitialAdOption;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class AdMixerAdapter {

    private static final String TAG = "Mobon_AdMixerAdapter";

    private Activity mActivity;
    private String PLACEMENT_PARAMETER;
    private AdView mBannerAdView;
    private int mAdType;
    private InterstitialAd mInterstitialAd;
    private InterstitialAd mEndingAd;
    private boolean isInterstitialLoaded;
    private boolean isTestMode;
    private View.OnClickListener mEndingListener;
    private View.OnClickListener mInterstitialListener;
    private boolean isLog;
    private Application mApplication;


    public AdMixerAdapter(Activity _activity) {
        mActivity = _activity;
    }

    public void setLog(boolean is) {
        isLog = is;
    }

    public void setTestMode(boolean is) {
        isTestMode = is;
    }

    public void setApplication(Application _app) {
        this.mApplication = _app;
    }

    public void close() {

    }

    public void init(String mediaKey, String key, int adType) {
        destroy();

        ArrayList<String> adunits = new ArrayList<String>(Arrays.asList(key));

        AdMixer.init(mActivity, mediaKey, adunits);

        this.PLACEMENT_PARAMETER = key;
        this.mAdType = adType;

        if (isLog)
            System.out.println(TAG + " : init   key : " + key + " : adtype : " + adType);

        AdInfo adInfo = new AdInfo(PLACEMENT_PARAMETER);
        adInfo.setMaxRetryCountInSlot(-1);

        switch (adType) {
            case MediationAdSize.BANNER_320_50:
                mBannerAdView = new AdView(mActivity);
                mBannerAdView.setAdInfo(adInfo, mActivity);
                setBannerLayoutParams(320, 50);
                break;
            case MediationAdSize.BANNER_320_100:
                mBannerAdView = new AdView(mActivity);
                mBannerAdView.setAdInfo(adInfo, mActivity);
                setBannerLayoutParams(320, 100);
                break;
            case MediationAdSize.BANNER_300_250:
                mBannerAdView = new AdView(mActivity);
                mBannerAdView.setAdInfo(adInfo, mActivity);
                setBannerLayoutParams(300, 250);
                break;
            case MediationAdSize.INTERSTITIAL:
                mInterstitialAd = new InterstitialAd(mActivity);
                PopupInterstitialAdOption adConfig1 = new PopupInterstitialAdOption();
                //adConfig1.setDisableBackKey(true);
                adInfo.setBackgroundAlpha(true);
                // adInfo.setInterstitialAdType(AdInfo.InterstitialAdType.Popup, adConfig1);
                mInterstitialAd.setAdInfo(adInfo, mActivity);
                break;
            case MediationAdSize.NATIVE:
                //  mNativeAd = new InMobiNative(mActivity,PLACEMENT_PARAMETER);
                break;
            case MediationAdSize.ENDING:
                mEndingAd = new InterstitialAd(mActivity);
                PopupInterstitialAdOption adConfig = new PopupInterstitialAdOption();
                adConfig.setDisableBackKey(false);
                adConfig.setButtonLeft("닫기", "#000000");
                adConfig.setButtonRight("종료", "#000000");
                adInfo.setInterstitialAdType(AdInfo.InterstitialAdType.Popup, adConfig);
                mEndingAd.setAdInfo(adInfo, mActivity);


                break;
            case MediationAdSize.VIDEO:

                break;
            default:
                mBannerAdView = new AdView(mActivity);
                mBannerAdView.setAdInfo(adInfo, mActivity);
                setBannerLayoutParams(320, 50);
                break;
        }
    }

    public Object getBannerView() {
        return mBannerAdView;
    }

    public Object getInterstitialView() {
        return mInterstitialAd;
    }


    public Object geEndingView() {
        if (mEndingAd != null) {
            return mEndingAd;
        }
        return null;
    }

    boolean isLoad = false;

    public void setAdListener(final View.OnClickListener _listner) {

        final View v = new View(mActivity);


        if (mAdType == MediationAdSize.INTERSTITIAL && mInterstitialAd != null) {
            mInterstitialListener = _listner;

            mInterstitialAd.setInterstitialAdListener(new InterstitialAdListener() {
                @Override
                public void onInterstitialAdReceived(String s, InterstitialAd interstitialAd) {
                    isInterstitialLoaded = true;
                    if (isLog)
                        System.out.println(TAG + "interstitial onAdLoaded with onAdLoadSucceeded: ");
                    Log.d(TAG, "onAdLoadSucceeded");

                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("code", MediationAdCode.AD_LISTENER_CODE_AD_LOAD);
                        v.setTag(obj);
                        _listner.onClick(v);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onInterstitialAdFailedToReceive(int i, String s, InterstitialAd interstitialAd) {
                    isInterstitialLoaded = false;
                    if (isLog)
                        System.out.println("interstitial onAdFailedToReceive : " + s);

                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("code", MediationAdCode.AD_LISTENER_CODE_ERROR);
                        obj.put("msg", s);
                        v.setTag(obj);
                        _listner.onClick(v);
                        mInterstitialAd = null;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onInterstitialAdClosed(InterstitialAd interstitialAd) {
                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("code", MediationAdCode.AD_LISTENER_CODE_AD_CLOSE);
                        v.setTag(obj);
                        _listner.onClick(v);
                        mInterstitialAd = null;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onInterstitialAdShown(String s, InterstitialAd interstitialAd) {
                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("code", MediationAdCode.AD_LISTENER_CODE_IMPRESSION);
                        v.setTag(obj);
                        _listner.onClick(v);
                        mInterstitialAd = null;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onLeftClicked(String s, InterstitialAd interstitialAd) {
                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("code", MediationAdCode.AD_LISTENER_CODE_AD_CLOSE);
                        v.setTag(obj);
                        _listner.onClick(v);
                        mInterstitialAd = null;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onRightClicked(String s, InterstitialAd interstitialAd) {
                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("code", MediationAdCode.AD_LISTENER_CODE_AD_CLOSE);
                        v.setTag(obj);
                        _listner.onClick(v);
                        mInterstitialAd = null;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            });
        } else if (mBannerAdView != null) {

            mBannerAdView.setAdViewListener(new AdViewListener() {
                @Override
                public void onReceivedAd(String s, AdView adView) {
                    if (isLog)
                        System.out.println(TAG + " Banner ad onAdLoadSucceeded  : ");

                    if (isLoad)
                        return;

                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("code", MediationAdCode.AD_LISTENER_CODE_AD_LOAD);
                        v.setTag(obj);
                        _listner.onClick(v);
                        isLoad = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailedToReceiveAd(int i, String s, AdView adView) {
                    if (isLog)
                        System.out.println(TAG + "Banner ad failed to load with error  : " + s);

                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("code", MediationAdCode.AD_LISTENER_CODE_ERROR);
                        obj.put("msg", s);
                        v.setTag(obj);
                        _listner.onClick(v);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onClickedAd(String s, AdView adView) {

                }
            });

        } else if (mAdType == MediationAdSize.ENDING && mEndingAd != null) {
            mEndingListener = _listner;

            mEndingAd.setInterstitialAdListener(new InterstitialAdListener() {
                @Override
                public void onInterstitialAdReceived(String s, InterstitialAd interstitialAd) {
                    isInterstitialLoaded = true;
                    if (isLog)
                        System.out.println(TAG + "ending onAdLoaded with onAdLoadSucceeded: ");
                    Log.d(TAG, "onAdLoadSucceeded");

                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("code", MediationAdCode.AD_LISTENER_CODE_AD_LOAD);
                        v.setTag(obj);
                        _listner.onClick(v);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onInterstitialAdFailedToReceive(int i, String s, InterstitialAd interstitialAd) {
                    isInterstitialLoaded = false;
                    if (isLog)
                        System.out.println("ending onAdFailedToReceive : " + s);

                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("code", MediationAdCode.AD_LISTENER_CODE_ERROR);
                        obj.put("msg", s);
                        v.setTag(obj);
                        _listner.onClick(v);
                        mEndingAd.onDestroy();
                        mEndingAd = null;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onInterstitialAdClosed(InterstitialAd interstitialAd) {
                    try {
                        if (mEndingAd != null) {
                            JSONObject obj = new JSONObject();
                            obj.put("code", MediationAdCode.AD_LISTENER_CODE_AD_CLOSE);
                            v.setTag(obj);
                            _listner.onClick(v);
                            mEndingAd.onDestroy();
                            mEndingAd = null;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onInterstitialAdShown(String s, InterstitialAd interstitialAd) {
                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("code", MediationAdCode.AD_LISTENER_CODE_IMPRESSION);
                        v.setTag(obj);
                        _listner.onClick(v);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onLeftClicked(String s, InterstitialAd interstitialAd) {
                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("code", MediationAdCode.AD_LISTENER_CODE_AD_CLOSE);
                        v.setTag(obj);
                        _listner.onClick(v);
                        mEndingAd.onDestroy();
                        mEndingAd = null;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onRightClicked(String s, InterstitialAd interstitialAd) {
                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("code", MediationAdCode.AD_LISTENER_CODE_FINISH_CLICK);
                        v.setTag(obj);
                        _listner.onClick(v);
                        mEndingAd.onDestroy();
                        mEndingAd = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            });
        }


    }

    public void loadAd() {

        if (isLog)
            System.out.println(TAG + "loadAd() call");
        try {

            if (mInterstitialAd != null)
                mInterstitialAd.loadInterstitial();

            if (mEndingAd != null)
                mEndingAd.loadInterstitial();


        } catch (Exception e) {
            System.out.println("AdMixer loadAd : " + e.getMessage());
        }

    }

    public boolean isLoaded() {
        if (isLog)
            System.out.println(TAG + "isLoaded() call");
        if (mAdType == MediationAdSize.INTERSTITIAL && mInterstitialAd != null && isInterstitialLoaded) {
            return true;
        } else if (mAdType == MediationAdSize.ENDING && mEndingAd != null && isInterstitialLoaded) {
            return true;
        }

        return false;
    }

    public boolean show() {
        if (isLog)
            System.out.println(TAG + "show() call");
        if (mAdType == MediationAdSize.INTERSTITIAL && mInterstitialAd != null) {
            mInterstitialAd.showInterstitial();
            return true;
        } else if (mAdType == MediationAdSize.ENDING && mEndingAd != null) {
            mEndingAd.showInterstitial();
            return true;
        }


        return false;
    }

    public void destroy() {
        if (isLog)
            System.out.println(TAG + "destory() call");

        if (mInterstitialAd != null) {
            mInterstitialAd.closeInterstitial();
            mInterstitialAd = null;
        }
        if (mEndingAd != null) {
            mEndingAd.closeInterstitial();
            mEndingAd = null;
        }
    }


    private void setBannerLayoutParams(int _width, int _height) {
        if (isLog)
            System.out.println(TAG + "setBannerLayoutParams() call");
        int width = toPixelUnits(_width);
        int height = toPixelUnits(_height);
        RelativeLayout.LayoutParams bannerLayoutParams = new RelativeLayout.LayoutParams(width, height);
        bannerLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        bannerLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mBannerAdView.setLayoutParams(bannerLayoutParams);
    }

    private int toPixelUnits(int dipUnit) {
        float density = mActivity.getResources().getDisplayMetrics().density;
        return Math.round(dipUnit * density);
    }

}