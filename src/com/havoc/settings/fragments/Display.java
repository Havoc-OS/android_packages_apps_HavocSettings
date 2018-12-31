/*
 * Copyright (C) 2018 Havoc-OS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.havoc.settings.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.UserHandle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.R; 
import com.android.settings.SettingsPreferenceFragment;

import com.havoc.settings.preferences.SystemSettingSeekBarPreference;
import com.havoc.settings.preferences.SystemSettingSwitchPreference;

public class Display extends SettingsPreferenceFragment implements 
        Preference.OnPreferenceChangeListener {

    public static final String TAG = "Display";

    private static final String SMART_PIXELS = "smart_pixels";
    private static final String ON_POWER_SAVE = "smart_pixels_on_power_save";
    private static final String SYSUI_ROUNDED_SIZE = "sysui_rounded_size";
    private static final String SYSUI_ROUNDED_CONTENT_PADDING = "sysui_rounded_content_padding";

    private PreferenceCategory mSmartPixelsCategory; 
    private SystemSettingSwitchPreference mSmartPixelsOnPowerSave;
    private ListPreference mVelocityFriction;
    private ListPreference mPositionFriction;
    private ListPreference mVelocityAmplitude;
    private SystemSettingSeekBarPreference mCornerRadius;
    private SystemSettingSeekBarPreference mContentPadding;

    ContentResolver resolver; 

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.havoc_settings_display);

        resolver = getActivity().getContentResolver(); 

        float velFriction = Settings.System.getFloatForUser(resolver,
                    Settings.System.STABILIZATION_VELOCITY_FRICTION,
                    0.1f,
                    UserHandle.USER_CURRENT);
        mVelocityFriction = (ListPreference) findPreference("stabilization_velocity_friction");
    	mVelocityFriction.setValue(Float.toString(velFriction));
    	mVelocityFriction.setSummary(mVelocityFriction.getEntry());
    	mVelocityFriction.setOnPreferenceChangeListener(this);
    	
    	float posFriction = Settings.System.getFloatForUser(resolver,
                    Settings.System.STABILIZATION_POSITION_FRICTION,
                    0.1f,
                    UserHandle.USER_CURRENT);
        mPositionFriction = (ListPreference) findPreference("stabilization_position_friction");
    	mPositionFriction.setValue(Float.toString(posFriction));
    	mPositionFriction.setSummary(mPositionFriction.getEntry());
    	mPositionFriction.setOnPreferenceChangeListener(this);
    
    	int velAmplitude = Settings.System.getIntForUser(resolver,
                    Settings.System.STABILIZATION_VELOCITY_AMPLITUDE,
                    8000,
                    UserHandle.USER_CURRENT);
        mVelocityAmplitude = (ListPreference) findPreference("stabilization_velocity_amplitude");
    	mVelocityAmplitude.setValue(Integer.toString(velAmplitude));
    	mVelocityAmplitude.setSummary(mVelocityAmplitude.getEntry());
    	mVelocityAmplitude.setOnPreferenceChangeListener(this);

        mSmartPixelsOnPowerSave = (SystemSettingSwitchPreference) findPreference(ON_POWER_SAVE);
        updateSmartPixelsPreference();
        updateDependency();

        Resources res = null;
        Context mContext = getContext();

        try {
            res = mContext.getPackageManager().getResourcesForApplication("com.android.systemui");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        float displayDensity = getResources().getDisplayMetrics().density;

        // Rounded Corner Radius
        int resourceIdRadius = res.getIdentifier("com.android.systemui:dimen/rounded_corner_radius", null, null);
        mCornerRadius = (SystemSettingSeekBarPreference) findPreference(SYSUI_ROUNDED_SIZE);
        int cornerRadius = Settings.Secure.getInt(mContext.getContentResolver(), Settings.Secure.SYSUI_ROUNDED_SIZE,
                (int) (res.getDimension(resourceIdRadius) / displayDensity));
        mCornerRadius.setValue(cornerRadius / 1);
        mCornerRadius.setOnPreferenceChangeListener(this);

        // Rounded Content Padding
        int resourceIdPadding = res.getIdentifier("com.android.systemui:dimen/rounded_corner_content_padding", null,
                null);
        mContentPadding = (SystemSettingSeekBarPreference) findPreference(SYSUI_ROUNDED_CONTENT_PADDING);
        int contentPadding = Settings.Secure.getInt(mContext.getContentResolver(),
                Settings.Secure.SYSUI_ROUNDED_CONTENT_PADDING,
                (int) (res.getDimension(resourceIdPadding) / displayDensity));
        mContentPadding.setValue(contentPadding / 1);
        mContentPadding.setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) { 
        final String key = preference.getKey(); 
        final String setting = getSystemPreferenceString(preference);
        if (preference != null && preference instanceof ListPreference) {
               ListPreference listPref = (ListPreference) preference;
               String value = (String) objValue;
               int index = listPref.findIndexOfValue(value);
               listPref.setSummary(listPref.getEntries()[index]);
    	    listPref.setValue(value);
    	    if(preference != mVelocityAmplitude){
    		Settings.System.putFloatForUser(getContentResolver(), setting, Float.valueOf(value),
    			UserHandle.USER_CURRENT);
    	    }else {
    		Settings.System.putIntForUser(getContentResolver(), setting, Integer.valueOf(value),
    			UserHandle.USER_CURRENT);
    	    }
    	} else if (preference == mCornerRadius) {
            Settings.Secure.putInt(getContext().getContentResolver(),Settings.Secure.SYSUI_ROUNDED_SIZE,
                    ((int) objValue) * 1);
        } else if (preference == mContentPadding) {
            Settings.Secure.putInt(getContext().getContentResolver(), Settings.Secure.SYSUI_ROUNDED_CONTENT_PADDING,
                    ((int) objValue) * 1);
        }
        updateDependency();
        return true; 
    }

    private String getSystemPreferenceString(Preference preference) {
        if (preference == null) {
                return "";
        } else if(preference == mVelocityFriction){
            return Settings.System.STABILIZATION_VELOCITY_FRICTION;
        } else if(preference == mPositionFriction){
            return Settings.System.STABILIZATION_POSITION_FRICTION;
        } else if(preference == mVelocityAmplitude){
            return Settings.System.STABILIZATION_VELOCITY_AMPLITUDE;
        }  
        return "";
    }

    private void updateSmartPixelsPreference() {
        PreferenceScreen prefSet = getPreferenceScreen();
        boolean enableSmartPixels = getContext().getResources().
                getBoolean(com.android.internal.R.bool.config_enableSmartPixels);
        mSmartPixelsCategory = (PreferenceCategory) prefSet.findPreference(SMART_PIXELS);

        if (!enableSmartPixels){
            prefSet.removePreference(mSmartPixelsCategory);
        }
    }

    private void updateDependency() {
        boolean mUseOnPowerSave = (Settings.System.getIntForUser(
                resolver, Settings.System.SMART_PIXELS_ON_POWER_SAVE,
                0, UserHandle.USER_CURRENT) == 1);
        PowerManager pm = (PowerManager)getActivity().getSystemService(Context.POWER_SERVICE);
        if (pm.isPowerSaveMode() && mUseOnPowerSave) {
            mSmartPixelsOnPowerSave.setEnabled(false);
        } else {
            mSmartPixelsOnPowerSave.setEnabled(true);
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
