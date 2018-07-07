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

import android.content.ContentResolver;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;

import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.utils.du.DUActionUtils;

import com.havoc.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.havoc.settings.preferences.SystemSettingSeekBarPreference; 

import lineageos.providers.LineageSettings; 
import android.provider.Settings; 

public class LockScreen extends SettingsPreferenceFragment
    implements Preference.OnPreferenceChangeListener {

    public static final String TAG = "LockScreen";

    private static final String KEY_LOCKSCREEN_CLOCK_SELECTION = "lockscreen_clock_selection";
    private static final String KEY_LOCKSCREEN_DATE_SELECTION = "lockscreen_date_selection";
    private static final String LOCKSCREEN_SECURITY_ALPHA = "lockscreen_security_alpha"; 
    private static final String LOCKSCREEN_ALPHA = "lockscreen_alpha"; 
    private static final String LOCK_CLOCK_FONTS = "lock_clock_fonts"; 
    private static final String LOCK_DATE_FONTS = "lock_date_fonts"; 
    private static final String CLOCK_FONT_SIZE  = "lockclock_font_size"; 
    private static final String DATE_FONT_SIZE  = "lockdate_font_size"; 
    private static final String ALARM_FONT_SIZE  = "lockalarm_font_size"; 
    private static final String LOCKSCREEN_MAX_NOTIF_CONFIG = "lockscreen_max_notif_config";
    private static final String LOCK_OWNERINFO_FONTS = "lock_ownerinfo_fonts"; 
    private static final String LOCKOWNER_FONT_SIZE = "lockowner_font_size"; 
    private static final String LOCK_ALARM_FONTS = "lock_alarm_fonts";  

    ListPreference mLockAlarmFonts;  
    ListPreference mLockClockFonts;
    ListPreference mLockDateFonts;  
    ListPreference mLockOwnerInfoFonts; 
    private ListPreference mLockscreenClockSelection;
    private ListPreference mLockscreenDateSelection;
    private SystemSettingSeekBarPreference mLsAlpha; 
    private SystemSettingSeekBarPreference mLsSecurityAlpha; 
    private SystemSettingSeekBarPreference mClockFontSize; 
    private SystemSettingSeekBarPreference mDateFontSize; 
    private SystemSettingSeekBarPreference mAlarmFontSize; 
    private SystemSettingSeekBarPreference mMaxKeyguardNotifConfig;
    private SystemSettingSeekBarPreference mOwnerInfoFontSize; 

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.havoc_settings_lockscreen);

        ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();

        mMaxKeyguardNotifConfig = (SystemSettingSeekBarPreference) findPreference(LOCKSCREEN_MAX_NOTIF_CONFIG);
        int kgconf = Settings.System.getIntForUser(resolver,
                Settings.System.LOCKSCREEN_MAX_NOTIF_CONFIG, 5, UserHandle.USER_CURRENT);
        mMaxKeyguardNotifConfig.setValue(kgconf);
        mMaxKeyguardNotifConfig.setOnPreferenceChangeListener(this);

        mLockscreenClockSelection = (ListPreference) findPreference(KEY_LOCKSCREEN_CLOCK_SELECTION);
        int clockSelection = Settings.System.getIntForUser(resolver,
                Settings.System.LOCKSCREEN_CLOCK_SELECTION, 0, UserHandle.USER_CURRENT);
        mLockscreenClockSelection.setValue(String.valueOf(clockSelection));
        mLockscreenClockSelection.setSummary(mLockscreenClockSelection.getEntry());
        mLockscreenClockSelection.setOnPreferenceChangeListener(this);

        mLockscreenDateSelection = (ListPreference) findPreference(KEY_LOCKSCREEN_DATE_SELECTION);
        int dateSelection = Settings.System.getIntForUser(resolver,
                Settings.System.LOCKSCREEN_DATE_SELECTION, 0, UserHandle.USER_CURRENT);
        mLockscreenDateSelection.setValue(String.valueOf(dateSelection));
        mLockscreenDateSelection.setSummary(mLockscreenDateSelection.getEntry());
        mLockscreenDateSelection.setOnPreferenceChangeListener(this);

        // Lockscren Date Fonts
        mLockDateFonts = (ListPreference) findPreference(LOCK_DATE_FONTS);
        mLockDateFonts.setValue(String.valueOf(Settings.System.getInt(
                getContentResolver(), Settings.System.LOCK_DATE_FONTS, 26)));
        mLockDateFonts.setSummary(mLockDateFonts.getEntry());
        mLockDateFonts.setOnPreferenceChangeListener(this);

        mLsSecurityAlpha = (SystemSettingSeekBarPreference) findPreference(LOCKSCREEN_SECURITY_ALPHA); 
        float alpha2 = Settings.System.getFloat(resolver, 
                Settings.System.LOCKSCREEN_SECURITY_ALPHA, 0.75f); 
        mLsSecurityAlpha.setValue((int)(100 * alpha2)); 
        mLsSecurityAlpha.setOnPreferenceChangeListener(this); 
 
        mLsAlpha = (SystemSettingSeekBarPreference) findPreference(LOCKSCREEN_ALPHA); 
        float alpha = Settings.System.getFloat(resolver, 
                Settings.System.LOCKSCREEN_ALPHA, 0.45f); 
        mLsAlpha.setValue((int)(100 * alpha)); 
        mLsAlpha.setOnPreferenceChangeListener(this); 
    
        mLockClockFonts = (ListPreference) findPreference(LOCK_CLOCK_FONTS); 
        mLockClockFonts.setValue(String.valueOf(Settings.System.getInt( 
                getContentResolver(), Settings.System.LOCK_CLOCK_FONTS, 0))); 
        mLockClockFonts.setSummary(mLockClockFonts.getEntry()); 
        mLockClockFonts.setOnPreferenceChangeListener(this); 

        mClockFontSize = (SystemSettingSeekBarPreference) findPreference(CLOCK_FONT_SIZE); 
        mClockFontSize.setValue(Settings.System.getInt(getContentResolver(), 
                Settings.System.LOCKCLOCK_FONT_SIZE, 78)); 
        mClockFontSize.setOnPreferenceChangeListener(this); 
         
        mDateFontSize = (SystemSettingSeekBarPreference) findPreference(DATE_FONT_SIZE);
        mDateFontSize.setValue(Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKDATE_FONT_SIZE,14));
        mDateFontSize.setOnPreferenceChangeListener(this);

        mAlarmFontSize = (SystemSettingSeekBarPreference) findPreference(ALARM_FONT_SIZE);
        mAlarmFontSize.setValue(Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKALARM_FONT_SIZE,14));
                mAlarmFontSize.setOnPreferenceChangeListener(this);

        // Lockscren Alarm Fonts  
        mLockAlarmFonts = (ListPreference) findPreference(LOCK_ALARM_FONTS);  
        mLockAlarmFonts.setValue(String.valueOf(Settings.System.getInt(  
                getContentResolver(), Settings.System.LOCK_ALARM_FONTS, 26)));  
                mLockAlarmFonts.setSummary(mLockAlarmFonts.getEntry());  
                mLockAlarmFonts.setOnPreferenceChangeListener(this);  

                 // Lockscren OwnerInfo Fonts 
        mLockOwnerInfoFonts = (ListPreference) findPreference(LOCK_OWNERINFO_FONTS); 
        mLockOwnerInfoFonts.setValue(String.valueOf(Settings.System.getInt( 
                getContentResolver(), Settings.System.LOCK_OWNERINFO_FONTS, 14))); 
        mLockOwnerInfoFonts.setSummary(mLockOwnerInfoFonts.getEntry()); 
        mLockOwnerInfoFonts.setOnPreferenceChangeListener(this); 
 
        // Lockscren OwnerInfo Size 
        mOwnerInfoFontSize = (SystemSettingSeekBarPreference) findPreference(LOCKOWNER_FONT_SIZE); 
        mOwnerInfoFontSize.setValue(Settings.System.getInt(getContentResolver(), 
                Settings.System.LOCKOWNER_FONT_SIZE,21)); 
        mOwnerInfoFontSize.setOnPreferenceChangeListener(this); 
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mLockscreenClockSelection) {
            int clockSelection = Integer.valueOf((String) newValue);
            int index = mLockscreenClockSelection.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(resolver,
                    Settings.System.LOCKSCREEN_CLOCK_SELECTION, clockSelection, UserHandle.USER_CURRENT);
            mLockscreenClockSelection.setSummary(mLockscreenClockSelection.getEntries()[index]);
            return true;
        } else if (preference == mLockscreenDateSelection) {
            int dateSelection = Integer.valueOf((String) newValue);
            int index = mLockscreenDateSelection.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(resolver,
                    Settings.System.LOCKSCREEN_DATE_SELECTION, dateSelection, UserHandle.USER_CURRENT);
            mLockscreenDateSelection.setSummary(mLockscreenDateSelection.getEntries()[index]);
            return true;
        } else if (preference == mLsSecurityAlpha) { 
            int alpha2 = (Integer) newValue; 
            Settings.System.putFloat(resolver, 
                    Settings.System.LOCKSCREEN_SECURITY_ALPHA, alpha2 / 100.0f); 
            return true; 
        } else if (preference == mLsAlpha) { 
            int alpha = (Integer) newValue; 
            Settings.System.putFloat(resolver, 
                    Settings.System.LOCKSCREEN_ALPHA, alpha / 100.0f); 
            return true; 
        } else  if (preference == mLockClockFonts) { 
            Settings.System.putInt(getContentResolver(), Settings.System.LOCK_CLOCK_FONTS, 
                    Integer.valueOf((String) newValue)); 
            mLockClockFonts.setValue(String.valueOf(newValue)); 
            mLockClockFonts.setSummary(mLockClockFonts.getEntry()); 
            return true; 
        }  else if (preference == mLockDateFonts) {
                Settings.System.putInt(getContentResolver(), Settings.System.LOCK_DATE_FONTS,
                        Integer.valueOf((String) newValue));
                mLockDateFonts.setValue(String.valueOf(newValue));
                mLockDateFonts.setSummary(mLockDateFonts.getEntry());
                return true;
        }   else if (preference == mClockFontSize) { 
            int top = (Integer) newValue; 
            Settings.System.putInt(getContentResolver(), 
                    Settings.System.LOCKCLOCK_FONT_SIZE, top*1); 
            return true; 
        } else if (preference == mDateFontSize) {
             int top = (Integer) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKDATE_FONT_SIZE, top*1);
            return true;
        } else  if (preference == mMaxKeyguardNotifConfig) {
            int value = (Integer) newValue;
            Settings.System.putInt(resolver,
                    Settings.System.LOCKSCREEN_MAX_NOTIF_CONFIG, value);
            return true;
        } else if (preference == mAlarmFontSize) {
                int top = (Integer) newValue;
               Settings.System.putInt(getContentResolver(),
                       Settings.System.LOCKALARM_FONT_SIZE, top*1);
               return true;
        } else if (preference == mLockOwnerInfoFonts) { 
            Settings.System.putInt(getContentResolver(), Settings.System.LOCK_OWNERINFO_FONTS, 
                    Integer.valueOf((String) newValue)); 
            mLockOwnerInfoFonts.setValue(String.valueOf(newValue)); 
            mLockOwnerInfoFonts.setSummary(mLockOwnerInfoFonts.getEntry()); 
            return true; 
        } else if (preference == mOwnerInfoFontSize) { 
            int top = (Integer) newValue; 
            Settings.System.putInt(getContentResolver(), 
                    Settings.System.LOCKOWNER_FONT_SIZE, top*1); 
            return true; 
        } else if (preference == mLockAlarmFonts) {  
             Settings.System.putInt(getContentResolver(), Settings.System.LOCK_ALARM_FONTS,  
                     Integer.valueOf((String) newValue));  
                     mLockAlarmFonts.setValue(String.valueOf(newValue));  
             mLockAlarmFonts.setSummary(mLockAlarmFonts.getEntry());  
             return true;  
        } 
        return false;
    }
    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
