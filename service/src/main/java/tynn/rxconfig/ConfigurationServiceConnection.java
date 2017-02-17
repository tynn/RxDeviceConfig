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

package tynn.rxconfig;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.IBinder;

import java.util.concurrent.atomic.AtomicReference;

import rx.Observer;
import rx.Subscription;

class ConfigurationServiceConnection implements ServiceConnection, Subscription {

    private final AtomicReference<Context> context;
    private final Observer<? super Configuration> observer;

    ConfigurationServiceConnection(Observer<? super Configuration> observer, Context context) {
        this.context = new AtomicReference<>(context);
        this.observer = observer;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        ((ConfigurationService.OnRegisterSubscriber) service).register(observer);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        observer.onError(new ServiceDisconnected());
    }

    @Override
    public void unsubscribe() {
        Context context = this.context.getAndSet(null);
        if (context != null) {
            context.unbindService(this);
        }
    }

    @Override
    public boolean isUnsubscribed() {
        return context.get() == null;
    }
}
