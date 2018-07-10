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
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.util.Log;
import com.android.internal.utils.du.DUActionUtils; 

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.havoc.Utils;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;

import com.havoc.settings.R;

import java.util.List;
import java.util.ArrayList;
import android.util.Log;

import net.margaritov.preference.colorpicker.ColorPickerPreference;
import com.havoc.settings.preferences.SystemSettingSeekBarPreference;

public class Weather extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener, Indexable {

    private static final String TAG = "Weather";
    private static final String CATEGORY_WEATHER = "weather_category";
    private static final String WEATHER_ICON_PACK = "weather_icon_pack";
    private static final String DEFAULT_WEATHER_ICON_PACKAGE = "org.omnirom.omnijaws";
    private static final String DEFAULT_WEATHER_ICON_PREFIX = "outline";
    private static final String CHRONUS_ICON_PACK_INTENT = "com.dvtonder.chronus.ICON_PACK";
    private static final String STATUS_BAR_TEMPERATURE = "status_bar_temperature";
    private static final String STATUS_BAR_TEMPERATURE_STYLE = "status_bar_temperature_style";
    private static final String PREF_STATUS_BAR_WEATHER_SIZE = "status_bar_weather_size";
    private static final String PREF_STATUS_BAR_WEATHER_FONT_STYLE = "status_bar_weather_font_style";
    private static final String PREF_STATUS_BAR_WEATHER_COLOR = "status_bar_weather_color";
    private static final String PREF_STATUS_BAR_WEATHER_IMAGE_COLOR = "status_bar_weather_image_color";
    private static final String TEMP_FONT_SIZE  = "locktemp_font_size";  
    private static final String CITY_FONT_SIZE  = "lockcity_font_size";  
    private static final String CONDITION_FONT_SIZE  = "lockcondition_font_size";  
    private static final String LOCK_TEMP_FONTS = "lock_temp_fonts"; 
    private static final String LOCK_CITY_FONTS = "lock_city_fonts"; 
    private static final String LOCK_CONDITION_FONTS = "lock_condition_fonts"; 
    private static final String KEY_LOCKSCREEN_WEATHER_SELECTION = "lockscreen_weather_selection"; 

    private ListPreference mStatusBarTemperature;
    private ListPreference mStatusBarTemperatureStyle;
    private SystemSettingSeekBarPreference mStatusBarTemperatureSize;
    private ListPreference mStatusBarTemperatureFontStyle;
    private ColorPickerPreference mStatusBarTemperatureColor;
    private ColorPickerPreference mStatusBarTemperatureImageColor;
    private PreferenceCategory mWeatherCategory;
    private ListPreference mWeatherIconPack;
    private SystemSettingSeekBarPreference mTempFontSize;  
    private SystemSettingSeekBarPreference mCityFontSize;  
    private SystemSettingSeekBarPreference mConditionFontSize;  
    private ListPreference mLockscreenWeatherSelection; 
    ListPreference mLockTempFonts; 
    ListPreference mLockCityFonts; 
    ListPreference mLockConditionFonts; 

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.havoc_settings_weather);
        final PreferenceScreen prefScreen = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

        mWeatherCategory = (PreferenceCategory) prefScreen.findPreference(CATEGORY_WEATHER);
        if (mWeatherCategory != null && !isOmniJawsServiceInstalled()) {
            prefScreen.removePreference(mWeatherCategory);
        } else {
            mWeatherIconPack = (ListPreference) findPreference(WEATHER_ICON_PACK);
            String settingHeaderPackage = Settings.System.getStringForUser(getContentResolver(),
                    Settings.System.OMNIJAWS_WEATHER_ICON_PACK, UserHandle.USER_CURRENT);
            if (settingHeaderPackage == null) {
                settingHeaderPackage = DEFAULT_WEATHER_ICON_PACKAGE + "." + DEFAULT_WEATHER_ICON_PREFIX;
            }

            List<String> entries = new ArrayList<String>();
            List<String> values = new ArrayList<String>();
            getAvailableWeatherIconPacks(entries, values);
            mWeatherIconPack.setEntries(entries.toArray(new String[entries.size()]));
            mWeatherIconPack.setEntryValues(values.toArray(new String[values.size()]));

            int valueIndex = mWeatherIconPack.findIndexOfValue(settingHeaderPackage);
            if (valueIndex == -1) {
                // no longer found
                settingHeaderPackage = DEFAULT_WEATHER_ICON_PACKAGE + "." + DEFAULT_WEATHER_ICON_PREFIX;
                valueIndex = mWeatherIconPack.findIndexOfValue(settingHeaderPackage);
            }

            mWeatherIconPack.setValueIndex(valueIndex >= 0 ? valueIndex : 0);
            mWeatherIconPack.setSummary(mWeatherIconPack.getEntry());
            mWeatherIconPack.setOnPreferenceChangeListener(this);
        }

        mLockscreenWeatherSelection = (ListPreference) findPreference(KEY_LOCKSCREEN_WEATHER_SELECTION); 
        int weatherSelection = Settings.System.getIntForUser(resolver, 
                Settings.System.LOCKSCREEN_WEATHER_SELECTION, 0, UserHandle.USER_CURRENT); 
                mLockscreenWeatherSelection.setValue(String.valueOf(weatherSelection)); 
                mLockscreenWeatherSelection.setSummary(mLockscreenWeatherSelection.getEntry()); 
                mLockscreenWeatherSelection.setOnPreferenceChangeListener(this); 

        // Lockscren temp Fonts 
        mLockTempFonts = (ListPreference) findPreference(LOCK_TEMP_FONTS); 
        mLockTempFonts.setValue(String.valueOf(Settings.System.getInt( 
                getContentResolver(), Settings.System.LOCK_TEMP_FONTS, 8))); 
                mLockTempFonts.setSummary(mLockTempFonts.getEntry()); 
                mLockTempFonts.setOnPreferenceChangeListener(this); 

        // Lockscren city Fonts 
        mLockCityFonts = (ListPreference) findPreference(LOCK_CITY_FONTS); 
        mLockCityFonts.setValue(String.valueOf(Settings.System.getInt( 
                getContentResolver(), Settings.System.LOCK_CITY_FONTS, 8))); 
                mLockCityFonts.setSummary(mLockCityFonts.getEntry()); 
                mLockCityFonts.setOnPreferenceChangeListener(this); 

        // Lockscren condition Fonts 
        mLockConditionFonts = (ListPreference) findPreference(LOCK_CONDITION_FONTS); 
        mLockConditionFonts.setValue(String.valueOf(Settings.System.getInt( 
                getContentResolver(), Settings.System.LOCK_CONDITION_FONTS, 8))); 
                mLockConditionFonts.setSummary(mLockConditionFonts.getEntry()); 
                mLockConditionFonts.setOnPreferenceChangeListener(this); 

        mTempFontSize = (SystemSettingSeekBarPreference) findPreference(TEMP_FONT_SIZE); 
        mTempFontSize.setValue(Settings.System.getInt(getContentResolver(), 
        Settings.System.LOCKTEMP_FONT_SIZE,14)); 
        mTempFontSize.setOnPreferenceChangeListener(this); 

        mCityFontSize = (SystemSettingSeekBarPreference) findPreference(CITY_FONT_SIZE); 
        mCityFontSize.setValue(Settings.System.getInt(getContentResolver(), 
        Settings.System.LOCKCITY_FONT_SIZE,14)); 
        mCityFontSize.setOnPreferenceChangeListener(this); 

        mConditionFontSize = (SystemSettingSeekBarPreference) findPreference(CONDITION_FONT_SIZE); 
        mConditionFontSize.setValue(Settings.System.getInt(getContentResolver(), 
        Settings.System.LOCKCONDITION_FONT_SIZE,14)); 
        mConditionFontSize.setOnPreferenceChangeListener(this); 

        int intColor;
        String hexColor;

        mStatusBarTemperature = (ListPreference) findPreference(STATUS_BAR_TEMPERATURE);
        int temperatureShow = Settings.System.getIntForUser(resolver,
                Settings.System.STATUS_BAR_SHOW_WEATHER_TEMP, 0,
                UserHandle.USER_CURRENT);
        mStatusBarTemperature.setValue(String.valueOf(temperatureShow));
        mStatusBarTemperature.setSummary(mStatusBarTemperature.getEntry());
        mStatusBarTemperature.setOnPreferenceChangeListener(this);

        mStatusBarTemperatureStyle = (ListPreference) findPreference(STATUS_BAR_TEMPERATURE_STYLE);
        int temperatureStyle = Settings.System.getIntForUser(resolver,
                Settings.System.STATUS_BAR_WEATHER_TEMP_STYLE, 0,
                UserHandle.USER_CURRENT);
        mStatusBarTemperatureStyle.setValue(String.valueOf(temperatureStyle));
        mStatusBarTemperatureStyle.setSummary(mStatusBarTemperatureStyle.getEntry());
        mStatusBarTemperatureStyle.setOnPreferenceChangeListener(this);

        mStatusBarTemperatureSize = (SystemSettingSeekBarPreference) findPreference(PREF_STATUS_BAR_WEATHER_SIZE);
        mStatusBarTemperatureSize.setValue(Settings.System.getIntForUser(resolver,
                Settings.System.STATUS_BAR_WEATHER_SIZE, 24,
                UserHandle.USER_CURRENT));
        mStatusBarTemperatureSize.setOnPreferenceChangeListener(this);

        mStatusBarTemperatureFontStyle = (ListPreference) findPreference(PREF_STATUS_BAR_WEATHER_FONT_STYLE);
        mStatusBarTemperatureFontStyle.setOnPreferenceChangeListener(this);
        mStatusBarTemperatureFontStyle.setValue(Integer.toString(Settings.System.getIntForUser(resolver,
                Settings.System.STATUS_BAR_WEATHER_FONT_STYLE, 0, UserHandle.USER_CURRENT)));
        mStatusBarTemperatureFontStyle.setSummary(mStatusBarTemperatureFontStyle.getEntry());

        mStatusBarTemperatureColor =
            (ColorPickerPreference) findPreference(PREF_STATUS_BAR_WEATHER_COLOR);
        intColor = Settings.System.getIntForUser(resolver,
                Settings.System.STATUS_BAR_WEATHER_COLOR, 0xffffffff, UserHandle.USER_CURRENT);
        hexColor = ColorPickerPreference.convertToARGB(intColor);
        mStatusBarTemperatureColor.setNewPreviewColor(intColor);
        if (intColor != 0xFFFFFFFF) {
            mStatusBarTemperatureColor.setSummary(hexColor);
        } else {
            mStatusBarTemperatureColor.setSummary(R.string.default_string);
        }
        mStatusBarTemperatureColor.setOnPreferenceChangeListener(this);

        mStatusBarTemperatureImageColor =
            (ColorPickerPreference) findPreference(PREF_STATUS_BAR_WEATHER_IMAGE_COLOR);
        intColor = Settings.System.getIntForUser(resolver,
                Settings.System.STATUS_BAR_WEATHER_IMAGE_COLOR, 0xffffffff, UserHandle.USER_CURRENT);
        hexColor = ColorPickerPreference.convertToARGB(intColor);
        mStatusBarTemperatureImageColor.setNewPreviewColor(intColor);
        if (intColor != 0xFFFFFFFF) {
            mStatusBarTemperatureImageColor.setSummary(hexColor);
        } else {
            mStatusBarTemperatureImageColor.setSummary(R.string.default_string);
        }
        mStatusBarTemperatureImageColor.setOnPreferenceChangeListener(this);

        updateWeatherOptions();
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {

        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mStatusBarTemperature) {
            int temperatureShow = Integer.valueOf((String) newValue);
            int index = mStatusBarTemperature.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(
                    resolver, Settings.System.STATUS_BAR_SHOW_WEATHER_TEMP, temperatureShow,
                    UserHandle.USER_CURRENT);
            mStatusBarTemperature.setSummary(
                    mStatusBarTemperature.getEntries()[index]);
            updateWeatherOptions();
            return true;
        } else if (preference == mStatusBarTemperatureStyle) {
            int temperatureStyle = Integer.valueOf((String) newValue);
            int index = mStatusBarTemperatureStyle.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(
                    resolver, Settings.System.STATUS_BAR_WEATHER_TEMP_STYLE, temperatureStyle,
                    UserHandle.USER_CURRENT);
            mStatusBarTemperatureStyle.setSummary(
                    mStatusBarTemperatureStyle.getEntries()[index]);
            return true;
        } else if (preference == mStatusBarTemperatureSize) {
            int width = ((Integer)newValue).intValue();
            Settings.System.putInt(resolver,
                    Settings.System.STATUS_BAR_WEATHER_SIZE, width);
            return true;
        } else if (preference == mStatusBarTemperatureFontStyle) {
            int val = Integer.parseInt((String) newValue);
            int index = mStatusBarTemperatureFontStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver,
                    Settings.System.STATUS_BAR_WEATHER_FONT_STYLE, val);
            mStatusBarTemperatureFontStyle.setSummary(mStatusBarTemperatureFontStyle.getEntries()[index]);
            return true;
        } else if (preference == mStatusBarTemperatureColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(resolver,
                    Settings.System.STATUS_BAR_WEATHER_COLOR, intHex);
            if (intHex != 0xFFFFFFFF) {
                mStatusBarTemperatureColor.setSummary(hex);
            } else {
                mStatusBarTemperatureColor.setSummary(R.string.default_string);
            }
            return true;
        } else if (preference == mStatusBarTemperatureImageColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(resolver,
                    Settings.System.STATUS_BAR_WEATHER_IMAGE_COLOR, intHex);
            if (intHex != 0xFFFFFFFF) {
                mStatusBarTemperatureImageColor.setSummary(hex);
            } else {
                mStatusBarTemperatureImageColor.setSummary(R.string.default_string);
            }
            return true;
        } else if (preference == mWeatherIconPack) {
            String value = (String) newValue;
            Settings.System.putStringForUser(getContentResolver(),
                    Settings.System.OMNIJAWS_WEATHER_ICON_PACK, value, UserHandle.USER_CURRENT);
            int valueIndex = mWeatherIconPack.findIndexOfValue(value);
            mWeatherIconPack.setSummary(mWeatherIconPack.getEntries()[valueIndex]);
        } else if (preference == mTempFontSize) { 
            int top = (Integer) newValue; 
           Settings.System.putInt(getContentResolver(), 
                   Settings.System.LOCKTEMP_FONT_SIZE, top*1); 
           return true; 
        } else if (preference == mCityFontSize) { 
            int top = (Integer) newValue; 
           Settings.System.putInt(getContentResolver(), 
                   Settings.System.LOCKCITY_FONT_SIZE, top*1); 
           return true; 
        } else if (preference == mConditionFontSize) { 
            int top = (Integer) newValue; 
           Settings.System.putInt(getContentResolver(), 
                   Settings.System.LOCKCONDITION_FONT_SIZE, top*1); 
           return true;
        } else if (preference == mLockTempFonts) { 
            Settings.System.putInt(getContentResolver(), Settings.System.LOCK_TEMP_FONTS, 
                    Integer.valueOf((String) newValue)); 
                    mLockTempFonts.setValue(String.valueOf(newValue)); 
                    mLockTempFonts.setSummary(mLockTempFonts.getEntry()); 
            return true;    
         } else if (preference == mLockCityFonts) { 
                Settings.System.putInt(getContentResolver(), Settings.System.LOCK_CITY_FONTS, 
                        Integer.valueOf((String) newValue)); 
                        mLockCityFonts.setValue(String.valueOf(newValue)); 
                        mLockCityFonts.setSummary(mLockCityFonts.getEntry()); 
                return true; 
        } else if (preference == mLockConditionFonts) { 
            Settings.System.putInt(getContentResolver(), Settings.System.LOCK_CONDITION_FONTS, 
                    Integer.valueOf((String) newValue)); 
                    mLockConditionFonts.setValue(String.valueOf(newValue)); 
                    mLockConditionFonts.setSummary(mLockConditionFonts.getEntry()); 
            return true; 
        } else if (preference == mLockscreenWeatherSelection) { 
            int weatherSelection = Integer.valueOf((String) newValue); 
            int index = mLockscreenWeatherSelection.findIndexOfValue((String) newValue); 
            Settings.System.putIntForUser(resolver, 
                    Settings.System.LOCKSCREEN_WEATHER_SELECTION, weatherSelection, UserHandle.USER_CURRENT); 
                    mLockscreenWeatherSelection.setSummary(mLockscreenWeatherSelection.getEntries()[index]); 
            return true; 
        } 
        return false;
    }

    private void updateWeatherOptions() {
        ContentResolver resolver = getActivity().getContentResolver();
        int status = Settings.System.getIntForUser(
                resolver, Settings.System.STATUS_BAR_SHOW_WEATHER_TEMP, 0, UserHandle.USER_CURRENT);
        if (status == 0) {
            mStatusBarTemperatureStyle.setEnabled(false);
            mStatusBarTemperatureColor.setEnabled(false);
            mStatusBarTemperatureSize.setEnabled(false);
            mStatusBarTemperatureFontStyle.setEnabled(false);
            mStatusBarTemperatureImageColor.setEnabled(false);
        } else if (status == 1 || status == 2){
            mStatusBarTemperatureStyle.setEnabled(true);
            mStatusBarTemperatureColor.setEnabled(true);
            mStatusBarTemperatureSize.setEnabled(true);
            mStatusBarTemperatureFontStyle.setEnabled(true);
            mStatusBarTemperatureImageColor.setEnabled(true);
        } else if (status == 3 || status == 4) {
            mStatusBarTemperatureStyle.setEnabled(true);
            mStatusBarTemperatureColor.setEnabled(true);
            mStatusBarTemperatureSize.setEnabled(true);
            mStatusBarTemperatureFontStyle.setEnabled(true);
            mStatusBarTemperatureImageColor.setEnabled(false);
        } else if (status == 5) {
            mStatusBarTemperatureStyle.setEnabled(true);
            mStatusBarTemperatureColor.setEnabled(false);
            mStatusBarTemperatureSize.setEnabled(false);
            mStatusBarTemperatureFontStyle.setEnabled(false);
            mStatusBarTemperatureImageColor.setEnabled(true);
        }
    }

    private boolean isOmniJawsServiceInstalled() {
        return Utils.isPackageInstalled(getActivity(), DEFAULT_WEATHER_ICON_PACKAGE);
    }

    private void getAvailableWeatherIconPacks(List<String> entries, List<String> values) {
        Intent i = new Intent();
        PackageManager packageManager = getActivity().getPackageManager();
        i.setAction("org.omnirom.WeatherIconPack");
        for (ResolveInfo r : packageManager.queryIntentActivities(i, 0)) {
            String packageName = r.activityInfo.packageName;
            String label = r.activityInfo.loadLabel(getActivity().getPackageManager()).toString();
            if (label == null) {
                label = r.activityInfo.packageName;
            }
            if (entries.contains(label)) {
                continue;
            }
            if (packageName.equals(DEFAULT_WEATHER_ICON_PACKAGE)) {
                values.add(0, r.activityInfo.name);
            } else {
                values.add(r.activityInfo.name);
            }

            if (packageName.equals(DEFAULT_WEATHER_ICON_PACKAGE)) {
                entries.add(0, label);
            } else {
                entries.add(label);
            }
        }
        i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(CHRONUS_ICON_PACK_INTENT);
        for (ResolveInfo r : packageManager.queryIntentActivities(i, 0)) {
            String packageName = r.activityInfo.packageName;
            String label = r.activityInfo.loadLabel(getActivity().getPackageManager()).toString();
            if (label == null) {
                label = r.activityInfo.packageName;
            }
            if (entries.contains(label)) {
                continue;
            }
            values.add(packageName + ".weather");

            entries.add(label);
        }
    }

    private boolean isOmniJawsEnabled() {
        final Uri SETTINGS_URI
            = Uri.parse("content://org.omnirom.omnijaws.provider/settings");

        final String[] SETTINGS_PROJECTION = new String[] {
            "enabled"
        };

        final Cursor c = getContentResolver().query(SETTINGS_URI, SETTINGS_PROJECTION,
                null, null, null);
        if (c != null) {
            int count = c.getCount();
            if (count == 1) {
                c.moveToPosition(0);
                boolean enabled = c.getInt(0) == 1;
                return enabled;
            }
        }
        return true;
    }

    public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                        boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();

                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.havoc_settings_weather;
                    result.add(sir);

                    return result;
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    ArrayList<String> result = new ArrayList<String>();
                    return result;
                }
    };

    public static void reset(Context mContext) {
        ContentResolver resolver = mContext.getContentResolver();
        Settings.System.putStringForUser(resolver,
                Settings.System.OMNIJAWS_WEATHER_ICON_PACK, null, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver, 
                Settings.System.LOCK_SCREEN_SHOW_WEATHER, 0, UserHandle.USER_CURRENT); 
        Settings.System.putIntForUser(resolver, 
                Settings.System.LOCK_SCREEN_WEATHER_CONDITION_ICON, 1, UserHandle.USER_CURRENT); 
        Settings.System.putIntForUser(resolver, 
                Settings.System.LOCK_SCREEN_SHOW_WEATHER_LOCATION, 1, UserHandle.USER_CURRENT);		
        Settings.System.putIntForUser(resolver,
                Settings.System.STATUS_BAR_SHOW_WEATHER_TEMP, 0, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.STATUS_BAR_WEATHER_TEMP_STYLE, 0, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.STATUS_BAR_WEATHER_IMAGE_COLOR, 0xFFFFFFFF, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.STATUS_BAR_WEATHER_SIZE, 14, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.STATUS_BAR_WEATHER_FONT_STYLE, 0, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.STATUS_BAR_WEATHER_COLOR, 0xFFFFFFFF, UserHandle.USER_CURRENT);		
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
