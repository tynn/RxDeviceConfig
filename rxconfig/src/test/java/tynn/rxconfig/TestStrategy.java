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

import android.content.Context;
import android.content.res.Configuration;

import rx.Observer;
import rx.Subscription;
import rx.functions.Action0;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

class TestStrategy implements RxDeviceConfig.Strategy, Action0 {

    final CompositeSubscription subscription = new CompositeSubscription();
    final PublishSubject<Configuration> emitter = PublishSubject.create();

    Observer isCalledWithObserver;
    Context isCalledWithContext;
    boolean isUnsubscribed;

    @Override
    public Subscription call(Observer<? super Configuration> observer, Context context) {
        isCalledWithObserver = observer;
        isCalledWithContext = context;
        subscription.add(Subscriptions.create(this));
        subscription.add(emitter.subscribe(observer));
        return subscription;
    }

    @Override
    public void call() {
        isUnsubscribed = true;
    }

    void assertCalledWith(Class<?> observer) {
        assertThat(isCalledWithObserver, instanceOf(observer));
    }

    void assertCalledWith(Context context) {
        assertThat(isCalledWithContext, is(context));
    }

    void assertUnsubscribed() {
        assertThat(isUnsubscribed, is(true));
    }
}
