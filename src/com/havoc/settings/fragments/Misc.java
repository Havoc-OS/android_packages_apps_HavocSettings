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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;
import android.app.AlertDialog;  
import android.app.Dialog;  
import android.content.ContentResolver;  
import android.content.res.Resources; 
import android.view.LayoutInflater;  
import android.view.View;  
import android.widget.AdapterView;  
import android.widget.AdapterView.OnItemClickListener;  
import android.widget.ListView;  
import android.widget.Toast;  
import com.havoc.settings.preferences.ScreenshotEditPackageListAdapter;  
import com.havoc.settings.preferences.ScreenshotEditPackageListAdapter.PackageItem;  

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.development.DevelopmentSettings;
import com.android.settings.SettingsPreferenceFragment;

import com.havoc.settings.R;

public class Misc extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    public static final String TAG = "Misc";

    private static final String MEDIA_SCANNER_ON_BOOT = "media_scanner_on_boot";

    private static final int DIALOG_SCREENSHOT_EDIT_APP = 1; 
 
    private Preference mScreenshotEditAppPref;  
    private ScreenshotEditPackageListAdapter mPackageAdapter;  

    private ListPreference mMSOB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.havoc_settings_misc);

        // MediaScanner behavior on boot
        mMSOB = (ListPreference) findPreference(MEDIA_SCANNER_ON_BOOT);
        int mMSOBValue = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.MEDIA_SCANNER_ON_BOOT, 0);
        mMSOB.setValue(String.valueOf(mMSOBValue));
        mMSOB.setSummary(mMSOB.getEntry());
        mMSOB.setOnPreferenceChangeListener(this);


       mPackageAdapter = new ScreenshotEditPackageListAdapter(getActivity());
       mScreenshotEditAppPref = findPreference("screenshot_edit_app");
       mScreenshotEditAppPref.setOnPreferenceClickListener(this);
   }

   @Override
   public Dialog onCreateDialog(int dialogId) {
       switch (dialogId) {
           case DIALOG_SCREENSHOT_EDIT_APP: {
               Dialog dialog;
               AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
               final ListView list = new ListView(getActivity());
               list.setAdapter(mPackageAdapter);
               alertDialog.setTitle(R.string.profile_choose_app);
               alertDialog.setView(list);
               dialog = alertDialog.create();
               list.setOnItemClickListener(new OnItemClickListener() {
                   @Override
                   public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                       // Add empty application definition, the user will be able to edit it later
                       PackageItem info = (PackageItem) parent.getItemAtPosition(position);
                       Settings.System.putString(getActivity().getContentResolver(),
                               Settings.System.SCREENSHOT_EDIT_USER_APP, info.packageName);
                       dialog.cancel();
                   }
               });
               return dialog;
           }
        }
       return super.onCreateDialog(dialogId);
   }

   @Override
   public int getDialogMetricsCategory(int dialogId) {
       switch (dialogId) {
           case DIALOG_SCREENSHOT_EDIT_APP:
               return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
           default:
               return 0;
       }
    }

    public boolean onPreferenceClick(Preference preference) {
        // Don't show the dialog if there are no available editor apps
        if (preference == mScreenshotEditAppPref && mPackageAdapter.getCount() > 0) {
            showDialog(DIALOG_SCREENSHOT_EDIT_APP);
        } else {
            Toast.makeText(getActivity(), getActivity().getString(R.string.screenshot_edit_app_no_editor),
                    Toast.LENGTH_LONG).show();
        }
        return true;
    }
        

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mMSOB) {
            int value = Integer.parseInt(((String) newValue).toString());
            Settings.System.putInt(getContentResolver(),
                    Settings.System.MEDIA_SCANNER_ON_BOOT, value);
            mMSOB.setValue(String.valueOf(value));
            mMSOB.setSummary(mMSOB.getEntries()[value]);
            return true;
        } 
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
