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
import android.content.res.Resources;
import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.Vibrator;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;

import com.android.settings.R;

import com.android.settings.SettingsPreferenceFragment;

import com.havoc.settings.preferences.CustomSeekBarPreference;

import com.android.internal.logging.nano.MetricsProto;

public class ButtonSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener{

    //Keys
    private static final String KEY_BUTTON_BRIGHTNESS = "button_brightness";
    private static final String KEY_BUTTON_BRIGHTNESS_SW = "button_brightness_sw";
    private static final String KEY_BACKLIGHT_TIMEOUT = "backlight_timeout";

    // category keys
    private static final String CATEGORY_HWKEY = "hardware_keys";

    private ListPreference mBacklightTimeout;
    private CustomSeekBarPreference mButtonBrightness;
    private SwitchPreference mButtonBrightness_sw;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.havoc_settings_buttons);

        final PreferenceScreen prefScreen = getPreferenceScreen();

        final boolean variableBrightness = getResources().getBoolean(
                com.android.internal.R.bool.config_deviceHasVariableButtonBrightness);

        mBacklightTimeout =
                (ListPreference) findPreference(KEY_BACKLIGHT_TIMEOUT);
        mButtonBrightness =
                (CustomSeekBarPreference) findPreference(KEY_BUTTON_BRIGHTNESS);
        mButtonBrightness_sw =
                (SwitchPreference) findPreference(KEY_BUTTON_BRIGHTNESS_SW);

        if (mBacklightTimeout != null) {
            mBacklightTimeout.setOnPreferenceChangeListener(this);
            int BacklightTimeout = Settings.System.getInt(getContentResolver(),
                    Settings.System.BUTTON_BACKLIGHT_TIMEOUT, 5000);
            mBacklightTimeout.setValue(Integer.toString(BacklightTimeout));
            mBacklightTimeout.setSummary(mBacklightTimeout.getEntry());
        }

        if (variableBrightness) {
            prefScreen.removePreference(mButtonBrightness_sw);
            if (mButtonBrightness != null) {
                int ButtonBrightness = Settings.System.getInt(getContentResolver(),
                        Settings.System.BUTTON_BRIGHTNESS, 255);
                mButtonBrightness.setValue(ButtonBrightness / 1);
                mButtonBrightness.setOnPreferenceChangeListener(this);
            }
        } else {
            prefScreen.removePreference(mButtonBrightness);
            if (mButtonBrightness_sw != null) {
                mButtonBrightness_sw.setChecked((Settings.System.getInt(getContentResolver(),
                        Settings.System.BUTTON_BRIGHTNESS, 1) == 1));
                mButtonBrightness_sw.setOnPreferenceChangeListener(this);
            }
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mBacklightTimeout) {
            String BacklightTimeout = (String) newValue;
            int BacklightTimeoutValue = Integer.parseInt(BacklightTimeout);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.BUTTON_BACKLIGHT_TIMEOUT, BacklightTimeoutValue);
            int BacklightTimeoutIndex = mBacklightTimeout
                    .findIndexOfValue(BacklightTimeout);
            mBacklightTimeout
                    .setSummary(mBacklightTimeout.getEntries()[BacklightTimeoutIndex]);
            return true;
        } else if (preference == mButtonBrightness) {
            int value = (Integer) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.BUTTON_BRIGHTNESS, value * 1);
            return true;
        } else if (preference == mButtonBrightness_sw) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.BUTTON_BRIGHTNESS, value ? 1 : 0);
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
