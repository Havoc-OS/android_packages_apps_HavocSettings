/*
 * Copyright (C) 2017 AICP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.havoc.settings.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.support.v7.preference.Preference;
import android.support.v14.preference.SwitchPreference;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.logging.nano.MetricsProto; 
import com.android.settings.SettingsPreferenceFragment;
import com.havoc.settings.R;

import com.havoc.support.colorpicker.ColorPickerPreference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class SlimRecents extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener, DialogInterface.OnDismissListener {

    private static final String RECENT_PANEL_LEFTY_MODE = "recent_panel_lefty_mode";
    private static final String RECENT_ICON_PACK = "slim_icon_pack";
    private static final String RECENT_PANEL_BG_COLOR = "recent_panel_bg_color";
    private static final String RECENT_CARD_BG_COLOR = "recent_card_bg_color";
    private static final String SLIM_MEM_BAR_COLOR = "slim_mem_bar_color";
    private static final String SLIM_MEM_TEXT_COLOR = "slim_mem_text_color";

    private SwitchPreference mRecentPanelLeftyMode;
    private Preference mIconPack;
    private ColorPickerPreference mRecentPanelBgColor;
    private ColorPickerPreference mRecentCardBgColor;
    private ColorPickerPreference mMemBarColor;
    private ColorPickerPreference mMemTextColor;

    // Icon pack
    private final static String[] sSupportedActions = new String[] {
        "org.adw.launcher.THEMES",
        "com.gau.go.launcherex.theme"
    };
    private static final String[] sSupportedCategories = new String[] {
        "com.fede.launcher.THEME_ICONPACK",
        "com.anddoes.launcher.THEME",
        "com.teslacoilsw.launcher.THEME"
    };
    private AlertDialog mDialog;
    private ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.slim_recents);

        mRecentPanelLeftyMode = (SwitchPreference) findPreference(RECENT_PANEL_LEFTY_MODE);
        mRecentPanelLeftyMode.setOnPreferenceChangeListener(this);
        mIconPack = findPreference(RECENT_ICON_PACK);

        // Recent panel background color
        mRecentPanelBgColor = (ColorPickerPreference) findPreference(RECENT_PANEL_BG_COLOR);
        mRecentPanelBgColor.setOnPreferenceChangeListener(this);
        final int intColor = Settings.System.getInt(getContext().getContentResolver(),
                Settings.System.RECENT_PANEL_BG_COLOR, 0x00ffffff);
        String hexColor = String.format("#%08x", (0x00ffffff & intColor));
        if (hexColor.equals("#00ffffff")) {
            mRecentPanelBgColor.setSummary(R.string.default_string);
        } else {
            mRecentPanelBgColor.setSummary(hexColor);
        }
        mRecentPanelBgColor.setNewPreviewColor(intColor);

        // Recent card background color
        mRecentCardBgColor = (ColorPickerPreference) findPreference(RECENT_CARD_BG_COLOR);
        mRecentCardBgColor.setOnPreferenceChangeListener(this);
        final int intColorCard = Settings.System.getInt(getContext().getContentResolver(),
                Settings.System.RECENT_CARD_BG_COLOR, 0x00ffffff);
        String hexColorCard = String.format("#%08x", (0x00ffffff & intColorCard));
        if (hexColorCard.equals("#00ffffff")) {
            mRecentCardBgColor.setSummary(R.string.default_auto_string);
        } else {
            mRecentCardBgColor.setSummary(hexColorCard);
        }
        mRecentCardBgColor.setNewPreviewColor(intColorCard);

        mMemBarColor = (ColorPickerPreference) findPreference(SLIM_MEM_BAR_COLOR);
        mMemBarColor.setOnPreferenceChangeListener(this);
        final int intMemColor = Settings.System.getInt(getContext().getContentResolver(),
                Settings.System.SLIM_MEM_BAR_COLOR, 0xffffffff);
        String hexMemColor = String.format("#%08x", (0xffffffff & intMemColor));
        if (hexMemColor.equals("#ffffffff")) {
            mMemBarColor.setSummary(R.string.default_string);
        } else {
            mMemBarColor.setSummary(hexMemColor);
        }
        mMemBarColor.setNewPreviewColor(intMemColor);

        mMemTextColor = (ColorPickerPreference) findPreference(SLIM_MEM_TEXT_COLOR);
        mMemTextColor.setOnPreferenceChangeListener(this);
        final int intTextColor = Settings.System.getInt(getContext().getContentResolver(),
                Settings.System.SLIM_MEM_TEXT_COLOR, 0xffffffff);
        String hexTextColor = String.format("#%08x", (0xffffffff & intTextColor));
        if (hexTextColor.equals("#ffffffff")) {
            mMemTextColor.setSummary(R.string.default_string);
        } else {
            mMemTextColor.setSummary(hexTextColor);
        }
        mMemTextColor.setNewPreviewColor(intTextColor);
    }

    @Override
    public void onResume() {
        super.onResume();

        boolean recentLeftyMode = Settings.System.getInt(getContext().getContentResolver(),
                Settings.System.RECENT_PANEL_GRAVITY, Gravity.END) == Gravity.START;
        mRecentPanelLeftyMode.setChecked(recentLeftyMode);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mRecentPanelLeftyMode) {
            Settings.System.putInt(getContext().getContentResolver(),
                    Settings.System.RECENT_PANEL_GRAVITY,
                    ((Boolean) newValue) ? Gravity.START : Gravity.END);
            return true;
        } else if (preference == mRecentPanelBgColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hex.equals("#00ffffff")) {
                preference.setSummary(R.string.default_string);
            } else {
                preference.setSummary(hex);
            }
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putIntForUser(getContext().getContentResolver(),
                    Settings.System.RECENT_PANEL_BG_COLOR,
                    intHex, UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mRecentCardBgColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hex.equals("#00ffffff")) {
                preference.setSummary(R.string.default_auto_string);
            } else {
                preference.setSummary(hex);
            }
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putIntForUser(getContext().getContentResolver(),
                    Settings.System.RECENT_CARD_BG_COLOR,
                    intHex, UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mMemBarColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hex.equals("#ffffffff")) {
                preference.setSummary(R.string.default_string);
            } else {
                preference.setSummary(hex);
            }
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putIntForUser(getContext().getContentResolver(),
                    Settings.System.SLIM_MEM_BAR_COLOR,
                    intHex, UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mMemTextColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hex.equals("#ffffffff")) {
                preference.setSummary(R.string.default_string);
            } else {
                preference.setSummary(hex);
            }
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putIntForUser(getContext().getContentResolver(),
                    Settings.System.SLIM_MEM_TEXT_COLOR,
                    intHex, UserHandle.USER_CURRENT);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference == mIconPack) {
            pickIconPack(getContext());
            return true;
        } else {
            return super.onPreferenceTreeClick(preference);
        }
    }

     /** Slim Recents Icon Pack Dialog **/
    private void pickIconPack(final Context context) {
        if (mDialog != null) {
            return;
        }
        Map<String, IconPackInfo> supportedPackages = getSupportedPackages(context);
        if (supportedPackages.isEmpty()) {
            Toast.makeText(context, R.string.no_iconpacks_summary, Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
        .setTitle(R.string.dialog_pick_iconpack_title)
        .setOnDismissListener(this)
        .setNegativeButton(R.string.cancel, null)
        .setView(createDialogView(context, supportedPackages));
        mDialog = builder.show();
    }

    private View createDialogView(final Context context, Map<String, IconPackInfo> supportedPackages) {
        final LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_iconpack, null);
        final IconAdapter adapter = new IconAdapter(context, supportedPackages);

        mListView = (ListView) view.findViewById(R.id.iconpack_list);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                        int position, long id) {
                if (adapter.isCurrentIconPack(position)) {
                    return;
                }
                String selectedPackage = adapter.getItem(position);
                Settings.System.putString(getContext().getContentResolver(),
                        Settings.System.SLIM_RECENTS_ICON_PACK, selectedPackage);
                mDialog.dismiss();
            }
        });

        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mDialog != null) {
            mDialog = null;
        }
    }

    private static class IconAdapter extends BaseAdapter {
        ArrayList<IconPackInfo> mSupportedPackages;
        LayoutInflater mLayoutInflater;
        String mCurrentIconPack;
        int mCurrentIconPackPosition = -1;

        IconAdapter(Context ctx, Map<String, IconPackInfo> supportedPackages) {
            mLayoutInflater = LayoutInflater.from(ctx);
            mSupportedPackages = new ArrayList<IconPackInfo>(supportedPackages.values());
            Collections.sort(mSupportedPackages, new Comparator<IconPackInfo>() {
                @Override
                public int compare(IconPackInfo lhs, IconPackInfo rhs) {
                    return lhs.label.toString().compareToIgnoreCase(rhs.label.toString());
                }
            });

            Resources res = ctx.getResources();
            String defaultLabel = res.getString(R.string.default_iconpack_title);
            Drawable icon = res.getDrawable(android.R.drawable.sym_def_app_icon);
            mSupportedPackages.add(0, new IconPackInfo(defaultLabel, icon, ""));
            mCurrentIconPack = Settings.System.getString(ctx.getContentResolver(),
                Settings.System.SLIM_RECENTS_ICON_PACK);
        }

        @Override
        public int getCount() {
            return mSupportedPackages.size();
        }

        @Override
        public String getItem(int position) {
            return (String) mSupportedPackages.get(position).packageName;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public boolean isCurrentIconPack(int position) {
            return mCurrentIconPackPosition == position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.iconpack_view_radio, null);
            }
            IconPackInfo info = mSupportedPackages.get(position);
            TextView txtView = (TextView) convertView.findViewById(R.id.title);
            txtView.setText(info.label);
            ImageView imgView = (ImageView) convertView.findViewById(R.id.icon);
            imgView.setImageDrawable(info.icon);
            RadioButton radioButton = (RadioButton) convertView.findViewById(R.id.radio);
            boolean isCurrentIconPack = info.packageName.equals(mCurrentIconPack);
            radioButton.setChecked(isCurrentIconPack);
            if (isCurrentIconPack) {
                mCurrentIconPackPosition = position;
            }
            return convertView;
        }
    }

    private Map<String, IconPackInfo> getSupportedPackages(Context context) {
        Intent i = new Intent();
        Map<String, IconPackInfo> packages = new HashMap<String, IconPackInfo>();
        PackageManager packageManager = context.getPackageManager();
        for (String action : sSupportedActions) {
            i.setAction(action);
            for (ResolveInfo r : packageManager.queryIntentActivities(i, 0)) {
                IconPackInfo info = new IconPackInfo(r, packageManager);
                packages.put(r.activityInfo.packageName, info);
            }
        }
        i = new Intent(Intent.ACTION_MAIN);
        for (String category : sSupportedCategories) {
            i.addCategory(category);
            for (ResolveInfo r : packageManager.queryIntentActivities(i, 0)) {
                IconPackInfo info = new IconPackInfo(r, packageManager);
                packages.put(r.activityInfo.packageName, info);
            }
            i.removeCategory(category);
        }
        return packages;
    }

    private static class IconPackInfo {
        String packageName;
        CharSequence label;
        Drawable icon;

        IconPackInfo(ResolveInfo r, PackageManager packageManager) {
            packageName = r.activityInfo.packageName;
            icon = r.loadIcon(packageManager);
            label = r.loadLabel(packageManager);
        }

        IconPackInfo(){
        }

        public IconPackInfo(String label, Drawable icon, String packageName) {
            this.label = label;
            this.icon = icon;
            this.packageName = packageName;
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
