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

import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.support.v7.preference.Preference;

import com.android.internal.logging.nano.MetricsProto; 
import com.android.settings.SettingsPreferenceFragment;

import com.havoc.settings.R;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class SlimRecentAppSidebarStyle extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String RECENT_APP_SIDEBAR_TEXT_COLOR = "recent_app_sidebar_text_color";
    private static final String RECENT_APP_SIDEBAR_BG_COLOR = "recent_app_sidebar_bg_color";

    private ColorPickerPreference mRecentSidebarTextColor;
    private ColorPickerPreference mRecentSidebarBgColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.slim_recent_app_sidebar_style);

        mRecentSidebarTextColor = (ColorPickerPreference) findPreference(RECENT_APP_SIDEBAR_TEXT_COLOR);
        mRecentSidebarTextColor.setOnPreferenceChangeListener(this);
        final int intColor = Settings.System.getInt(getContext().getContentResolver(),
                Settings.System.RECENT_APP_SIDEBAR_TEXT_COLOR, 0xffffffff);
        String hexColor = String.format("#%08x", (0xffffffff & intColor));
        if (hexColor.equals("#ffffffff")) {
            mRecentSidebarTextColor.setSummary(R.string.default_string);
        } else {
            mRecentSidebarTextColor.setSummary(hexColor);
        }
        mRecentSidebarTextColor.setNewPreviewColor(intColor);

        mRecentSidebarBgColor = (ColorPickerPreference) findPreference(RECENT_APP_SIDEBAR_BG_COLOR);
        mRecentSidebarBgColor.setOnPreferenceChangeListener(this);
        final int intColorCard = Settings.System.getInt(getContext().getContentResolver(),
                Settings.System.RECENT_APP_SIDEBAR_BG_COLOR, 0x00ffffff);
        String hexColorCard = String.format("#%08x", (0x00ffffff & intColorCard));
        if (hexColorCard.equals("#00ffffff")) {
            mRecentSidebarBgColor.setSummary(R.string.default_string);
        } else {
            mRecentSidebarBgColor.setSummary(hexColorCard);
        }
        mRecentSidebarBgColor.setNewPreviewColor(intColorCard);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mRecentSidebarTextColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hex.equals("#ffffffff")) {
                preference.setSummary(R.string.default_string);
            } else {
                preference.setSummary(hex);
            }
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putIntForUser(getContext().getContentResolver(),
                    Settings.System.RECENT_APP_SIDEBAR_TEXT_COLOR,
                    intHex, UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mRecentSidebarBgColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hex.equals("#00ffffff")) {
                preference.setSummary(R.string.default_string);
            } else {
                preference.setSummary(hex);
            }
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putIntForUser(getContext().getContentResolver(),
                    Settings.System.RECENT_APP_SIDEBAR_BG_COLOR,
                    intHex, UserHandle.USER_CURRENT);
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
