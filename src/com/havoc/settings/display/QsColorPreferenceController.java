/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.havoc.settings.display;

import android.content.Context;
import android.content.ContentResolver;
import android.graphics.Color;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextUtils;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settingslib.core.AbstractPreferenceController;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import libcore.util.Objects;
import java.util.ArrayList;
import java.util.List;

public class QsColorPreferenceController extends AbstractPreferenceController implements
        Preference.OnPreferenceChangeListener {

    private static final String QS_PANEL_COLOR = "qs_panel_color";
    static final int DEFAULT_QS_PANEL_COLOR = 0xffffffff;

    private ColorPickerPreference mQsPanelColor;

    public QsColorPreferenceController(Context context) {
        super(context);
    }

    @Override
    public String getPreferenceKey() {
        return QS_PANEL_COLOR;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        int intColor;
        String hexColor;

        mQsPanelColor = (ColorPickerPreference) screen.findPreference(QS_PANEL_COLOR);
        mQsPanelColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getIntForUser(mContext.getContentResolver(),
                Settings.System.QS_PANEL_BG_COLOR, DEFAULT_QS_PANEL_COLOR, UserHandle.USER_CURRENT);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mQsPanelColor.setSummary(hexColor);
        mQsPanelColor.setNewPreviewColor(intColor);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mQsPanelColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putIntForUser(mContext.getContentResolver(),
                    Settings.System.QS_PANEL_BG_COLOR, intHex, UserHandle.USER_CURRENT);
        }
        return true;
    }
}
