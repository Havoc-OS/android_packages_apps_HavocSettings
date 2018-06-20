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

import net.margaritov.preference.colorpicker.ColorPickerPreference;
import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.development.DevelopmentSettings;
import com.android.settings.SettingsPreferenceFragment;

import com.havoc.settings.R;

import lineageos.providers.LineageSettings;

public class StatusBar extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    public static final String TAG = "StatusBar";

    private static final String QUICK_PULLDOWN = "quick_pulldown";
    private static final String SMART_PULLDOWN = "smart_pulldown";
    private static final String DATA_ACTIVITY_ARROWS = "data_activity_arrows";
    private static final String WIFI_ACTIVITY_ARROWS = "wifi_activity_arrows";
    private static final String TICKER_MODE = "status_bar_show_ticker";
    private static final String STATUS_BAR_BATTERY_STYLE = "status_bar_battery_style";
    private static final String SHOW_BATTERY_PERCENT = "show_battery_percent";
    private static final String TEXT_CHARGING_SYMBOL = "text_charging_symbol";
    private static final String HAVOC_LOGO = "status_bar_havoc_logo"; 
    private static final String HAVOC_LOGO_COLOR = "status_bar_havoc_logo_color"; 
    private static final String HAVOC_LOGO_POSITION = "status_bar_havoc_logo_position"; 
    private static final String HAVOC_LOGO_STYLE = "status_bar_havoc_logo_style"; 

    public static final int BATTERY_STYLE_PORTRAIT = 0;
    public static final int BATTERY_STYLE_CIRCLE = 1;
    public static final int BATTERY_STYLE_DOTTED_CIRCLE = 2;
    public static final int BATTERY_STYLE_SQUARE = 3;
    public static final int BATTERY_STYLE_TEXT = 4;
    public static final int BATTERY_STYLE_HIDDEN = 5;

    private ListPreference mQuickPulldown;
    private ListPreference mSmartPulldown;
    private SwitchPreference mDataActivityEnabled;
    private SwitchPreference mWifiActivityEnabled;
    private ListPreference mTickerMode;
    private ListPreference mBatteryStyle;
    private ListPreference mBatteryPercent;
    private ListPreference mTextSymbol;
    private SwitchPreference mHavocLogo; 
    private ColorPickerPreference mHavocLogoColor; 
    private ListPreference mHavocLogoPosition; 
    private ListPreference mHavocLogoStyle; 

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.havoc_settings_statusbar);

        ContentResolver resolver = getActivity().getContentResolver();

        mQuickPulldown = (ListPreference) findPreference(QUICK_PULLDOWN);
        int quickPulldownValue = LineageSettings.System.getIntForUser(resolver,
                LineageSettings.System.STATUS_BAR_QUICK_QS_PULLDOWN, 0, UserHandle.USER_CURRENT);
        mQuickPulldown.setValue(String.valueOf(quickPulldownValue));
        updatePulldownSummary(quickPulldownValue);
        mQuickPulldown.setOnPreferenceChangeListener(this);

        mSmartPulldown = (ListPreference) findPreference(SMART_PULLDOWN);
        int smartPulldown = Settings.System.getIntForUser(resolver,
                Settings.System.QS_SMART_PULLDOWN, 0, UserHandle.USER_CURRENT);
        mSmartPulldown.setValue(String.valueOf(smartPulldown));
        updateSmartPulldownSummary(smartPulldown);
        mSmartPulldown.setOnPreferenceChangeListener(this);

        mDataActivityEnabled = (SwitchPreference) findPreference(DATA_ACTIVITY_ARROWS);
        boolean mActivityEnabled = Settings.System.getIntForUser(resolver,
                Settings.System.DATA_ACTIVITY_ARROWS,
                showActivityDefault(getActivity()), UserHandle.USER_CURRENT) != 0;
        mDataActivityEnabled.setChecked(mActivityEnabled);
        mDataActivityEnabled.setOnPreferenceChangeListener(this);

        mWifiActivityEnabled = (SwitchPreference) findPreference(WIFI_ACTIVITY_ARROWS);
        mActivityEnabled = Settings.System.getIntForUser(resolver,
                Settings.System.WIFI_ACTIVITY_ARROWS,
                showActivityDefault(getActivity()), UserHandle.USER_CURRENT) != 0;
        mWifiActivityEnabled.setChecked(mActivityEnabled);
        mWifiActivityEnabled.setOnPreferenceChangeListener(this);

        mTickerMode = (ListPreference) findPreference(TICKER_MODE);
        mTickerMode.setOnPreferenceChangeListener(this);

        mBatteryStyle = (ListPreference) findPreference(STATUS_BAR_BATTERY_STYLE);
        int batterystyle = Settings.System.getIntForUser(resolver,
                Settings.System.STATUS_BAR_BATTERY_STYLE, 0,
                UserHandle.USER_CURRENT);
        mBatteryStyle.setValue(String.valueOf(batterystyle));
        mBatteryStyle.setSummary(mBatteryStyle.getEntry());
        mBatteryStyle.setOnPreferenceChangeListener(this);

        mBatteryPercent = (ListPreference) findPreference(SHOW_BATTERY_PERCENT);
        int batterypercent = Settings.System.getIntForUser(resolver,
                Settings.System.SHOW_BATTERY_PERCENT, 0,
                UserHandle.USER_CURRENT);
        mBatteryPercent.setValue(String.valueOf(batterypercent));
        mBatteryPercent.setSummary(mBatteryPercent.getEntry());
        mBatteryPercent.setOnPreferenceChangeListener(this);

        mTextSymbol = (ListPreference) findPreference(TEXT_CHARGING_SYMBOL);
        int textsymbol = Settings.System.getIntForUser(resolver,
                Settings.System.TEXT_CHARGING_SYMBOL, 0,
                UserHandle.USER_CURRENT);
        mTextSymbol.setValue(String.valueOf(textsymbol));
        mTextSymbol.setSummary(mTextSymbol.getEntry());
        updateBatteryOptions();
        mTextSymbol.setOnPreferenceChangeListener(this);

        mHavocLogo = (SwitchPreference) findPreference(HAVOC_LOGO); 
        mHavocLogo.setOnPreferenceChangeListener(this); 
 
        mHavocLogoPosition = (ListPreference) findPreference(HAVOC_LOGO_POSITION); 
        int havocLogoPosition = Settings.System.getIntForUser(resolver, 
                Settings.System.STATUS_BAR_HAVOC_LOGO_POSITION, 0, 
                UserHandle.USER_CURRENT); 
        mHavocLogoPosition.setValue(String.valueOf(havocLogoPosition)); 
        mHavocLogoPosition.setSummary(mHavocLogoPosition.getEntry()); 
        mHavocLogoPosition.setOnPreferenceChangeListener(this); 
 
        mHavocLogoColor = 
                (ColorPickerPreference) findPreference(HAVOC_LOGO_COLOR); 
        int intColor = Settings.System.getIntForUser(resolver, 
                Settings.System.STATUS_BAR_HAVOC_LOGO_COLOR, 0xFFFFFFFF, 
                UserHandle.USER_CURRENT); 
        String hexColor = ColorPickerPreference.convertToARGB(intColor); 
        mHavocLogoColor.setNewPreviewColor(intColor); 
        if (intColor != 0xFFFFFFFF) { 
            mHavocLogoColor.setSummary(hexColor); 
        } else { 
            mHavocLogoColor.setSummary(R.string.default_string); 
        } 
        mHavocLogoColor.setOnPreferenceChangeListener(this); 
 
        mHavocLogoStyle = (ListPreference) findPreference(HAVOC_LOGO_STYLE); 
        int havocLogoStyle = Settings.System.getIntForUser(resolver, 
                Settings.System.STATUS_BAR_HAVOC_LOGO_STYLE, 0, 
                UserHandle.USER_CURRENT); 
        mHavocLogoStyle.setValue(String.valueOf(havocLogoStyle)); 
        mHavocLogoStyle.setSummary(mHavocLogoStyle.getEntry()); 
        mHavocLogoStyle.setOnPreferenceChangeListener(this); 
 
        boolean mLogoEnabled = Settings.System.getIntForUser(resolver, 
                Settings.System.STATUS_BAR_HAVOC_LOGO, 
                0, UserHandle.USER_CURRENT) != 0; 
        toggleLogo(mLogoEnabled); 
    } 
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mQuickPulldown) {
            int value = Integer.parseInt((String) newValue);
            LineageSettings.System.putIntForUser(resolver, LineageSettings.System.STATUS_BAR_QUICK_QS_PULLDOWN,
                    value, UserHandle.USER_CURRENT);
            updatePulldownSummary(value);
            return true;
        } else if (preference == mSmartPulldown) {
            int value = Integer.parseInt((String) newValue);
            Settings.System.putIntForUser(resolver, Settings.System.QS_SMART_PULLDOWN, value, UserHandle.USER_CURRENT);
            updateSmartPulldownSummary(value);
            return true;
        } else if (preference == mDataActivityEnabled) {
            boolean showing = ((Boolean)newValue);
            Settings.System.putIntForUser(resolver, Settings.System.DATA_ACTIVITY_ARROWS,
                    showing ? 1 : 0, UserHandle.USER_CURRENT);
            mDataActivityEnabled.setChecked(showing);
            return true;
        } else if (preference == mWifiActivityEnabled) {
            boolean showing = ((Boolean)newValue);
            Settings.System.putIntForUser(resolver, Settings.System.WIFI_ACTIVITY_ARROWS,
                    showing ? 1 : 0, UserHandle.USER_CURRENT);
            mWifiActivityEnabled.setChecked(showing);
            return true;
        } else if (preference.equals(mTickerMode)) { 
            int value = Integer.parseInt((String) newValue); 
            Settings.System.putInt(resolver, Settings.System.STATUS_BAR_SHOW_TICKER, value); 
            int index = mTickerMode.findIndexOfValue((String) newValue); 
            mTickerMode.setSummary(mTickerMode.getEntries()[index]); 
            return true;			
        } else if (preference == mBatteryStyle) {
            int value = Integer.parseInt((String) newValue);
            int index = mBatteryStyle.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(
                resolver, Settings.System.STATUS_BAR_BATTERY_STYLE, value,
                UserHandle.USER_CURRENT);
            mBatteryStyle.setSummary(
                    mBatteryStyle.getEntries()[index]);
            updateBatteryOptions();
            return true;
        } else if (preference == mBatteryPercent) {
            int value = Integer.parseInt((String) newValue);
            int index = mBatteryPercent.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(
                resolver, Settings.System.SHOW_BATTERY_PERCENT, value,
                UserHandle.USER_CURRENT);
            mBatteryPercent.setSummary(
                    mBatteryPercent.getEntries()[index]);
            updateBatteryOptions();
            return true;
        } else if (preference == mTextSymbol) {
            int value = Integer.parseInt((String) newValue);
            int index = mTextSymbol.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(
                resolver, Settings.System.TEXT_CHARGING_SYMBOL, value,
                UserHandle.USER_CURRENT);
            mTextSymbol.setSummary(
                    mTextSymbol.getEntries()[index]);
            return true;
        } else if (preference == mHavocLogo) { 
            boolean value = (Boolean) newValue; 
            toggleLogo(value); 
            return true; 
        } else if (preference == mHavocLogoColor) { 
            String hex = ColorPickerPreference.convertToARGB( 
                Integer.parseInt(String.valueOf(newValue))); 
            int value = ColorPickerPreference.convertToColorInt(hex); 
            Settings.System.putIntForUser(resolver, 
                Settings.System.STATUS_BAR_HAVOC_LOGO_COLOR, value, 
                UserHandle.USER_CURRENT); 
            if (value != 0xFFFFFFFF) { 
                mHavocLogoColor.setSummary(hex); 
            } else { 
                mHavocLogoColor.setSummary(R.string.default_string); 
            } 
            return true; 
        } else if (preference == mHavocLogoPosition) { 
            int value = Integer.parseInt((String) newValue); 
            int index = mHavocLogoPosition.findIndexOfValue((String) newValue); 
            Settings.System.putIntForUser( 
                resolver, Settings.System.STATUS_BAR_HAVOC_LOGO_POSITION, value, 
                UserHandle.USER_CURRENT); 
            mHavocLogoPosition.setSummary( 
                    mHavocLogoPosition.getEntries()[index]); 
            return true; 
        } else if (preference == mHavocLogoStyle) { 
            int value = Integer.parseInt((String) newValue); 
            int index = mHavocLogoStyle.findIndexOfValue((String) newValue); 
            Settings.System.putIntForUser( 
                resolver, Settings.System.STATUS_BAR_HAVOC_LOGO_STYLE, value, 
                UserHandle.USER_CURRENT); 
            mHavocLogoStyle.setSummary( 
                    mHavocLogoStyle.getEntries()[index]); 
            return true; 
        } 
        return false;
    }

    private void updateBatteryOptions() {
        ContentResolver resolver = getActivity().getContentResolver();
        int batterystyle = Settings.System.getIntForUser(resolver,
                Settings.System.STATUS_BAR_BATTERY_STYLE, 1,
                UserHandle.USER_CURRENT);
        mBatteryPercent.setEnabled(batterystyle != BATTERY_STYLE_TEXT && batterystyle != BATTERY_STYLE_HIDDEN);
        mTextSymbol.setEnabled(batterystyle == BATTERY_STYLE_TEXT);
    }

    private void updatePulldownSummary(int value) {
        Resources res = getResources();

        if (value == 0) {
            // quick pulldown deactivated
            mQuickPulldown.setSummary(res.getString(R.string.status_bar_quick_qs_pulldown_off));
        } else if (value == 3) {
            // quick pulldown always
            mQuickPulldown.setSummary(res.getString(R.string.status_bar_quick_qs_pulldown_always));
        } else {
            String direction = res.getString(value == 2
                    ? R.string.status_bar_quick_qs_pulldown_left
                    : R.string.status_bar_quick_qs_pulldown_right);
            mQuickPulldown.setSummary(res.getString(R.string.status_bar_quick_qs_pulldown_summary, direction));
        }
    }

    private void updateSmartPulldownSummary(int value) {
        Resources res = getResources();

        if (value == 0) {
            // Smart pulldown deactivated
            mSmartPulldown.setSummary(res.getString(R.string.smart_pulldown_off));
        } else if (value == 3) {
            mSmartPulldown.setSummary(res.getString(R.string.smart_pulldown_none_summary));
        } else {
            String type = res.getString(value == 1
                    ? R.string.smart_pulldown_dismissable
                    : R.string.smart_pulldown_ongoing);
            mSmartPulldown.setSummary(res.getString(R.string.smart_pulldown_summary, type));
        }
    }

    public void toggleLogo(boolean enabled) { 
        mHavocLogoColor.setEnabled(enabled); 
        mHavocLogoPosition.setEnabled(enabled); 
        mHavocLogoStyle.setEnabled(enabled); 
    } 

    public static int showActivityDefault(Context context) {

        /*final boolean showByDefault = context.getResources().getBoolean(
                com.android.internal.R.bool.config_showActivity);

        if (showByDefault) {
            return 1;
        }*/

        return 0;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
