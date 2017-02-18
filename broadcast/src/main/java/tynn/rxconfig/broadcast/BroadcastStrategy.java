/*
 * Copyright (C) 2017 Christian Schmitz
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

package tynn.rxconfig.broadcast;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;

import rx.Observer;
import rx.Subscription;
import tynn.rxconfig.RxDeviceConfig;

/**
 *
 */
public final class BroadcastStrategy implements RxDeviceConfig.Strategy {

    private final IntentFilter filter = new IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED);

    @Override
    public Subscription call(Observer<? super Configuration> observer, Context context) {
        ConfigurationBroadcastReceiver receiver
                = new ConfigurationBroadcastReceiver(observer, context);
        context.registerReceiver(receiver, filter);
        return receiver;
    }
}
