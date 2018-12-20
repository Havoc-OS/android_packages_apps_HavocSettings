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
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.SharedPreferences; 
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.havoc.HavocUtils;
import com.android.settings.dashboard.SummaryLoader;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.havoc.settings.Utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Recents extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String KEY_CATEGORY_STOCK = "stock_recents";
    private static final String KEY_CATEGORY_IMMERSIVE = "immersive";
    private static final String KEY_CATEGORY_SLIM = "slim_recents_category";
    private static final String RECENTS_CLEAR_ALL_LOCATION = "recents_clear_all_location";
    private static final String RECENTS_LAYOUT_STYLE_PREF = "recents_layout_style";
    private static final String IMMERSIVE_RECENTS = "immersive_recents"; 
    private static final String RECENTS_DATE = "recents_full_screen_date"; 
    private static final String RECENTS_CLOCK = "recents_full_screen_clock"; 

    private PreferenceCategory mStockCat;
    private PreferenceCategory mImmersiveCat;
    private PreferenceCategory mSlimCat;
    private ListPreference mImmersiveRecents; 
    private ListPreference mRecentsClearAllLocation;
    private SwitchPreference mRecentsClearAll;
    private ListPreference mRecentsLayoutStylePref;
    private SwitchPreference mClock; 
    private SwitchPreference mDate; 

    private SharedPreferences mPreferences; 
    private Context mContext; 

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.havoc_settings_recents);

        ContentResolver resolver = getActivity().getContentResolver();
        mContext = getActivity().getApplicationContext(); 

        // clear all recents
        mRecentsClearAllLocation = (ListPreference) findPreference(RECENTS_CLEAR_ALL_LOCATION);
        int location = Settings.System.getIntForUser(resolver,
                Settings.System.RECENTS_CLEAR_ALL_LOCATION, 3, UserHandle.USER_CURRENT);
        mRecentsClearAllLocation.setValue(String.valueOf(location));
        mRecentsClearAllLocation.setSummary(mRecentsClearAllLocation.getEntry());
        mRecentsClearAllLocation.setOnPreferenceChangeListener(this);

        // recents layout style
        mRecentsLayoutStylePref = (ListPreference) findPreference(RECENTS_LAYOUT_STYLE_PREF);
        int type = Settings.System.getInt(resolver,
                Settings.System.RECENTS_LAYOUT_STYLE, 0);
        mRecentsLayoutStylePref.setValue(String.valueOf(type));
        mRecentsLayoutStylePref.setSummary(mRecentsLayoutStylePref.getEntry());
        mRecentsLayoutStylePref.setOnPreferenceChangeListener(this);

        mImmersiveRecents = (ListPreference) findPreference(IMMERSIVE_RECENTS); 
        int mode = Settings.System.getInt(getContentResolver(), 
        Settings.System.IMMERSIVE_RECENTS, 0); 
            mImmersiveRecents.setValue(String.valueOf(mode)); 
        mImmersiveRecents.setSummary(mImmersiveRecents.getEntry()); 
        mImmersiveRecents.setOnPreferenceChangeListener(this); 

        mStockCat = (PreferenceCategory) findPreference(KEY_CATEGORY_STOCK);
        mSlimCat = (PreferenceCategory) findPreference(KEY_CATEGORY_SLIM);
        mImmersiveCat = (PreferenceCategory) findPreference(KEY_CATEGORY_IMMERSIVE);
        updateRecentsState(type); 

        mClock = (SwitchPreference) findPreference(RECENTS_CLOCK); 
        mDate = (SwitchPreference) findPreference(RECENTS_DATE); 
        updateDisablestate(mode); 
    }

    public void updateRecentsState(int type) {
        switch(type){ 
            case 0: 
                mStockCat.setEnabled(false);
                mImmersiveCat.setEnabled(false);
                mSlimCat.setEnabled(false);
                Settings.System.putInt(getActivity().getContentResolver(),
                Settings.System.USE_SLIM_RECENTS, 0);
                break; 
            case 1: 
                mStockCat.setEnabled(true);
                mImmersiveCat.setEnabled(true);
                mSlimCat.setEnabled(false);
                Settings.System.putInt(getActivity().getContentResolver(),
                Settings.System.USE_SLIM_RECENTS, 0);
                break; 
            case 2: 
                mStockCat.setEnabled(true);
                mImmersiveCat.setEnabled(true);
                mSlimCat.setEnabled(false);
                Settings.System.putInt(getActivity().getContentResolver(),
                Settings.System.USE_SLIM_RECENTS, 0);
                break; 
            case 3: 
                mStockCat.setEnabled(false);
                mImmersiveCat.setEnabled(false);
                mSlimCat.setEnabled(true);
                Settings.System.putInt(getActivity().getContentResolver(),
                Settings.System.USE_SLIM_RECENTS, 1);
                break; 
            default: 
                break;
        }
    }

    public void updateDisablestate(int mode) { 
        if (mode == 0 || mode == 2) { 
           mClock.setEnabled(false); 
           mDate.setEnabled(false); 
        } else { 
           mClock.setEnabled(true); 
           mDate.setEnabled(true); 
        } 
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mRecentsClearAllLocation) {
            int value = Integer.parseInt((String) objValue);
            int index = mRecentsClearAllLocation.findIndexOfValue((String) objValue);
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                Settings.System.RECENTS_CLEAR_ALL_LOCATION, value, UserHandle.USER_CURRENT);
            mRecentsClearAllLocation.setSummary(mRecentsClearAllLocation.getEntries()[index]);
            return true;
        } else if (preference == mRecentsLayoutStylePref) {
            int type = Integer.valueOf((String) objValue);
            int index = mRecentsLayoutStylePref.findIndexOfValue((String) objValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.RECENTS_LAYOUT_STYLE, type);
            mRecentsLayoutStylePref.setSummary(mRecentsLayoutStylePref.getEntries()[index]);
            updateRecentsState(type); 
            if (type != 0) { // Disable swipe up gesture, if oreo type selected
                Settings.Secure.putInt(getActivity().getContentResolver(),
                    Settings.Secure.SWIPE_UP_TO_SWITCH_APPS_ENABLED, 0);
            }
            HavocUtils.restartSystemUi(getContext());
            return true;
        }  if (preference == mImmersiveRecents) {
            int mode = Integer.valueOf((String) objValue); 
            Settings.System.putIntForUser(getActivity().getContentResolver(), Settings.System.IMMERSIVE_RECENTS,
                    Integer.parseInt((String) objValue), UserHandle.USER_CURRENT);
            mImmersiveRecents.setValue((String) objValue);
            mImmersiveRecents.setSummary(mImmersiveRecents.getEntry());
            updateDisablestate(mode);
            mPreferences = mContext.getSharedPreferences("recent_settings", Activity.MODE_PRIVATE);
            if (!mPreferences.getBoolean("first_info_shown", false) && objValue != null) {
                getActivity().getSharedPreferences("recent_settings", Activity.MODE_PRIVATE)
                        .edit()
                        .putBoolean("first_info_shown", true)
                        .commit();
                openAOSPFirstTimeWarning();
            }
            return true;
        }
        return false;
    }

    private void openAOSPFirstTimeWarning() { 
        new AlertDialog.Builder(getActivity()) 
                .setTitle(getResources().getString(R.string.aosp_first_time_title)) 
                .setMessage(getResources().getString(R.string.aosp_first_time_message)) 
                .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() { 
                        public void onClick(DialogInterface dialog, int whichButton) { 
                        } 
                }).show(); 
    } 

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
