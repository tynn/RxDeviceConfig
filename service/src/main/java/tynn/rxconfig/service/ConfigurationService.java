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

package tynn.rxconfig.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import rx.Observer;

/**
 *
 */
public class ConfigurationService extends Service {

    final Set<Observer<? super Configuration>> observers = Collections
            .newSetFromMap(new WeakHashMap<Observer<? super Configuration>, Boolean>());

    @Override
    public IBinder onBind(Intent intent) {
        return new OnRegisterSubscriber();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        observers.clear();
        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        ArrayList<Observer<? super Configuration>> observers
                = new ArrayList<>(this.observers);
        for (Observer<? super Configuration> observer : observers) {
            observer.onNext(new Configuration(newConfig));
        }
    }

    class OnRegisterSubscriber extends Binder {

        void register(Observer<? super Configuration> observer) {
            observers.add(observer);
        }
    }
}
