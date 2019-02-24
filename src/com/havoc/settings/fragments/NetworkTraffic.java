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

import com.havoc.support.preferences.CustomSeekBarPreference;
import com.havoc.support.preferences.SystemSettingSwitchPreference;

public class NetworkTraffic extends SettingsPreferenceFragment implements 
        Preference.OnPreferenceChangeListener {

    private static final String NETWORK_TRAFFIC_HIDEARROW = "network_traffic_hidearrow";
    private static final String NETWORK_TRAFFIC_LOCATION = "network_traffic_location";
    private static final String NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD = "network_traffic_autohide_threshold";
    private static final String NETWORK_TRAFFIC_TYPE = "network_traffic_type";
    private static final String NETWORK_TRAFFIC_REFRESH_INTERVAL = "network_traffic_refresh_interval";

    private SystemSettingSwitchPreference mNetMonitor;
    private SystemSettingSwitchPreference mHideArrows;
    private ListPreference mNetTrafficLocation;
    private ListPreference mNetTrafficType;
    private CustomSeekBarPreference mThreshold;
    private CustomSeekBarPreference mNetTrafficRefreshInterval;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.network_traffic); 

        ContentResolver resolver = getActivity().getContentResolver();

        mThreshold = (CustomSeekBarPreference) findPreference(NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD);
        int threshold = Settings.System.getIntForUser(resolver,
                Settings.System.NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD, 1, UserHandle.USER_CURRENT);
        mThreshold.setValue(threshold);
        mThreshold.setOnPreferenceChangeListener(this);

        mNetTrafficType = (ListPreference) findPreference(NETWORK_TRAFFIC_TYPE);
        int nettype = Settings.System.getIntForUser(resolver,
                Settings.System.NETWORK_TRAFFIC_TYPE, 0, UserHandle.USER_CURRENT);
        mNetTrafficType.setValue(String.valueOf(nettype));
        mNetTrafficType.setSummary(mNetTrafficType.getEntry());
        mNetTrafficType.setOnPreferenceChangeListener(this);

        mNetTrafficLocation = (ListPreference) findPreference(NETWORK_TRAFFIC_LOCATION);
        int location = Settings.System.getInt(resolver,
                Settings.System.NETWORK_TRAFFIC_LOCATION, 0);
        mNetTrafficLocation.setValue(String.valueOf(location));
        mNetTrafficLocation.setSummary(mNetTrafficLocation.getEntry());
        mNetTrafficLocation.setOnPreferenceChangeListener(this);

        mHideArrows = (SystemSettingSwitchPreference) findPreference(NETWORK_TRAFFIC_HIDEARROW);

        mNetTrafficRefreshInterval = (CustomSeekBarPreference) findPreference(NETWORK_TRAFFIC_REFRESH_INTERVAL);
        int interval = Settings.System.getIntForUser(resolver,
                Settings.System.NETWORK_TRAFFIC_REFRESH_INTERVAL, 2, UserHandle.USER_CURRENT);
        mNetTrafficRefreshInterval.setValue(interval);
        mNetTrafficRefreshInterval.setOnPreferenceChangeListener(this);

		updateTrafficLocation(location);     
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mNetTrafficLocation) {
            int location = Integer.valueOf((String) newValue);
            int index = mNetTrafficLocation.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.NETWORK_TRAFFIC_LOCATION, location);
            mNetTrafficLocation.setSummary(mNetTrafficLocation.getEntries()[index]);
            updateTrafficLocation(location);
            return true;
        } else if (preference == mNetTrafficType) {
            int val = Integer.valueOf((String) newValue);
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.NETWORK_TRAFFIC_TYPE, val,
                    UserHandle.USER_CURRENT);
            int index = mNetTrafficType.findIndexOfValue((String) newValue);
            mNetTrafficType.setSummary(mNetTrafficType.getEntries()[index]);
            return true;
        } else if (preference == mThreshold) {
            int value = (Integer) newValue;
            Settings.System.putIntForUser(resolver,
                    Settings.System.NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD, value, UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mNetTrafficRefreshInterval) {
            int interval = (Integer) newValue;
            Settings.System.putIntForUser(resolver,
                    Settings.System.NETWORK_TRAFFIC_REFRESH_INTERVAL, interval, UserHandle.USER_CURRENT);
            return true;
        } 
        return false; 
    }

    public void updateTrafficLocation(int location) {
        switch(location){ 
            case 0:
                mNetTrafficType.setEnabled(false);
                mThreshold.setEnabled(false);
                mHideArrows.setEnabled(false);
                mNetTrafficRefreshInterval.setEnabled(false);
                Settings.System.putInt(getActivity().getContentResolver(),
                Settings.System.NETWORK_TRAFFIC_STATE, 0);
                Settings.System.putInt(getActivity().getContentResolver(),
                Settings.System.NETWORK_TRAFFIC_EXPANDED_STATUS_BAR_STATE, 0);
                break;
            case 1:
                mNetTrafficType.setEnabled(true);
                mThreshold.setEnabled(true);
                mHideArrows.setEnabled(true);
                mNetTrafficRefreshInterval.setEnabled(true);
                Settings.System.putInt(getActivity().getContentResolver(),
                Settings.System.NETWORK_TRAFFIC_STATE, 1);
                Settings.System.putInt(getActivity().getContentResolver(),
                Settings.System.NETWORK_TRAFFIC_EXPANDED_STATUS_BAR_STATE, 0);
                break;
            case 2:
                mNetTrafficType.setEnabled(true);
                mThreshold.setEnabled(true);
                mHideArrows.setEnabled(true);
                mNetTrafficRefreshInterval.setEnabled(true);
                Settings.System.putInt(getActivity().getContentResolver(),
                Settings.System.NETWORK_TRAFFIC_STATE, 0);
                Settings.System.putInt(getActivity().getContentResolver(),
                Settings.System.NETWORK_TRAFFIC_EXPANDED_STATUS_BAR_STATE, 1);
                break;
            default: 
                break;
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
