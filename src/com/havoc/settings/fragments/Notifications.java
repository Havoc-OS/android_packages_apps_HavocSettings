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
 
import android.app.ActivityManagerNative;
import android.content.Context;
import android.content.ContentResolver;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManagerGlobal;
import android.view.IWindowManager;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.provider.Settings; 

import java.util.Locale;
import android.text.TextUtils;
import android.view.View;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto;

import com.havoc.settings.preferences.Utils;
import com.havoc.settings.preferences.SystemSettingSwitchPreference;

public class Notifications extends SettingsPreferenceFragment
    implements OnPreferenceChangeListener {
 
    public static final String TAG = "Notifications";

    private static final String INCALL_VIB_OPTIONS = "incall_vib_options";
    private static final String DISABLE_IMMERSIVE_MESSAGE = "disable_immersive_message"; 
    private static final String FLASHLIGHT_ON_CALL = "flashlight_on_call";
    private static final String DISABLE_FC_NOTIFICATIONs = "disable_fc_notifications";

    private SwitchPreference mDisableFCN;
    private SwitchPreference mDisableIM; 
    private ListPreference mFlashlightOnCall;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.havoc_settings_notifications);

        final ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefSet = getPreferenceScreen();
		
		PreferenceCategory incallVibCategory = (PreferenceCategory) findPreference(INCALL_VIB_OPTIONS); 
        if (!Utils.isVoiceCapable(getActivity())) {
        prefSet.removePreference(incallVibCategory); 
        }

        mDisableFCN = (SwitchPreference) findPreference(DISABLE_FC_NOTIFICATIONs); 
        mDisableFCN.setChecked(Settings.System.getIntForUser(resolver, 
        Settings.System.DISABLE_FC_NOTIFICATIONS, 1, UserHandle.USER_CURRENT) == 1); 
        mDisableFCN.setOnPreferenceChangeListener(this); 

        mDisableIM = (SwitchPreference) findPreference(DISABLE_IMMERSIVE_MESSAGE); 
        mDisableIM.setOnPreferenceChangeListener(this); 
        int DisableIM = Settings.System.getInt(getContentResolver(), 
                DISABLE_IMMERSIVE_MESSAGE, 0); 
        mDisableIM.setChecked(DisableIM != 0); 

        
        mFlashlightOnCall = (ListPreference) findPreference(FLASHLIGHT_ON_CALL);
        Preference FlashOnCall = findPreference("flashlight_on_call");
        int flashlightValue = Settings.System.getInt(getContentResolver(),
                Settings.System.FLASHLIGHT_ON_CALL, 0);
        mFlashlightOnCall.setValue(String.valueOf(flashlightValue));
        mFlashlightOnCall.setSummary(mFlashlightOnCall.getEntry());
        mFlashlightOnCall.setOnPreferenceChangeListener(this);

        if (!Utils.deviceSupportsFlashLight(getActivity())) {
            prefSet.removePreference(FlashOnCall);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mDisableIM) { 
            boolean value = (Boolean) objValue; 
            Settings.System.putInt(getContentResolver(), DISABLE_IMMERSIVE_MESSAGE, 
                    value ? 1 : 0); 
            return true; 
        } else if (preference == mFlashlightOnCall) {
            int flashlightValue = Integer.parseInt(((String) objValue).toString());
            Settings.System.putInt(getContentResolver(),
                  Settings.System.FLASHLIGHT_ON_CALL, flashlightValue);
            mFlashlightOnCall.setValue(String.valueOf(flashlightValue));
            mFlashlightOnCall.setSummary(mFlashlightOnCall.getEntry());
            return true;
        } else if (preference == mDisableFCN) { 
            boolean value = (Boolean) objValue; 
            Settings.System.putIntForUser(resolver, 
                    Settings.System.DISABLE_FC_NOTIFICATIONS, value ? 1: 0, UserHandle.USER_CURRENT); 
            return true; 
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
