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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.development.DevelopmentSettings;
import com.android.settings.SettingsPreferenceFragment;

import com.havoc.settings.R;

import lineageos.providers.LineageSettings; 

import com.havoc.settings.preferences.CustomSeekBarPreference;

public class LockScreen extends SettingsPreferenceFragment
    implements Preference.OnPreferenceChangeListener {

    public static final String TAG = "LockScreen";

    private static final String CLOCK_FONT_SIZE  = "lockclock_font_size"; 
    private static final String DATE_FONT_SIZE  = "lockdate_font_size"; 	

    private CustomSeekBarPreference mClockFontSize; 
    private CustomSeekBarPreference mDateFontSize; 	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.havoc_settings_lockscreen);

        ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();
		
        mClockFontSize = (CustomSeekBarPreference) findPreference(CLOCK_FONT_SIZE); 
        mClockFontSize.setValue(Settings.System.getInt(getContentResolver(), 
                Settings.System.LOCKCLOCK_FONT_SIZE, 78)); 
        mClockFontSize.setOnPreferenceChangeListener(this); 
 
        mDateFontSize = (CustomSeekBarPreference) findPreference(DATE_FONT_SIZE); 
        mDateFontSize.setValue(Settings.System.getInt(getContentResolver(), 
                Settings.System.LOCKDATE_FONT_SIZE,14)); 
        mDateFontSize.setOnPreferenceChangeListener(this);		
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mClockFontSize) { 
            int top = (Integer) newValue; 
            Settings.System.putInt(getContentResolver(), 
                    Settings.System.LOCKCLOCK_FONT_SIZE, top*1); 
            return true; 
        } else if (preference == mDateFontSize) { 
            int top = (Integer) newValue; 
            Settings.System.putInt(getContentResolver(), 
                    Settings.System.LOCKDATE_FONT_SIZE, top*1); 
            return true; 			
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
