/*
 * Copyright (C) 2017 The ABC rom
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

import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.internal.logging.nano.MetricsProto;

import com.havoc.support.preferences.SystemSettingSwitchPreference;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.havoc.support.colorpicker.ColorPickerPreference;

public class BatteryLightSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private ColorPickerPreference mLowColor;
    private ColorPickerPreference mMediumColor;
    private ColorPickerPreference mFullColor;
    private ColorPickerPreference mReallyFullColor;
    private ColorPickerPreference mFastColor;
    private SystemSettingSwitchPreference mLowBatteryBlinking;
    private SystemSettingSwitchPreference mFastChargeEnable;
    private SystemSettingSwitchPreference mBatteryBlend;

    private PreferenceCategory mColorCategory;
    private PreferenceCategory mFastColorCategory;
    private PreferenceCategory mColorBlendCategory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.battery_light_settings);

        PreferenceScreen prefSet = getPreferenceScreen();
        mColorCategory = (PreferenceCategory) findPreference("battery_light_cat");
        mFastColorCategory = (PreferenceCategory) findPreference("fast_color_cat");
        mColorBlendCategory = (PreferenceCategory) findPreference("blend_category");

        mLowBatteryBlinking = (SystemSettingSwitchPreference)prefSet.findPreference("battery_light_low_blinking");
        if (getResources().getBoolean(
                        com.android.internal.R.bool.config_ledCanPulse)) {
            mLowBatteryBlinking.setChecked(Settings.System.getIntForUser(getContentResolver(),
                            Settings.System.BATTERY_LIGHT_LOW_BLINKING, 0, UserHandle.USER_CURRENT) == 1);
            mLowBatteryBlinking.setOnPreferenceChangeListener(this);
        } else {
            prefSet.removePreference(mLowBatteryBlinking);
        }

        if (getResources().getBoolean(com.android.internal.R.bool.config_multiColorBatteryLed)) {
            int color = Settings.System.getIntForUser(getContentResolver(),
                    Settings.System.BATTERY_LIGHT_LOW_COLOR, 0xFFFF0000,
                            UserHandle.USER_CURRENT);
            mLowColor = (ColorPickerPreference) findPreference("battery_light_low_color");
            mLowColor.setAlphaSliderEnabled(false);
            mLowColor.setNewPreviewColor(color);
            String hexLowColor = String.format("#%08x", (0xFFFF0000 & color));
            if (hexLowColor.equals("#ffff0000")) {
                mLowColor.setSummary(R.string.default_string);
            } else {
                mLowColor.setSummary(hexLowColor);
            }
            mLowColor.setOnPreferenceChangeListener(this);

            color = Settings.System.getIntForUser(getContentResolver(),
                    Settings.System.BATTERY_LIGHT_MEDIUM_COLOR, 0xFFFFFF00,
                            UserHandle.USER_CURRENT);
            mMediumColor = (ColorPickerPreference) findPreference("battery_light_medium_color");
            mMediumColor.setAlphaSliderEnabled(false);
            mMediumColor.setNewPreviewColor(color);
            String hexMediumColor = String.format("#%08x", (0xFFFFFF00 & color));
            if (hexMediumColor.equals("#ffffff00")) {
                mMediumColor.setSummary(R.string.default_string);
            } else {
                mMediumColor.setSummary(hexMediumColor);
            }
            mMediumColor.setOnPreferenceChangeListener(this);

            color = Settings.System.getIntForUser(getContentResolver(),
                    Settings.System.BATTERY_LIGHT_FULL_COLOR, 0xFFFFFF00,
                            UserHandle.USER_CURRENT);
            mFullColor = (ColorPickerPreference) findPreference("battery_light_full_color");
            mFullColor.setAlphaSliderEnabled(false);
            mFullColor.setNewPreviewColor(color);
            String hexFullColor = String.format("#%08x", (0xFFFFFF00 & color));
            if (hexFullColor.equals("#ffffff00")) {
                mFullColor.setSummary(R.string.default_string);
            } else {
                mFullColor.setSummary(hexFullColor);
            }
            mFullColor.setOnPreferenceChangeListener(this);

            color = Settings.System.getIntForUser(getContentResolver(),
                    Settings.System.BATTERY_LIGHT_REALLYFULL_COLOR, 0xFF00FF00,
                            UserHandle.USER_CURRENT);
            mReallyFullColor = (ColorPickerPreference) findPreference("battery_light_reallyfull_color");
            mReallyFullColor.setAlphaSliderEnabled(false);
            mReallyFullColor.setNewPreviewColor(color);
            String hexReallyFullColor = String.format("#%08x", (0xFF00FF00 & color));
            if (hexReallyFullColor.equals("#ff00ff00")) {
                mReallyFullColor.setSummary(R.string.default_string);
            } else {
                mReallyFullColor.setSummary(hexReallyFullColor);
            }
            mReallyFullColor.setOnPreferenceChangeListener(this);

            mBatteryBlend = (SystemSettingSwitchPreference) findPreference(Settings.System.BATTERY_LIGHT_BLEND);
            mBatteryBlend.setOnPreferenceChangeListener(this);
        } else {
            if (mColorCategory != null) {
                prefSet.removePreference(mColorCategory);
            }
            if (mColorBlendCategory != null) {
                prefSet.removePreference(mColorBlendCategory);
            }
        }

        if (getResources().getBoolean(com.android.internal.R.bool.config_FastChargingLedSupported)) {
            mFastChargeEnable = (SystemSettingSwitchPreference)
                    findPreference(Settings.System.FAST_CHARGING_LED_ENABLED);
            mFastChargeEnable.setOnPreferenceChangeListener(this);

            int color = Settings.System.getIntForUser(getContentResolver(),
                    Settings.System.FAST_BATTERY_LIGHT_COLOR, 0xFF0000FF,
                            UserHandle.USER_CURRENT);
            mFastColor = (ColorPickerPreference) findPreference("fast_battery_light_color");
            mFastColor.setAlphaSliderEnabled(false);
            mFastColor.setNewPreviewColor(color);
            String hexFastColor = String.format("#%08x", (0xFF0000FF & color));
            if (hexFastColor.equals("#ff0000ff")) {
                mFastColor.setSummary(R.string.default_string);
            } else {
                mFastColor.setSummary(hexFastColor);
            }
            mFastColor.setOnPreferenceChangeListener(this);
        } else {
            prefSet.removePreference(mFastColorCategory);
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.equals(mLowColor)) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hex.equals("#ffff0000")) {
                preference.setSummary(R.string.default_string);
            } else {
                preference.setSummary(hex);
            }
            int color = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.BATTERY_LIGHT_LOW_COLOR, color,
                    UserHandle.USER_CURRENT);
            return true;
        } else if (preference.equals(mMediumColor)) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hex.equals("#ffffff00")) {
                preference.setSummary(R.string.default_string);
            } else {
                preference.setSummary(hex);
            }
            int color = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.BATTERY_LIGHT_MEDIUM_COLOR, color,
                    UserHandle.USER_CURRENT);
            return true;
        } else if (preference.equals(mFullColor)) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hex.equals("#ffffff00")) {
                preference.setSummary(R.string.default_string);
            } else {
                preference.setSummary(hex);
            }
            int color = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.BATTERY_LIGHT_FULL_COLOR, color,
                    UserHandle.USER_CURRENT);
            return true;
        } else if (preference.equals(mReallyFullColor)) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hex.equals("#ff00ff00")) {
                preference.setSummary(R.string.default_string);
            } else {
                preference.setSummary(hex);
            }
            int color = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.BATTERY_LIGHT_REALLYFULL_COLOR, color,
                    UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mLowBatteryBlinking) {
            boolean value = (Boolean) newValue;
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.BATTERY_LIGHT_LOW_BLINKING, value ? 1 : 0,
                    UserHandle.USER_CURRENT);
            mLowBatteryBlinking.setChecked(value);
            return true;
        } else if (preference == mFastChargeEnable) {
            boolean value = (Boolean) newValue;
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.FAST_CHARGING_LED_ENABLED, value ? 1 : 0,
                    UserHandle.USER_CURRENT);
            mFastChargeEnable.setChecked(value);
            return true;
        } else if (preference.equals(mFastColor)) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hex.equals("#ff0000ff")) {
                preference.setSummary(R.string.default_string);
            } else {
                preference.setSummary(hex);
            }
            int color = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.FAST_BATTERY_LIGHT_COLOR, color,
                    UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mBatteryBlend) {
            boolean value = (Boolean) newValue;
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.BATTERY_LIGHT_BLEND, value ? 1 : 0,
                    UserHandle.USER_CURRENT);
            mBatteryBlend.setChecked(value);
            return true;
        }
        return false;
    }
}