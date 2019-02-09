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

import com.havoc.support.colorpicker.ColorPickerPreference;

public class Visualizer extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String LOCK_SCREEN_VISUALIZER_CUSTOM_COLOR = "lock_screen_visualizer_custom_color";

    private ColorPickerPreference mVisualizerColor;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        addPreferencesFromResource(R.xml.visualizer_settings);
        ContentResolver resolver = getActivity().getContentResolver();

        mVisualizerColor = (ColorPickerPreference) findPreference(LOCK_SCREEN_VISUALIZER_CUSTOM_COLOR);
        int visColor = Settings.System.getInt(resolver,
                Settings.System.LOCK_SCREEN_VISUALIZER_CUSTOM_COLOR, 0xFFFFFFFF);
        String visColorHex = String.format("#%08x", (0xFFFFFFFF & visColor));
        if (visColorHex.equals("#ffffffff")) {
            mVisualizerColor.setSummary(R.string.default_string);
        } else {
            mVisualizerColor.setSummary(visColorHex);
        }
        mVisualizerColor.setNewPreviewColor(visColor);
        mVisualizerColor.setAlphaSliderEnabled(true);
        mVisualizerColor.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mVisualizerColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hex.equals("#ffffffff")) {
                preference.setSummary(R.string.default_string);
            } else {
                preference.setSummary(hex);
            }
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(resolver,
                    Settings.System.LOCK_SCREEN_VISUALIZER_CUSTOM_COLOR, intHex);
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
