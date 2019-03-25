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
import android.content.FontInfo;
import android.content.IFontService;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.provider.Settings;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.widget.Toast;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.havoc.HavocUtils;

import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.display.FontDialogPreference;
import com.android.settings.havoc.AccentPicker;
import com.android.settingslib.drawer.SettingsDrawerActivity;

import com.havoc.settings.R;
import com.havoc.settings.Utils;
import com.havoc.settings.fragments.ThemePicker;
import com.havoc.support.colorpicker.ColorPickerPreference;
import com.havoc.support.preferences.CustomSeekBarPreference;
import com.havoc.support.preferences.SystemPropListPreference;
import com.havoc.support.preferences.SystemSettingMasterSwitchPreference;

import java.util.List;
import java.util.ArrayList;

public class Interface extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    public static final String TAG = "Interface";

    private String KEY_THEME_PICKER = "theme_picker";
    private String KEY_ACCENT_PICKER = "accent_picker";
    private static final String UI_STYLE = "ui_style";
    private static final String SYSTEM_UI_THEME = "systemui_theme_style";
    private static final String QS_PANEL_COLOR = "qs_panel_color";
    private static final String QS_PANEL_ALPHA = "qs_panel_alpha";
    private static final String QS_HEADER_STYLE = "qs_header_style";
    private static final String STATUS_BAR_CUSTOM_HEADER = "status_bar_custom_header";
    private static final String QS_TILE_STYLE = "qs_tile_style";
    private static final String KEY_FONT_PICKER_FRAGMENT_PREF = "custom_font";
    private static final String SWITCH_STYLE = "switch_style";
    private static final String KEY_ICON_SHAPE = "persist.system.iconshape";
    static final int DEFAULT_QS_PANEL_COLOR = 0xFFFFFFFF;

    private ColorPickerPreference mQsPanelColor;
    private CustomSeekBarPreference mQsPanelAlpha;
    private ListPreference mUiStyle;
    private ListPreference mSystemUiThemeStyle;
    private ListPreference mQsHeaderStyle;
    private ListPreference mQsTileStyle;
    private ListPreference mSwitchStyle;
    private Preference mAccentPicker;
    private Preference mThemePicker;
    private SystemPropListPreference mIconShape;
    private SystemSettingMasterSwitchPreference mCustomHeader;
    private FontDialogPreference mFontPreference;

    Context mContext;

    IFontService mFontService = IFontService.Stub.asInterface(ServiceManager.getService("havocfont"));

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.havoc_settings_interface);

        ContentResolver resolver = getActivity().getContentResolver();

        mUiStyle = (ListPreference) findPreference(UI_STYLE);
        int uiStyle = Settings.System.getInt(resolver,
                Settings.System.UI_STYLE, 0);
        int valueIndex = mUiStyle.findIndexOfValue(String.valueOf(uiStyle));
        mUiStyle.setValueIndex(valueIndex >= 0 ? valueIndex : 0);
        mUiStyle.setSummary(mUiStyle.getEntry());
        mUiStyle.setOnPreferenceChangeListener(this);

        mSystemUiThemeStyle = (ListPreference) findPreference(SYSTEM_UI_THEME);
        int systemuiThemeStyle = Settings.System.getInt(resolver,
                Settings.System.SYSTEM_UI_THEME, 0);
        int themeValueIndex = mSystemUiThemeStyle.findIndexOfValue(String.valueOf(systemuiThemeStyle));
        mSystemUiThemeStyle.setValueIndex(themeValueIndex >= 0 ? themeValueIndex : 0);
        mSystemUiThemeStyle.setSummary(mSystemUiThemeStyle.getEntry());
        mSystemUiThemeStyle.setOnPreferenceChangeListener(this);

        mAccentPicker = findPreference(KEY_ACCENT_PICKER);

        mThemePicker = findPreference(KEY_THEME_PICKER);

        mQsPanelColor = (ColorPickerPreference) findPreference(QS_PANEL_COLOR);
        mQsPanelColor.setOnPreferenceChangeListener(this);
        int intColor = Settings.System.getIntForUser(resolver,
                Settings.System.QS_PANEL_BG_COLOR, DEFAULT_QS_PANEL_COLOR, UserHandle.USER_CURRENT);
        String hexColor = String.format("#%08x", (0xFFFFFFFF & intColor));
        if (hexColor.equals("#ffffffff")) {
            mQsPanelColor.setSummary(R.string.default_string);
        } else {
            mQsPanelColor.setSummary(hexColor);
        }
        mQsPanelColor.setNewPreviewColor(intColor);

        mQsPanelAlpha = (CustomSeekBarPreference) findPreference(QS_PANEL_ALPHA);
        int qsPanelAlpha = Settings.System.getIntForUser(resolver,
                Settings.System.QS_PANEL_BG_ALPHA, 255, UserHandle.USER_CURRENT);
        mQsPanelAlpha.setValue(qsPanelAlpha);
        mQsPanelAlpha.setOnPreferenceChangeListener(this);

        mQsHeaderStyle = (ListPreference) findPreference(QS_HEADER_STYLE);
        int qsHeaderStyle = Settings.System.getInt(resolver,
                Settings.System.QS_HEADER_STYLE, 0);
        int headerValueIndex = mQsHeaderStyle.findIndexOfValue(String.valueOf(qsHeaderStyle));
        mQsHeaderStyle.setValueIndex(headerValueIndex >= 0 ? headerValueIndex : 0);
        mQsHeaderStyle.setSummary(mQsHeaderStyle.getEntry());
        mQsHeaderStyle.setOnPreferenceChangeListener(this);

        mCustomHeader = (SystemSettingMasterSwitchPreference) findPreference(STATUS_BAR_CUSTOM_HEADER);
        int qsHeader = Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_CUSTOM_HEADER, 0);
        mCustomHeader.setChecked(qsHeader != 0);
        mCustomHeader.setOnPreferenceChangeListener(this);

        mQsTileStyle = (ListPreference) findPreference(QS_TILE_STYLE);
        int qsTileStyle = Settings.System.getInt(resolver,
                Settings.System.QS_TILE_STYLE, 0);
        int tileValueIndex = mQsTileStyle.findIndexOfValue(String.valueOf(qsTileStyle));
        mQsTileStyle.setValueIndex(tileValueIndex >= 0 ? tileValueIndex : 0);
        mQsTileStyle.setSummary(mQsTileStyle.getEntry());
        mQsTileStyle.setOnPreferenceChangeListener(this);

        mSwitchStyle = (ListPreference) findPreference(SWITCH_STYLE);
        int switchStyle = Settings.System.getInt(resolver,
                Settings.System.SWITCH_STYLE, 2);
        int switchValueIndex = mSwitchStyle.findIndexOfValue(String.valueOf(switchStyle));
        mSwitchStyle.setValueIndex(switchValueIndex >= 0 ? switchValueIndex : 0);
        mSwitchStyle.setSummary(mSwitchStyle.getEntry());
        mSwitchStyle.setOnPreferenceChangeListener(this);

        mFontPreference = (FontDialogPreference) findPreference(KEY_FONT_PICKER_FRAGMENT_PREF);
        mFontPreference.setSummary(getCurrentFontInfo().fontName.replace("_", " "));

        mIconShape = (SystemPropListPreference) findPreference(KEY_ICON_SHAPE);
        mIconShape.setOnPreferenceChangeListener(this);

        updateThemePicker(systemuiThemeStyle);
    }

    public void updateThemePicker(int systemuiThemeStyle) {
        switch(systemuiThemeStyle){ 
            case 0:
                mThemePicker.setEnabled(false);
                break;
            case 1:
                mThemePicker.setEnabled(false);
                break;
            case 2:
                mThemePicker.setEnabled(false);
                break;
            case 3:
                mThemePicker.setEnabled(true);
                break;
            case 4:
                mThemePicker.setEnabled(true);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mUiStyle) {
            String value = (String) newValue;
            Settings.System.putInt(resolver, Settings.System.UI_STYLE, Integer.valueOf(value));
            int valueIndex = mUiStyle.findIndexOfValue(value);
            mUiStyle.setSummary(mUiStyle.getEntries()[valueIndex]);
            HavocUtils.restartSystemUi(getContext());
            return true;
        } else if (preference == mSystemUiThemeStyle) {
            String value = (String) newValue;
            Settings.System.putInt(resolver, Settings.System.SYSTEM_UI_THEME, Integer.valueOf(value));
            int systemuiThemeStyle = mSystemUiThemeStyle.findIndexOfValue(value);
            mSystemUiThemeStyle.setSummary(mSystemUiThemeStyle.getEntries()[systemuiThemeStyle]);
            updateThemePicker(systemuiThemeStyle);
            return true;
        } else if (preference == mQsPanelColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hex.equals("#ffffffff")) {
                preference.setSummary(R.string.default_string);
            } else {
                preference.setSummary(hex);
            }
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putIntForUser(resolver,
                    Settings.System.QS_PANEL_BG_COLOR, intHex, UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mQsPanelAlpha) {
            int bgAlpha = (Integer) newValue;
            Settings.System.putIntForUser(resolver,
                    Settings.System.QS_PANEL_BG_ALPHA, bgAlpha, UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mQsHeaderStyle) {
            String value = (String) newValue;
            Settings.System.putInt(resolver, Settings.System.QS_HEADER_STYLE, Integer.valueOf(value));
            int valueIndex = mQsHeaderStyle.findIndexOfValue(value);
            mQsHeaderStyle.setSummary(mQsHeaderStyle.getEntries()[valueIndex]);
            return true;
        } else if (preference == mCustomHeader) {
            boolean header = (Boolean) newValue;
            Settings.System.putInt(resolver,
                    Settings.System.STATUS_BAR_CUSTOM_HEADER, header ? 1 : 0);
            return true;
        } else if (preference == mQsTileStyle) {
            String value = (String) newValue;
            Settings.System.putInt(resolver, Settings.System.QS_TILE_STYLE, Integer.valueOf(value));
            int valueIndex = mQsTileStyle.findIndexOfValue(value);
            mQsTileStyle.setSummary(mQsTileStyle.getEntries()[valueIndex]);
            return true;
        } else if (preference == mSwitchStyle) {
            String value = (String) newValue;
            Settings.System.putInt(resolver, Settings.System.SWITCH_STYLE, Integer.valueOf(value));
            int valueIndex = mSwitchStyle.findIndexOfValue(value);
            mSwitchStyle.setSummary(mSwitchStyle.getEntries()[valueIndex]);
            return true;
        } else if (preference == mIconShape) {
            Utils.showRebootDialog(getActivity(), getString(R.string.icon_shape_changed_title),
                    getString(R.string.icon_shape_changed_message));
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference == mAccentPicker) {
            AccentPicker.show(this);
        } else if (preference == mThemePicker) {
            ThemePicker.show(this);
        }
        return super.onPreferenceTreeClick(preference);
    }

    private FontInfo getCurrentFontInfo() {
        try {
            return mFontService.getFontInfo();
        } catch (RemoteException e) {
            return FontInfo.getDefaultFontInfo();
        }
    }

	public void stopProgress() {
        mFontPreference.stopProgress();
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
