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
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.development.DevelopmentSettings;
import com.android.settings.SettingsPreferenceFragment;

import com.havoc.settings.R;

import lineageos.providers.LineageSettings;

public class StatusBar extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    public static final String TAG = "StatusBar";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.havoc_settings_statusbar);

        ContentResolver resolver = getActivity().getContentResolver();
    }

    public static void reset(Context mContext) {
        ContentResolver resolver = mContext.getContentResolver();

        LineageSettings.Secure.putIntForUser(resolver,
                LineageSettings.Secure.NETWORK_TRAFFIC_MODE, 0, UserHandle.USER_CURRENT);
        LineageSettings.Secure.putIntForUser(resolver,
                LineageSettings.Secure.NETWORK_TRAFFIC_AUTOHIDE, 0, UserHandle.USER_CURRENT);
        LineageSettings.Secure.putIntForUser(resolver,
                LineageSettings.Secure.NETWORK_TRAFFIC_UNITS, 1, UserHandle.USER_CURRENT);
        LineageSettings.Secure.putIntForUser(resolver,
                LineageSettings.Secure.NETWORK_TRAFFIC_SHOW_UNITS, 1, UserHandle.USER_CURRENT);
    }

    
    @Override 
    public boolean onPreferenceChange(Preference preference, Object newValue) { 
        return false; 
    } 

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
