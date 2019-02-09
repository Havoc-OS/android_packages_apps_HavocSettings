/*
 * Copyright (C) 2019 Havoc-OS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.havoc.settings.fragments;

import android.content.Context;
import android.content.ContentResolver;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.android.internal.logging.nano.MetricsProto;

import com.havoc.support.preferences.CustomSeekBarPreference;

public class TickerSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String STATUS_BAR_TICKER_DURATION = "status_bar_ticker_tick_duration";

    private CustomSeekBarPreference mTickerDuration;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        addPreferencesFromResource(R.xml.ticker_settings);
        ContentResolver resolver = getActivity().getContentResolver();

        mTickerDuration = (CustomSeekBarPreference) findPreference(STATUS_BAR_TICKER_DURATION);
        int tickerDuration = Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_TICKER_TICK_DURATION, 3000);
        mTickerDuration.setValue(tickerDuration);
        mTickerDuration.setOnPreferenceChangeListener(this);	
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mTickerDuration) {
            int value = (Integer) newValue;
            Settings.System.putInt(resolver,
                    Settings.System.STATUS_BAR_TICKER_TICK_DURATION, value);
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
