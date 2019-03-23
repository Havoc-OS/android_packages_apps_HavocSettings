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
import android.os.UserHandle;
import android.text.TextUtils;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settingslib.core.AbstractPreferenceController;

import com.havoc.support.preferences.SystemSettingMasterSwitchPreference;

import libcore.util.Objects;
import java.util.ArrayList;
import java.util.List;

public class QsCustomHeaderPreferenceController extends AbstractPreferenceController implements
        Preference.OnPreferenceChangeListener {

    private static final String STATUS_BAR_CUSTOM_HEADER = "status_bar_custom_header";

    private SystemSettingMasterSwitchPreference mCustomHeader;

    public QsCustomHeaderPreferenceController(Context context) {
        super(context);
    }

    @Override
    public String getPreferenceKey() {
        return STATUS_BAR_CUSTOM_HEADER;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        mCustomHeader = (SystemSettingMasterSwitchPreference) screen.findPreference(STATUS_BAR_CUSTOM_HEADER);
        int qsHeader = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.STATUS_BAR_CUSTOM_HEADER, 0);
        mCustomHeader.setChecked(qsHeader != 0);
        mCustomHeader.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mCustomHeader) {
            boolean header = (Boolean) newValue;
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.STATUS_BAR_CUSTOM_HEADER, header ? 1 : 0);
        }
        return true;
    }
}
