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
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.havoc.HavocUtils;
import com.android.settings.SettingsPreferenceFragment;

import com.havoc.settings.R;

public class Recents extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String RECENTS_LAYOUT_STYLE_PREF = "recents_layout_style";
    private static final String KEY_STOCK_RECENTS = "stock_recents_fragment";
    private static final String KEY_SLIM_RECENTS = "slim_recents_fragment";

    private ListPreference mRecentsLayoutStylePref;
    private PreferenceScreen mStockRecents;
    private PreferenceScreen mSlimRecents;

    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.havoc_settings_recents);

        ContentResolver resolver = getActivity().getContentResolver();
        mContext = getActivity().getApplicationContext(); 

        mRecentsLayoutStylePref = (ListPreference) findPreference(RECENTS_LAYOUT_STYLE_PREF);
        int type = Settings.System.getInt(resolver,
                Settings.System.RECENTS_LAYOUT_STYLE, 0);
        mRecentsLayoutStylePref.setValue(String.valueOf(type));
        mRecentsLayoutStylePref.setSummary(mRecentsLayoutStylePref.getEntry());
        mRecentsLayoutStylePref.setOnPreferenceChangeListener(this);

        mStockRecents = (PreferenceScreen) findPreference(KEY_STOCK_RECENTS);
        mSlimRecents = (PreferenceScreen) findPreference(KEY_SLIM_RECENTS);
        updateRecentsState(type); 
    }

    public void updateRecentsState(int type) {
        switch(type){ 
            case 0:
                mStockRecents.setEnabled(false);
                mSlimRecents.setEnabled(false);
                Settings.System.putInt(getActivity().getContentResolver(),
                Settings.System.USE_SLIM_RECENTS, 0);
                break;
            case 1:
                mStockRecents.setEnabled(true);
                mSlimRecents.setEnabled(false);
                Settings.System.putInt(getActivity().getContentResolver(),
                Settings.System.USE_SLIM_RECENTS, 0);
                break;
            case 2:
                mStockRecents.setEnabled(true);
                mSlimRecents.setEnabled(false);
                Settings.System.putInt(getActivity().getContentResolver(),
                Settings.System.USE_SLIM_RECENTS, 0);
                break;
            case 3:
                mStockRecents.setEnabled(true);
                mSlimRecents.setEnabled(false);
                Settings.System.putInt(getActivity().getContentResolver(),
                Settings.System.USE_SLIM_RECENTS, 0);
                break;
            case 4:
                mStockRecents.setEnabled(false);
                mSlimRecents.setEnabled(true);
                Settings.System.putInt(getActivity().getContentResolver(),
                Settings.System.USE_SLIM_RECENTS, 1);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mRecentsLayoutStylePref) {
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
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
