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

import android.os.Bundle;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.SettingsPreferenceFragment;

import com.havoc.settings.R;

public class AggressiveBattery extends SettingsPreferenceFragment {

    public static final String TAG = "AggressiveBattery";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.aggressive_battery);

        mFooterPreferenceMixin.createFooterPreference().setTitle(R.string.aggressive_battery_warning_text);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
