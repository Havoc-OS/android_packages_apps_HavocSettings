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

import com.havoc.support.preferences.SecureSettingSwitchPreference;
import com.havoc.support.preferences.CustomSeekBarPreference;
import com.havoc.support.preferences.SystemSettingSwitchPreference;

public class ScreenStabilization extends SettingsPreferenceFragment implements 
        Preference.OnPreferenceChangeListener {

    public static final String TAG = "ScreenStabilization";

    private ListPreference mVelocityFriction;
    private ListPreference mPositionFriction;
    private ListPreference mVelocityAmplitude;

    ContentResolver resolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.screen_stabilization); 
        resolver = getActivity().getContentResolver();   

        float velFriction = Settings.System.getFloatForUser(resolver,
                Settings.System.STABILIZATION_VELOCITY_FRICTION, 0.1f,
                UserHandle.USER_CURRENT);
        mVelocityFriction = (ListPreference) findPreference("stabilization_velocity_friction");
    	mVelocityFriction.setValue(Float.toString(velFriction));
    	mVelocityFriction.setSummary(mVelocityFriction.getEntry());
    	mVelocityFriction.setOnPreferenceChangeListener(this);
    	
    	float posFriction = Settings.System.getFloatForUser(resolver,
                Settings.System.STABILIZATION_POSITION_FRICTION, 0.1f,
                UserHandle.USER_CURRENT);
        mPositionFriction = (ListPreference) findPreference("stabilization_position_friction");
    	mPositionFriction.setValue(Float.toString(posFriction));
    	mPositionFriction.setSummary(mPositionFriction.getEntry());
    	mPositionFriction.setOnPreferenceChangeListener(this);
    
    	int velAmplitude = Settings.System.getIntForUser(resolver,
                Settings.System.STABILIZATION_VELOCITY_AMPLITUDE, 8000,
                UserHandle.USER_CURRENT);
        mVelocityAmplitude = (ListPreference) findPreference("stabilization_velocity_amplitude");
    	mVelocityAmplitude.setValue(Integer.toString(velAmplitude));
    	mVelocityAmplitude.setSummary(mVelocityAmplitude.getEntry());
    	mVelocityAmplitude.setOnPreferenceChangeListener(this);   
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final String setting = getSystemPreferenceString(preference);
        if (preference != null && preference instanceof ListPreference) {
            ListPreference listPref = (ListPreference) preference;
            String value = (String) objValue;
            int index = listPref.findIndexOfValue(value);
            listPref.setSummary(listPref.getEntries()[index]);
    	    listPref.setValue(value);
    	    if (preference != mVelocityAmplitude){
    		    Settings.System.putFloatForUser(getContentResolver(), setting, Float.valueOf(value),
    			    UserHandle.USER_CURRENT);
    	    } else {
    		    Settings.System.putIntForUser(getContentResolver(), setting, Integer.valueOf(value),
    			    UserHandle.USER_CURRENT);
    	    }
    	}
        return false; 
    }

    private String getSystemPreferenceString(Preference preference) {
        if (preference == null) {
            return "";
        } else if (preference == mVelocityFriction) {
            return Settings.System.STABILIZATION_VELOCITY_FRICTION;
        } else if (preference == mPositionFriction) {
            return Settings.System.STABILIZATION_POSITION_FRICTION;
        } else if (preference == mVelocityAmplitude) {
            return Settings.System.STABILIZATION_VELOCITY_AMPLITUDE;
        }
        return "";
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
