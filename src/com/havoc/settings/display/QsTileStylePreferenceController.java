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
import android.os.Bundle;
import android.text.TextUtils;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settingslib.core.AbstractPreferenceController;

import libcore.util.Objects;
import java.util.ArrayList;
import java.util.List;


public class QsTileStylePreferenceController extends AbstractPreferenceController implements
        Preference.OnPreferenceChangeListener {

    private static final String QS_TILE_STYLE = "qs_tile_style";
    private ListPreference mQsTileStyle;

    public QsTileStylePreferenceController(Context context) {
        super(context);
    }

    @Override
    public String getPreferenceKey() {
        return QS_TILE_STYLE;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        mQsTileStyle = (ListPreference) screen.findPreference(QS_TILE_STYLE);
        int qsTileStyle = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.QS_TILE_STYLE, 0);
        int valueIndex = mQsTileStyle.findIndexOfValue(String.valueOf(qsTileStyle));
        mQsTileStyle.setValueIndex(valueIndex >= 0 ? valueIndex : 0);
        mQsTileStyle.setSummary(mQsTileStyle.getEntry());
        mQsTileStyle.setOnPreferenceChangeListener(this);
    }
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mQsTileStyle) {
            String value = (String) newValue;
            Settings.System.putInt(mContext.getContentResolver(), Settings.System.QS_TILE_STYLE, Integer.valueOf(value));
            int valueIndex = mQsTileStyle.findIndexOfValue(value);
            mQsTileStyle.setSummary(mQsTileStyle.getEntries()[valueIndex]);
        }
        return true;
    }
}
