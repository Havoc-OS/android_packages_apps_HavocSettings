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
import android.os.UserHandle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;

import com.android.internal.logging.nano.MetricsProto; 
import com.android.settings.SettingsPreferenceFragment;

import com.havoc.settings.R;
import com.havoc.support.preferences.CustomSeekBarPreference;

public class System extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    public static final String TAG = "System";

    private static final String SHOW_CPU_INFO_KEY = "show_cpu_info";
    private static final String BURN_INTERVAL_KEY = "burn_in_protection_interval";

    private SwitchPreference mShowCpuInfo;
    private CustomSeekBarPreference mBurnInterval;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.havoc_settings_system);
        ContentResolver resolver = getActivity().getContentResolver();

        mShowCpuInfo = (SwitchPreference) findPreference(SHOW_CPU_INFO_KEY);
        mShowCpuInfo.setChecked(Settings.Global.getInt(getActivity().getContentResolver(),
                Settings.Global.SHOW_CPU_OVERLAY, 0) == 1);
        mShowCpuInfo.setOnPreferenceChangeListener(this);

        mBurnInterval = (CustomSeekBarPreference) findPreference(BURN_INTERVAL_KEY);
        int burninterval = Settings.System.getInt(resolver,
                Settings.System.BURN_IN_PROTECTION_INTERVAL, 60);
        mBurnInterval.setValue(burninterval);
        mBurnInterval.setOnPreferenceChangeListener(this);
    }

    private void writeCpuInfoOptions(boolean value) {
        Settings.Global.putInt(getActivity().getContentResolver(),
                Settings.Global.SHOW_CPU_OVERLAY, value ? 1 : 0);
        Intent service = (new Intent())
                .setClassName("com.android.systemui", "com.android.systemui.CPUInfoService");
        if (value) {
            getActivity().startService(service);
        } else {
            getActivity().stopService(service);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mShowCpuInfo) {
            writeCpuInfoOptions((Boolean) newValue);
            return true;
        } else if (preference == mBurnInterval) {
            int interval = (Integer) newValue;
            Settings.System.putIntForUser(resolver,
                    Settings.System.BURN_IN_PROTECTION_INTERVAL, interval, UserHandle.USER_CURRENT);
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
